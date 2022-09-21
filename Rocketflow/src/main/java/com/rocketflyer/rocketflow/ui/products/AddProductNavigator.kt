package com.rocketflyer.rocketflow.ui.products

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface AddProductNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSendImageResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUnitsResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?)
}