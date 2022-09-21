package com.rocketflyer.rocketflow.ui.attendance

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.EmployeeListAttendanceRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 22/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class EmployeeListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<EmployeeListNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getEmployeeList(httpManager: HttpManager, request: EmployeeListAttendanceRequest) {
        this.httpManager = httpManager
        GetEmployeeList(request).hitApi()
    }

    inner class GetEmployeeList(private var data: EmployeeListAttendanceRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiApplication.getApiMap()[ApiType.TEAM_ATTENDANCES]!!
            dataManager.employeeListInEvents(this, httpManager, data, api)
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