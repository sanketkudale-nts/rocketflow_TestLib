package com.rocketflyer.rocketflow.ui.emergencyphone

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 5/12/18
 */
interface EmergencyContactNavigator : BaseNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    fun handleSaveSettingResponse(callback: ApiCallback, result: Any?, error: APIError?)
}