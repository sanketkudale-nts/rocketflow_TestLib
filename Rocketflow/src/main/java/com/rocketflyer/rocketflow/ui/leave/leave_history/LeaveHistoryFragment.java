package com.rocketflyer.rocketflow.ui.leave.leave_history;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.request.AttendanceReq;
import com.tracki.data.model.request.CancelLeaveRequest;
import com.tracki.data.model.request.LeaveHistoryRequest;
import com.tracki.data.model.response.config.AttendanceStatusData;
import com.tracki.data.model.response.config.LeaveHistory;
import com.tracki.data.model.response.config.LeaveHistoryData;
import com.tracki.data.model.response.config.LeaveStatus;
import com.tracki.data.model.response.config.LeaveSummaryResponse;
import com.tracki.data.model.response.config.ProfileInfo;
import com.tracki.data.model.response.config.Summary;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.FragmentLeaveHistoryBinding;
import com.tracki.ui.attendance.AttendanceStatusAdapter;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.custom.GlideApp;
import com.tracki.ui.leave.LeaveActivity;
import com.tracki.ui.leave.leave_history.LeaveHistoryAdapter;
import com.tracki.ui.leave.leave_history.LeaveHistoryNavigator;
import com.tracki.ui.leave.leave_history.LeaveHistoryViewModel;
import com.tracki.ui.leave.leave_history.OnViewClick;
import com.tracki.ui.leavedetails.LeaveDetailsData;
import com.tracki.ui.leavedetails.UsersLeaveDetailsActivity;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class LeaveHistoryFragment extends BaseFragment<FragmentLeaveHistoryBinding, LeaveHistoryViewModel>
        implements LeaveHistoryNavigator, OnViewClick, AttendanceStatusAdapter.AttendanceSelectedListener, View.OnClickListener {

    public static final String TAG = LeaveHistoryFragment.class.getSimpleName();

    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;
    @Inject
    LeaveHistoryAdapter leaveHistoryAdapter;

    @Inject
    AttendanceStatusAdapter attendancesStatusAdapter;

    private LeaveHistoryViewModel mLeaveHistoryViewModel;
    private LeaveHistoryRequest leaveHistoryRequest;
    private LinearLayout llHeader;
    private LeaveDetailsData detailsData;
    private long fromDate;
    private long toDate;
    private TextView tvFromDate;
    private CardView cardFromDate;
    private Button btnSubmit;
    private LeaveStatus status;
    private ProfileInfo userData;
    private String userId;

    public static LeaveHistoryFragment newInstance(ProfileInfo userData) {
        Bundle args = new Bundle();
        args.putSerializable("userData", userData);
        LeaveHistoryFragment fragment = new LeaveHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onEditClick(@NotNull LeaveHistoryData leaveHistoryData) {
        ((LeaveActivity) getBaseActivity()).leaveHistoryData = leaveHistoryData;
        ((LeaveActivity) getBaseActivity()).isEdit = true;
        ((LeaveActivity) getBaseActivity()).viewPager.setCurrentItem(0, true);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_leave_history;
    }

    private TextView tvTotalLeaveCount;
    private TextView tvRemainingLeaveCount;
    private TextView tvTakenLeaveCount;
    private CardView cvSummary;

    @Override
    public LeaveHistoryViewModel getViewModel() {
//        mLeaveHistoryViewModel = new ViewModelProvider(this).get(LeaveHistoryViewModel.class);
        mLeaveHistoryViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LeaveHistoryViewModel.class);
        return mLeaveHistoryViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLeaveHistoryViewModel.setNavigator(this);
        if (getArguments() != null) {
            userData = (ProfileInfo) getArguments().getSerializable("userData");
            if (userData != null && userData.getUserId() != null)
                userId = userData.getUserId();
        }
    }

    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        if (fromDate != 0) {
            c.setTimeInMillis(fromDate);
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        long minTime = 0L;

        if (getBaseActivity() != null) {
            CommonUtils.openDatePicker(getBaseActivity(), mYear, mMonth,
                    mDay, minTime, 0, (view, year, monthOfYear, dayOfMonth) -> {


                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        CommonUtils.openDatePicker(getBaseActivity(), mYear, mMonth,
                                mDay, calendar.getTimeInMillis(), 0, (view_, yearEnd, monthOfYearEnd, dayOfMonthEnd) -> {

                                    fromDate = calendar.getTimeInMillis();
                                    Calendar calEnd = Calendar.getInstance();
                                    calEnd.set(Calendar.YEAR, yearEnd);
                                    calEnd.set(Calendar.MONTH, monthOfYearEnd);
                                    calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
                                    calEnd.set(Calendar.HOUR_OF_DAY, 23);
                                    calEnd.set(Calendar.MINUTE, 59);
                                    calEnd.set(Calendar.SECOND, 0);
                                    toDate = calEnd.getTimeInMillis();
                                    tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));


                                });

                    });
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentLeaveHistoryBinding mFragmentLeaveHistoryBinding = getViewDataBinding();




        RecyclerView rvLeaveHistory = mFragmentLeaveHistoryBinding.rvLeaveHistory;
        tvTotalLeaveCount = mFragmentLeaveHistoryBinding.tvTotalLeavesCount;
        tvRemainingLeaveCount = mFragmentLeaveHistoryBinding.tvRemainIngCount;
        tvTakenLeaveCount = mFragmentLeaveHistoryBinding.tvTakenLeavesCount;
        cvSummary = mFragmentLeaveHistoryBinding.cvSummary;
        llHeader = mFragmentLeaveHistoryBinding.llHeader;
        rvLeaveHistory.setAdapter(leaveHistoryAdapter);
        tvFromDate = mFragmentLeaveHistoryBinding.tvFromDate;
        cardFromDate = mFragmentLeaveHistoryBinding.cardFromDate;
        btnSubmit = mFragmentLeaveHistoryBinding.btnSubmit;
        cardFromDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        status = CommonUtils.getLeaveStatusConstant(AppConstants.APPLIED);
        rvLeaveHistory.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 0);

        toDate = c.getTimeInMillis();

        Calendar fromCal = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        fromCal.add(Calendar.DATE, -30);


        fromDate = fromCal.getTimeInMillis();
        tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));

        cvSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailsData != null) {
                    Intent intent = new Intent(getBaseActivity(), UsersLeaveDetailsActivity.class);
                    intent.putExtra("details", detailsData);
                    startActivity(intent);
                }
            }
        });

        List<AttendanceStatusData> list = new ArrayList<>();

        AttendanceStatusData PRESENT = new AttendanceStatusData();
        PRESENT.setStatus(AppConstants.APPLIED);
        PRESENT.setSelected(true);
        // PRESENT.setCount(numDaysPresent);
        list.add(PRESENT);

        AttendanceStatusData ABSENT = new AttendanceStatusData();
        ABSENT.setStatus(AppConstants.APPROVED);
