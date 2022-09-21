package com.rocketflyer.rocketflow.ui.newcreatetask

import com.tracki.data.DataManager
import com.tracki.data.model.request.*
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.*
import com.tracki.utils.rx.SchedulerProvider

open class GetUserSuggestionListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<SuggestionListNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api


    fun clientSearchApi(httpManager: HttpManager, clientSearchRequest: ClientSearchRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        ClientSearch(clientSearchRequest).hitApi()
    }


    /**
     *
     */
    inner class ClientSearch(var clientSearchRequest: ClientSearchRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.returnList(this@ClientSearch, result, error)

        }

        override fun hitApi() {
            dataManager.getUserListForSuggestion(this@ClientSearch, httpManager, clientSearchRequest, api)
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