package com.tracki.ui.cart

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface CartNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleCartResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleCreateOrderResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleApplyCouponResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun linkInventoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
}