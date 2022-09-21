package com.rocketflyer.rocketflow.ui.buddyrequest

import com.tracki.data.DataManager
import com.tracki.data.model.request.AcceptRejectBuddyRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 6/12/18
 */
class BuddyRequestViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<BuddyRequestNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api

    fun getInvitations(httpManager: HttpManager, api: Api) {
        this.httpManager = httpManager
        this.api = api
        Invites().hitApi()
    }

    fun acceptRequest(httpManager: HttpManager, request: AcceptRejectBuddyRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        AcceptReject(request).hitApi()
    }

    inner class AcceptReject(var request: AcceptRejectBuddyRequest) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            return navigator.acceptRejectResponse(this, result, error)
        }

        override fun hitApi() {
            dataManager.acceptRejectRequest(this, httpManager, request, api)
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

    inner class Invites : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            return navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            dataManager.getInvites(this, httpManager, api)
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

