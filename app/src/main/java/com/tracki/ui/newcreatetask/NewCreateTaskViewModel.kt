package com.tracki.ui.newcreatetask

import android.view.View
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.*
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.*
import com.tracki.utils.rx.SchedulerProvider
import java.io.File
import java.util.*

open class NewCreateTaskViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<NewCreateTaskNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api
    private lateinit var createTaskRequest: CreateTaskRequest

    fun onAssignNowClicked() {
        navigator.onAssignNowClicked()
    }

    fun selectDateTime(view: View) {
        navigator.selectDateTime(view)
    }

    fun isViewNullOrEmpty(string: String): Boolean {
        return CommonUtils.isViewNullOrEmpty(string)
    }

    fun isMobileValid(string: String): Boolean {
        return CommonUtils.isMobileValid(string)
    }

    fun selectLocation(view: View) {
        navigator.openPlaceAutoComplete(view)
    }

    fun createTaskApi(httpManager: HttpManager, createTaskRequest: CreateTaskRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        this.createTaskRequest = createTaskRequest
        CreateTask().hitApi()
    }

    fun onPointOfContactClick() {
        navigator.pointOfContactClicked()
    }


    /**
     *
     */
    inner class CreateTask : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleResponse(this@CreateTask, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.createTask(this@CreateTask, httpManager, createTaskRequest, api)
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }
    }

    inner class UpdateTask : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleUpdateResponse(this@UpdateTask, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.updateTask(this@UpdateTask, httpManager, createTaskRequest, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }

    }

    fun updateTaskApi(httpManager: HttpManager, createTaskRequest: CreateTaskRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        this.createTaskRequest = createTaskRequest
        UpdateTask().hitApi()
    }




    open fun checkFleet(httpManager: HttpManager?) {
        this.httpManager = httpManager!!
        FleetListingAPI().hitApi()
    }

    open fun checkBuddy(httpManager: HttpManager?) {
        this.httpManager = httpManager!!
        BuddyListingAPI().hitApi()
    }
    inner class BuddyListingAPI  : ApiCallback {
        private val buddyRequest: BuddiesRequest
        private val api: Api?
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.checkBuddyResponse(this@BuddyListingAPI, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.buddyListing(this@BuddyListingAPI, httpManager, api, buddyRequest)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {

        }
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {}

        init {
            val statusList: MutableList<BuddyStatus> = ArrayList()
            statusList.add(BuddyStatus.ON_TRIP)
            statusList.add(BuddyStatus.OFFLINE)
            statusList.add(BuddyStatus.IDLE)
            buddyRequest = BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, true)
            api = TrackiApplication.getApiMap()[ApiType.BUDDIES]
        }
    }

    inner class FleetListingAPI : ApiCallback {
        var fleetRequest = FleetRequest("ALL")
        var api = TrackiApplication.getApiMap()[ApiType.FLEETS]
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.checkFleetResponse(this@FleetListingAPI, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.fleetListing(this@FleetListingAPI, httpManager, fleetRequest, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {}
    }

    /////
    fun uploadTaskData(dynamicFormMainData: DynamicFormMainData, httpManager: HttpManager, api: Api?) {
        this.httpManager = httpManager
        this.api = api!!
        UploadFormList(dynamicFormMainData).hitApi()
    }


    inner class UploadFormList(val dynamicFormMainData: DynamicFormMainData) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null) {
                dataManager.uploadFormList(this@UploadFormList, httpManager, dynamicFormMainData, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }

    }

    fun uploadFileList(hashMap: HashMap<String, ArrayList<File>>, httpManager: HttpManager, api: Api?,isApi:Boolean) {
        this.httpManager = httpManager
        this.api = api!!
        UploadFiles(hashMap,isApi).hitApi()
    }

    inner class UploadFiles(val hashMap: HashMap<String, ArrayList<File>>,var isApi: Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(isApi) {
                if(navigator!=null) {
                    navigator.upLoadFileApiResponse(this, result, error)
                }
            } else{
                if(navigator!=null) {
                    navigator.upLoadFileDisposeApiResponse(this, result, error)
                }
            }
        }

        override fun hitApi() {
            dataManager.uploadFiles(this@UploadFiles, httpManager, hashMap, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }

    }

    fun getRegionList(httpManager: HttpManager,isStart: Boolean, regionRequest: RegionRequest) {
        this.httpManager = httpManager
        RegionList(isStart,regionRequest).hitApi()
    }
    /**
     *
     */
    inner class RegionList(var isStart: Boolean,var regionRequest: RegionRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleRegionListResponse(this@RegionList, result, error,isStart)
            }
        }

        override fun hitApi() {
            var api =TrackiApplication.getApiMap()[ApiType.GET_REGIONS]
            if(api!=null) {
                if(dataManager!=null) {
                    dataManager.getRegionList(this@RegionList, httpManager, regionRequest, api)
                }
            }else{
                Log.e("message","GET_REGIONS api is null")
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }
    }
    fun getStateList(httpManager: HttpManager,getManualLocationRequest: GetManualLocationRequest,isStart: Boolean) {
        this.httpManager = httpManager
        StateList(getManualLocationRequest,isStart).hitApi()
    }
    /**
     *
     */
    inner class StateList(var manualLocationRequest: GetManualLocationRequest,var isStart: Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleStateListResponse(this@StateList, result, error,isStart)
            }
        }

        override fun hitApi() {
            var api =TrackiApplication.getApiMap()[ApiType.GET_STATES]
            if(api!=null){
                if(dataManager!=null) {
                    dataManager.getStateList(this@StateList, httpManager, manualLocationRequest, api)
                }
            }else{
                Log.e("message","GET_STATES api is null")
            }

        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }
    }
    fun getCityList(httpManager: HttpManager,getManualLocationRequest: GetManualLocationRequest, isStart: Boolean) {
        this.httpManager = httpManager
        CityList(getManualLocationRequest,isStart).hitApi()
    }
    /**
     *
     */
    inner class CityList(var manualLocationRequest: GetManualLocationRequest,var isStart: Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleCityListResponse(this@CityList, result, error,isStart)
            }
        }

        override fun hitApi() {
            var api =TrackiApplication.getApiMap()[ApiType.GET_CITIES]
            if(api!=null) {
                if(dataManager!=null) {
                    dataManager.getCityList(this@CityList, httpManager, manualLocationRequest, api)
                }
            }else{
                Log.e("message","GET_CITIES api is null")
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }
    }

    fun getHubList(httpManager: HttpManager,getManualLocationRequest: GetManualLocationRequest, isStart: Boolean) {
        this.httpManager = httpManager
        HubList(getManualLocationRequest,isStart).hitApi()
    }
    inner class HubList(var manualLocationRequest: GetManualLocationRequest,var isStart: Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handleHubListResponse(this@HubList, result, error,isStart)
            }
        }

        override fun hitApi() {
            var api =TrackiApplication.getApiMap()[ApiType.GET_HUBS]
            if(api!=null){
                if(dataManager!=null) {
                    dataManager.getHubList(this@HubList, httpManager, manualLocationRequest, api)
                }
            }
            else{
                Log.e("message","GET_HUBS api is null")
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {
        }
    }
}