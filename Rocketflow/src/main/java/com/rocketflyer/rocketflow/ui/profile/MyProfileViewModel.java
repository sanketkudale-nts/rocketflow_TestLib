package com.rocketflyer.rocketflow.ui.profile;

import android.view.View;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.UpdateFileRequest;
import com.tracki.data.model.request.UpdateProfileRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 3/10/18
 */
public class MyProfileViewModel extends BaseViewModel<MyProfileNavigator> {
    private UpdateProfileRequest updateProfileRequest;
    private HttpManager httpManager;
    private Api api;
    private UpdateFileRequest updateFileRequest;

    MyProfileViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onEditMobileNumber(View view) {
        getNavigator().editMobileNumber(view);
    }

    public void onUploadProfilePic() {
        getNavigator().uploadProfilePic();
    }

    void updateProfile(UpdateProfileRequest updateProfileRequest, HttpManager httpManager, Api api) {
        this.updateProfileRequest = updateProfileRequest;
        this.httpManager = httpManager;
        this.api = api;
        new UpdateProfile().hitApi();
    }



    class UpdateProfile implements ApiCallback {
        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().updateProfile(this, httpManager, updateProfileRequest, api);
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
    void uploadProfilePic(UpdateFileRequest uri, HttpManager httpManager, Api api) {
        this.httpManager = httpManager;
        this.api = api;
        this.updateFileRequest = uri;
        new UpdateProfilePic().hitApi();
    }
    class UpdateProfilePic implements ApiCallback {
        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleProfilePicResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().updateProfilePic(this, httpManager, updateFileRequest, api);
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
