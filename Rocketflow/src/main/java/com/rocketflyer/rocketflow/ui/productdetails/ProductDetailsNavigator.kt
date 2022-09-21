package com.rocketflyer.rocketflow.ui.productdetails

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface ProductDetailsNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handStockEntryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?)
}