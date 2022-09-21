package com.rocketflyer.rocketflow.ui.roleselection

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator
import com.tracki.utils.NextScreen

interface TaskSelectionNavigator : BaseNavigator {

    fun handleInitiateSignUpResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleConfigResponse(callback: ApiCallback, result: Any?, error: APIError?, nextScreen: NextScreen?, sdkAccessId: String?)

    fun handlePunchInPunchOutResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?, nextScreen: NextScreen?)
}