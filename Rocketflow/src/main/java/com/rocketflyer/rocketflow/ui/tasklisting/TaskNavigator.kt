package com.rocketflyer.rocketflow.ui.tasklisting

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 8/10/18
 */
interface TaskNavigator : BaseNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    fun checkBuddyResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    fun checkFleetResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    fun checkInventory(callback: ApiCallback?, result: Any?, error: APIError?)
}