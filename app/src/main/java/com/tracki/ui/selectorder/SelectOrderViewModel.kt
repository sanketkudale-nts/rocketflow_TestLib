package com.tracki.ui.selectorder

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.GetManualLocationRequest
import com.tracki.data.model.request.LinkInventoryRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.Log
import com.tracki.utils.rx.SchedulerProvider

class SelectOrderViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<SelectOrderNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getProductCategory( flavourId: String?,categoryId:String?,httpManager: HttpManager, request: PaginationRequest?) {
        this.httpManager = httpManager
        GetProductCategory(flavourId,categoryId,request).hitApi()
    }

    inner class GetProductCategory(var flavourId: String?,var categoryId: String?,var paginationRequest: PaginationRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.TASK_PRODUCT_CATEGORIES)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.TASK_PRODUCT_CATEGORIES]!!
                var apiUrl = Api()
                if (paginationRequest != null&&flavourId!=null&&categoryId!=null) {
                    apiUrl.url =
                            oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset+ "&flavorId=${flavourId}"+ "&categoryId=${categoryId}"
                } else if(flavourId!=null&&categoryId!=null) {
                    apiUrl.url = oldApi.url+ "?flavorId=${flavourId}"+ "&categoryId=${categoryId}"
                }else if(flavourId!=null) {
                    apiUrl.url = oldApi.url+ "?flavorId=${flavourId}"
                }else if(categoryId!=null) {
                    apiUrl.url = oldApi.url+ "?categoryId=${categoryId}"
                }else{
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.TASK_PRODUCT_CATEGORIES
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getProductsCategory(this, httpManager, apiUrl)
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

    fun getProductByKeyWord(categoryId:String?,flavourId: String?,geoId:String?,httpManager: HttpManager, request: PaginationRequest?) {
        this.httpManager = httpManager
        GetProductListByKeyWord(categoryId,flavourId,geoId, request).hitApi()
    }

    inner class GetProductListByKeyWord(var categoryId:String?,var flavourId: String?,var geoId:String?, var paginationRequest: PaginationRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_PRODUCTS)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.GET_TASK_PRODUCTS]!!
                var apiUrl = Api()
                if (paginationRequest != null&&flavourId!=null&&categoryId!=null&&geoId!=null) {

                    apiUrl.url =
                        oldApi.url + "?limit=" +
                                paginationRequest!!.limit + "&offset=" +
                                paginationRequest!!.offset  +
                                "&keyword=${paginationRequest!!.keyword}"+ "&categoryId=${categoryId}"+ "&flavorId=${flavourId}"+"&geoId=${geoId}"
                }else{

                    apiUrl.url =
                        oldApi.url + "?limit=" +
                                paginationRequest!!.limit + "&offset=" +
                                paginationRequest!!.offset  +
                                "&keyword=${paginationRequest!!.keyword}"+ "&categoryId=${categoryId}"+ "&flavorId=${flavourId}"
                }
                apiUrl.name = ApiType.GET_TASK_PRODUCTS
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

    fun getProduct(httpManager: HttpManager,categoryId:String?, cid: String?,flavourId:String?,geoId:String?, request: PaginationRequest?) {
        this.httpManager = httpManager
        GetProductList(categoryId,cid,flavourId,geoId, request).hitApi()
    }

    inner class GetProductList(var categoryId:String?,var cid: String?,var flavourId: String?,var geoId:String?, var paginationRequest: PaginationRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_PRODUCTS)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.GET_TASK_PRODUCTS]!!
                var apiUrl = Api()
                if (paginationRequest != null) {
                    if (cid != null&&flavourId!=null&&geoId!=null) {
                        apiUrl.url =
                            oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&cid=${cid}" + "&loadBy=" + paginationRequest!!.loadBy + "&flavorId=${flavourId}"+ "&categoryId=${categoryId}"+"&geoId=${geoId}"

                    }
                    else if (cid != null&&flavourId!=null) {
                        apiUrl.url =
                                oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&cid=${cid}" + "&loadBy=" + paginationRequest!!.loadBy + "&flavorId=${flavourId}"+ "&categoryId=${categoryId}"

                    }else if (cid != null) {
                        apiUrl.url =
                            oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&cid=${cid}" + "&loadBy=" + paginationRequest!!.loadBy+ "&categoryId=${categoryId}"

                    }else {
                        apiUrl.url =
                                oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&loadBy=" + paginationRequest!!.loadBy+ "&categoryId=${categoryId}"
                    }
                } else {
                    if (cid != null&&flavourId!=null&&geoId!=null) {
                        apiUrl.url = oldApi.url + "?cid=${cid}"+ "?flavorId=${flavourId}"+ "&categoryId=${categoryId}"+"&geoId=${geoId}"
                    }
                    else if (cid != null&&flavourId!=null) {
                        apiUrl.url = oldApi.url + "?cid=${cid}"+ "?flavorId=${flavourId}"+ "&categoryId=${categoryId}"
                    } else if (cid != null) {
                        apiUrl.url = oldApi.url + "?cid=${cid}"+ "&categoryId=${categoryId}"
                    } else {
                        apiUrl.url = oldApi.url
                    }
                }
                apiUrl.name = ApiType.GET_TASK_PRODUCTS
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

    fun linkInventory(httpManager: HttpManager, request: LinkInventoryRequest) {
        this.httpManager = httpManager
        LinkInventory(request).hitApi()
    }

    inner class LinkInventory(private var data: LinkInventoryRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.linkInventoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.LINK_INVENTORY)) {
                val api = TrackiApplication.getApiMap()[ApiType.LINK_INVENTORY]!!
//            val api = Api()
//            api.url = "https://qa2.rocketflyer.in/rfapi/secure/tracki/linkInventory"
//            api.name = ApiType.LINK_INVENTORY
//            api.timeOut = 50
                if(dataManager!=null)
                    dataManager.linkInventory(this, httpManager, data, api)
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

    fun getHubList(httpManager: HttpManager, getManualLocationRequest: GetManualLocationRequest) {
        this.httpManager = httpManager
        HubList(getManualLocationRequest).hitApi()
    }

    inner class HubList(var manualLocationRequest: GetManualLocationRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleHubListResponse(this@HubList, result, error)
        }

        override fun hitApi() {
            var api = TrackiApplication.getApiMap()[ApiType.GET_HUBS]
            if (api != null) {
                dataManager.getHubList(this@HubList, httpManager, manualLocationRequest, api)
            } else {
                Log.e("message", "GET_HUBS api is null")
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

}