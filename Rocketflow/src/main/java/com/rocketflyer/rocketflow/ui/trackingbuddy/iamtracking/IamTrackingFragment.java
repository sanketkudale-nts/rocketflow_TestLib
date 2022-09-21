package com.rocketflyer.rocketflow.ui.trackingbuddy.iamtracking;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.model.request.BuddiesRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.model.response.config.BuddyListResponse;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.FragmentIAmTrackingBinding;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.trackingbuddy.buddydetail.TrackingBuddyDetailActivity;
import com.tracki.ui.trackingbuddy.iamtracking.IamTrackingNavigator;
import com.tracki.ui.trackingbuddy.iamtracking.IamTrackingViewModel;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.BuddyInfo;
import com.tracki.utils.BuddyStatus;
import com.tracki.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by rahul on 5/10/18
 */
public class IamTrackingFragment extends BaseFragment<FragmentIAmTrackingBinding, IamTrackingViewModel>
        implements IamTrackingNavigator, IamTrackingAdapter.IamTrackingAdapterListener {

    @Inject
    IamTrackingAdapter mIamTrackingAdapter;
    FragmentIAmTrackingBinding mFragmentIamTrackingBinding;
    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;


    private IamTrackingViewModel mIamTrackingViewModel;
    private Api api;
    private BuddiesRequest buddyRequest;

    public static IamTrackingFragment newInstance() {
        Bundle args = new Bundle();
        IamTrackingFragment fragment = new IamTrackingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_i_am_tracking;
    }

    @Override
    public IamTrackingViewModel getViewModel() {
//        mIamTrackingViewModel = new ViewModelProvider(this).get(IamTrackingViewModel.class);
        mIamTrackingViewModel = ViewModelProviders.of(this, mViewModelFactory).get(IamTrackingViewModel.class);
        return mIamTrackingViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIamTrackingViewModel.setNavigator(this);
        mIamTrackingAdapter.setListener(this);

        api = TrackiApplication.getApiMap().get(ApiType.BUDDIES);
        List<BuddyStatus> statusList = new ArrayList<>();
        statusList.add(BuddyStatus.IDLE);
        statusList.add(BuddyStatus.OFFLINE);
        statusList.add(BuddyStatus.ON_TRIP);
        buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentIamTrackingBinding = getViewDataBinding();
        setRecyclerView();
        subscribeToLiveData();
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
//        showLoading();
        //fetch hashMap of buddies and pass true to show check button.
        mIamTrackingViewModel.fetchBuddyList(buddyRequest, httpManager, api);
    }

    private void subscribeToLiveData() {
        mIamTrackingViewModel.getBuddyListLiveData().observe(this, lists -> {
            if (lists != null) {
                mIamTrackingViewModel.addItemsToList(lists);
                mIamTrackingAdapter.addItems(lists);
            }
        });
    }

    @Override
    public void onAdapterItemClick(Buddy trackingBean) {
        startActivity(TrackingBuddyDetailActivity.newIntent(getBaseActivity())
                .putExtra(AppConstants.Extra.EXTRA_BUDDY, trackingBean));
    }

    @Override
    public void handleResponse(@Nullable ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
//        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, getBaseActivity())) {
            BuddyListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result), BuddyListResponse.class);
            mIamTrackingViewModel.getBuddyListLiveData().setValue(buddyListResponse.getBuddies());
        }
    }

    private void setRecyclerView() {
        RecyclerView amTrackingRecyclerView = mFragmentIamTrackingBinding.amTrackingRecyclerView;
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        amTrackingRecyclerView.setLayoutManager(mLayoutManager);
        amTrackingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        amTrackingRecyclerView.setAdapter(mIamTrackingAdapter);
        mIamTrackingAdapter.addItems(mIamTrackingViewModel.getBuddyObservableArrayList());
        mIamTrackingAdapter.notifyDataSetChanged();
    }
}
