package com.rocketflyer.rocketflow.ui.introscreens.introfragment

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 22/2/19
 */
interface IntroScreenNavigator : BaseNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}