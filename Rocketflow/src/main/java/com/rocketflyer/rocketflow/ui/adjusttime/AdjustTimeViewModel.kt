package com.rocketflyer.rocketflow.ui.adjusttime
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseNavigator
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.leave.apply_leave.ApplyLeaveNavigator
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class AdjustTimeViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<AdjustTimeNavigator>(dataManager, schedulerProvider) {

    private var httpManager: HttpManager? = null




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


}