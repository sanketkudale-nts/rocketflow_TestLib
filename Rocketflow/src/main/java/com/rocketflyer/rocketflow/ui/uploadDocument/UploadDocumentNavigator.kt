package com.rocketflyer.rocketflow.ui.uploadDocument

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


interface UploadDocumentNavigator : BaseNavigator {

    fun handleSendImageResponse(callback: ApiCallback, result: Any?, error: APIError?)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

}