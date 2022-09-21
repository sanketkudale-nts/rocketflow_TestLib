package com.tracki.ui.leave.leave_summary;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.LeaveSummaryResponse;
import com.tracki.data.model.response.config.Summary;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.FragmentLeaveSummaryBinding;
import com.tracki.ui.base.BaseFragment;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class LeaveSummaryFragment extends BaseFragment<FragmentLeaveSummaryBinding, LeaveSummaryViewModel>
        implements LeaveSummaryNavigator {

    public static final String TAG = LeaveSummaryFragment.class.getSimpleName();

    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;
    @Inject
    LeaveSummaryAdapter leaveSummaryAdapter;


    private FragmentLeaveSummaryBinding mFragmentLeaveSummaryBinding;
    private LeaveSummaryViewModel mLeaveSummaryViewModel;

    public static LeaveSummaryFragment newInstance() {
        Bundle args = new Bundle();
        LeaveSummaryFragment fragment = new LeaveSummaryFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_leave_summary;
    }

    @Override
    public LeaveSummaryViewModel getViewModel() {
//        mLeaveSummaryViewModel = new ViewModelProvider(this).get(LeaveSummaryViewModel.class);
        mLeaveSummaryViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LeaveSummaryViewModel.class);
        return mLeaveSummaryViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLeaveSummaryViewModel.setNavigator(this);
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
        showLoading();
        hideKeyboard();

    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        ((LeaveActivity) getBaseActivity()).setFilterVisibility(View.GONE);
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentLeaveSummaryBinding = getViewDataBinding();


//        Balance balance = new Balance();
//        balance.setCount(10);
//        balance.setType(LeaveType.PRIVILEGE_LEAVE);
//
//        Balance balance2 = new Balance();
//        balance2.setCount(11);
//        balance2.setType(LeaveType.SICK_LEAVE);
//
//        Balance balance3 = new Balance();
//        balance3.setCount(15);
//        balance3.setType(LeaveType.CASUAL_LEAVE);
//
//        int totalLeaves = balance.getCount() + balance2.getCount() + balance3.getCount();
//
//        mFragmentLeaveSummaryBinding.tvLeave1.setText(CommonUtils.getLeaveTypeString(balance.getType()));
//        mFragmentLeaveSummaryBinding.tvLeave2.setText(CommonUtils.getLeaveTypeString(balance2.getType()));
//        mFragmentLeaveSummaryBinding.tvLeave3.setText(CommonUtils.getLeaveTypeString(balance3.getType()));
//
//
//        mFragmentLeaveSummaryBinding.tvLeave1Count.setText(String.valueOf(balance.getCount()));
//        mFragmentLeaveSummaryBinding.tvLeave2Count.setText(String.valueOf(balance2.getCount()));
//        mFragmentLeaveSummaryBinding.tvLeave3Count.setText(String.valueOf(balance3.getCount()));
//        mFragmentLeaveSummaryBinding.tvTotalLeavesCount.setText(String.valueOf(totalLeaves));


    }


    public void fetchLeaves() {
        mLeaveSummaryViewModel.fetchLeaves(httpManager);
    }


    @Override
    public void handleResponse(@NotNull ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, getContext())) {
            Log.i("STATUS", result + "");

            LeaveSummaryResponse leaveSummaryResponse = new Gson().fromJson(String.valueOf(result), LeaveSummaryResponse.class);


            List<Summary> summaryList;

            if (leaveSummaryResponse.getSummary() != null && leaveSummaryResponse.getSummary().getSummary() != null) {
                summaryList = leaveSummaryResponse.getSummary().getSummary();

                // mFragmentLeaveSummaryBinding.tvLeave1.setText(leave[0]);
                if (summaryList != null && !summaryList.isEmpty()) {

                    if(summaryList.size()==1){
                        mFragmentLeaveSummaryBinding.llCasualAccount.setVisibility(View.VISIBLE);
                        mFragmentLeaveSummaryBinding.llSlickAccount.setVisibility(View.GONE);
                        if (summaryList.get(0).getType() != null){
                            mFragmentLeaveSummaryBinding.tvLeave2.setText(CommonUtils.getLeaveTypeString(summaryList.get(0).getType()));
                            mFragmentLeaveSummaryBinding.tvLeave2Count.setText(String.valueOf(summaryList.get(0).getRemaing() + "/" +
                                    summaryList.get(0).getAllowed()));
                        }
                        mFragmentLeaveSummaryBinding.tvTotalLeavesCount.setText(String.valueOf(summaryList.get(0).getRemaing()  + "/" + (summaryList.get(0).getAllowed() )));

                    }
                    else if(summaryList.size()==2){
                        mFragmentLeaveSummaryBinding.llCasualAccount.setVisibility(View.VISIBLE);
                        if (summaryList.get(0).getType() != null){
                            mFragmentLeaveSummaryBinding.tvLeave2.setText(CommonUtils.getLeaveTypeString(summaryList.get(0).getType()));
                            mFragmentLeaveSummaryBinding.tvLeave2Count.setText(String.valueOf(summaryList.get(0).getRemaing() + "/" +
                                    summaryList.get(0).getAllowed()));
                        }
                        mFragmentLeaveSummaryBinding.llSlickAccount.setVisibility(View.VISIBLE);
                        if (summaryList.get(1).getType() != null)
                            mFragmentLeaveSummaryBinding.tvLeave3.setText(CommonUtils.getLeaveTypeString(summaryList.get(1).getType()));
                        mFragmentLeaveSummaryBinding.tvLeave3Count.setText(String.valueOf(summaryList.get(1).getRemaing() + "/" +
                                summaryList.get(1).getAllowed()));
                        mFragmentLeaveSummaryBinding.tvTotalLeavesCount.setText(String.valueOf(summaryList.get(0).getRemaing() +
                                summaryList.get(1).getRemaing() + "/" + (summaryList.get(0).getAllowed() + summaryList.get(1).getAllowed())));
                    }


                }
                else {
                    mFragmentLeaveSummaryBinding.tvTitle.setVisibility(View.GONE);
                    mFragmentLeaveSummaryBinding.cardViewTaskDetail.setVisibility(View.GONE);
                    mFragmentLeaveSummaryBinding.emptyMsg.setVisibility(View.VISIBLE);


                }


            } else {
                mFragmentLeaveSummaryBinding.tvTitle.setVisibility(View.GONE);
                mFragmentLeaveSummaryBinding.cardViewTaskDetail.setVisibility(View.GONE);
                mFragmentLeaveSummaryBinding.emptyMsg.setVisibility(View.VISIBLE);


            }

        }


    }
}

