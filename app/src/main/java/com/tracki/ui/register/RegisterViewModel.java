package com.tracki.ui.register;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.LoginRequest;
import com.tracki.data.model.request.RegisterRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.ui.otp.OtpViewModel;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.NextScreen;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 5/9/18
 */
public class RegisterViewModel extends BaseViewModel<RegisterNavigator> implements ApiCallback {
    private RegisterRequest userRequest;
    private HttpManager httpManager;
    private Api api;
    private ObservableField<String> timer;
    private ObservableBoolean retry, timerVisibility;
    private final int OTP_LENGTH = 6;
    RegisterViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
        timer = new ObservableField<>("01.00");
        timerVisibility = new ObservableBoolean(true);
        retry = new ObservableBoolean(false);
    }
    boolean isValidOtp(String otp) {
        return !CommonUtils.isViewNullOrEmpty(otp) && otp.length() == OTP_LENGTH;
    }
    public void onClickResend() {
        getNavigator().onClickResend();
    }
    public ObservableBoolean getTimerVisibility() {
        return timerVisibility;
    }

    public ObservableBoolean getRetry() {
        return retry;
    }

    public ObservableField<String> getTimer() {
        return timer;
    }
    public void onGetStartedClick(View v) {
        CommonUtils.preventTwoClick(v);
        getNavigator().validateAndRegister();
    }

    public boolean isViewNullOrEmpty(String string) {
        return CommonUtils.isViewNullOrEmpty(string);
    }

    boolean isEmailValid(String email) {
        return CommonUtils.isEmailValid(email);
    }

    boolean isMobileValid(String mobile) {
        return CommonUtils.isMobileValid(mobile);
    }

    void registerUser(RegisterRequest userRequest, HttpManager httpManager, Api api) {
        this.userRequest = userRequest;
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
        getDataManager().signUp(this, httpManager, userRequest, api);
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

    void getConfig(HttpManager httpManager, NextScreen nextScreen, String sdkAccessId) {
        this.httpManager = httpManager;
        new Config(nextScreen, sdkAccessId).hitApi();
    }

    public class Config implements ApiCallback {
        NextScreen nextScreen;
        String sdkAccessId;

        public Config(NextScreen nextScreen, String sdkAccessId) {
            this.sdkAccessId = sdkAccessId;
            this.nextScreen = nextScreen;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleConfigResponse(this, result, error, nextScreen, sdkAccessId);
        }

        @Override
        public void hitApi() {
            getDataManager().getConfig(httpManager, this);
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
    public void login(String mobile, NextScreen actionType, HttpManager httpManager, Api apiUrl) {
        LoginRequest loginRequest = new LoginRequest(actionType, mobile);
        this.httpManager = httpManager;
        this.api = apiUrl;
        new ResendOtp(loginRequest).hitApi();
    }
    public class ResendOtp implements ApiCallback {
        public ResendOtp(LoginRequest loginRequest) {
            this.loginRequest = loginRequest;
        }

        LoginRequest loginRequest=null;

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleResendOtpResponse(this, result, error);
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

}
