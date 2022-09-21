package com.tracki.ui.userlisting

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface UserListNewNavigator  : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)

}