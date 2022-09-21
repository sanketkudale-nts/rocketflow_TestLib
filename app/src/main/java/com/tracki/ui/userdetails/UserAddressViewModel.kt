package com.tracki.ui.userdetails

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.PlaceRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class UserAddressViewModel  (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<UserAddressNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager



    fun getUserAddress(httpManager: HttpManager, userId:String?) {
        this.httpManager = httpManager
        GetUserAddress(userId).hitApi()
    }

    inner class GetUserAddress(var userId: String?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.USER_ADDRESS_GET)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.USER_ADDRESS_GET]!!
                val api = Api()
                if(userId!=null) {
                    api.url = oldApi.url+"?userId="+userId
                }else{
                    api.url=oldApi.url
                }
                api.name = ApiType.USER_ADDRESS_GET
                api.timeOut = oldApi.timeOut
                if(dataManager!=null)
                    dataManager.getUserAddress(this, httpManager, api)
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





    fun deleteAddress(httpManager: HttpManager,placeId:String?) {
        this.httpManager = httpManager
        DeleteAddress(placeId).hitApi()
    }

    inner class DeleteAddress(var placeId: String?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleDeleteResponse(this, result, error)
        }

        override fun hitApi() {
            var placeRequest=PlaceRequest()
            placeRequest.placeId=placeId
            if(TrackiApplication.getApiMap().containsKey(ApiType.USER_ADDRESS_DELETE)) {
                val api = TrackiApplication.getApiMap()[ApiType.USER_ADDRESS_DELETE]!!
                if(dataManager!=null)
                    dataManager.deleteAddress(this,placeRequest, httpManager, api)
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