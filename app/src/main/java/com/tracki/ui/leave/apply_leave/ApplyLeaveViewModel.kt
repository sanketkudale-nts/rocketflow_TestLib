package com.tracki.ui.leave.apply_leave


import androidx.databinding.ObservableField
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.PunchInOut
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.LeaveType
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider


class ApplyLeaveViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<ApplyLeaveNavigator>(dataManager, schedulerProvider) {

    private var httpManager: HttpManager? = null
    private var event: PunchInOut? = null
    private var api: Api? = null
    public var isDataVisible =ObservableField(false)


    fun onClickSubmit()
    {
        navigator.onClickSubmit();
    }

    fun stringToEnum(str: String): LeaveType? {
        when (str) {
            "Privilege Leave" -> return LeaveType.PRIVILEGE_LEAVE
            "Sick Leave" -> return LeaveType.SICK_LEAVE
            "Casual Leave" -> return LeaveType.CASUAL_LEAVE
            else -> return null
        }
    }

    fun applyLeave(httpManager: HttpManager, data: Any?) {
        this.httpManager = httpManager
        this.event = event
        ApplyLeave(data).hitApi()
    }




    inner class ApplyLeave(var data: Any?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.APPLY_LEAVE]
            dataManager.applyLeave(this, httpManager, data, apiUrl)
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

    fun applyLeaveEdit(httpManager: HttpManager, data: Any?) {
        this.httpManager = httpManager
        this.event = event
        ApplyLeaveEdit(data).hitApi()
    }


    inner class ApplyLeaveEdit(var data: Any?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleEditResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.EDIT_LEAVE]
            dataManager.applyLeaveEdit(this, httpManager, data, apiUrl)
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

    fun onPageLoad(httpManager: HttpManager)
    {
        this.httpManager = httpManager
        PageLoad().hitApi()
    }


    inner class PageLoad() : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            isDataVisible.set(true)
            navigator.handleLeaveSummaryResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.LEAVE_SUMMARY]
            dataManager.getLeaveSummary(this, httpManager, apiUrl)
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

    fun getServerTime(httpManager: HttpManager) {
        this.httpManager = httpManager
        GetServerTime().hitApi()
    }




    inner class GetServerTime() : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleServerTimeResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.SERVER_TIME]
            dataManager.getServerTime(this, httpManager, apiUrl)
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

    fun getServerOnSubmitTime(httpManager: HttpManager) {
        this.httpManager = httpManager
        GetServerOnSubmitTime().hitApi()
    }




    inner class GetServerOnSubmitTime() : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleServerTimeOnSubmitResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.SERVER_TIME]
            dataManager.getServerTime(this, httpManager, apiUrl)
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


