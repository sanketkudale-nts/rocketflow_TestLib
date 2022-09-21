package com.tracki.ui.roleselection

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.InitiateSignUpRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.NextScreen
import com.tracki.utils.rx.SchedulerProvider

class TaskSelectionViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<TaskSelectionNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api


    fun initiateSignUp(httpManager: HttpManager, api: Api, data: InitiateSignUpRequest) {
        this.httpManager = httpManager
        this.api = api
        InitiateSignUp(data).hitApi()
    }

    /**
     *
     */
    inner class InitiateSignUp(var data: InitiateSignUpRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleInitiateSignUpResponse(this@InitiateSignUp, result, error)
        }

        override fun hitApi() {
            dataManager.getAccountList(this@InitiateSignUp, httpManager, data, api)
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


    fun getConfig(httpManager: HttpManager?, nextScreen: NextScreen?, sdkAccessId: String?) {
        this.httpManager = httpManager!!
        Config(nextScreen, sdkAccessId).hitApi()
    }

    inner class Config(var nextScreen: NextScreen?, var sdkAccessId: String?) : ApiCallback {
        override fun onResponse(result: Any, error: APIError?) {
            navigator.handleConfigResponse(this, result, error, nextScreen, sdkAccessId)
        }

        override fun hitApi() {
            dataManager.getConfig(httpManager, this)
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

    fun getPunchInPunchOutData(httpManager: HttpManager?, nextScreen: NextScreen?) {
        this.httpManager = httpManager!!
        PunchInPunchOutData(nextScreen).hitApi()
    }

    inner class PunchInPunchOutData(var nextScreen: NextScreen?) : ApiCallback {
        override fun onResponse(result: Any, error: APIError?) {
            navigator.handlePunchInPunchOutResponse(this, result, error, nextScreen)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.GET_LAST_PUNCH]
            dataManager.punchInPunchOutData(this, httpManager, apiUrl)
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