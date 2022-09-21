package com.tracki.ui.myInventory

import com.tracki.data.DataManager
import com.tracki.data.model.request.TransactionRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.wallet.TransactionNavigator
import com.tracki.utils.rx.SchedulerProvider

class MyInventoryViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<TransactionNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager

    fun getDocument(httpManager: HttpManager, request: TransactionRequest) {
        this.httpManager = httpManager
        GetDocuments(request).hitApi()
    }

    inner class GetDocuments(private var data: TransactionRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleTransactionResponse(this, result, error)
        }

        override fun hitApi() {
            /*val api = TrackiApplication.getApiMap()[ApiType.WALLET_TRANSACTIONS]!!
            dataManager.linkInventory(this, httpManager, data, api)*/
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