package com.rocketflyer.rocketflow.ui.service.transition//package com.tracki.ui.service.transition
//
//import com.tracki.data.DataManager
//import com.tracki.data.model.request.AcceptRejectRequest
//import com.tracki.data.model.request.CreateTaskRequest
//import com.tracki.data.model.response.config.Task
//import com.tracki.data.model.response.config.Api
//import com.tracki.data.network.APIError
//import com.tracki.data.network.ApiCallback
//import com.tracki.data.network.HttpManager
//import com.tracki.ui.base.BaseViewModel
//import com.tracki.utils.rx.SchedulerProvider
//
///**
// * Created by rahul on 11/3/19
// */
//class TransitionServiceViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
//        BaseViewModel<TransitionServiceNavigator>(dataManager, schedulerProvider) {
//
//    private lateinit var httpManager: HttpManager
//    private lateinit var api: Api
//    private lateinit var createTaskRequest: CreateTaskRequest
//
//    fun createTaskApi(httpManager: HttpManager, createTaskRequest: CreateTaskRequest, api: Api) {
//        this.httpManager = httpManager
//        this.api = api
//        this.createTaskRequest = createTaskRequest
//        CreateTask().hitApi()
//    }
//
//    fun endTask(httpManager: HttpManager, task: Task, api: Api) {
//        this.httpManager = httpManager
//        this.api = api
//        EndTask(AcceptRejectRequest(task.taskId!!)).hitApi()
//    }
//
//    /**
//     *
//     */
//    inner class CreateTask : ApiCallback {
//
//        override fun onResponse(result: Any?, error: APIError?) {
//            navigator.handleResponse(this@CreateTask, result, error)
//        }
//
//        override fun hitApi() {
//            dataManager.createTask(this@CreateTask, httpManager, createTaskRequest, api)
//        }
//
//        override fun isAvailable() = true
//
//        override fun onNetworkErrorClose() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun onRequestTimeOut() {
//            navigator.showTimeOutMessage()
//        }
//
//        override fun onLogout() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }
//
//    inner class EndTask(var acceptRejectRequest: AcceptRejectRequest) : ApiCallback {
//
//        override fun onResponse(result: Any, error: APIError) {
//            navigator.handleEndTaskResponse(this, result, error)
//        }
//
//        override fun hitApi() {
//            dataManager.endTask(this, httpManager, acceptRejectRequest, api)
//        }
//
//        override fun isAvailable(): Boolean {
//            return true
//        }
//
//        override fun onNetworkErrorClose() {
//
//        }
//
//        override fun onRequestTimeOut() {
//            navigator.showTimeOutMessage()
//        }
//
//        override fun onLogout() {
//
//        }
//    }
//}