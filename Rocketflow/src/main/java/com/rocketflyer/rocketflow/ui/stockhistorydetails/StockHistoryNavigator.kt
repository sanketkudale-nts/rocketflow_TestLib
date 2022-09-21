package com.rocketflyer.rocketflow.ui.stockhistorydetails

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface StockHistoryNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handStockHistoryDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?)
}