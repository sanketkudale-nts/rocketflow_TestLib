package com.tracki.ui.productlist

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface ProductListNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSubCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleTerminalCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateProductStatusResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleDeleteProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)


}