package com.rocketflyer.rocketflow.ui.changemobile;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.LoginRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.NextScreen;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 3/10/18
 */
public class ChangeMobileViewModel extends BaseViewModel<ChangeMobileNavigator> implements ApiCallback {
    private HttpManager httpManager;
    private Api api;
    private LoginRequest loginRequest;

    ChangeMobileViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onProceedClick() {
        getNavigator().onProceedClick();
    }

    public boolean isMobileValid(String mobile) {
        return CommonUtils.isMobileValid(mobile);
    }

    void changeMobile(String mobile, HttpManager httpManager, Api api) {
        loginRequest = new LoginRequest(NextScreen.CHANGE_MOBILE, mobile);
        this.httpManager = httpManager;
        this.api = api;
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().login(this, httpManager, loginRequest, api);
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
