package com.tracki.ui.payouts

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 11/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface AdminUserPayoutsNavigator : BaseNavigator {
    fun viewDetails()
    fun search()
    fun selectDateRange()
    fun checkBuddyResponse(callback: ApiCallback?, result: Any?, error: APIError?)

}