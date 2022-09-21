package com.tracki.ui.attendance.attendance_tab;

import android.graphics.Color;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tracki.data.model.BaseResponse;
import com.tracki.data.model.request.PunchInOut;
import com.tracki.data.model.request.PunchRequest;
import com.tracki.data.model.response.config.AttendanceCountResponse;
import com.tracki.data.model.response.config.LeaveHistoryData;
import com.tracki.ui.custom.GlideApp;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.request.AttendanceReq;
import com.tracki.data.model.response.config.AttendanceResponse;
import com.tracki.data.model.response.config.AttendanceStatus;
import com.tracki.data.model.response.config.AttendanceStatusData;
import com.tracki.data.model.response.config.Data;
import com.tracki.data.model.response.config.LeaveStatus;
import com.tracki.data.model.response.config.ProfileInfo;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.FragmentAttendanceBinding;
import com.tracki.ui.attendance.AllTypeAttendanceAdapter;
import com.tracki.ui.attendance.AttendanceStatusAdapter;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.tasklisting.PaginationListener;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;
import com.tracki.utils.TrackiToast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import static com.tracki.ui.tasklisting.PaginationListener.PAGE_START;

public class AttendanceFragment extends BaseFragment<FragmentAttendanceBinding, AttendanceViewModel>
        implements AttendanceNavigator, View.OnClickListener, AttendanceStatusAdapter.AttendanceSelectedListener {

    private static final String TAG = AttendanceFragment.class.getSimpleName();


    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;
    @Inject
    AllTypeAttendanceAdapter attendanceAdapter;

    @Inject
    AttendanceStatusAdapter attendancesStatusAdapter;

    private RecyclerView rvAttendance;
    private FragmentAttendanceBinding mFragmentAttendanceBinding;
    private AttendanceViewModel mAttendanceViewModel;
    private LeaveStatus status;
    private int numDaysPresent = 0;
    private int totalNumDays = 0;
    private long fromDate;
    private long toDate;
    private TextView tvFromDate;
    private CardView cardFromDate;
    private Button btnSubmit;
    private Button btnPunchIn;
    private Button btnPunchOut;
    private LinearLayout llPunchButton;
    private String userId;
    private ProfileInfo userData;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private boolean subordinate=false;

    public static AttendanceFragment newInstance(ProfileInfo userData, Boolean isShowUpperCad, Boolean subordinate) {
        Bundle args = new Bundle();
        args.putSerializable("userData", userData);
        args.putSerializable("showUpperCard", isShowUpperCad);
        args.putSerializable("subordinate", subordinate);
        AttendanceFragment fragment = new AttendanceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_attendance;
    }

    @Override
    public AttendanceViewModel getViewModel() {
        mAttendanceViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AttendanceViewModel.class);
        return mAttendanceViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAttendanceViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentAttendanceBinding = getViewDataBinding();
        tvFromDate = mFragmentAttendanceBinding.tvFromDate;
        cardFromDate = mFragmentAttendanceBinding.cardFromDate;
        btnSubmit = mFragmentAttendanceBinding.btnSubmit;
        btnPunchIn = mFragmentAttendanceBinding.btnPunchIn;
        btnPunchOut = mFragmentAttendanceBinding.btnPunchOut;
        llPunchButton = mFragmentAttendanceBinding.llPunchButton;
        cardFromDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnPunchIn.setOnClickListener(this);
        btnPunchOut.setOnClickListener(this);
        setRecyclerView();

     setDateRange();


        if (getArguments() != null) {
            Boolean showUpperCard = getArguments().getBoolean("showUpperCard");
             subordinate = getArguments().getBoolean("subordinate");
            if (showUpperCard) {
                mFragmentAttendanceBinding.cardUpper.setVisibility(View.VISIBLE);
            } else {
                mFragmentAttendanceBinding.cardUpper.setVisibility(View.GONE);
            }
            userData = (ProfileInfo) getArguments().getSerializable("userData");
/*
            if (subordinate) {
                if(userData.getStatus()!=null&&userData.getStatus().equals(LeaveStatus.NOT_UPDATED.name())){
                    btnPunchIn.setVisibility(View.VISIBLE);
                    btnPunchOut.setVisibility(View.GONE);
                }else if(userData.getStatus()!=null&&userData.getStatus().equals(LeaveStatus.PRESENT.name()
                )){
                    btnPunchOut.setVisibility(View.VISIBLE);
                    btnPunchIn.setVisibility(View.GONE);
                }
                llPunchButton.setVisibility(View.VISIBLE);
            }
*/
            if (userData != null && userData.getUserId() != null)
                userId = userData.getUserId();
            if (userData.getProfileImg() != null && !userData.getProfileImg().isEmpty()) {
                GlideApp.with(requireContext())
                        .load(userData.getProfileImg())
                        .apply(new RequestOptions().circleCrop()
                                .placeholder(R.drawable.ic_my_profile))
                        .error(R.drawable.ic_my_profile)
                        .into(mFragmentAttendanceBinding.ivUserImage);
            }
            if (userData.getName() != null) {
                mFragmentAttendanceBinding.tvUserName.setText(userData.getName());
            }
            final Calendar to = Calendar.getInstance();
            to.set(Calendar.HOUR_OF_DAY, 23);
            to.set(Calendar.MINUTE, 59);
            to.set(Calendar.SECOND, 0);


            Calendar from = Calendar.getInstance();
            from.set(Calendar.HOUR_OF_DAY, 0);
            from.set(Calendar.MINUTE, 0);
            from.set(Calendar.SECOND, 0);
            AttendanceReq attendanceReq = new AttendanceReq();
            attendanceReq.setFrom(from.getTimeInMillis());
            attendanceReq.setTo(to.getTimeInMillis());//Added 86340000 to set the time of 'to' to end of day
            attendanceReq.setUserId(userId);
            mAttendanceViewModel.todayAttendance(httpManager, attendanceReq);

        }
        getCountMap();


    }

    private void setDateRange() {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 0);

        toDate = c.getTimeInMillis();

        Calendar fromCal = Calendar.getInstance();
        fromCal.set(Calendar.HOUR_OF_DAY, 0);
        fromCal.set(Calendar.MINUTE, 0);
        fromCal.set(Calendar.SECOND, 0);
        fromCal.add(Calendar.DATE, -7);


        fromDate = fromCal.getTimeInMillis();

        //Setting default values

        tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));
    }

    private void setmapCounter() {
        List<AttendanceStatusData> list = new ArrayList<>();

        AttendanceStatusData PRESENT = new AttendanceStatusData();
        PRESENT.setStatus(AppConstants.PRESENT);
        PRESENT.setSelected(true);
        // PRESENT.setCount(numDaysPresent);
        list.add(PRESENT);

        AttendanceStatusData ABSENT = new AttendanceStatusData();
        ABSENT.setStatus(AppConstants.ABSENT);
//        ABSENT.setCount(attendanceResponse.getAttendanceCountMap().getABSENT());
        list.add(ABSENT);

        AttendanceStatusData ON_LEAVE = new AttendanceStatusData();
        ON_LEAVE.setStatus(AppConstants.ON_LEAVE);
//        ON_LEAVE.setCount(attendanceResponse.getAttendanceCountMap().getON_LEAVE());
        list.add(ON_LEAVE);

        AttendanceStatusData DAY_OFF = new AttendanceStatusData();
        DAY_OFF.setStatus(AppConstants.DAY_OFF);
//        DAY_OFF.setCount(attendanceResponse.getAttendanceCountMap().getDAY_OFF());
        list.add(DAY_OFF);


        attendancesStatusAdapter.addItems((ArrayList<AttendanceStatusData>) list);

        attendancesStatusAdapter.setListener(this);
        mFragmentAttendanceBinding.setAttendancesStatusAdapter(attendancesStatusAdapter);
    }

    @Override
    public void setUserVisibleHint(boolean isUserVisible) {
        super.setUserVisibleHint(isUserVisible);
        // when fragment visible to user and view is not null then enter here.
        if (isUserVisible && getRootView() != null && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!getUserVisibleHint()) {
            return;
        }


        //hit api

    }


    @Override
    public void onClickSearch(long from, long to, LeaveStatus status) {

    }

    @Override
    public void handleTodayAttendanceResponse(ApiCallback apiCallback, Object result, APIError apiError) {
        if (result != null) {
            try {
                AttendanceResponse attendanceResponse = new Gson().fromJson(String.valueOf(result), AttendanceResponse.class);
                if (attendanceResponse.getData() != null && !attendanceResponse.getData().isEmpty()) {
                    Data data = attendanceResponse.getData().get(0);
                    if (data != null) {
                        if (CommonUtils.timeLies(data.getDate())) {
                            String status = CommonUtils.getStatusString(data.getStatus());
                            int color = CommonUtils.getAttendanceStatusColor(data.getStatus());
                            mFragmentAttendanceBinding.tvTodayStatus.setText(status + "");
                            mFragmentAttendanceBinding.tvTodayStatus.setTextColor(color);
                            if (data.getHours() != null) {
                                mFragmentAttendanceBinding.tvWorkingHoursValue.setText(data.getHours());
                                if (data.getPunchInAt() > 0 && data.getPunchOutAt() == 0L) {
                                    mAttendanceViewModel.isPunchedInVisible().set(false);
                                    mAttendanceViewModel.isPunchedIn().set(false);
                                    mAttendanceViewModel.getButtonText().set("Punch In");
                                    btnPunchOut.setVisibility(View.VISIBLE);
                                    btnPunchIn.setVisibility(View.GONE);


                                } else if (data.getPunchInAt() > 0 && data.getPunchOutAt() > 0L) {
                                    mAttendanceViewModel.isPunchedIn().set(false);
                                    mAttendanceViewModel.isPunchedInVisible().set(true);
                                    mAttendanceViewModel.getButtonText().set("Punch Out");
                                    btnPunchIn.setVisibility(View.VISIBLE);
                                    btnPunchOut.setVisibility(View.GONE);

                                } else {
                                    mAttendanceViewModel.isPunchedInVisible().set(false);
                                }
                                if (subordinate) {
                                    llPunchButton.setVisibility(View.VISIBLE);
                                }


                            } else {
                                mFragmentAttendanceBinding.tvWorkingHoursValue.setText("N/A");
                            }
                        } else {
                            mAttendanceViewModel.isPunchedInVisible().set(false);
                            mFragmentAttendanceBinding.tvTodayStatus.setText("N/A");
                            mFragmentAttendanceBinding.tvWorkingHoursValue.setText("N/A");
                            mFragmentAttendanceBinding.tvTodayStatus.setTextColor(Color.BLACK);
                            btnPunchIn.setVisibility(View.VISIBLE);
                            btnPunchOut.setVisibility(View.GONE);
                            if (subordinate) {
                                llPunchButton.setVisibility(View.VISIBLE);
                            }
                        }


                    }

                } else {
                    mAttendanceViewModel.isPunchedInVisible().set(false);
                    mFragmentAttendanceBinding.tvTodayStatus.setText("N/A");
                    mFragmentAttendanceBinding.tvWorkingHoursValue.setText("N/A");
                    mFragmentAttendanceBinding.tvTodayStatus.setTextColor(Color.BLACK);
                    btnPunchIn.setVisibility(View.VISIBLE);
                    btnPunchOut.setVisibility(View.GONE);
                    if (subordinate) {
                        llPunchButton.setVisibility(View.VISIBLE);
                    }else{
                        llPunchButton.setVisibility(View.GONE);
                    }
                }
            } catch (com.google.gson.JsonSyntaxException | NullPointerException | IllegalStateException exception) {
                mAttendanceViewModel.isPunchedInVisible().set(false);
                mFragmentAttendanceBinding.tvTodayStatus.setText("N/A");
                mFragmentAttendanceBinding.tvWorkingHoursValue.setText("N/A");
                mFragmentAttendanceBinding.tvTodayStatus.setTextColor(Color.BLACK);

            }
        } else {
            mAttendanceViewModel.isPunchedInVisible().set(false);
            mFragmentAttendanceBinding.tvTodayStatus.setText("N/A");
            mFragmentAttendanceBinding.tvWorkingHoursValue.setText("N/A");
            mFragmentAttendanceBinding.tvTodayStatus.setTextColor(Color.BLACK);

        }
      /*  if(mAttendanceViewModel.isPunchedInVisible().get()){
            btnPunchIn.setVisibility(View.VISIBLE);
            btnPunchOut.setVisibility(View.GONE);
        }else{
            btnPunchOut.setVisibility(View.VISIBLE);
            btnPunchIn.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void handleAttendanceMapResponse(ApiCallback apiCallback, Object result, APIError apiError) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, apiError, result, getBaseActivity())) {
            AttendanceCountResponse attendanceResponse = new Gson().fromJson(String.valueOf(result), AttendanceCountResponse.class);
            if (attendanceResponse != null) {
                totalNumDays = attendanceResponse.getWorkingDays();
                mFragmentAttendanceBinding.tvWorkingDays.setText(totalNumDays + "");
            }
            if (attendanceResponse != null && attendanceResponse.getData() != null) {
                numDaysPresent = attendanceResponse.getData().getPRESENT();
                try {
                    int presentPercentage = (numDaysPresent * 100 / totalNumDays);
                    mFragmentAttendanceBinding.progressBarPresent.setProgress(presentPercentage);
                    mFragmentAttendanceBinding.tvProgressPresent.setText(presentPercentage + "%");
                } catch (ArithmeticException e) {
                    mFragmentAttendanceBinding.progressBarPresent.setProgress(0);
                    mFragmentAttendanceBinding.tvProgressPresent.setText(0 + "%");
                }
                try {
                    int leavePercentage = (attendanceResponse.getData().getON_LEAVE() * 100 / totalNumDays);
                    mFragmentAttendanceBinding.progressBarLeave.setProgress(leavePercentage);
                    mFragmentAttendanceBinding.tvProgressLeave.setText(leavePercentage + "%");
                } catch (ArithmeticException e) {
                    mFragmentAttendanceBinding.progressBarLeave.setProgress(0);
                    mFragmentAttendanceBinding.tvProgressLeave.setText(0 + "%");
                }
                try {
                    int absentPercentage = (attendanceResponse.getData().getABSENT() * 100 / totalNumDays);
                    mFragmentAttendanceBinding.progressBarAbsent.setProgress(absentPercentage);
                    mFragmentAttendanceBinding.tvProgressAbsent.setText(absentPercentage + "%");
                } catch (ArithmeticException e) {
                    mFragmentAttendanceBinding.progressBarAbsent.setProgress(0);
                    mFragmentAttendanceBinding.tvProgressAbsent.setText(0 + "%");

                }


                attendancesStatusAdapter.updateCount(AppConstants.PRESENT, numDaysPresent);
                attendancesStatusAdapter.updateCount(AppConstants.ABSENT, attendanceResponse.getData().getABSENT());
                attendancesStatusAdapter.updateCount(AppConstants.ON_LEAVE, attendanceResponse.getData().getON_LEAVE());
                attendancesStatusAdapter.updateCount(AppConstants.DAY_OFF, attendanceResponse.getData().getDAY_OFF());
            } else {
                mFragmentAttendanceBinding.progressBarPresent.setProgress(0);
                mFragmentAttendanceBinding.tvProgressPresent.setText(0 + "%");
                mFragmentAttendanceBinding.progressBarLeave.setProgress(0);
                mFragmentAttendanceBinding.tvProgressLeave.setText(0 + "%");
                mFragmentAttendanceBinding.progressBarAbsent.setProgress(0);
                mFragmentAttendanceBinding.tvProgressAbsent.setText(0 + "%");
                attendancesStatusAdapter.updateCount(AppConstants.PRESENT, 0);
                attendancesStatusAdapter.updateCount(AppConstants.ABSENT, 0);
                attendancesStatusAdapter.updateCount(AppConstants.ON_LEAVE, 0);
                attendancesStatusAdapter.updateCount(AppConstants.DAY_OFF, 0);
            }


            numDaysPresent = 0;
            totalNumDays = 0;
            if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {

                status = CommonUtils.getLeaveStatusConstant(AppConstants.PRESENT);
//                AttendanceReq attendanceReq = new AttendanceReq();
//                attendanceReq.setFrom(fromDate);
//                attendanceReq.setTo(toDate);//Added 86340000 to set the time of 'to' to end of day
//                attendanceReq.setStatus(status);
//                attendanceReq.setUserId(userId);
//                mAttendanceViewModel.getAttendance(httpManager, attendanceReq);
//                showLoading();
                isLastPage = false;
                currentPage = PAGE_START;
                getAttendance();
            }


        } else {
            mAttendanceViewModel.isPunchedInVisible().set(false);
            mFragmentAttendanceBinding.tvTodayStatus.setText("N/A");
            mFragmentAttendanceBinding.tvWorkingHoursValue.setText("N/A");
            mFragmentAttendanceBinding.tvTodayStatus.setTextColor(Color.BLACK);

            mFragmentAttendanceBinding.progressBarPresent.setProgress(0);
            mFragmentAttendanceBinding.tvProgressPresent.setText(0 + "%");
            mFragmentAttendanceBinding.progressBarLeave.setProgress(0);
            mFragmentAttendanceBinding.tvProgressLeave.setText(0 + "%");
            mFragmentAttendanceBinding.progressBarAbsent.setProgress(0);
            mFragmentAttendanceBinding.tvProgressAbsent.setText(0 + "%");
            attendancesStatusAdapter.updateCount(AppConstants.PRESENT, 0);
            attendancesStatusAdapter.updateCount(AppConstants.ABSENT, 0);
            attendancesStatusAdapter.updateCount(AppConstants.ON_LEAVE, 0);
            attendancesStatusAdapter.updateCount(AppConstants.DAY_OFF, 0);
            attendanceAdapter.setAttendance(new ArrayList<>());
        }
    }

    @Override
    public void handlePunchInOutResponse(@NonNull ApiCallback apiCallback, @Nullable Object result, @Nullable APIError error, PunchInOut event) {
      hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, getContext())) {
            BaseResponse baseResponse = new Gson().fromJson(String.valueOf(result), BaseResponse.class);

            if(baseResponse.getSuccessful()){
               llPunchButton.setVisibility(View.GONE);
                getTodayAttendance();
                setDateRange();
                getCountMap();
             /*   if(event==PunchInOut.PUNCH_IN){
                    AttendanceStatusData PRESENT = new AttendanceStatusData();
                    PRESENT.setStatus(AppConstants.PRESENT);
                    PRESENT.setSelected(true);
                    int count=attendancesStatusAdapter.getCountOfStatus(AppConstants.PRESENT);
                    attendancesStatusAdapter.updateCount(AppConstants.PRESENT, count+1);
                    onItemSelected(PRESENT);
                }else if(event==PunchInOut.PUNCH_OUT){
                    AttendanceStatusData PRESENT = new AttendanceStatusData();
                    PRESENT.setStatus(AppConstants.PRESENT);
                    PRESENT.setSelected(true);
                    onItemSelected(PRESENT);
                }*/

            }
        }
    }

    private void getTodayAttendance() {
        showLoading();
        final Calendar to = Calendar.getInstance();
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 0);


        Calendar from = Calendar.getInstance();
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        AttendanceReq attendanceReq = new AttendanceReq();
        attendanceReq.setFrom(from.getTimeInMillis());
        attendanceReq.setTo(to.getTimeInMillis());//Added 86340000 to set the time of 'to' to end of day
        attendanceReq.setUserId(userId);
        mAttendanceViewModel.todayAttendance(httpManager, attendanceReq);

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
    public void handleAttendanceResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        isLoading = false;
        mFragmentAttendanceBinding.contentProgressBar.setVisibility(View.INVISIBLE);
        if (CommonUtils.handleResponse(apiCallback, error, result, getBaseActivity())) {
            Log.i("STATUS: ", "SUCCESSFUL");

            AttendanceResponse attendanceResponse = new Gson().fromJson(String.valueOf(result), AttendanceResponse.class);
            if (attendanceResponse != null) {
                setRecyclerView();

                switch (status) {
                    case PRESENT:
                        if (attendanceResponse.getPresent() != null) {
                            attendanceAdapter.setAttendance(attendanceResponse.getPresent());
                        }
                        break;
                    case ON_LEAVE:
                        if (attendanceResponse.getLeave() != null) {
                            List<Data> list = new ArrayList<>();
                            for (LeaveHistoryData data : attendanceResponse.getLeave()) {
                                Data lData = new Data();
                                lData.setDate(data.getAppliedOn());
                                lData.setStatus(AttendanceStatus.ON_LEAVE);
                                lData.setFrom(data.getFrom());
                                lData.setTo(data.getTo());
                                lData.setLeaveType(data.getLeaveType());
                                lData.setRemarks(data.getRemarks());
                                lData.setDayKeys(data.getDayKeys());
                                list.add(lData);

                            }
                            attendanceAdapter.setAttendance(list);
                        }
                        break;
                    case DAY_OFF:
                        if (attendanceResponse.getDayOff() != null) {
                            attendanceAdapter.setAttendance(attendanceResponse.getDayOff());
                        }
                        break;
                    case ABSENT:
                        if (attendanceResponse.getAbsent() != null) {
                            attendanceAdapter.setAttendance(attendanceResponse.getAbsent());
                        }
                        break;
                }
                CommonUtils.showLogMessage("e", "adapter total_count =>",
                        "" + attendanceAdapter.getItemCount());
                CommonUtils.showLogMessage("e", "fetch total_count =>",
                        "" + attendanceResponse.getDataCount());
                if (attendanceAdapter.getItemCount() >= attendanceResponse.getDataCount()) {
                    isLastPage = true;
                } else {
                    isLastPage = false;
                }
            }


        } else {
            setRecyclerView();
            attendanceAdapter.setAttendance(new ArrayList<>());
        }
    }

    private void setRecyclerView() {
        if (rvAttendance == null) {
            rvAttendance = mFragmentAttendanceBinding.rvAttendance;
            //  mLayoutManager= (LinearLayoutManager) rvAttendance.getLayoutManager();
            try {
                mLayoutManager = new LinearLayoutManager(getBaseActivity());
                mLayoutManager.setOrientation(RecyclerView.VERTICAL);
                rvAttendance.setLayoutManager(mLayoutManager);
                rvAttendance.setItemAnimator(new DefaultItemAnimator());
            } catch (IllegalArgumentException e) {

            }
        }
        rvAttendance.setAdapter(attendanceAdapter);
        rvAttendance.addOnScrollListener(new PaginationListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                getAttendance();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public void handleResponse(@NotNull ApiCallback apiCallback, Object result, APIError error) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cardFromDate) {
            openDatePicker();
        } else if (id == R.id.btnPunchIn) {
            perFormPunchInOut(PunchInOut.PUNCH_IN);
        } else if (id == R.id.btnPunchOut) {
            perFormPunchInOut(PunchInOut.PUNCH_OUT);
        } else if (id == R.id.btnSubmit) {
            getCountMap();
        }

    }

    private void perFormPunchInOut(PunchInOut event) {
        Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        //open time picker
        CommonUtils.openTimePicker(getBaseActivity(), mHour, mMinute,
                (view, hourOfDay, minute) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    cal.set(Calendar.SECOND, 0);

                    PunchRequest punchRequest = new PunchRequest();
                    punchRequest.setDate(cal.getTimeInMillis());
                    punchRequest.setEvent(event);
                    punchRequest.setUserId(userId);
                    showLoading();
                    mAttendanceViewModel.punch(httpManager, punchRequest, event);

                });
    }

    private void getCountMap() {
        if (fromDate > toDate) {
            TrackiToast.Message.showLong(requireContext(), "End date cannot be less than start date");
        } else {
            setmapCounter();
            attendanceAdapter.clearData();
            AttendanceReq attendanceReq = new AttendanceReq();
            attendanceReq.setFrom(fromDate);
            attendanceReq.setTo(toDate);//Added 86340000 to set the time of 'to' to end of day
            attendanceReq.setUserId(userId);
            mAttendanceViewModel.getAttendanceCount(httpManager, attendanceReq);
            showLoading();

        }
    }

    private void getAttendance() {
        if (fromDate > toDate) {
            TrackiToast.Message.showLong(requireContext(), "End date cannot be less than start date");
        } else {
            AttendanceReq attendanceReq = new AttendanceReq();
            attendanceReq.setFrom(fromDate);
            attendanceReq.setTo(toDate);//Added 86340000 to set the time of 'to' to end of day
            attendanceReq.setStatus(status);
            attendanceReq.setUserId(userId);
            attendanceReq.setLimit(10);
            attendanceReq.setOffset((currentPage - 1) * 10);
            mFragmentAttendanceBinding.contentProgressBar.setVisibility(View.VISIBLE);
            mAttendanceViewModel.getAttendance(httpManager, attendanceReq);

        }
    }

    @Override
    public void onItemSelected(@NotNull AttendanceStatusData status) {
        this.status = CommonUtils.getLeaveStatusConstant(status.getStatus());
        isLastPage = false;
        currentPage = PAGE_START;
        attendanceAdapter.clearData();
        //  setRecyclerView();
        getAttendance();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if(mLayoutManager!=null)
//        mLayoutManager=null;
    }
}
