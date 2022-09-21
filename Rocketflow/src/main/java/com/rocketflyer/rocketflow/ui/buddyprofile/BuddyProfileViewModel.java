package com.rocketflyer.rocketflow.ui.buddyprofile;

import android.view.View;

import com.tracki.data.DataManager;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 9/10/18
 */
public class BuddyProfileViewModel extends BaseViewModel<BuddyProfileNavigator> implements ApiCallback {
    private HttpManager httpManager;
    private Buddy buddy;
    private Api api;

    public BuddyProfileViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }


    public boolean isViewNullOrEmpty(String string) {
        return CommonUtils.isViewNullOrEmpty(string);
    }

    public boolean isMobileValid(String mobile) {
        return CommonUtils.isMobileValid(mobile);
    }

    public void openTimePicker(View view) {
        getNavigator().openTimePicker(view);
    }

    void updateBuddyProfile(Buddy buddy, HttpManager httpManager, Api api) {
        this.httpManager = httpManager;
        this.buddy = buddy;
        this.api = api;
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().updateBuddy(this, httpManager, buddy, api);
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
