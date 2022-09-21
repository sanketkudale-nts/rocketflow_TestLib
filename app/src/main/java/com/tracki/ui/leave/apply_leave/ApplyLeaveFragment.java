package com.tracki.ui.leave.apply_leave;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.BaseResponse;
import com.tracki.data.model.request.ApplyLeaveReq;
import com.tracki.data.model.response.config.LeaveHistoryData;
import com.tracki.data.model.response.config.LeaveSummaryResponse;
import com.tracki.data.model.response.config.LeaveType;
import com.tracki.data.model.response.config.Summary;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.FragmentApplyLeaveBinding;
import com.tracki.ui.adjusttime.AdjustTimeActivity;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.leave.LeaveActivity;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.Log;
import com.tracki.utils.TrackiToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class ApplyLeaveFragment extends BaseFragment<FragmentApplyLeaveBinding, ApplyLeaveViewModel>
        implements ApplyLeaveNavigator {

    public static final String TAG = ApplyLeaveFragment.class.getSimpleName();
    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;
    Spinner spnLeaveType;
    private File image = null;
    private ApplyLeaveViewModel mApplyViewModel;
    private String[] permissionArray = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Uri uri;
    private Button btnFrom, btnTo;
    private EditText edtComments;
    private long fromInMillis, toInMillis, fromDefault, toDefault;
    private FragmentApplyLeaveBinding mFragmentApplyLeaveBinding;
    private TextView tvLeaveBalance;
    private int sickLeaveCnt = 0;
    private int casualLeaveCnt = 0;
    private int sickLeaveBalance = 0, casualLeaveBalance = 0;
    private LeaveHistoryData leaveHistoryData;
    int yr, mnth, dayofmnth, mYear, mMonth, mDay, hourTo, minTo, hourFrom, minFrom;

    public static ApplyLeaveFragment newInstance() {
        Bundle args = new Bundle();
        ApplyLeaveFragment fragment = new ApplyLeaveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_apply_leave;
    }

    @Override
    public ApplyLeaveViewModel getViewModel() {
//        mApplyViewModel = new ViewModelProvider(this).get(ApplyLeaveViewModel.class);
        mApplyViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ApplyLeaveViewModel.class);
        return mApplyViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplyViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentApplyLeaveBinding = getViewDataBinding();
        spnLeaveType = mFragmentApplyLeaveBinding.spnLeaveType;


        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getBaseActivity(),
                R.layout.item_simple_spinner, new String[]{"Select", "Casual Leave", "Sick Leave"});
        spnAdapter.setDropDownViewResource(R.layout.item_simple_spinner_dropdown);
        spnLeaveType.setAdapter(spnAdapter);
        btnFrom = mFragmentApplyLeaveBinding.btnDateFrom;
        btnTo = mFragmentApplyLeaveBinding.btnDateTo;
        edtComments = mFragmentApplyLeaveBinding.edtComments;
        tvLeaveBalance = mFragmentApplyLeaveBinding.tvLeaveBalance;


    }

    private void performUiTask() {
        final Calendar c = Calendar.getInstance();
        Calendar fromCal = (Calendar) c.clone();
        if (getBaseActivity() != null && ((LeaveActivity) getBaseActivity()).isEdit) {
            leaveHistoryData = ((LeaveActivity) getBaseActivity()).leaveHistoryData;
            if (leaveHistoryData != null) {
                c.setTimeInMillis(leaveHistoryData.getTo());
                fromCal.setTimeInMillis(leaveHistoryData.getFrom());
            }
        }
        toDefault = c.getTimeInMillis();
        hourTo = c.get(Calendar.HOUR_OF_DAY);
        minTo = c.get(Calendar.MINUTE);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        toInMillis = c.getTimeInMillis();

        btnTo.setText(DateTimeUtil.getParsedDateApply(toDefault));

        hourFrom = fromCal.get(Calendar.HOUR_OF_DAY);
        minFrom = fromCal.get(Calendar.MINUTE);
        fromDefault = fromCal.getTimeInMillis();
        fromCal.set(Calendar.HOUR_OF_DAY, 0);
        fromCal.set(Calendar.MINUTE, 0);
        fromCal.set(Calendar.SECOND, 0);
        yr = fromCal.get(Calendar.YEAR);
        mnth = fromCal.get(Calendar.MONTH);
        dayofmnth = fromCal.get(Calendar.DAY_OF_MONTH);

        fromInMillis = fromCal.getTimeInMillis();
        btnFrom.setText(DateTimeUtil.getParsedDateApply(fromDefault));

        btnFrom.setOnClickListener(v -> {
            CommonUtils.openDatePicker(getBaseActivity(), yr, mnth,
                    dayofmnth, System.currentTimeMillis(), 0, (vw, year, monthOfYear, dayOfMonth) -> {
//                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        CommonUtils.openTimePicker(getBaseActivity(), hourFrom, minFrom, (vt, hr, mn) -> {

                            fromCal.set(Calendar.YEAR, year);
                            fromCal.set(Calendar.MONTH, monthOfYear);
                            fromCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            fromCal.set(Calendar.HOUR_OF_DAY, hr);
                            fromCal.set(Calendar.MINUTE, mn);
                            fromCal.set(Calendar.SECOND, 0);

                            Log.e(TAG, "<--DATE is: " + fromCal.toString() +
                                    "<-=-=-=-=-==>" + fromCal.getTime().toString());


                            fromInMillis = fromCal.getTimeInMillis();
                            btnFrom.setText(DateTimeUtil.getParsedDateApply(fromInMillis));
                            yr = fromCal.get((Calendar.YEAR));
                            mnth = fromCal.get(Calendar.MONTH);
                            dayofmnth = fromCal.get(Calendar.DAY_OF_MONTH);
                            hourFrom = fromCal.get(Calendar.HOUR_OF_DAY);
                            minFrom = fromCal.get(Calendar.MINUTE);
                        });


//
//                        sickLeaveBalance = (int) (sickLeaveCnt - ((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24) + 1));
//                        casualLeaveBalance = (int) (casualLeaveCnt - ((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24) + 1));


//                        if (fromInMillis<toInMillis) {
//                            if (spnLeaveType.getSelectedItem().toString().equalsIgnoreCase(
//                                    "Sick Leave")) {
//                                if (sickLeaveBalance > 0) {
//                                    tvLeaveBalance.setText(sickLeaveBalance
//                                            + " "
//                                            + spnLeaveType.getSelectedItem().toString() + "s" + " remaining");
//
//                                } else {
//                                    tvLeaveBalance.setText("No " + spnLeaveType.getSelectedItem().toString() + "s" + " remaining");
//                                }
//                            }
//
//                            if (spnLeaveType.getSelectedItem().toString().equalsIgnoreCase("Casual Leave")
//                            ) {
//
//                                if (casualLeaveBalance > 0) {
//                                    tvLeaveBalance.setText(casualLeaveCnt - ((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24) + 1)
//                                            + " "
//                                            + spnLeaveType.getSelectedItem().toString() + "s" + " remaining");
//
//                                } else {
//                                    tvLeaveBalance.setText("No "
//                                            + spnLeaveType.getSelectedItem().toString() + "s" + " remaining");
//                                }
//                            }
//                        }
//
//                        if (((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24) + 1) > (sickLeaveCnt + casualLeaveCnt)) {
//                            TrackiToast.Message.showLong(getBaseActivity(), "Insufficient Leave Balance!");
//                        }
                    });
        });
        btnTo.setOnClickListener(v -> {
            CommonUtils.openDatePicker(getBaseActivity(), mYear, mMonth,
                    mDay, 0, 0, (vw2, year2, monthOfYear2, dayOfMonth2) -> {
//                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        CommonUtils.openTimePicker(getBaseActivity(), hourTo, minTo, (vt2, hr2, mn2) -> {

                            c.set(Calendar.YEAR, year2);
                            c.set(Calendar.MONTH, monthOfYear2);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth2);
                            c.set(Calendar.HOUR_OF_DAY, hr2);
                            c.set(Calendar.MINUTE, mn2);
                            c.set(Calendar.SECOND, 0);

                            toInMillis = c.getTimeInMillis();
                            btnTo.setText(DateTimeUtil.getParsedDateApply(toInMillis));

                            mYear = c.get((Calendar.YEAR));
                            mMonth = c.get(Calendar.MONTH);
                            mDay = c.get(Calendar.DAY_OF_MONTH);
                            hourTo = c.get(Calendar.HOUR_OF_DAY);
                            minTo = c.get(Calendar.MINUTE);
                        });


//                        sickLeaveBalance = (int) (sickLeaveCnt - ((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24) + 1));
//                        casualLeaveBalance = (int) (casualLeaveCnt - ((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24) + 1));


//                        if (spnLeaveType.getSelectedItem().toString().equalsIgnoreCase(
//                                "Sick Leave")) {
//                            if (sickLeaveBalance > 0) {
//                                tvLeaveBalance.setText((sickLeaveCnt - ((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24) + 1))
//                                        + " Sick Leaves Remaining");
//                            }
//                        } else {
//                            tvLeaveBalance.setText("No"
//                                    + " Sick Leaves Remaining");
//                        }
//
//                        if (spnLeaveType.getSelectedItem().toString().equalsIgnoreCase("Casual Leave")
//                        ) {
//                            if (casualLeaveBalance > 0) {
//                                tvLeaveBalance.setText((casualLeaveCnt - ((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24) + 1))
//                                        + " Casual Leaves Remaining");
//                            }
//                        } else {
//                            tvLeaveBalance.setText("No"
//                                    + " Casual Leaves Remaining");
//                        }
//
//
//                        if (((toInMillis - fromInMillis) / (1000 * 60 * 60 * 24)) > (sickLeaveCnt + casualLeaveCnt)) {
//                            TrackiToast.Message.showLong(getBaseActivity(), "Insufficient Leave Balance!");
//                        }
                    });
        });


        if (spnLeaveType.getSelectedItem().toString().equalsIgnoreCase("Select")) {
            tvLeaveBalance.setVisibility(View.GONE);
        } else
            tvLeaveBalance.setVisibility(View.VISIBLE);

        spnLeaveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (!spnLeaveType.getSelectedItem().toString().equalsIgnoreCase("Select"))
                    tvLeaveBalance.setVisibility(View.VISIBLE);

                else

                    tvLeaveBalance.setVisibility(View.GONE);


                switch (String.valueOf(spnLeaveType.getSelectedItem())) {
                    case "Casual Leave":
                        if (spnLeaveType.getSelectedItem().toString().equalsIgnoreCase("Casual Leave")
                        ) {
                            if (casualLeaveBalance > 0) {
                                tvLeaveBalance.setText(casualLeaveCnt
                                        + " Casual Leaves Remaining");
                            } else {
                                tvLeaveBalance.setText("No"
                                        + " Casual Leaves Remaining");
                            }
                        }
                        break;

                    case "Sick Leave":
                        if (spnLeaveType.getSelectedItem().toString().equalsIgnoreCase("Sick Leave")
                        ) {

                            if (sickLeaveBalance > 0) {
                                tvLeaveBalance.setText(sickLeaveCnt
                                        + " Sick Leaves Remaining");
                            } else {
                                tvLeaveBalance.setText("No"
                                        + " Sick Leaves Remaining");
                            }
                        }
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void applyLeaveEdit() {
        ApplyLeaveReq applyLeaveReq = new ApplyLeaveReq();
        applyLeaveReq.setFrom(fromInMillis);
        applyLeaveReq.setTo(toInMillis);
        applyLeaveReq.setRemarks(mFragmentApplyLeaveBinding.edtComments.getText().toString());
        applyLeaveReq.setLeaveType(CommonUtils.getLeaveTypeConstant(mFragmentApplyLeaveBinding.spnLeaveType.getSelectedItem().toString()));
        applyLeaveReq.setLrId(leaveHistoryData.getLrId());
        mApplyViewModel.applyLeaveEdit(httpManager, applyLeaveReq);
    }

    private void loadEditData() {
        leaveHistoryData = ((LeaveActivity) getBaseActivity()).leaveHistoryData;
        mFragmentApplyLeaveBinding.spnLeaveType.setSelection(getSpinnerPosFromValue(CommonUtils.getLeaveTypeString(leaveHistoryData.getLeaveType())));
        mFragmentApplyLeaveBinding.edtComments.setText(leaveHistoryData.getRemarks());

        //Subtract 19800000 to convert from UTC to IST
        mFragmentApplyLeaveBinding.btnDateFrom.setText(DateTimeUtil.getParsedDateApply(leaveHistoryData.getFrom()));
        //Subtract 84598977 to (convert UTC to IST + remove the extra time (EOD time) sent by backend
        mFragmentApplyLeaveBinding.btnDateTo.setText(DateTimeUtil.getParsedDateApply(leaveHistoryData.getTo()));


    }

    private int getSpinnerPosFromValue(String spnValue) {
        switch (spnValue) {
            case "Casual Leave":
                return 1;

            case "Sick Leave":
                return 2;

            default:
                return 0;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isUserVisible) {
        super.setUserVisibleHint(isUserVisible);
        // when fragment visible to user and view is not null then enter here.
        if (isUserVisible && getRootView() != null) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        showLoading();
        mApplyViewModel.getServerTime(httpManager);


    }

    public void performSubmit() {
        if (toInMillis < fromInMillis) {
            TrackiToast.Message.showLong(getBaseActivity(), "End date cannot be less than start date");
        } else {
            if (spnLeaveType.getSelectedItem().toString().equalsIgnoreCase("Select")) {
                TrackiToast.Message.showLong(getBaseActivity(), "Please select leave type!");
            }

//        else if ((toInMillis - fromInMillis) / (1000*60*60*24) > (sickLeaveCnt+casualLeaveCnt))
//        {
//
//        }

            else {

                if (((LeaveActivity) getBaseActivity()).isEdit) {
                    applyLeaveEdit();
                    ((LeaveActivity) getBaseActivity()).isEdit = false;
                    showLoading();
                } else {
                    ApplyLeaveReq applyLeaveReq = new ApplyLeaveReq();
                    applyLeaveReq.setFrom(fromInMillis);
                    applyLeaveReq.setTo(toInMillis);
                    applyLeaveReq.setRemarks(edtComments.getText().toString());
                    applyLeaveReq.setLeaveType(mApplyViewModel.stringToEnum(spnLeaveType.getSelectedItem().toString()));

                    mApplyViewModel.applyLeave(httpManager, applyLeaveReq);
                    showLoading();

                }


            }
        }
    }

    @Override
    public void onClickSubmit() {
        showLoading();
        mApplyViewModel.getServerOnSubmitTime(httpManager);

    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, getContext())) {
            Log.i("STATUS", result + "");
            TrackiToast.Message.showLong(getBaseActivity(), "Applied for leave successfully!");
            ((LeaveActivity) getBaseActivity()).viewPager.setCurrentItem(2, true);
            spnLeaveType.setSelection(0);
            tvLeaveBalance.setVisibility(View.GONE);
            btnFrom.setText(DateTimeUtil.getParsedDate(fromDefault));
            btnTo.setText(DateTimeUtil.getParsedDate(toDefault));
            edtComments.setText("");
        }


    }

    @Override
    public void handleEditResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, getContext())) {
            Log.i("STATUS", result + "");
            TrackiToast.Message.showLong(getBaseActivity(), "Applied for leave successfully!");
            ((LeaveActivity) getBaseActivity()).viewPager.setCurrentItem(2, true);
        }


    }

    @Override
    public void handleLeaveSummaryResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, getContext())) {
            Log.i("STATUS", result + "");
            LeaveSummaryResponse leaveSummaryResponse = new Gson().fromJson(String.valueOf(result), LeaveSummaryResponse.class);

            List<Summary> summaryList = null;
            if (leaveSummaryResponse.getSummary() != null) {
                if (leaveSummaryResponse.getSummary().getSummary() != null)
                    summaryList = leaveSummaryResponse.getSummary().getSummary();
            }
            if (summaryList != null) {
                for (int i = 0; i < summaryList.size(); i++) {
                    if (summaryList.get(i).getType() != null
                            && summaryList.get(i).getType().equals(LeaveType.SICK_LEAVE)
                        /* || summaryList.get(i).getType().equals(LeaveType.SEEK_LEAVE*)*/) {
                        sickLeaveCnt = summaryList.get(i).getRemaing();
                    } else if (summaryList.get(i).getType() != null && summaryList.get(i).getType().equals(LeaveType.CASUAL_LEAVE)) {
                        casualLeaveCnt = summaryList.get(i).getRemaing();
                    }
                }
            }

            sickLeaveBalance = sickLeaveCnt;
            casualLeaveBalance = casualLeaveCnt;
        }

    }

    @Override
    public void handleServerTimeResponse(@NotNull ApiCallback callback, @org.jetbrains.annotations.Nullable Object result, @org.jetbrains.annotations.Nullable APIError error) {
        hideLoading();
        if(CommonUtils.handleResponse(callback, error, result, getContext())) {
            JSONConverter jsonConverter = new JSONConverter();
            BaseResponse baseResponse = (BaseResponse) jsonConverter.jsonToObject(result.toString(), BaseResponse.class);

            if (baseResponse != null) {
                long now = Long.parseLong(baseResponse.getTime());
                if (Math.abs(now - System.currentTimeMillis()) >= AppConstants.TIME_DIFF) {
                    // the device's time is wrong
                    // startErrorActivity();
                    startActivityForResult(new Intent(getBaseActivity(), AdjustTimeActivity.class), AppConstants.REQUEST_TIME_ZONE);
                } else {
                    if (getBaseActivity() != null) {
                        getBaseActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                performUiTask();
                                if (((LeaveActivity) getBaseActivity()).isEdit) {
                                    loadEditData();
                                }

                                mApplyViewModel.onPageLoad(httpManager);
                                showLoading();
                            }
                        });
                    }

                }
            }
        }
    }

    @Override
    public void handleServerTimeOnSubmitResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        if(CommonUtils.handleResponse(callback, error, result, getContext())) {
            JSONConverter jsonConverter = new JSONConverter();
            BaseResponse baseResponse = (BaseResponse) jsonConverter.jsonToObject(result.toString(), BaseResponse.class);

            if (baseResponse != null) {
                long now = Long.parseLong(baseResponse.getTime());
                if (Math.abs(now - System.currentTimeMillis()) >= AppConstants.TIME_DIFF) {
                    // the device's time is wrong
                    // startErrorActivity();
                    startActivityForResult(new Intent(getBaseActivity(), AdjustTimeActivity.class), AppConstants.REQUEST_TIME_ZONE);
                } else {
                    if (getBaseActivity() != null) {
                        getBaseActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                performSubmit();
                            }
                        });
                    }


                }
            }
        }

    }
}