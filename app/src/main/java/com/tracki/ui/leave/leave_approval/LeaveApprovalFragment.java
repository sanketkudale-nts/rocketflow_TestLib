package com.tracki.ui.leave.leave_approval;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.request.ApproveRejectLeaveRequest;
import com.tracki.data.model.request.LeaveApprovalRequest;
import com.tracki.data.model.response.config.Action;
import com.tracki.data.model.response.config.LeaveApproval;
import com.tracki.data.model.response.config.LeaveStatus;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.FragmentLeaveApprovalBinding;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.common.ApproveRejectLeaveDialog;
import com.tracki.ui.common.OnClickApproveRejectNowListener;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.Log;
import com.tracki.utils.TrackiToast;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import javax.inject.Inject;

public class LeaveApprovalFragment extends BaseFragment<FragmentLeaveApprovalBinding, LeaveApprovalViewModel>
        implements LeaveApprovalNavigator, OnClickApproveRejectNowListener, OnClickRejectListener {

    public static final String TAG = LeaveApprovalFragment.class.getSimpleName();

    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;
    @Inject
    LeaveApprovalAdapter leaveApprovalAdapter;
    private LeaveApprovalRequest leaveApprovalRequest;

    private String lrId, remarks;
    private long updatedAt;
    private FragmentLeaveApprovalBinding mFragmentLeaveApprovalBinding;
    private LeaveApprovalViewModel mLeaveApprovalViewModel;
    private long fromInMillis, toInMillis;
    private LeaveStatus status;

    private ApproveRejectLeaveRequest approveRejectLeaveRequest;

    private ProgressDialog mProgressDialog;
    private LinearLayout llHeader;


    public LeaveApprovalFragment() {
    }

    public static LeaveApprovalFragment newInstance() {
        Bundle args = new Bundle();
        LeaveApprovalFragment fragment = new LeaveApprovalFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_leave_approval;
    }

    @Override
    public LeaveApprovalViewModel getViewModel() {
//        mLeaveApprovalViewModel = new ViewModelProvider(this).get(LeaveApprovalViewModel.class);
        mLeaveApprovalViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LeaveApprovalViewModel.class);
        return mLeaveApprovalViewModel;
    }


    @Override
    public void onClickReject(String lrId, long updatedAt) {
        this.lrId = lrId;
        this.updatedAt = updatedAt;
        ApproveRejectLeaveDialog approveRejectLeaveDialog = new ApproveRejectLeaveDialog(getBaseActivity(), this, AppConstants.REJECTING_LEAVE, AppConstants.REJECT_NOW,
                Action.REJECT);
        approveRejectLeaveDialog.show();
        Window window = approveRejectLeaveDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClickApproveRejectNow(String comment, Action action) {
        if (action == Action.REJECT) {
            approveRejectLeaveRequest = new ApproveRejectLeaveRequest();
            approveRejectLeaveRequest.setLrId(lrId);
            approveRejectLeaveRequest.setUpdatedAt(System.currentTimeMillis());
            approveRejectLeaveRequest.setComments(comment);
            mLeaveApprovalViewModel.rejectLeave(httpManager, approveRejectLeaveRequest);

        } else if (action == Action.APPROVE) {
            approveRejectLeaveRequest = new ApproveRejectLeaveRequest();
            approveRejectLeaveRequest.setLrId(lrId);
            approveRejectLeaveRequest.setUpdatedAt(System.currentTimeMillis());
            approveRejectLeaveRequest.setRemarks(remarks);
            mLeaveApprovalViewModel.approveLeave(httpManager, approveRejectLeaveRequest);

        }
        showLoading();


    }

    @Override
    public void onClickApprove(@NotNull String lrId, @NotNull String remarks, long updatedAt) {
        this.lrId = lrId;
        this.remarks = remarks;
        this.updatedAt = updatedAt;
        ApproveRejectLeaveDialog approveRejectLeaveDialog = new ApproveRejectLeaveDialog(getBaseActivity(),
                this, AppConstants.APPROVING_LEAVE, AppConstants.APPROVE_NOW,
                Action.APPROVE);

        approveRejectLeaveDialog.show();
        Window window = approveRejectLeaveDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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

        setLeaveReqData(fromInMillis, toInMillis, status);
        mLeaveApprovalViewModel.getLeaveApprovalData(httpManager, leaveApprovalRequest);
        showLoading();
        hideKeyboard();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLeaveApprovalViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentLeaveApprovalBinding = getViewDataBinding();
        mFragmentLeaveApprovalBinding.rvApproval.setAdapter(leaveApprovalAdapter);
        mFragmentLeaveApprovalBinding.rvApproval.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        llHeader=mFragmentLeaveApprovalBinding.llHeader;


//        spnStatus = mFragmentLeaveApprovalBinding.spnStatus;
//        btnDateFrom = mFragmentLeaveApprovalBinding.btnDateFrom;
//        btnDateTo = mFragmentLeaveApprovalBinding.btnDateTo;
//
//
//        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getBaseActivity(),
//                android.R.layout.simple_spinner_item, new String[]{"All", "Pending", "Approved", "Rejected"});
//        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnStatus.setAdapter(spnAdapter);
//
////        LeaveApprovalData leaveApprovalData = new LeaveApprovalData();
////        leaveApprovalData.setStatus(LeaveApprovalStatus.PENDING);
////        leaveApprovalData.setFrom("12th Jan");
////        leaveApprovalData.setTo("14th Jan");
////        leaveApprovalData.setLeaveNum("3");
////        leaveApprovalData.setLeaveType(LeaveType.CASUAL_LEAVE);
////        leaveApprovalData.setApprovedOrRejectAt("1st Dec, 2019");
////        leaveApprovalData.setApprovedOrRejectedBy("Batman");
////        leaveApprovalData.setRemarks("Going to Las Vegas to party");
////        leaveApprovalData.setAppliedOn("10th Jan");
////        leaveApprovalData.setAppliedBy("Contento");
////
////
////        LeaveApprovalData leaveApprovalData2 = new LeaveApprovalData();
////        leaveApprovalData2.setStatus(LeaveApprovalStatus.APPROVED);
////        leaveApprovalData2.setFrom("12th Jan");
////        leaveApprovalData2.setTo("14th Jan");
////        leaveApprovalData2.setLeaveNum("1");
////        leaveApprovalData2.setLeaveType(LeaveType.CASUAL_LEAVE);
////        leaveApprovalData2.setApprovedOrRejectedBy("The Undertaker");
////        leaveApprovalData2.setRemarks("Going to have a good time");
////        leaveApprovalData2.setApprovedOrRejectAt("15th Feb, 2070");
////        leaveApprovalData2.setAppliedOn("10th Jan");
////        leaveApprovalData2.setAppliedBy("Spires");
////
////
////
////
////        LeaveApprovalData leaveApprovalData3 = new LeaveApprovalData();
////        leaveApprovalData3.setStatus(LeaveApprovalStatus.REJECTED);
////        leaveApprovalData3.setFrom("12th Jan");
////        leaveApprovalData3.setTo("14th Jan");
////        leaveApprovalData3.setLeaveNum("1");
////        leaveApprovalData3.setLeaveType(LeaveType.SEEK_LEAVE);
////        leaveApprovalData3.setApprovedOrRejectAt("Stone Cold Steve Austin");
////        leaveApprovalData3.setRemarks("Want to sleep all day");
////        leaveApprovalData3.setAppliedOn("10th Jan");
////        leaveApprovalData3.setAppliedBy("DETA");
////
////
////
////
////        List<LeaveApprovalData> dataList = new ArrayList<>();
////        dataList.add(leaveApprovalData);
////        dataList.add(leaveApprovalData2);
////        dataList.add(leaveApprovalData3);
////
////
////        LeaveApproval leaveApproval = new LeaveApproval();
////        leaveApproval.setLeaveRequests(dataList);
//
//        mFragmentLeaveApprovalBinding.rvApproval.setAdapter(leaveApprovalAdapter);
//        mFragmentLeaveApprovalBinding.rvApproval.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
//
//
//
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
//        int mYear = c.get(Calendar.YEAR);
//        int mMonth = c.get(Calendar.MONTH);
//        int mDay = c.get(Calendar.DAY_OF_MONTH);
        toInMillis = c.getTimeInMillis();
//        btnDateTo.setText(DateTimeUtil.getParsedDate(toInMillis));
//
        Calendar fromCal = (Calendar) c.clone();
        fromCal.add(Calendar.DATE, -30);
//        int yr = fromCal.get(Calendar.YEAR);
//        int mnth = fromCal.get(Calendar.MONTH);
//        int dayofmnth = fromCal.get(Calendar.DAY_OF_MONTH);

        fromInMillis = fromCal.getTimeInMillis();
//        btnDateFrom.setText(DateTimeUtil.getParsedDate(fromInMillis));
//
//        btnDateFrom.setOnClickListener(v -> {
//            CommonUtils.openDatePicker(getBaseActivity(), yr, mnth,
//                    dayofmnth, 0, 0, (vw, year, monthOfYear, dayOfMonth) -> {
////                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
//
//                        fromCal.set(Calendar.YEAR, year);
//                        fromCal.set(Calendar.MONTH, monthOfYear);
//                        fromCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
////                        fromCal.set(Calendar.HOUR_OF_DAY, 0);
////                        fromCal.set(Calendar.MINUTE, 0);
////                        fromCal.set(Calendar.SECOND, 0);
//
//                        Log.e(TAG, "<--DATE is: " + fromCal.toString() +
//                                "<-=-=-=-=-==>" + fromCal.getTime().toString());
//                        fromInMillis = fromCal.getTimeInMillis();
//
//                        if (fromCal.getTimeInMillis()>toInMillis)
//                            TrackiToast.Message.showLong(getBaseActivity(),"Start date cannot be more than end date!");
//
//                        else {
//                            fromInMillis = fromCal.getTimeInMillis();
//                            btnDateFrom.setText(DateTimeUtil.getParsedDate(fromInMillis));
//                        }
//                    });
//        });
//        btnDateTo.setOnClickListener(v -> {
//            CommonUtils.openDatePicker(getBaseActivity(), mYear, mMonth,
//                    mDay, fromInMillis, System.currentTimeMillis(), (vw2, year2, monthOfYear2, dayOfMonth2) -> {
////                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
//
//                        c.set(Calendar.YEAR, year2);
//                        c.set(Calendar.MONTH, monthOfYear2);
//                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth2);
////                        c.set(Calendar.HOUR_OF_DAY, 0);
////                        c.set(Calendar.MINUTE, 0);
////                        c.set(Calendar.SECOND, 0);
//
//                        toInMillis = c.getTimeInMillis();
//                        if (c.getTimeInMillis()<fromInMillis)
//                            TrackiToast.Message.showLong(getBaseActivity(),"End date cannot be less than start date!");
//
//                        else {
//                            toInMillis = c.getTimeInMillis();
//                            btnDateTo.setText(DateTimeUtil.getParsedDate(toInMillis));
//                        }                    });
//        });

    }

    private void setLeaveReqData(long from, long to, LeaveStatus status) {
        leaveApprovalRequest = new LeaveApprovalRequest();
        leaveApprovalRequest.setFrom(from);
        leaveApprovalRequest.setTo(to + 86340000);
        leaveApprovalRequest.setStatus(status);
    }


    public void onClickSearchApproval(long from, long to, LeaveStatus status) {
        setLeaveReqData(from, to, status);
        mLeaveApprovalViewModel.getLeaveApprovalData(httpManager, leaveApprovalRequest);
        showLoading();
    }


    @Override
    public void handleResponse(ApiCallback apiCallback, Object result, APIError error) {
        if (CommonUtils.handleResponse(apiCallback, error, result, getBaseActivity())) {
            Log.i("STATUS: ", "SUCCESSFUL");

            LeaveApproval leaveApproval = new Gson().fromJson(String.valueOf(result), LeaveApproval.class);
            if(leaveApproval.getLeaveRequests()==null||leaveApproval.getLeaveRequests().isEmpty()){
                llHeader.setVisibility(View.GONE);
            }else{
                llHeader.setVisibility(View.VISIBLE);
            }
            leaveApprovalAdapter.setList(leaveApproval.getLeaveRequests(), this);
        }else{
            llHeader.setVisibility(View.GONE);
        }
        hideLoading();
    }

    @Override
    public void handleApproveResponse(ApiCallback apiCallback, Object result, APIError error) {
        if (CommonUtils.handleResponse(apiCallback, error, result, getBaseActivity())) {
            Log.i("STATUS: ", "SUCCESSFUL");
            mLeaveApprovalViewModel.getLeaveApprovalData(httpManager, leaveApprovalRequest);
            TrackiToast.Message.showLong(getBaseActivity(), "Leave approved successfully!");
            setLeaveReqData(fromInMillis, toInMillis, status);
            mLeaveApprovalViewModel.getLeaveApprovalData(httpManager, leaveApprovalRequest);
        }
        hideLoading();

    }


    @Override
    public void handleRejectResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, getBaseActivity())) {
            Log.i("STATUS: ", "SUCCESSFUL");
            mLeaveApprovalViewModel.getLeaveApprovalData(httpManager, leaveApprovalRequest);
            TrackiToast.Message.showLong(getBaseActivity(), "Leave rejected successfully!");
            setLeaveReqData(fromInMillis, toInMillis, status);
            mLeaveApprovalViewModel.getLeaveApprovalData(httpManager, leaveApprovalRequest);
        }

    }
}
