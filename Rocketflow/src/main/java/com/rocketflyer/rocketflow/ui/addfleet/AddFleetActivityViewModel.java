package com.rocketflyer.rocketflow.ui.addfleet;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.AddFleetRequest;
import com.tracki.data.model.request.UpdateFileRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 6/9/18
 */
public class AddFleetActivityViewModel extends BaseViewModel<AddFleetNavigator> implements ApiCallback {
    private HttpManager httpManager;
    private Api api;
    private AddFleetRequest addFleetRequest;
    private UpdateFileRequest updateFileRequest;

    AddFleetActivityViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onUploadClick() {
        getNavigator().uploadImage();
    }

    public void onSubmitClick() {
        getNavigator().validateViews();
    }

    public boolean isViewEmpty(String string) {
        return CommonUtils.isViewNullOrEmpty(string);
    }

    public void addFleet(HttpManager httpManager, AddFleetRequest addFleetRequest, Api api) {
        this.httpManager = httpManager;
        this.api = api;
        this.addFleetRequest = addFleetRequest;
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().addFleet(this, httpManager, addFleetRequest, api);
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

    void uploadFleetPic(UpdateFileRequest file, HttpManager httpManager, Api api) {
        this.httpManager = httpManager;
        this.api = api;
        this.updateFileRequest = file;
        new UpdateProfilePic().hitApi();
    }

    class UpdateProfilePic implements ApiCallback {
        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleFleetPicResponse(UpdateProfilePic.this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().updateProfilePic(UpdateProfilePic.this, httpManager, updateFileRequest, api);
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
