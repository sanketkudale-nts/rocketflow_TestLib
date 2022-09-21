package com.rocketflyer.rocketflow.ui.category

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.UpdateFileRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class AddProductCategoryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<AddProductCategoryNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager

    fun addProductCategory(httpManager: HttpManager, request: AddCategoryRequest?) {
        this.httpManager = httpManager
        AddProductCategory(request).hitApi()
    }

    inner class AddProductCategory(var request: AddCategoryRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.ADD_PRODUCT_CATEGORY)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.ADD_PRODUCT_CATEGORY]!!

                if (dataManager != null)
                    dataManager.addProductCategory(this, request, httpManager, apiUrl)
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

    fun updateProductcategory(httpManager: HttpManager, request: AddCategoryRequest?) {
        this.httpManager = httpManager
        UpdateProductCategory(request).hitApi()
    }

    inner class UpdateProductCategory(var request: AddCategoryRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        ///UPDATE_PRODUCT_CATEGORY
        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT_CATEGORY)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.UPDATE_PRODUCT_CATEGORY]!!

                if (dataManager != null)
                    dataManager.updateProductCategory(this, request, httpManager, apiUrl)
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

}