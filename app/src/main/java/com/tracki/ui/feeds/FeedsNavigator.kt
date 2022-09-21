package com.tracki.ui.feeds

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 04/01/21.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface FeedsNavigator  : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
     fun handleCommentsResponse(callback: ApiCallback, result: Any?, error: APIError?)
     fun handlePostCommentsResponse(callback: ApiCallback, result: Any?, error: APIError?)

}