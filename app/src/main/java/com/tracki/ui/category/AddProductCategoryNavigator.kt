package com.tracki.ui.category

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface AddProductCategoryNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSendImageResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)
    fun handleUpdateProductCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?)

}