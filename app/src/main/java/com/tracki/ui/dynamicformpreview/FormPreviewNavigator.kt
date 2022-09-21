package com.tracki.ui.dynamicformpreview

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface FormPreviewNavigator : BaseNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }
}