package com.tracki.ui.myDocument

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface DocumentNavigator: BaseNavigator {

    fun handleDocumentResponse(callback: ApiCallback?,result: Any?, error: APIError?)
    fun handleDeleteResponse(callback: ApiCallback?,result: Any?,error: APIError?)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
}