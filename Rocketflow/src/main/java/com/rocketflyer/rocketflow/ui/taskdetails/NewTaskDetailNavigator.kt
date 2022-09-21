package com.rocketflyer.rocketflow.ui.taskdetails

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface NewTaskDetailNavigator :BaseNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)
    fun expandCollapse()
}