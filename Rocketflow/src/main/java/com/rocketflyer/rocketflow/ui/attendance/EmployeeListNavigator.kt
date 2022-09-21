package com.rocketflyer.rocketflow.ui.attendance

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 22/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface EmployeeListNavigator  : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

}