package com.tracki.ui.main;

import com.tracki.TrackiApplication;
import com.tracki.data.DataManager;
import com.tracki.data.model.request.BuddiesRequest;
import com.tracki.data.model.request.EndTaskRequest;
import com.tracki.data.model.request.FleetRequest;
import com.tracki.data.model.request.HomeRequest;
import com.tracki.data.model.request.PunchInOut;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.ApiType;
import com.tracki.utils.BuddyInfo;
import com.tracki.utils.BuddyStatus;
import com.tracki.utils.Log;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 6/9/18
 */
public class MainViewModel extends BaseViewModel<MainNavigator> {
    private static final String TAG = "MainViewModel";
    public HttpManager httpManager;

    public MainViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
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

    void logout(HttpManager httpManager) {
        this.httpManager = httpManager;
        new LogoutAPI().hitApi();
    }



    void home(HttpManager httpManager, List<BuddyStatus> statusList) {
        this.httpManager = httpManager;
        new HomeAPI(statusList).hitApi();
    }



    void endTask(HttpManager httpManager, EndTaskRequest endTaskRequest) {
        this.httpManager = httpManager;
        new EndTask(endTaskRequest).hitApi();
    }

    /**
     * Method used to filter user selected data  and filter the buddy hashMap and return.
     *
     * @param filterSelectedList filter selected hashMap
     * @param buddyArrayList     buddy hashMap
     * @return filtered buddy hashMap
     */
    List<Buddy> filterUserData(ArrayList<Integer> filterSelectedList, ArrayList<Buddy> buddyArrayList) {
        try {
            ArrayList<Buddy> buddies = new ArrayList<>();
            String all = null, live = null, idle = null, offline = null;
            for (int i = 0; i < filterSelectedList.size(); i++) {
                switch (filterSelectedList.get(i)) {
                    case 0:
                        all = "All";
                        break;
                    case 1:
                        live = "LIVE";
                        break;
                    case 2:
                        idle = "IDLE";
                        break;
                    case 3:
                        offline = "OFFLINE";
                        break;
                }
            }

            for (int i = 0; i < buddyArrayList.size(); i++) {
                Buddy buddy = buddyArrayList.get(i);
                if (all != null && buddy.getStatus().name().equalsIgnoreCase(all)) {
                    buddies.add(buddy);
                }
                if (live != null && buddy.getStatus().name().equalsIgnoreCase(live)) {
                    buddies.add(buddy);
                }
                if (idle != null && buddy.getStatus().name().equalsIgnoreCase(idle)) {
                    buddies.add(buddy);
                }
                if (offline != null && buddy.getStatus().name().equalsIgnoreCase(offline)) {
                    buddies.add(buddy);
                }
            }
            return buddies;
        } catch (Exception e) {
            Log.e(TAG, "Exception inside filterUserData(): " + e);
            return null;
        }
    }



    private class HomeAPI implements ApiCallback {
        private HomeRequest homeRequest;

        HomeAPI(List<BuddyStatus> statusList) {
            this.homeRequest = new HomeRequest(statusList);
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleResponse(HomeAPI.this, result, error);
        }

        @Override
        public void hitApi() {
            Api api = TrackiApplication.getApiMap().get(ApiType.HOME);
            getDataManager().callHomeApi(HomeAPI.this, httpManager, api, homeRequest);
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

    private class LogoutAPI implements ApiCallback {
        private Api api = TrackiApplication.getApiMap().get(ApiType.LOGOUT);

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().onSuccessfulLogout(LogoutAPI.this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().logout(LogoutAPI.this, httpManager, api);
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
    void getUserLocations(HttpManager httpManager) {
        this.httpManager = httpManager;
        new GetUserLocations().hitApi();
    }

    private class GetUserLocations implements ApiCallback {
        private Api api = TrackiApplication.getApiMap().get(ApiType.USER_LOCATIONS);

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleMyPlaceResponse(GetUserLocations.this, result, error);
        }

        @Override
        public void hitApi() {
            if(api!=null)
            getDataManager().getUserLocation(GetUserLocations.this, httpManager, api);
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
    void punch(HttpManager httpManager, Object data, PunchInOut event) {
        this.httpManager = httpManager;
        new Punch(data,event).hitApi();
    }
    class Punch implements ApiCallback {
        private Api api = TrackiApplication.getApiMap().get(ApiType.PUNCH);
        Object data;
        PunchInOut event;

        public Punch(Object data, PunchInOut event) {
            this.data = data;
            this.event = event;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handlePunchInOutResponse(this, result, error, event);
        }

        @Override
        public void hitApi() {
            getDataManager().punch(Punch.this, httpManager,data, api);
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


    class EndTask implements ApiCallback {
        EndTaskRequest endTaskRequest;

        EndTask(EndTaskRequest endTaskRequest) {
            this.endTaskRequest = endTaskRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleEndTaskResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            Api api = TrackiApplication.getApiMap().get(ApiType.END_TASK);
            getDataManager().endTask(this, httpManager, endTaskRequest, api);
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

    void markOnlineOffline(HttpManager httpManager, Object data) {
        this.httpManager = httpManager;
        new MarkOnlineOffline(data).hitApi();
    }

    class MarkOnlineOffline implements ApiCallback {
        private Api api = TrackiApplication.getApiMap().get(ApiType.CHANGE_STATUS);
        Object data;

        public MarkOnlineOffline(Object data) {
            this.data = data;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleOnlineOfflineResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().punch(MarkOnlineOffline.this, httpManager, data, api);
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
