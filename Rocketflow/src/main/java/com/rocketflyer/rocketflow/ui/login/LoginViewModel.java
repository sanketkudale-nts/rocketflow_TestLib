package com.rocketflyer.rocketflow.ui.login;

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
 * Created by rahul on 3/9/18
 */
public class LoginViewModel extends BaseViewModel<LoginNavigator> implements ApiCallback {
    private LoginRequest loginRequest;
    private HttpManager httpManager;
    private Api apiUrl;

    LoginViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void login(String mobile, NextScreen actionType, HttpManager httpManager, Api apiUrl) {
        loginRequest = new LoginRequest(actionType, mobile);
        this.httpManager = httpManager;
        this.apiUrl = apiUrl;
        hitApi();
    }

    boolean isMobileValid(String mobile) {
        return CommonUtils.isMobileValid(mobile);
    }

    public void onServerLoginClick() {
        getNavigator().login();
    }

    boolean isCountryCodeInvalid(String countryCode) {
        return CommonUtils.isViewNullOrEmpty(countryCode)
                && !countryCode.contains("+")
                && !(countryCode.charAt(0) == '+')
                && countryCode.charAt(1) == '+';

    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().login(this, httpManager, loginRequest, apiUrl);
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
