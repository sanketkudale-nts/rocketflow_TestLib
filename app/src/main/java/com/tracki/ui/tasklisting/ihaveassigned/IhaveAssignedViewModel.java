package com.tracki.ui.tasklisting.ihaveassigned;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.tracki.data.DataManager;
import com.tracki.data.model.request.ExecuteUpdateRequest;
import com.tracki.data.model.request.TaskRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.List;

/**
 * Created by rahul abrol on 05/10/18.
 */

public class IhaveAssignedViewModel extends BaseViewModel<IhaveAssignedNavigator> {

    public final ObservableList<Task> taskObservableArrayList = new ObservableArrayList<>();

    // Create a LiveData with a List of @Task
    private MutableLiveData<List<Task>> taskListLiveData;
    private HttpManager httpManager;
    private Api apiUrl;
    private TaskRequest buddyRequest;

    IhaveAssignedViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    MutableLiveData<List<Task>> getTaskListLiveData() {
        if (taskListLiveData == null) {
            taskListLiveData = new MutableLiveData<>();
        }
        return taskListLiveData;
    }

    void addItemsToList(@NonNull List<Task> driverList) {
        taskObservableArrayList.clear();
        taskObservableArrayList.addAll(driverList);
    }

    ObservableList<Task> getBuddyObservableArrayList() {
        return taskObservableArrayList;
    }

    void getTaskList(HttpManager httpManager, Api api, TaskRequest buddyRequest) {
        this.httpManager = httpManager;
        this.apiUrl = api;
        this.buddyRequest = buddyRequest;
        new GetTaskList().hitApi();
    }

//    void cancelTask(HttpManager httpManager, Task task, Api api) {
//        this.httpManager = httpManager;
//        this.apiUrl = api;
//        new CancelTask(new AcceptRejectRequest(task.getTaskId())).hitApi();
//    }
//
//    void endTask(HttpManager httpManager, Task task, Api api) {
//        this.httpManager = httpManager;
//        this.apiUrl = api;
//        new EndTask(new AcceptRejectRequest(task.getTaskId())).hitApi();
//    }

    void executeUpdates(HttpManager httpManager, ExecuteUpdateRequest request, Api api) {
        this.httpManager = httpManager;
        this.apiUrl = api;
        new ExecuteUpdateTask(request).hitApi();
    }

    class ExecuteUpdateTask implements ApiCallback {

        ExecuteUpdateRequest request;

        ExecuteUpdateTask(ExecuteUpdateRequest request) {
            this.request = request;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            if(getNavigator()!=null)
            getNavigator().handleExecuteUpdateResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            if(getDataManager()!=null)
            getDataManager().executeUpdateTask(this, httpManager, request, apiUrl);
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

//    class EndTask implements ApiCallback {
//        AcceptRejectRequest acceptRejectRequest;
//
//        EndTask(AcceptRejectRequest acceptRejectRequest) {
//            this.acceptRejectRequest = acceptRejectRequest;
//        }
//
//        @Override
//        public void onResponse(Object result, APIError error) {
//            getNavigator().handleEndTaskResponse(this, result, error);
//        }
//
//        @Override
//        public void hitApi() {
//            getDataManager().endTask(this, httpManager, acceptRejectRequest, apiUrl);
//        }
//
//        @Override
//        public boolean isAvailable() {
//            return true;
//        }
//
//        @Override
//        public void onNetworkErrorClose() {
//
//        }
//
//        @Override
//        public void onRequestTimeOut(ApiCallback callBack) {
//            getNavigator().showTimeOutMessage(callBack);
//        }
//
//        @Override
//        public void onLogout() {
//
//        }
//    }

//    class CancelTask implements ApiCallback {
//        AcceptRejectRequest acceptRejectRequest;
//
//        CancelTask(AcceptRejectRequest acceptRejectRequest) {
//            this.acceptRejectRequest = acceptRejectRequest;
//        }
//
//        @Override
//        public void onResponse(Object result, APIError error) {
//            getNavigator().handleCancelTaskResponse(this, result, error);
//        }
//
//        @Override
//        public void hitApi() {
//            getDataManager().cancelTask(this, httpManager, acceptRejectRequest, apiUrl);
//        }
//
//        @Override
//        public boolean isAvailable() {
//            return true;
//        }
//
//        @Override
//        public void onNetworkErrorClose() {
//
//        }
//
//        @Override
//        public void onRequestTimeOut(ApiCallback callBack) {
//            getNavigator().showTimeOutMessage(callBack);
//        }
//
//        @Override
//        public void onLogout() {
//
//        }
//    }

    class GetTaskList implements ApiCallback {

        @Override
        public void onResponse(Object result, APIError error) {
            if(getNavigator()!=null)
            getNavigator().handleResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            if(getDataManager()!=null)
            getDataManager().getTasksList(this, httpManager, buddyRequest, apiUrl);
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
