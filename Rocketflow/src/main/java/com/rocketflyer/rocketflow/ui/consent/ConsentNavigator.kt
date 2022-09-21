package com.rocketflyer.rocketflow.ui.consent

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 16/5/19
 */
interface ConsentNavigator : BaseNavigator {
    fun onDeclineClick()
    fun onAgreeClick()

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }
}