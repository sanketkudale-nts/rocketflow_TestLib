package com.rocketflyer.rocketflow.ui.messages


import com.tracki.data.DataManager
import com.tracki.data.model.request.BuddiesRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

class MessageViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<MessageNavigator>(dataManager, schedulerProvider) {

    fun onFabClick() {
        navigator.onFabClick()
    }

    private var httpManager: HttpManager? = null
    private var apiUrl: Api? = null

    internal fun fetchBuddyList(buddyRequest: BuddiesRequest, httpManager: HttpManager, apiUrl: Api) {
        this.httpManager = httpManager
        this.apiUrl = apiUrl
        //createBuddyList(selection);
        GetBuddyListing(buddyRequest).hitApi()
    }

    inner class GetBuddyListing(private val buddyRequest: BuddiesRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this@GetBuddyListing, result, error)
        }

        override fun hitApi() {
            dataManager.buddyListing(this, httpManager, apiUrl, buddyRequest)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {

        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}
    }


}