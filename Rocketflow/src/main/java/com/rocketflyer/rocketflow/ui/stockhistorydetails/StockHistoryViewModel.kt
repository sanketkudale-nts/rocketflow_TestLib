package com.rocketflyer.rocketflow.ui.stockhistorydetails

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.productdetails.StockEntryRequest
import com.tracki.ui.selectorder.PaginationRequest
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class StockHistoryViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<StockHistoryNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager

    fun getStockHistory(
        httpManager: HttpManager,
        pid: String?,
        paginationData: PaginationRequest,
        day:Long
    ) {
        this.httpManager = httpManager
        GetStockEntry(pid,paginationData,day).hitApi()
    }

    inner class GetStockEntry(
        var pid: String?,
        var paginationData: PaginationRequest,
        var day:Long,

    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handStockHistoryDetailsResponse(this, result, error)
        }

        override fun hitApi() {
            var data= StockEntryRequest()
            data.limit=paginationData.limit
            data.offset=paginationData.offset
            data.day=day
            data.pid=pid
            if (TrackiApplication.getApiMap().containsKey(ApiType.STOCK_HISTORY_DETAIL)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.STOCK_HISTORY_DETAIL]!!
                var apiUrl = Api()
                if (pid != null) {
                    apiUrl.url =
                        oldApi.url!!
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.STOCK_HISTORY_DETAIL
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getStockEntry(this,data, httpManager, apiUrl)
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