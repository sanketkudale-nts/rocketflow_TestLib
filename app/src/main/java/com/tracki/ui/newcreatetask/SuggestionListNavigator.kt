package com.tracki.ui.newcreatetask

import com.tracki.data.model.response.config.ClientData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface SuggestionListNavigator :BaseNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
     fun returnList(callback: ApiCallback, result: Any?, error: APIError?):List<ClientData>?
}