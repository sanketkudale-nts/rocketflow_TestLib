package com.rocketflyer.rocketflow.ui.tasklisting;

import com.tracki.TrackiApplication;
import com.tracki.data.DataManager;
import com.tracki.data.model.request.BuddiesRequest;
import com.tracki.data.model.request.FleetRequest;
import com.tracki.data.model.request.InventoryRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.ui.tasklisting.TaskNavigator;
import com.tracki.utils.ApiType;
import com.tracki.utils.BuddyInfo;
import com.tracki.utils.BuddyStatus;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 8/10/18
 */
public class TaskViewModel extends BaseViewModel<TaskNavigator> {
    private HttpManager httpManager;

    TaskViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    void checkBuddy(HttpManager httpManager) {
        this.httpManager = httpManager;
        new BuddyListingAPI().hitApi();
    }

    void checkFleet(HttpManager httpManager) {
        this.httpManager = httpManager;
        new FleetListingAPI().hitApi();
    }


    private class BuddyListingAPI implements ApiCallback {
        private BuddiesRequest buddyRequest;
        private Api api;

        BuddyListingAPI() {
            List<BuddyStatus> statusList = new ArrayList<>();
            statusList.add(BuddyStatus.ON_TRIP);
            statusList.add(BuddyStatus.OFFLINE);
            statusList.add(BuddyStatus.IDLE);
            buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, true);
            api = TrackiApplication.getApiMap().get(ApiType.BUDDIES);
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().checkBuddyResponse(BuddyListingAPI.this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().buddyListing(BuddyListingAPI.this, httpManager, api, buddyRequest);
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

    private class FleetListingAPI implements ApiCallback {

        FleetRequest fleetRequest = new FleetRequest("ALL");
        Api api = TrackiApplication.getApiMap().get(ApiType.FLEETS);

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().checkFleetResponse(FleetListingAPI.this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().fleetListing(FleetListingAPI.this, httpManager, fleetRequest, api);
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

    void checkInventoryData(HttpManager httpManager, InventoryRequest request) {
        this.httpManager = httpManager;
        new CheckInventory(request).hitApi();
    }

    class CheckInventory implements ApiCallback {
        InventoryRequest data;

        public CheckInventory(InventoryRequest data) {
            this.data = data;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().checkInventory(this, result, error);
        }

        @Override
        public void hitApi() {
            Api api = TrackiApplication.getApiMap().get(ApiType.GET_TASK_INVENTORIES);

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