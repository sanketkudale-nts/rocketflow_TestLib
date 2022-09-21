package com.tracki.ui.setting

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 16/10/18
 */
interface SettingNavigator : BaseNavigator {
    fun openEmergencyContactActivity()
    fun openEmergencyMessageActivity()
    fun sendLocalDbToServerActivity()
    fun handleSaveSettingResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)
}