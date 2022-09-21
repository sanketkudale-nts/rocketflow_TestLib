package com.rocketflyer.rocketflow.ui.likeslist

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
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
class LikeListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<LikesListNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getAllLikes(httpManager: HttpManager, request: GetCommentsOfPosts) {
        this.httpManager = httpManager
        GetAllLikes(request).hitApi()
    }

    inner class GetAllLikes(private var data: GetCommentsOfPosts) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            val apiMain = TrackiApplication.getApiMap()[ApiType.GET_LIKES]!!
            val api = Api()
            api.name = ApiType.GET_LIKES
            api.timeOut = apiMain.timeOut
            api.url = "${apiMain.url}?postId=${data.postId}"
            dataManager.getAllLikes(this, httpManager, data, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

}