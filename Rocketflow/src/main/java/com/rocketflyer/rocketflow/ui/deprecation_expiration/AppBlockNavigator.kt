package com.rocketflyer.rocketflow.ui.deprecation_expiration

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface AppBlockNavigator : BaseNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}