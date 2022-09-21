package com.rocketflyer.rocketflow.ui.chat

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.rx.SchedulerProvider
import java.io.File

class ChatViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<ChatNavigator>(dataManager, schedulerProvider) {
    private var httpManager: HttpManager? = null

    fun uploadImage(httpManager: HttpManager, images: HashMap<String, ArrayList<File>>) {
        this.httpManager = httpManager
        UploadImage(images).hitApi()
    }

    inner class UploadImage(var imageList: Any) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleImageResponse(this, result, error)
        }

        override fun hitApi() {
            var api = TrackiApplication.getApiMap()[ApiType.UPLOAD_MEDIA]
            if(api!=null)
            dataManager.uploadFiles(this, httpManager, imageList, api)
            else{
                CommonUtils.showLogMessage("e","api not get","ApiType.UPLOAD_MEDIA")
            }
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