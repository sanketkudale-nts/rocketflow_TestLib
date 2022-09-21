package com.rocketflyer.rocketflow.ui.trackingbuddy.trackingme;

import android.content.Intent;
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
import com.tracki.databinding.FragmentTrackingMeBinding;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.trackingbuddy.buddydetail.TrackingBuddyDetailActivity;
import com.tracki.ui.trackingbuddy.trackingme.TrackingMeNavigator;
import com.tracki.ui.trackingbuddy.trackingme.TrackingMeViewModel;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.BuddyInfo;
import com.tracki.utils.BuddyStatus;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

/**
 * Created by rahul on 5/10/18
 */
public class TrackingMeFragment extends BaseFragment<FragmentTrackingMeBinding, TrackingMeViewModel>
        implements TrackingMeNavigator, TrackingMeAdapter.TrackMeAdapterListener {

    private static final String TAG = "TrackingMeFragment";

    @Inject
    TrackingMeAdapter mTrackingMeAdapter;
    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;


    private Api api;
    private FragmentTrackingMeBinding mFragmentTrackingMeBinding;
    private TrackingMeViewModel mTrackingMeViewModel;
    private BuddiesRequest buddyRequest;

    public static TrackingMeFragment newInstance() {
        Bundle args = new Bundle();
        TrackingMeFragment fragment = new TrackingMeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tracking_me;
    }

    @Override
    public TrackingMeViewModel getViewModel() {
//        mTrackingMeViewModel = new ViewModelProvider(this).get(TrackingMeViewModel.class);
        mTrackingMeViewModel = ViewModelProviders.of(this, mViewModelFactory).get(TrackingMeViewModel.class);
        return mTrackingMeViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTrackingMeViewModel.setNavigator(this);
        mTrackingMeAdapter.setListener(this);

        api = TrackiApplication.getApiMap().get(ApiType.BUDDIES);
        List<BuddyStatus> statusList = new ArrayList<>();
        statusList.add(BuddyStatus.IDLE);
        statusList.add(BuddyStatus.OFFLINE);
        statusList.add(BuddyStatus.ON_TRIP);
        buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKING_ME, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentTrackingMeBinding = getViewDataBinding();
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
        mTrackingMeViewModel.fetchBuddyList(buddyRequest, httpManager, api);
    }

    private void subscribeToLiveData() {
        mTrackingMeViewModel.getBuddyListLiveData().observe(this, lists -> {
            if (lists != null) {
                mTrackingMeViewModel.addItemsToList(lists);
                mTrackingMeAdapter.addItems(lists);
            }
        });
    }

    @Override
    public void onAdapterItemClick(Buddy trackingBean) {
        startActivityForResult(TrackingBuddyDetailActivity.newIntent(getBaseActivity())
                        .putExtra(AppConstants.Extra.EXTRA_BUDDY, trackingBean)
                        /*sent this field if we want to show cancel button else not*/
                        .putExtra(AppConstants.Extra.EXTRA_SHOW_CANCEL_BUTTON, true),
                AppConstants.REQUEST_CODE_CANCEL_BUDDY_REQUEST);
    }

    @Override
    public void handleResponse(@Nullable ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
//        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, getBaseActivity())) {
            BuddyListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result), BuddyListResponse.class);
            List<Buddy> list = buddyListResponse.getBuddies();
            mTrackingMeViewModel.getBuddyListLiveData().setValue(list);
//            mTrackingMeAdapter.addItems(mTrackingMeViewModel.getBuddyObservableArrayList());

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_CANCEL_BUDDY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Cancel request successfully");
            }
        }
    }

    private void setRecyclerView() {
        RecyclerView trackingMeRecyclerView = mFragmentTrackingMeBinding.trackingMeRecyclerView;
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        trackingMeRecyclerView.setLayoutManager(mLayoutManager);
        trackingMeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trackingMeRecyclerView.setAdapter(mTrackingMeAdapter);
        mTrackingMeAdapter.addItems(mTrackingMeViewModel.getBuddyObservableArrayList());
        mTrackingMeAdapter.notifyDataSetChanged();
    }
}
