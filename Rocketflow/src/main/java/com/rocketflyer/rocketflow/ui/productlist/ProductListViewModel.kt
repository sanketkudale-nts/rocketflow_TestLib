package com.rocketflyer.rocketflow.ui.productlist

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.ui.selectorder.PaginationRequest
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.rx.SchedulerProvider

class ProductListViewModel(
    dataManager: DataManager,
    schedulerProvider: SchedulerProvider
) :
    BaseViewModel<ProductListNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getTerminalCategory(
        httpManager: HttpManager,
        request: PaginationRequest?,
        flavorId: String?,
        loadBy: String = "ALL"
    ) {
        this.httpManager = httpManager
        GetTerminalCategory(request, flavorId, loadBy).hitApi()
    }

    inner class GetTerminalCategory(
        var paginationRequest: PaginationRequest?,
        var flavorId: String?,
        var loadBy: String
    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleTerminalCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.TERMINAL_CATEGORIES)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.TERMINAL_CATEGORIES]!!
                var apiUrl = Api()
                if (flavorId != null && paginationRequest != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&flavorId=" + flavorId + "&loadBy=" + loadBy + "&terminal=true" + "&parent=true"
                } else if (flavorId != null) {
                    apiUrl.url =
                        oldApi.url + "?flavorId=" + flavorId + "&loadBy=" + loadBy + "&terminal=true" + "&parent=false"
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.PRODUCT_CATEGORIES
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getProductTerminalCatgeory(this, httpManager, apiUrl)
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


    fun getSubCategories(
        httpManager: HttpManager,
        request: PaginationRequest?,
        flavorId: String?,
        categoryId: String?,
        loadBy: String = "ALL"
    ) {
        this.httpManager = httpManager
        GetSubCategory(request, flavorId, categoryId, loadBy).hitApi()
    }

    inner class GetSubCategory(
        var paginationRequest: PaginationRequest?,
        var flavorId: String?,
        var categoryId: String?,
        var loadBy: String
    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleSubCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.SUB_CATEGORIES)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.SUB_CATEGORIES]!!
                var apiUrl = Api()
                if (flavorId != null && paginationRequest != null && categoryId != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&flavorId=" + flavorId + "&loadBy=" + loadBy + "&categoryId=" + categoryId
                } else if (flavorId != null && paginationRequest != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&flavorId=" + flavorId + "&loadBy=" + loadBy
                } else if (paginationRequest != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&loadBy=" + loadBy
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.SUB_CATEGORIES
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getProductsSubCategory(this, httpManager, apiUrl)
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

    fun deleteProduct(httpManager: HttpManager, request: CatalogProduct?) {
        this.httpManager = httpManager
        DeleteProduct(request).hitApi()
    }

    inner class DeleteProduct(var request: CatalogProduct?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleDeleteProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.DELETE_PRODUCT)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.DELETE_PRODUCT]
                if (dataManager != null)
                    dataManager.deleteProduct(this, request, httpManager, apiUrl)
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

    fun updateStatusProductCategory(httpManager: HttpManager, request: CatalogProduct?) {
        this.httpManager = httpManager
        UpdateProductStatus(request!!).hitApi()
    }

    inner class UpdateProductStatus(var request: CatalogProduct) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUpdateProductStatusResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT_STATUS)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.UPDATE_PRODUCT_STATUS]
                if (dataManager != null)
                    dataManager.deleteProduct(this, request, httpManager, apiUrl)
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


    fun getProducts(
        httpManager: HttpManager,
        request: PaginationRequest?,
        flavorId: String?,
        cid: String?,
        loadBy: String = "ALL"
    ) {
        this.httpManager = httpManager
        GetProducts(request, flavorId, cid, loadBy).hitApi()
    }

    inner class GetProducts(
        var paginationRequest: PaginationRequest?,
        var flavorId: String?,
        var cid: String?,
        var loadBy: String
    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_PRODUCTS)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.GET_PRODUCTS]!!
                var apiUrl = Api()
                if (flavorId != null && paginationRequest != null && cid != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&flavorId=" + flavorId + "&loadBy=" + loadBy + "&cid=" + cid
                } else if (flavorId != null && paginationRequest != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&flavorId=" + flavorId + "&loadBy=" + loadBy
                } else if (paginationRequest != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&loadBy=" + loadBy
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.GET_PRODUCTS
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getProducts(this, httpManager, apiUrl)
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


    fun getProductByKeyWord(httpManager: HttpManager, request: PaginationRequest?) {
        this.httpManager = httpManager
        GetProductListByKeyWord(request).hitApi()
    }

    inner class GetProductListByKeyWord(var paginationRequest: PaginationRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.SEARCH_PRODUCT)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.SEARCH_PRODUCT]!!
                var apiUrl = Api()
                if (paginationRequest != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" +
                                paginationRequest!!.limit + "&offset=" +
                                paginationRequest!!.offset +
                                "&keyword=${paginationRequest!!.keyword}"
                }
                apiUrl.name = ApiType.SEARCH_PRODUCT
                apiUrl.timeOut = oldApi.timeOut

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.getProducts(this, httpManager, apiUrl)
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