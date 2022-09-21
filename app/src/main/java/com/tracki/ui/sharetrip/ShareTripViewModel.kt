package com.tracki.ui.sharetrip

import com.tracki.data.DataManager
import com.tracki.data.model.request.ShareTrip
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 1/3/19
 *
 * Class used to share your live trip with selected time slot and after that  given time  the link expired.
 */
class ShareTripViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<ShareTripNavigator>(dataManager, schedulerProvider), ApiCallback {

    lateinit var httpManager: HttpManager
    lateinit var request: ShareTrip
    lateinit var api: Api

    fun getSharableLink(httpManager: HttpManager, api: Api, shareTrip: ShareTrip) {
        this.httpManager = httpManager
        this.api = api
        this.request = shareTrip
        hitApi()
    }

    override fun onResponse(result: Any?, error: APIError?) {
        navigator.handleResponse(this, result, error)
    }

    override fun hitApi() {
        dataManager.getSharableLink(this, httpManager, request, api)
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

