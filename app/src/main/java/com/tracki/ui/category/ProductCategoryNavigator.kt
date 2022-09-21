package com.tracki.ui.category

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface ProductCategoryNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateProductStatusCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleDeleteProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
}