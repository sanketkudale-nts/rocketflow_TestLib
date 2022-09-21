package com.rocketflyer.rocketflow.ui.attendance.teamattendance
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 26/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface TeamAttendanceNavigator :BaseNavigator {

    fun handleTeamAttendanceResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
}