package com.rocketflyer.rocketflow.ui.webview

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 23/10/18
 */
interface WebViewNavigator : BaseNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}