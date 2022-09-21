package com.tracki.ui.newdynamicform

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface NewDynamicNavigator :BaseNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }
    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)

    fun  handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
}