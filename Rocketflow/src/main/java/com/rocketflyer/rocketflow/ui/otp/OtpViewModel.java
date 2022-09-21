package com.rocketflyer.rocketflow.ui.otp;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.tracki.TrackiApplication;
import com.tracki.data.DataManager;
import com.tracki.data.model.request.LoginRequest;
import com.tracki.data.model.request.OtpRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.ApiType;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.NextScreen;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 5/9/18
 */
public class OtpViewModel extends BaseViewModel<OtpNavigator> implements ApiCallback {
    private final int OTP_LENGTH = 6;
    private OtpRequest otpRequest;
    private HttpManager httpManager;
    private Api api;
    private ObservableField<String> timer;
    private ObservableBoolean retry, timerVisibility;
    private LoginRequest loginRequest;

    private Boolean changeDevice = false;

    OtpViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
        timer = new ObservableField<>("01.00");
        timerVisibility = new ObservableBoolean(true);
        retry = new ObservableBoolean(false);
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

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public void onClickResend() {
        getNavigator().onClickResend();
    }

    public void onButtonClick(View view) {
        CommonUtils.preventTwoClick(view);
        getNavigator().verifyOtp();

    }

    boolean isValidOtp(String otp) {
        return !CommonUtils.isViewNullOrEmpty(otp) && otp.length() == OTP_LENGTH;
    }

    void verifyOtpServerRequest(OtpRequest otpRequest, HttpManager httpManager, Api api) {
        this.otpRequest = otpRequest;
        this.httpManager = httpManager;
        this.api = api;
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi()
    {

        if(getDataManager()!=null)
        getDataManager().verifyOtp(httpManager, this, otpRequest, api);
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

    }

    @Override
    public void onLogout() {

    }



    void getConfig(HttpManager httpManager, @Nullable NextScreen nextScreen, String sdkAccessId) {
        this.httpManager = httpManager;
        new Config(nextScreen, sdkAccessId).hitApi();
    }

    public class Config implements ApiCallback {
        NextScreen nextScreen;
        String sdkAccessId;

        public Config(@Nullable NextScreen nextScreen, @Nullable String sdkAccessId) {
            this.sdkAccessId = sdkAccessId;
            this.nextScreen = nextScreen;
        }

        @Override
        public void onResponse(Object result, @Nullable APIError error) {
            if(getNavigator()!=null)
            getNavigator().handleConfigResponse(this, result, error, nextScreen, sdkAccessId);
        }

        @Override
        public void hitApi() {
            if(getDataManager()!=null)
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
            if(getNavigator()!=null)
            getNavigator().showTimeOutMessage(callBack);
        }

        @Override
        public void onLogout() {

        }
    }

    public void login(String mobile, NextScreen actionType, HttpManager httpManager, Api apiUrl) {
        loginRequest = new LoginRequest(actionType, mobile);
        this.httpManager = httpManager;
        this.api = apiUrl;
        new ResendOtp().hitApi();
    }
    public class ResendOtp implements ApiCallback {

        @Override
        public void onResponse(Object result, APIError error) {
            if(getNavigator()!=null)
            getNavigator().handleResendOtpResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            if(getDataManager()!=null)
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
            if(getNavigator()!=null)
            getNavigator().showTimeOutMessage(callBack);
        }

        @Override
        public void onLogout() {

        }
    }

    public void getPunchInPunchOutData(HttpManager httpManager, @Nullable NextScreen nextScreen) {
        this.httpManager = httpManager;
        new PunchInPunchOutData(nextScreen).hitApi();
    }
     class PunchInPunchOutData implements ApiCallback {
         NextScreen nextScreen;
         public PunchInPunchOutData(@Nullable NextScreen nextScreen) {
             this.nextScreen = nextScreen;
         }

         @Override
         public void onResponse(Object result, APIError error) {
             if(getNavigator()!=null)
             getNavigator().handlePunchInPunchOutResponse(this, result, error,nextScreen);
         }

         @Override
         public void hitApi() {
             if(TrackiApplication.getApiMap().containsKey(ApiType.GET_LAST_PUNCH))
             {
                 Api apiUrl = TrackiApplication.getApiMap().get(ApiType.GET_LAST_PUNCH);
                 if (getDataManager() != null)
                     getDataManager().punchInPunchOutData(this, httpManager, apiUrl);
             }
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
             if(getNavigator()!=null)
             getNavigator().showTimeOutMessage(callBack);

         }

         @Override
         public void onLogout() {

         }
    }
}
