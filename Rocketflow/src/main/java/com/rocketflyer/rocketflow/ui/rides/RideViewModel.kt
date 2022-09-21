package com.rocketflyer.rocketflow.ui.rides

import androidx.databinding.ObservableField
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.GetTaskByDateRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.AppConstants
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by Rahul Abrol on 29/12/19.
 */
class RideViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<RideNavigator>(dataManager, schedulerProvider) {
    private lateinit var httpManager: HttpManager
    val totalRides = ObservableField<String>("0")
    val totalEarnings = ObservableField<String>(AppConstants.INR + " 0")

    fun getMyEarnings(httpManager: HttpManager, earningRequest: GetTaskByDateRequest) {
        this.httpManager = httpManager
        GetTaskByDate(earningRequest).hitApi()
    }

    inner class GetTaskByDate(private val earningRequest: GetTaskByDateRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiApplication.getApiMap()[ApiType.EXECUTIVE_PAYOUTS]!!
            dataManager.getTaskByDate(this, httpManager, earningRequest, api)
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