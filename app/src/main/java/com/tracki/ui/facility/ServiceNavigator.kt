package com.tracki.ui.facility

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator
import com.tracki.utils.NextScreen

interface ServiceNavigator : BaseNavigator {

    fun handleUpdateServiceResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleConfigResponse(callback: ApiCallback, result: Any?, error: APIError?, nextScreen: NextScreen?, sdkAccessId: String?)

    fun handlePunchInPunchOutResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?, nextScreen: NextScreen?)

    fun handleGetSavedServiceResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleUpdateSavedServiceResponse(callback: ApiCallback, result: Any?, error: APIError?)

}