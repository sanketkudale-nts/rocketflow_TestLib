package com.tracki.ui.scanqrcode

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.AcceptRejectRequest
import com.tracki.data.model.request.QrScanRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.UserData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.Log
import com.tracki.utils.rx.SchedulerProvider

class QrCodeValueViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<QrCodeNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager



    fun getQrCodeValue(httpManager: HttpManager, id: String) {
        this.httpManager = httpManager
        GetQrCodeValue(id).hitApi()
    }

    inner class GetQrCodeValue(var id: String) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            Log.e("qrResp","$result - error=>${error?.errorType}")
            navigator.handleQrCodeResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.ENTITY_SCANNER)) {
                val apiMain = TrackiApplication.getApiMap()[ApiType.ENTITY_SCANNER]!!
                val api = Api()
                api.name = ApiType.ENTITY_SCANNER
                api.timeOut = apiMain.timeOut
                api.url = apiMain.url!!

                val qrScanRequest = QrScanRequest()
                qrScanRequest.code = id

                Log.e("dataManager","$dataManager  api=>${apiMain.url}")


                if(dataManager!=null){
                    dataManager.getQrCodeValue(this, httpManager, api, qrScanRequest)
                }

            }
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

    fun getUserDetail(httpManager: HttpManager, userId: String?) {
        this.httpManager = httpManager
        GetUserDetails(userId).hitApi()
    }

    inner class GetUserDetails(var userId: String?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUserDetailsResponse(this@GetUserDetails, result, error)
        }

        override fun hitApi() {
            if (dataManager != null) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.USER_DETAIL]!!
                val api = Api()
                if (userId != null) {
                    api.url = oldApi.url + "?fetchAddress=false&userId=${userId}"
                } else {
                    api.url = oldApi.url
                }
                api.name = ApiType.USER_DETAIL
                api.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getUserDetails(this, httpManager, api)
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getTaskDetails(httpManager: HttpManager, taskId: String?) {
        this.httpManager = httpManager
        GetTaskDetails(taskId).hitApi()
    }

    inner class GetTaskDetails(var taskId: String?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this@GetTaskDetails, result, error)
        }

        override fun hitApi() {
            if (dataManager != null) {
                val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_BY_ID]!!
                var request= AcceptRejectRequest(taskId!!)
                if (dataManager != null)
                    dataManager.getTaskById(this, httpManager, request, api)
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getProductDetails(
        httpManager: HttpManager,
        pid: String?,
    ) {
        this.httpManager = httpManager
        GetProductDetails(pid).hitApi()
    }

    inner class GetProductDetails(
        var pid: String?,

        ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductDetailsResponse(this, result, error,pid)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.PRODUCT_DETAIL)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.PRODUCT_DETAIL]!!
                var apiUrl = Api()
                if (pid != null) {
                    apiUrl.url =
                        oldApi.url + "?pid=" + pid
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.PRODUCT_DETAIL
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getProductDetails(this, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

}