package com.tracki.ui.emergencymessage

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 20/2/19
 */
interface EmergencyMessageNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}