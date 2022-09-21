package com.rocketflyer.rocketflow.ui.main.taskdashboard;

import com.tracki.TrackiApplication;
import com.tracki.data.DataManager;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.ApiType;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.List;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TaskDashBoardViewModel extends BaseViewModel<TaskDashBoardNavigator> {
    private static final String TAG = "MainViewModel";
    public HttpManager httpManager;

    public TaskDashBoardViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }


    class DashBoardRequest {
        String categoryId;
        Long from;
        Long to;
        String loadBy;
        String userId;
        List<String> hubIds;
        String regionId;
        String cityId;
        String stateId;
        String placeId;

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        Long date;

        public void setDate(Long date) {
            this.date = date;
        }

        boolean userGeoReq;

        public boolean isUserGeoReq() {
            return userGeoReq;
        }

        public void setUserGeoReq(boolean userGeoReq) {
            this.userGeoReq = userGeoReq;
        }

        public List<String> getHubIds() {
            return hubIds;
        }

        public void setHubIds(List<String> hubIds) {
            this.hubIds = hubIds;
        }

        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public Long getFrom() {
            return from;
        }

        public void setFrom(Long from) {
            this.from = from;
        }

        public Long getTo() {
            return to;
        }

        public void setTo(Long to) {
            this.to = to;
        }

        public String getLoadBy() {
            return loadBy;
        }

        public void setLoadBy(String loadBy) {
            this.loadBy = loadBy;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    void dashboardApi(HttpManager httpManager, String categoryId, Long from, Long to, String userId, String LOADBY, List<String> hubIds, String regionId, String stateId, String cityId, boolean userGeoReq,String placeId) {
        this.httpManager = httpManager;
        DashBoardRequest dashBoardRequest = new DashBoardRequest();
        dashBoardRequest.setCategoryId(categoryId);
        dashBoardRequest.setFrom(from);
        dashBoardRequest.setTo(to);
        dashBoardRequest.setRegionId(regionId);
        dashBoardRequest.setStateId(stateId);
        dashBoardRequest.setCityId(cityId);
        dashBoardRequest.setHubIds(hubIds);
        dashBoardRequest.setPlaceId(placeId);
        dashBoardRequest.setUserGeoReq(userGeoReq);
        dashBoardRequest.setDate(System.currentTimeMillis());
        //dashBoardRequest.setLoadBy("ASSIGNED_TO_ME");
        if(LOADBY!=null&&!LOADBY.isEmpty()) {
            dashBoardRequest.setLoadBy(LOADBY);
        }
        dashBoardRequest.setUserId(userId);
        new DashboardAPI(dashBoardRequest).hitApi();
    }


    private class DashboardAPI implements ApiCallback {
        private DashBoardRequest dashboardRequest;

        public DashboardAPI(DashBoardRequest dashboardRequest) {
            this.dashboardRequest = dashboardRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            if(getNavigator()!=null) {
                getNavigator().handleDashboardResponse(DashboardAPI.this, result, error);
            }
        }

        @Override
        public void hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.DASHBOARD_TASKS)) {
                Api api = TrackiApplication.getApiMap().get(ApiType.DASHBOARD_TASKS);
                if(getDataManager()!=null)
                getDataManager().callDashboardApi(DashboardAPI.this, httpManager, api, dashboardRequest);
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
            getNavigator().showTimeOutMessage(callBack);
        }

        @Override
        public void onLogout() {

        }
    }

    void getInsights(HttpManager httpManager) {
        this.httpManager = httpManager;
        new GetInsights().hitApi();
    }

    private class GetInsights implements ApiCallback {

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleInsightsResponse(GetInsights.this, result, error);
        }

        @Override
        public void hitApi() {
            Api api = TrackiApplication.getApiMap().get(ApiType.GET_INSIGHTS);
            if(api!=null) {
                if(getDataManager()!=null)
                getDataManager().getInsights(GetInsights.this, httpManager, api);
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
