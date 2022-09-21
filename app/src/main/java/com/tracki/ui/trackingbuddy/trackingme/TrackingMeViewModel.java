package com.tracki.ui.trackingbuddy.trackingme;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.BuddiesRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.List;

/**
 * Created by rahul abrol on 05/10/18.
 */

public class TrackingMeViewModel extends BaseViewModel<TrackingMeNavigator> implements ApiCallback {
    public final ObservableList<Buddy> buddyObservableArrayList = new ObservableArrayList<>();
    private MutableLiveData<List<Buddy>> buddyListLiveData;
    private BuddiesRequest buddiesRequest;
    private HttpManager httpManager;
    private Api api;

    TrackingMeViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    MutableLiveData<List<Buddy>> getBuddyListLiveData() {
        if (buddyListLiveData == null) {
            buddyListLiveData = new MutableLiveData<>();
        }
        return buddyListLiveData;
    }

    void addItemsToList(@NonNull List<Buddy> driverList) {
        buddyObservableArrayList.clear();
        buddyObservableArrayList.addAll(driverList);
    }

    ObservableList<Buddy> getBuddyObservableArrayList() {
        return buddyObservableArrayList;
    }


    void fetchBuddyList(BuddiesRequest buddyRequest, HttpManager httpManager, Api api) {
        this.httpManager = httpManager;
        this.api = api;
        this.buddiesRequest = buddyRequest;
        //createBuddyList(selection);
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().buddyListing(this, httpManager, api, buddiesRequest);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void onNetworkErrorClose() {

    }

    @Override
    public void onRequestTimeOut(ApiCallback callBack) {
        getNavigator().showTimeOutMessage(callBack);
    }

    @Override
    public void onLogout() {

    }
}