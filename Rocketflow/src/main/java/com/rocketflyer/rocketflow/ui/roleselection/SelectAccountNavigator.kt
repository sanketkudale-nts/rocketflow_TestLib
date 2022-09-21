package com.rocketflyer.rocketflow.ui.roleselection

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface SelectAccountNavigator: BaseNavigator {

    fun handleSelectAccountResponse(callback: ApiCallback, result: Any?, error: APIError?)
}