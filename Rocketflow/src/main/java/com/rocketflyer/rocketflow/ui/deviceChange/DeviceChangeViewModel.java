package com.rocketflyer.rocketflow.ui.deviceChange;

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
import com.tracki.utils.Log;
import com.tracki.utils.rx.SchedulerProvider;

public class DeviceChangeViewModel extends BaseViewModel<DeviceChangeNavigator> implements ApiCallback {

    private HttpManager httpManager;
    private Api api;


    private Boolean changeDevice = false;

    DeviceChangeViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }



    void changeDevice(HttpManager httpManager, Api api1) {
        this.httpManager = httpManager;
        /*Api api1 = TrackiApplication.getApiMap().get(ApiType.INITIATE_DEVICE_CHANGE);
        Log.e("deviceChange2",api1.getName().toString()+"");*/
        this.api = api1;
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
        getDataManager().initiateDeviceChange(this,httpManager,api);
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
}
