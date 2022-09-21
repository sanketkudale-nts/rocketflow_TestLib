package com.rocketflyer.rocketflow.ui.feeds

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.CommentsPostRequest
import com.tracki.data.model.request.EmployeeListAttendanceRequest
import com.tracki.data.model.request.GetCommentsOfPosts
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 04/01/21.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class FeedsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<FeedsNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getAllPost(httpManager: HttpManager, request: EmployeeListAttendanceRequest) {
        this.httpManager = httpManager
        GetAllPost(request).hitApi()
    }

    inner class GetAllPost(private var data: EmployeeListAttendanceRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_ALL_POST)) {
                val api = TrackiApplication.getApiMap()[ApiType.GET_ALL_POST]!!
                if(dataManager!=null)
                dataManager.getAllPost(this, httpManager, data, api)
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
    fun getPostComments(httpManager: HttpManager, request: GetCommentsOfPosts) {
        this.httpManager = httpManager
        GetComments(request).hitApi()
    }

    inner class GetComments(private var data: GetCommentsOfPosts) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleCommentsResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_COMMENTS)) {
                val apiMain = TrackiApplication.getApiMap()[ApiType.GET_COMMENTS]!!
                val api = Api()
                api.name = ApiType.GET_COMMENTS
                api.timeOut = apiMain.timeOut
                api.url = "${apiMain.url}?postId=${data.postId}"
                if(dataManager!=null)
                dataManager.getComments(this, httpManager, data, api)
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

    fun sendComments(httpManager: HttpManager, request: CommentsPostRequest) {
        this.httpManager = httpManager
        SendComments(request).hitApi()
    }

    inner class SendComments(private var data: CommentsPostRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handlePostCommentsResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.UPDATE_REACTION)) {
                val api = TrackiApplication.getApiMap()[ApiType.UPDATE_REACTION]!!
                if(dataManager!=null)
                dataManager.updateReaction(this, httpManager, data, api)
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
}