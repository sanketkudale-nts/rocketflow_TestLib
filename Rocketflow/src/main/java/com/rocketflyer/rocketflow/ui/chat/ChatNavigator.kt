package com.rocketflyer.rocketflow.ui.chat

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface ChatNavigator : BaseNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }
    fun handleImageResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)

}