//        ABSENT.setCount(attendanceResponse.getAttendanceCountMap().getABSENT());
        list.add(ABSENT);

        AttendanceStatusData ON_LEAVE = new AttendanceStatusData();
        ON_LEAVE.setStatus(AppConstants.REJECTED);
//        ON_LEAVE.setCount(attendanceResponse.getAttendanceCountMap().getON_LEAVE());
        list.add(ON_LEAVE);

        AttendanceStatusData DAY_OFF = new AttendanceStatusData();
        DAY_OFF.setStatus(AppConstants.CANCELLED);
//        DAY_OFF.setCount(attendanceResponse.getAttendanceCountMap().getDAY_OFF());
        list.add(DAY_OFF);


        attendancesStatusAdapter.addItems((ArrayList<AttendanceStatusData>) list);
        attendancesStatusAdapter.setListener(this);
        mFragmentLeaveHistoryBinding.setAttendancesStatusAdapter(attendancesStatusAdapter);
    }


    private void getRequest(long from, long to, LeaveStatus status) {
        leaveHistoryRequest = new LeaveHistoryRequest();
        leaveHistoryRequest.setFrom(from);
        //leaveHistoryRequest.setTo(to + 86340000);
        leaveHistoryRequest.setTo(to);
        leaveHistoryRequest.setUserId(userId);
        leaveHistoryRequest.setStatus(status);
    }

    public void fetchLeaves() {
        detailsData = null;
        mLeaveHistoryViewModel.fetchLeaves(httpManager,userId);
    }

    public void onClickSearchHistory(long from, long to, LeaveStatus status) {
        getRequest(from, to, status);
        showLoading();
        mLeaveHistoryViewModel.getLeaveHistory(httpManager, leaveHistoryRequest);

    }

    @Override
    public void onLeaveCancelClick(@NotNull String lrId, @NotNull String remarks) {
        long cancelTime = System.currentTimeMillis();
        CancelLeaveRequest cancelLeaveRequest = new CancelLeaveRequest();
        cancelLeaveRequest.setLrId(lrId);
        cancelLeaveRequest.setRemarks(remarks);
        cancelLeaveRequest.setUpdatedAt(cancelTime);


        mLeaveHistoryViewModel.cancelLeave(httpManager, cancelLeaveRequest);
        showLoading();

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
        fetchLeaves();
//        getRequest(fromInMillis, toInMillis + 86340000, null);
//        showLoading();
//        mLeaveHistoryViewModel.getLeaveHistory(httpManager, leaveHistoryRequest);
//        hideKeyboard();
        getLeaveHistory();


    }


    @Override
    public void handleResponse(@NotNull ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, getBaseActivity())) {
            Log.i("STATUS: ", "SUCCESSFUL");

            LeaveHistory leaveHistory = new Gson().fromJson(String.valueOf(result), LeaveHistory.class);
            if (leaveHistory.getHistory() == null || leaveHistory.getHistory().isEmpty()) {
                llHeader.setVisibility(View.GONE);
            } else {
                llHeader.setVisibility(View.VISIBLE);
            }

            List<LeaveHistoryData> leaveHistoryDataList = leaveHistory.getHistory();
            leaveHistoryAdapter.setList(leaveHistoryDataList, this);
            if (leaveHistory.getLeaveMap() != null) {
                attendancesStatusAdapter.updateCount(AppConstants.APPLIED, leaveHistory.getLeaveMap().getPENDING());
                attendancesStatusAdapter.updateCount(AppConstants.APPROVED, leaveHistory.getLeaveMap().getAPPROVED());
                attendancesStatusAdapter.updateCount(AppConstants.REJECTED, leaveHistory.getLeaveMap().getREJECTED());
                attendancesStatusAdapter.updateCount(AppConstants.CANCELLED, leaveHistory.getLeaveMap().getCANCELLED());
            } else {
                attendancesStatusAdapter.updateCount(AppConstants.APPLIED, 0);
                attendancesStatusAdapter.updateCount(AppConstants.APPROVED, 0);
                attendancesStatusAdapter.updateCount(AppConstants.REJECTED, 0);
                attendancesStatusAdapter.updateCount(AppConstants.CANCELLED, 0);
            }

        } else {
            llHeader.setVisibility(View.GONE);
            attendancesStatusAdapter.updateCount(AppConstants.APPLIED, 0);
            attendancesStatusAdapter.updateCount(AppConstants.APPROVED, 0);
            attendancesStatusAdapter.updateCount(AppConstants.REJECTED, 0);
            attendancesStatusAdapter.updateCount(AppConstants.CANCELLED, 0);
            leaveHistoryAdapter.clearList(new ArrayList<>());
        }

    }

    @Override
    public void handleCancelResponse(ApiCallback apiCallback, Object result, APIError error) {
        if (CommonUtils.handleResponse(apiCallback, error, result, getBaseActivity())) {
            Log.i("STATUS: ", "SUCCESSFUL");
            showLoading();
            mLeaveHistoryViewModel.getLeaveHistory(httpManager, leaveHistoryRequest);
        }
    }

    @Override
    public void handleLeaveSummaryResponse(@Nullable ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        int remainingLeave = 0;
        int takenLeave = 0;
        int totalLeave = 0;
        detailsData = null;
        if (CommonUtils.handleResponse(callback, error, result, getContext())) {
            Log.i("STATUS", result + "");

            LeaveSummaryResponse leaveSummaryResponse = new Gson().fromJson(String.valueOf(result), LeaveSummaryResponse.class);
            if (leaveSummaryResponse != null && leaveSummaryResponse.getSuccessful() && leaveSummaryResponse.getSummary() != null
                    && leaveSummaryResponse.getSummary().getSummary() != null && !leaveSummaryResponse.getSummary().getSummary().isEmpty()) {
                detailsData = new LeaveDetailsData();
                detailsData.setSummaryList(leaveSummaryResponse.getSummary().getSummary());
                for (Summary s : leaveSummaryResponse.getSummary().getSummary()) {
                    remainingLeave = remainingLeave + s.getRemaing();
                    takenLeave = takenLeave + s.getTaken();
                    totalLeave = totalLeave + s.getAllowed();
                }
                detailsData.setRemainingLeave(remainingLeave);
                detailsData.setTakenLeave(takenLeave);
                detailsData.setTotalLeave(totalLeave);

            }
        }
        tvRemainingLeaveCount.setText(remainingLeave + "");
        tvTakenLeaveCount.setText(takenLeave + "");
        tvTotalLeaveCount.setText(totalLeave + "");
    }

    @Override
    public void onItemSelected(@NotNull AttendanceStatusData status) {
        this.status = CommonUtils.getLeaveStatusConstant(status.getStatus());
        getLeaveHistory();


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cardFromDate) {
            openDatePicker();
        } else if (id == R.id.btnSubmit) {
            getLeaveHistory();
        }

    }

    private void getLeaveHistory() {
        getRequest(fromDate, toDate, status);
        showLoading();
        mLeaveHistoryViewModel.getLeaveHistory(httpManager, leaveHistoryRequest);
        hideKeyboard();
    }
}
