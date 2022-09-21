package com.tracki.ui.notification

import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 11/10/18
 */
open class NotificationViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<NotificationNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api

    fun getNotificationList(httpManager: HttpManager, api: Api) {
        this.httpManager = httpManager
        this.api = api

        GetNotification().hitApi()
    }

    fun clearAll(httpManager: HttpManager, api: Api) {
        this.httpManager = httpManager
        this.api = api
        ClearNotification().hitApi()
    }

    inner class GetNotification : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            dataManager.getNotifications(this, httpManager, api)
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

    inner class ClearNotification : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleClearNotificationResponse(this@ClearNotification, result, error)
        }

        override fun hitApi() {
            dataManager.clearNotifications(this@ClearNotification, httpManager, api)

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