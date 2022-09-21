package com.tracki.ui.main.filter

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.*
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.*
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by Vikas Kesharvani on 04/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
open class TaskFilterViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<TaskFilterNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api

    fun getRegionList(httpManager: HttpManager, isStart: Boolean,regionRequest: RegionRequest) {
        this.httpManager = httpManager
        RegionList(isStart,regionRequest).hitApi()
    }

    fun submitFilter() {
        navigator.submitFilter()

    }

    fun clearFilter() {
        navigator.clearFilter()
    }

    /**
     *
     */
    inner class RegionList(var isStart: Boolean,var regionRequest: RegionRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleRegionListResponse(this@RegionList, result, error, isStart)
        }

        override fun hitApi() {
            var api = TrackiApplication.getApiMap()[ApiType.GET_REGIONS]
            if (api != null) {
                dataManager.getRegionList(this@RegionList, httpManager, regionRequest, api)
            } else {
                Log.e("message", "GET_REGIONS api is null")
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getStateList(httpManager: HttpManager, getManualLocationRequest: GetManualLocationRequest, isStart: Boolean) {
        this.httpManager = httpManager
        StateList(getManualLocationRequest, isStart).hitApi()
    }

    /**
     *
     */
    inner class StateList(var manualLocationRequest: GetManualLocationRequest, var isStart: Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleStateListResponse(this@StateList, result, error, isStart)
        }

        override fun hitApi() {
            var api = TrackiApplication.getApiMap()[ApiType.GET_STATES]
            if (api != null) {
                dataManager.getStateList(this@StateList, httpManager, manualLocationRequest, api)
            } else {
                Log.e("message", "GET_STATES api is null")
            }

        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getCityList(httpManager: HttpManager, getManualLocationRequest: GetManualLocationRequest, isStart: Boolean) {
        this.httpManager = httpManager
        CityList(getManualLocationRequest, isStart).hitApi()
    }

    /**
     *
     */
    inner class CityList(var manualLocationRequest: GetManualLocationRequest, var isStart: Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleCityListResponse(this@CityList, result, error, isStart)
        }

        override fun hitApi() {
            var api = TrackiApplication.getApiMap()[ApiType.GET_CITIES]
            if (api != null) {
                dataManager.getCityList(this@CityList, httpManager, manualLocationRequest, api)
            } else {
                Log.e("message", "GET_CITIES api is null")
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getHubList(httpManager: HttpManager, getManualLocationRequest: GetManualLocationRequest, isStart: Boolean) {
        this.httpManager = httpManager
        HubList(getManualLocationRequest, isStart).hitApi()
    }

    inner class HubList(var manualLocationRequest: GetManualLocationRequest, var isStart: Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleHubListResponse(this@HubList, result, error, isStart)
        }

        override fun hitApi() {
            var api = TrackiApplication.getApiMap()[ApiType.GET_HUBS]
            if (api != null) {
                dataManager.getHubList(this@HubList, httpManager, manualLocationRequest, api)
            } else {
                Log.e("message", "GET_HUBS api is null")
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }
}