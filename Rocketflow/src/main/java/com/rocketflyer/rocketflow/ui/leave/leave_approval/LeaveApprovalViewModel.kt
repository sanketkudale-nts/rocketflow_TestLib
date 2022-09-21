package com.rocketflyer.rocketflow.ui.leave.leave_approval


import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class LeaveApprovalViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<LeaveApprovalNavigator>(dataManager, schedulerProvider) {

    var httpManager:HttpManager?=null

    fun onClickSearch()
    {
       // navigator.onClickSearchApproval()
    }

    fun getLeaveApprovalData(httpManager: HttpManager, data : Any) {
        this.httpManager = httpManager;
        LeaveApproval(data).hitApi()

    }



    inner class LeaveApproval (var data: Any?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.APPROVALS]
            dataManager.getLeaveRequests(this, httpManager, data, apiUrl)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }

    fun rejectLeave(httpManager: HttpManager, data : Any) {
        this.httpManager = httpManager;
        RejectLeave(data).hitApi()

    }


    inner class RejectLeave (var data: Any?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleRejectResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.REJECT_LEAVE]
            dataManager.rejectLeave(this, httpManager, data, apiUrl)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }



    fun approveLeave(httpManager: HttpManager, data : Any) {
        this.httpManager = httpManager;
        ApproveLeave(data).hitApi()

    }

    inner class ApproveLeave (var data: Any?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleApproveResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.APPROVE_LEAVE]
            dataManager.approveLeave(this, httpManager, data, apiUrl)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }

}