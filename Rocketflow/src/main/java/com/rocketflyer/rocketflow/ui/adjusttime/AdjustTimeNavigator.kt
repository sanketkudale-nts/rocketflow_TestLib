package com.rocketflyer.rocketflow.ui.adjusttime

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface AdjustTimeNavigator:BaseNavigator {
    fun handleServerTimeResponse(callback: ApiCallback, result: Any?, error: APIError?)
}