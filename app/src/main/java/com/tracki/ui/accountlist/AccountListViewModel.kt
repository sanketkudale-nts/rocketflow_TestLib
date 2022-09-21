package com.tracki.ui.accountlist

import com.tracki.data.DataManager
import com.tracki.data.model.request.LoginRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.CommonUtils
import com.tracki.utils.NextScreen
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 16/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class AccountListViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?) : BaseViewModel<AccountListNavigator>(dataManager, schedulerProvider), ApiCallback{


    private var loginRequest: LoginRequest? = null
    private var httpManager: HttpManager? = null
    private var apiUrl: Api? = null



    fun login(mobile: String?, actionType: NextScreen?, httpManager: HttpManager?, apiUrl: Api?) {
        loginRequest = LoginRequest(actionType!!, mobile!!)
        this.httpManager = httpManager
        this.apiUrl = apiUrl
        hitApi()
    }

    fun isMobileValid(mobile: String?): Boolean {
        return CommonUtils.isMobileValid(mobile)
    }

    fun onServerLoginClick() {
        navigator.login()
    }


    override fun onResponse(result: Any?, error: APIError?) {
        navigator.handleResponse(this, result, error)
    }

    override fun onLogout() {
    }

    override fun onNetworkErrorClose() {
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun onRequestTimeOut(callBack: ApiCallback?) {
        navigator.showTimeOutMessage(callBack!!)
    }

    override fun hitApi() {
        dataManager.login(this, httpManager, loginRequest, apiUrl)
    }

    fun getConfig(httpManager: HttpManager?) {
        this.httpManager = httpManager
        Config().hitApi()
    }
    inner class Config() : ApiCallback {
        override fun onResponse(result: Any, error: APIError?) {
            navigator.handleConfigResponse(this, result, error)
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

        override fun onLogout() {

        }
    }
}