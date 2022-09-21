package com.rocketflyer.rocketflow.ui.attendance.teamattendance

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.TeamAttendanceRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 26/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TeamAttendanceViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<TeamAttendanceNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager




    fun getTeamAttendance(httpManager: HttpManager, request: TeamAttendanceRequest) {
        this.httpManager = httpManager
        GetTeamAttendance(request).hitApi()
    }

    inner class GetTeamAttendance(private var data: TeamAttendanceRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleTeamAttendanceResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap()[ApiType.TEAM_ATTENDANCE]!=null){
                val api = TrackiApplication.getApiMap()[ApiType.TEAM_ATTENDANCE]!!
                dataManager.teamAttendance(this, httpManager, data, api)
            }else{
                CommonUtils.showLogMessage("e","TEAM_ATTENDANCE Api is NUll=>","TEAM_ATTENDANCE Api is NUll=>")
            }

        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }
}