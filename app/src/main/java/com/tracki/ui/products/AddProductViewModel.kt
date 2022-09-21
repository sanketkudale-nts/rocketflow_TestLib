package com.tracki.ui.products

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.UpdateFileRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.selectorder.PaginationRequest
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class AddProductViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<AddProductNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager

    fun addProduct(httpManager: HttpManager, request: AddProductRequest?) {
        this.httpManager = httpManager
        AddProduct(request).hitApi()
    }

    inner class AddProduct(var request: AddProductRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.ADD_PRODUCT)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.ADD_PRODUCT]!!

                if (dataManager != null)
                    dataManager.addProduct(this, request, httpManager, apiUrl)
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

    fun updateProduct(httpManager: HttpManager, request: AddProductRequest?) {
        this.httpManager = httpManager
        UpdateProduct(request).hitApi()
    }

    inner class UpdateProduct(var request: AddProductRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUpdateResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.UPDATE_PRODUCT]!!

                if (dataManager != null)
                    dataManager.updateProduct(this, request, httpManager, apiUrl)
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


    fun getUnits(httpManager: HttpManager, ) {
        this.httpManager = httpManager
        GetUnits().hitApi()
    }

    inner class GetUnits() : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUnitsResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_UNITS)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.GET_UNITS]!!

                if (dataManager != null)
                    dataManager.getUnits(this, httpManager, apiUrl)
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
                navigator.handleProductDetailsResponse(this, result, error)
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