package com.rocketflyer.rocketflow.ui.selectorder

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface SelectOrderNavigator :BaseNavigator{

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleCreateOrderResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun linkInventoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleHubListResponse(callback: ApiCallback, result: Any?, error: APIError?)
}