package com.tracki.ui.feeddetails

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class FeedDetailsViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<FeedDetailsNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getFeedsDetails(httpManager: HttpManager, feedId: String) {
        this.httpManager = httpManager
        GetFeedDetails(feedId).hitApi()
    }

    inner class GetFeedDetails(private var feedId: String) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_POST_DETAIL)) {
                val apiMain = TrackiApplication.getApiMap()[ApiType.GET_POST_DETAIL]!!
                val api = Api()
                api.name = ApiType.GET_POST_DETAIL
                api.timeOut = apiMain.timeOut
                if(apiMain.url?.contains("{id}")!!){
                    api.url = "${apiMain.url!!.replace("{id}",feedId)}"
                }else{
                    api.url = "${apiMain.url}/${feedId}"
                }


                if(dataManager!=null){
                    dataManager.getFeedDetails(this, httpManager, api)
                }

            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

}