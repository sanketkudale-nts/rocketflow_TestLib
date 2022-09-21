package com.tracki.ui.dynamicform

import com.tracki.data.DataManager
import com.tracki.data.model.request.DynamicFormMainData
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider
import java.io.File

/**
 * Created by rahul on 20/3/19
 */
class DynamicFormViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<DynamicFormNavigator>(dataManager, schedulerProvider) {

    /*  private var httpManager: HttpManager? = null
      private var api: Api? = null

      fun onProceedClick() {
          navigator.onSubmitClick()
      }*/

    private var httpManager: HttpManager? = null
    private var api: Api? = null


    fun uploadTaskData(dynamicFormMainData: DynamicFormMainData, httpManager: HttpManager, api: Api?) {
        this.httpManager = httpManager
        this.api = api
        UploadFormList(dynamicFormMainData).hitApi()
    }


    inner class UploadFormList(val dynamicFormMainData: DynamicFormMainData) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(dataManager!=null)
            dataManager.uploadFormList(this@UploadFormList, httpManager, dynamicFormMainData, api)
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

    fun uploadFileList(hashMap: HashMap<String, ArrayList<File>>, httpManager: HttpManager, api: Api?,isApi:Boolean) {
        this.httpManager = httpManager
        this.api = api!!
        UploadFiles(hashMap,isApi).hitApi()
    }

    inner class UploadFiles(val hashMap: HashMap<String, ArrayList<File>>,var isApi:Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(isApi) {
                if(navigator!=null)
                navigator.upLoadFileApiResponse(this, result, error)
            }
            else {
                if(navigator!=null)
                navigator.upLoadFileDisposeApiResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null)
            dataManager.uploadFiles(this@UploadFiles, httpManager, hashMap, api)
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