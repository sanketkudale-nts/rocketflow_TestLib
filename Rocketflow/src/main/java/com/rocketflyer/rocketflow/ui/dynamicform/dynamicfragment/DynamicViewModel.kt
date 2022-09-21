package com.rocketflyer.rocketflow.ui.dynamicform.dynamicfragment

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.OtpRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Created by Rahul Abrol on 13/7/19.
 */
class DynamicViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<DynamicNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun executiveMap(position: Int,target: String , httpManager: HttpManager,rollId:String?) {
        this.httpManager = httpManager
        ExecutiveMap(position,target,rollId).hitApi()
    }

    private inner class ExecutiveMap(val position: Int,var target :String,var rollId:String?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleExecutiveMapResponse(position, this, result, error)
        }

        override fun hitApi() {
            var apiMap=ApiType.valueOf(target)
            //val api = TrackiApplication.getApiMap()[ApiType.EXECUTIVE_MAP]

            val api = TrackiApplication.getApiMap()[apiMap]
            if(rollId!=null){
                var queryString="?roleIds="+ URLEncoder.encode(rollId, StandardCharsets.UTF_8.toString())
                var apiNew=Api()
                apiNew.url=api!!.url+queryString
                apiNew.name=api.name
                apiNew.timeOut=api!!.timeOut
                apiNew.cacheable=api!!.cacheable
                dataManager.executeMap(this@ExecutiveMap, httpManager, apiNew)

            }else{
                dataManager.executeMap(this@ExecutiveMap, httpManager, api)
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

    fun getTaskData( httpManager: HttpManager, data:Any?) {
        this.httpManager = httpManager
        GetTaskData(data).hitApi()
    }


    fun getPreviousFormData( httpManager: HttpManager, data:Any?) {
        this.httpManager = httpManager
        GetPreviousFormData(data).hitApi()
    }

    private inner class GetPreviousFormData(var data:Any?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleGetPreviousFormResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_DATA]
                if(dataManager!=null)
                dataManager.taskData(this, httpManager, data, api)
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

    private inner class GetTaskData(var data:Any?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleGetTaskDataResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_TASK_DATA)) {
                val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_DATA]
                if(dataManager!=null)
                dataManager.taskData(this, httpManager, data, api)
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

    fun uploadFileList(hashMap: HashMap<String, ArrayList<File>>, httpManager: HttpManager) {
        this.httpManager = httpManager
        UploadFiles(hashMap).hitApi()
    }

        inner class UploadFiles(val hashMap: HashMap<String, ArrayList<File>>) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleUploadFileResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
            dataManager.uploadFiles(this@UploadFiles, httpManager, hashMap, api)
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

    fun verifyOtpServerRequest(otpRequest: OtpRequest, httpManager: HttpManager?) {
        this.httpManager = httpManager!!
        VerifyOtp(otpRequest).hitApi()
    }

    inner class VerifyOtp(val otpRequest: OtpRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleUploadFileResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.VERIFY_MOBILE)) {
                val api = TrackiApplication.getApiMap()[ApiType.VERIFY_MOBILE]
                if(dataManager!=null)
                dataManager.verifyOtp(httpManager, this, otpRequest, api)
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