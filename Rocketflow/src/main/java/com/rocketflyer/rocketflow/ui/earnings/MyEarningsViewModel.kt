package com.rocketflyer.rocketflow.ui.earnings

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.MyEarningRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.AppConstants
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by Rahul Abrol on 27/12/19.
 */
class MyEarningsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<MyEarningsNavigator>(dataManager, schedulerProvider) {

    val isTodayEarning = ObservableBoolean(true)
    val totalRides = ObservableField<String>("0")
    val totalEarnings = ObservableField<String>(AppConstants.INR + " 0")
    val dateRange = ObservableField<String>("Select Date Range")

    fun setDateRange(string: String) {
        dateRange.set(string)
    }

    fun selectDateRange() {
        navigator.selectDateRange()
    }

    fun viewDetails() {
        navigator.viewDetails()
    }

    fun search() {
        navigator.search()
    }

    private lateinit var httpManager: HttpManager

    fun getMyEarnings(httpManager: HttpManager, earningRequest: MyEarningRequest) {
        this.httpManager = httpManager
        GetMyEarnings(earningRequest).hitApi()
    }

    inner class GetMyEarnings(private val earningRequest: MyEarningRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiApplication.getApiMap()[ApiType.MY_EARNINGS]!!
            dataManager.getMyEarnings(this, httpManager, earningRequest, api)
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