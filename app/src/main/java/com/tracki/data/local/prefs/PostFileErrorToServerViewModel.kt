package com.tracki.data.local.prefs


import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.response.config.ApiErrorRequest

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class PostFileErrorToServerViewModel  (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<PostErrorToServerNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun postFileErrorToServer(httpManager: HttpManager,request:ApiErrorRequest) {
        this.httpManager = httpManager
        PostErrorServer(request).hitApi()
    }

    inner class PostErrorServer(var request: ApiErrorRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handlePostErrorResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.APP_ERROR)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.APP_ERROR]!!

                if(dataManager!=null)
                    dataManager.postFileErrorToServer(this, httpManager,request, oldApi)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }



}