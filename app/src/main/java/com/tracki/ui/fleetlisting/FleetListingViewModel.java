package com.tracki.ui.fleetlisting;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.ChangeFleetStatusApiRequest;
import com.tracki.data.model.request.DeleteFleetRequest;
import com.tracki.data.model.request.FleetRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Fleet;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.List;

/**
 * Created by rahul on 14/9/18
 */
public class FleetListingViewModel extends BaseViewModel<FleetListingNavigator> {

    public final ObservableList<Fleet> fleetObservableArrayList = new ObservableArrayList<>();
    private MutableLiveData<List<Fleet>> fleetListLiveData;

    private HttpManager httpManager;
    private Api api;
    private FleetRequest fleetRequest;

    FleetListingViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    void addItemsToList(@NonNull List<Fleet> driverList) {
        fleetObservableArrayList.clear();
        fleetObservableArrayList.addAll(driverList);
    }

    MutableLiveData<List<Fleet>> getFleetListLiveData() {
        if (fleetListLiveData == null) {
            fleetListLiveData = new MutableLiveData<>();
        }
        return fleetListLiveData;
    }

    ObservableList<Fleet> getFleetObservableArrayList() {
        return fleetObservableArrayList;
    }

    public void onProceedClick() {
        getNavigator().onProceedClick();
    }

    public void onFabClick() {
        getNavigator().openAddFleetActivity();
    }


    void fetchFleets(HttpManager httpManager, Api api, FleetRequest fleetRequest) {
        this.httpManager = httpManager;
        this.api = api;
        new GetFleetListing(fleetRequest).hitApi();
    }

    void deleteFleet(HttpManager httpManager, DeleteFleetRequest fleetRequest, Api api) {
        this.httpManager = httpManager;
        this.api = api;
        new DeleteFleet(fleetRequest).hitApi();
    }
    void changeFleetStatus(HttpManager httpManager, ChangeFleetStatusApiRequest fleetRequest, Api api) {
        this.httpManager = httpManager;
        this.api = api;
        new ChangeFleetStatus(fleetRequest).hitApi();
    }

    class DeleteFleet implements ApiCallback {
        private final DeleteFleetRequest fleetRequest;

        DeleteFleet(DeleteFleetRequest fleetRequest) {
            this.fleetRequest = fleetRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().refreshFleetList(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().deleteFleet(this, httpManager, fleetRequest, api);
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
    class ChangeFleetStatus implements ApiCallback {
        private final ChangeFleetStatusApiRequest fleetRequest;

        ChangeFleetStatus(ChangeFleetStatusApiRequest fleetRequest) {
            this.fleetRequest = fleetRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().changeFleetStatus(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().changeFleetStatus(this, httpManager, fleetRequest, api);
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

    class GetFleetListing implements ApiCallback {
        private final FleetRequest fleetRequest;

        GetFleetListing(FleetRequest fleetRequest) {
            this.fleetRequest = fleetRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().fleetListing(this, httpManager, fleetRequest, api);
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
