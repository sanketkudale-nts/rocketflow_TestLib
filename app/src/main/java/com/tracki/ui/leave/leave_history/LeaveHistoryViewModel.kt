package com.tracki.ui.leave.leave_history


import androidx.databinding.ObservableField
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class LeaveHistoryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<LeaveHistoryNavigator>(dataManager, schedulerProvider) {
    var httpManager:HttpManager?=null
    var isDataVisible = ObservableField(false)

    fun onClickSearch()
    {
       // navigator.onClickSearchHistory()
    }

    fun getLeaveHistory(httpManager: HttpManager, data : Any) {
        this.httpManager = httpManager;
        MyLeaves(data).hitApi()

    }



    inner class MyLeaves (var data: Any?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.LEAVE_HISTORY)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.LEAVE_HISTORY]
                if(dataManager!=null)
                dataManager.getMyLeaves(this, httpManager, data, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }


    fun cancelLeave(httpManager: HttpManager, data : Any) {
        this.httpManager = httpManager;
        CancelLeave(data).hitApi()

    }


    inner class CancelLeave (var data: Any?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleCancelResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.CANCEL_LEAVE)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.CANCEL_LEAVE]
                if (dataManager != null)
                    dataManager.cancelLeave(this, httpManager, data, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }

    fun fetchLeaves(httpManager: HttpManager,userId:String?)
    {
        this.httpManager = httpManager;
        LeaveSummary(userId).hitApi();
    }

    inner class LeaveSummary(var userId: String?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                isDataVisible.set(true)
                navigator.handleLeaveSummaryResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.LEAVE_SUMMARY)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.LEAVE_SUMMARY]
                val api = Api()
                if(userId!=null) {
                    api.url = apiUrl!!.url+"?uid="+userId
                }else{
                    api.url=apiUrl!!.url
                }
                api.name = ApiType.LEAVE_SUMMARY
                api.timeOut = apiUrl.timeOut
                if(dataManager!=null)
                dataManager.getLeaveSummary(this, httpManager, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }

}