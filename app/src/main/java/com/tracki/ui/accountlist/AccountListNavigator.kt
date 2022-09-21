package com.tracki.ui.accountlist

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 16/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface AccountListNavigator:BaseNavigator {
    fun login()
    fun handleConfigResponse(config: ApiCallback?, result: Any?, error: APIError?)

}