package com.tracki.ui.placelist

import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.addplace.Hub
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.CommonUtils
import com.tracki.utils.rx.SchedulerProvider

open class MyPlacesViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<MyPlaceNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api



    fun isViewNullOrEmpty(string: String): Boolean {
        return CommonUtils.isViewNullOrEmpty(string)
    }

    fun isMobileValid(string: String): Boolean {
        return CommonUtils.isMobileValid(string)
    }

     open  fun addPlacesActivity(){
         navigator.openAddPlaceActivity()

     }

    fun getLocation(httpManager: HttpManager, api: Api) {
        this.httpManager = httpManager
        this.api = api
        GetLocation().hitApi()
    }

    fun deleteUserLocation(httpManager: HttpManager, api: Api,hub: Hub) {
        this.httpManager = httpManager
        this.api = api
        DeleteLocation(hub).hitApi()
    }



    /**
     *Get Current Location List
     */
    inner class GetLocation : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleMyPlaceResponse(this@GetLocation, result, error)
        }

        override fun hitApi() {
            dataManager.getUserLocation(this@GetLocation, httpManager,  api)
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



    /**
     *Get Current Location List
     */
    inner class DeleteLocation(var hub: Hub) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleDeleteMyPlaceResponse(this@DeleteLocation, result, error)
        }

        override fun hitApi() {
            dataManager.deleteUserLocation(this@DeleteLocation, httpManager,api,  hub)
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