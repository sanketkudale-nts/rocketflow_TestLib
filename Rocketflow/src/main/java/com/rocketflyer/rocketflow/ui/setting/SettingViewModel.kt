package com.rocketflyer.rocketflow.ui.setting

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider
import java.io.File

/**
 * Created by rahul on 16/10/18
 */
class SettingViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<SettingNavigator>(dataManager, schedulerProvider), ApiCallback {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api
    private lateinit var data: Any

    fun onClickEmergencyContacts() {
        navigator.openEmergencyContactActivity()
    }

    fun sendLocalDbToServerActivity() {
        navigator.sendLocalDbToServerActivity()
    }

    fun onClickEmergencyMessage() {
        navigator.openEmergencyMessageActivity()
    }

    fun getSettings(httpManager: HttpManager, api: Api?) {
        this.httpManager = httpManager
        this.api = api!!
        hitApi()
    }

    override fun onResponse(result: Any?, error: APIError?) {
        navigator.handleResponse(this, result, error)
    }

    override fun hitApi() {
        dataManager.getSettings(this, httpManager, api)
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

    fun uploadFileList(hashMap: HashMap<String, ArrayList<File>>, httpManager: HttpManager) {
        this.httpManager = httpManager
        UploadFiles(hashMap).hitApi()
    }

    inner class UploadFiles(val hashMap: HashMap<String, ArrayList<File>>) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleUploadFileResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
            dataManager.uploadFiles(this@UploadFiles, httpManager, hashMap, api)
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

    fun saveSettings(httpManager: HttpManager, data: Any, api: Api?) {
        this.httpManager = httpManager
        this.api = api!!
        this.data = data
        SaveSettings().hitApi()
    }
}