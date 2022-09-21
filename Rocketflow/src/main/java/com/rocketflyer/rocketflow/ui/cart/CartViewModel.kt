package com.rocketflyer.rocketflow.ui.cart

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.LinkInventoryRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.rx.SchedulerProvider

class CartViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<CartNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getCart(httpManager: HttpManager, request: CartRequest) {
        this.httpManager = httpManager
        GetCart(request).hitApi()
    }

    inner class GetCart(var request: CartRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleCartResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.VIEW_CART)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.VIEW_CART]!!
               /* var apiUrl = Api()
                apiUrl.url = oldApi.url
                apiUrl.name = ApiType.VIEW_CART
                apiUrl.timeOut = oldApi.timeOut*/
                if (dataManager != null)
                    dataManager.getCart(this, httpManager, request,oldApi)
            } /*else {
                val api = Api()
                var url = "https://qa2.rocketflyer.in/rfapi/secure/rb/store/order/cart"

                api.url = url
                api.name = ApiType.VIEW_CART
                api.timeOut = 12
                if (dataManager != null)
                    dataManager.getCart(this, httpManager,request, api)
            }*/
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

    fun applyCoupon(httpManager: HttpManager, request: ApplyCouponRequest?) {
        this.httpManager = httpManager
        ApplyCoupon(request).hitApi()
    }

    inner class ApplyCoupon( var request: ApplyCouponRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleApplyCouponResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.VALIDATE_COUPON)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.VALIDATE_COUPON]!!
                var apiUrl = Api()
                apiUrl.url = oldApi.url
                apiUrl.name = ApiType.VALIDATE_COUPON
                apiUrl.timeOut = oldApi.timeOut

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.applyCoupon(this, httpManager,request, apiUrl)
            } /*else {
                var apiUrl = Api()
                var url = "https://qa2.rocketflyer.in/rfapi/secure/rb/store/coupon/validatePromo"
                apiUrl.url = url
                apiUrl.name = ApiType.VALIDATE_COUPON
                apiUrl.timeOut = 12

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.applyCoupon(this, httpManager,request, apiUrl)
            }*/
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

  /*  fun createOrder(httpManager: HttpManager, request: CreateOrderRequest?) {
        this.httpManager = httpManager
        CreateOrder(request).hitApi()
    }*/

   /* inner class CreateOrder( var request: CreateOrderRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleCreateOrderResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.CREATE_ORDER)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.CREATE_ORDER]!!
                var apiUrl = Api()
                apiUrl.url = oldApi.url
                apiUrl.name = ApiType.CREATE_ORDER
                apiUrl.timeOut = oldApi.timeOut

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.createOrder(this, httpManager,request, apiUrl)
            } else {
                var apiUrl = Api()
                var url = "https://qa2.rocketflyer.in/rfapi/secure/rb/store/order/create"
                apiUrl.url = url
                apiUrl.name = ApiType.CREATE_ORDER
                apiUrl.timeOut = 12

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.createOrder(this, httpManager,request, apiUrl)
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
    }*/

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

}