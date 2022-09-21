package com.rocketflyer.rocketflow.ui.userdetails

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface UserAddressNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleDeleteResponse(callback: ApiCallback, result: Any?, error: APIError?)

}