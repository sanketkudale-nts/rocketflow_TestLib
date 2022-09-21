package com.tracki.ui.uploadDocument

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.TransactionRequest
import com.tracki.data.model.request.UpdateFileRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.wallet.TransactionNavigator
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class UploadDocumentViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<UploadDocumentNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager

    fun uploadImage(data: UpdateFileRequest, httpManager: HttpManager?) {
        this.httpManager = httpManager!!
        UpdateProfilePic(data).hitApi()
    }

    inner class UpdateProfilePic(var data: UpdateFileRequest) : ApiCallback {
        override fun onResponse(result: Any, error: APIError?) {
            if (navigator != null)
                navigator.handleSendImageResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.UPLOAD_FILE_AGAINEST_ENTITY)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE_AGAINEST_ENTITY]!!

                if (dataManager != null)
                    dataManager.updateProfilePic(this, httpManager, data, apiUrl)
            }

        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}
    }


    fun addDocument(data: UploadDocumentRequest, httpManager: HttpManager?) {
        this.httpManager = httpManager!!
        AddDocuments(data).hitApi()
    }

    inner class AddDocuments(var data: UploadDocumentRequest) : ApiCallback {
        override fun onResponse(result: Any, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.CREATE_DOCUMENT)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.CREATE_DOCUMENT]!!

                if (dataManager != null)
                    dataManager.addDocuments(this, httpManager, data, apiUrl)
            }

        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}
    }


    fun updateDocument(data: UploadDocumentRequest, httpManager: HttpManager?) {
        this.httpManager = httpManager!!
        UpdateDocuments(data).hitApi()
    }

    inner class UpdateDocuments(var data: UploadDocumentRequest) : ApiCallback {
        override fun onResponse(result: Any, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.UPDATE_DOCUMENT)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.UPDATE_DOCUMENT]!!

                if (dataManager != null)
                    dataManager.updateDocuments(this, httpManager, data, apiUrl)
            }

        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}
    }


}