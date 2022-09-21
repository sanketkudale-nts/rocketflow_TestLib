package com.rocketflyer.rocketflow.ui.emergencyphone

import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 5/12/18
 */
class EmergencyContactViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<EmergencyContactNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api
    private lateinit var data: Any

    fun saveSettings(httpManager: HttpManager, data: Any, api: Api?) {
        this.httpManager = httpManager
        this.api = api!!
        this.data = data
        SaveSettings().hitApi()
    }
    inner class SaveSettings : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleSaveSettingResponse(this, result, error)
        }

        override fun hitApi() {
            dataManager.saveSettings(this, httpManager, data, api)
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