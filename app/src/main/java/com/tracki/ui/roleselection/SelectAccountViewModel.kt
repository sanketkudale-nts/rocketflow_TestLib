package com.tracki.ui.roleselection

import com.tracki.data.DataManager
import com.tracki.data.model.request.AccountListRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

open class SelectAccountViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<SelectAccountNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api


    fun getAccountList(httpManager: HttpManager, api: Api, data: AccountListRequest) {
        this.httpManager = httpManager
        this.api = api
        GetAccountList(data).hitApi()
    }

    /**
     *
     */
    inner class GetAccountList(var data: AccountListRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleSelectAccountResponse(this@GetAccountList, result, error)
        }

        override fun hitApi() {
            dataManager.getAccountList(this@GetAccountList, httpManager, data, api)
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }


}