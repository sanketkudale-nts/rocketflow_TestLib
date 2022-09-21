package com.tracki.ui.wallet

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.TransactionRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 18/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TransactionViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<TransactionNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager



    fun getTransaction(httpManager: HttpManager, request: TransactionRequest) {
        this.httpManager = httpManager
        GetTransaction(request).hitApi()
    }

    inner class GetTransaction(private var data: TransactionRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleTransactionResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.WALLET_TRANSACTIONS)) {
                val api = TrackiApplication.getApiMap()[ApiType.WALLET_TRANSACTIONS]!!
                dataManager.linkInventory(this, httpManager, data, api)
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

    fun getBalance(httpManager: HttpManager) {
        this.httpManager = httpManager
        GetBalance().hitApi()
    }

    inner class GetBalance() : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_WALLET_BALANCE)) {
                val api = TrackiApplication.getApiMap()[ApiType.GET_WALLET_BALANCE]!!
                dataManager.getBalance(this, httpManager, api)
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