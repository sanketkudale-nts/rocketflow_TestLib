package com.rocketflyer.rocketflow.ui.productdetails

import androidx.annotation.Nullable
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.PaginationData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.selectorder.PaginationRequest
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class ProductDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<ProductDetailsNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
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


    fun getStockEntry(
        httpManager: HttpManager,
        pid: String?,
        paginationData: PaginationRequest,
        starDate: Long,
        endDate: Long,
        action: String? = null,
        geoId :String?
    ) {
        this.httpManager = httpManager
        GetStockEntry(pid, paginationData, starDate, endDate, action, geoId).hitApi()
    }

    inner class GetStockEntry(
        var pid: String?,
        var paginationData: PaginationRequest,
        var starDate: Long,
        var endDate: Long,
        var action: String?,
        var geoId: String?
    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handStockEntryResponse(this, result, error)
        }

        override fun hitApi() {
            var data = StockEntryRequest()
            data.limit = paginationData.limit
            data.offset = paginationData.offset
            data.start = starDate
            data.end = endDate
            data.action = action
            data.pid = pid
            data.geoId = geoId
            if (TrackiApplication.getApiMap().containsKey(ApiType.DAILY_STOCK_HISTORY)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.DAILY_STOCK_HISTORY]!!
                var apiUrl = Api()
                if (pid != null) {
                    apiUrl.url =
                        oldApi.url!!
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.DAILY_STOCK_HISTORY
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getStockEntry(this, data, httpManager, apiUrl)
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