package com.tracki.ui.addbuddy;

import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.AddBuddyRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 18/9/18
 */
public class AddBuddyViewModel extends BaseViewModel<AddBuddyNavigator> implements ApiCallback {
    ObservableArrayList<String> vehicleList = new ObservableArrayList<>();
    private HttpManager httpManager;
    private AddBuddyRequest addBuddyRequest;
    private Api api;

    AddBuddyViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public ObservableArrayList<String> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(ObservableArrayList<String> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public void onFabClick() {
        getNavigator().validate();
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public boolean isViewNullOrEmpty(String string) {
        return CommonUtils.isViewNullOrEmpty(string);
    }

  /*  public boolean isEmailValid(String email) {
        return !CommonUtils.isViewNullOrEmpty(email) && CommonUtils.isEmailValid(email);
    }*/

    public boolean isMobileValid(String mobile) {
        return CommonUtils.isMobileValid(mobile);
    }

    public void openTimePicker(View view) {
        getNavigator().openTimePicker(view);
    }

    public void addBuddyInvite(HttpManager httpManager, AddBuddyRequest addBuddyRequest, Api api) {
        this.httpManager = httpManager;
        this.addBuddyRequest = addBuddyRequest;
        this.api = api;
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().addBuddyInvitation(this, httpManager, addBuddyRequest, api);
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
