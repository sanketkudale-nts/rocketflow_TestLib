package com.tracki.ui.leave.leave_summary


import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class LeaveSummaryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<LeaveSummaryNavigator>(dataManager, schedulerProvider)
{

    private var httpManager: HttpManager? = null



    fun fetchLeaves(httpManager: HttpManager)
    {
        this.httpManager = httpManager;
        LeaveSummary().hitApi();
    }

    inner class LeaveSummary() : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
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


}