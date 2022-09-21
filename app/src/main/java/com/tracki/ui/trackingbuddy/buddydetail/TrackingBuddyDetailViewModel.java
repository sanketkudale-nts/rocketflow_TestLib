package com.tracki.ui.trackingbuddy.buddydetail;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.DeleteBuddyRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 9/10/18
 */
public class TrackingBuddyDetailViewModel extends
        BaseViewModel<TrackingBuddyDetailNavigator> implements ApiCallback {
    private HttpManager httpManager;
    private Api api;
    private DeleteBuddyRequest delete;

    TrackingBuddyDetailViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onCancelTrackRequest() {
        getNavigator().cancelTrackRequest();
    }

    void cancelApi(HttpManager httpManager, Api api, String buddyId) {
        this.httpManager = httpManager;
        this.api = api;
        delete = new DeleteBuddyRequest(buddyId);
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().cancelTrackingRequest(this,
                httpManager, delete, api);
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
