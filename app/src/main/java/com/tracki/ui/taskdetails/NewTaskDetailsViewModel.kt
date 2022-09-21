package com.tracki.ui.taskdetails


import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.AcceptRejectRequest
import com.tracki.data.model.request.ExecuteUpdateRequest

import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.dynamicform.dynamicfragment.DynamicNavigator
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider


class NewTaskDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<TDNavigator>(dataManager, schedulerProvider), ApiCallback {


    private val tag = "TaskDetailViewModel"

    lateinit var httpManager: HttpManager
    lateinit var request: AcceptRejectRequest
    lateinit var api: Api

    private inner class GetTaskData(var data:Any?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleGetTaskDataResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_DATA]
                if(dataManager!=null)
                    dataManager.taskData(this, httpManager, data, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

   /* fun getTaskData( httpManager: HttpManager, data:Any?) {
        this.httpManager = httpManager
        GetTaskData(data).hitApi()
    }


    fun getPreviousFormData( httpManager: HttpManager, data:Any?) {
        this.httpManager = httpManager
        GetPreviousFormData(data).hitApi()
    }

    private inner class GetPreviousFormData(var data:Any?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleGetPreviousFormResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_DATA]
                if(dataManager!=null)
                    dataManager.taskData(this, httpManager, data, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    private inner class GetTaskData(var data:Any?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleGetTaskDataResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_DATA]
                if(dataManager!=null)
                    dataManager.taskData(this, httpManager, data, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }*/

    fun expandCollapse(){
        navigator.expandCollapse()
    }

    fun getTaskData( httpManager: HttpManager, data:Any?) {
        this.httpManager = httpManager
        GetTaskData(data).hitApi()
    }


    fun getTaskById(httpManager: HttpManager, request: AcceptRejectRequest, api: Api?) {
        this.httpManager = httpManager
        this.request = request
        this.api = api!!
        hitApi()
    }

    override fun onResponse(result: Any?, error: APIError?) {
        navigator.handleResponse(this, result, error)
    }

    override fun hitApi() {
        dataManager.getTaskById(this, httpManager, request, api)
    }

    override fun isAvailable() = true

    override fun onNetworkErrorClose() {

    }

    override fun onRequestTimeOut(callBack: ApiCallback) {
        navigator.showTimeOutMessage(callBack)
    }

    override fun onLogout() {

    }


    //    void acceptTask(HttpManager httpManager, String taskId, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new AcceptTask(new AcceptRejectRequest(taskId)).hitApi();
    //    }
    //    void startTask(HttpManager httpManager, StartTaskRequest startTaskRequest, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new StartTask(startTaskRequest).hitApi();
    //    }
    //    void cancelTask(HttpManager httpManager, String taskId, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new CancelTask(new CancelRequest(taskId, false)).hitApi();
    //    }
    //    void endTask(HttpManager httpManager, EndTaskRequest endTaskRequest, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new EndTask(endTaskRequest).hitApi();
    //    }
    //    void arriveReachTask(HttpManager httpManager, ArriveReachRequest request, Api api) {
    //        this.httpManager = httpManager;
    //        this.apiUrl = api;
    //        new ArriveReachTask(request).hitApi();
    //
    //    }
    fun executeUpdates(httpManager: HttpManager?, request: ExecuteUpdateRequest?, api: Api?) {
        this.httpManager = httpManager!!
        this.api = api!!
        ExecuteUpdateTask(request).hitApi()
    }

    inner class ExecuteUpdateTask(var request: ExecuteUpdateRequest?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleExecuteUpdateResponse(this, result, error)
        }

        override fun hitApi() {
            dataManager.executeUpdateTask(this, httpManager, request, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }


}