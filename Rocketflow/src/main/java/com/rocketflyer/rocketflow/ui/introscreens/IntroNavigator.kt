package com.rocketflyer.rocketflow.ui.introscreens

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 22/2/19
 */
interface IntroNavigator : BaseNavigator {
    fun skipClicked()
    fun nextClicked()
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}