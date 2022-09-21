package com.tracki.ui.account;

import com.tracki.data.DataManager;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 3/10/18
 */
public class MyAccountViewModel extends BaseViewModel<MyAccountNavigator> implements ApiCallback {

    private HttpManager httpManager;
    private Api api;

    MyAccountViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);

    }

    void getProfile(HttpManager httpManager, Api api) {
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
        getDataManager().getProfile(this, httpManager, api);
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
