package com.rocketflyer.rocketflow.ui.myDocument

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.getDocsRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.selectorder.PaginationRequest
import com.tracki.utils.ApiType
import com.tracki.utils.Log
import com.tracki.utils.rx.SchedulerProvider

class MyDocumentViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<DocumentNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager

    fun getDocument(httpManager: HttpManager, request: getDocsRequest, paginationRequest: PaginationRequest?) {
        this.httpManager = httpManager
        GetDocuments(request,paginationRequest).hitApi()
    }

    fun deleteDocument(httpManager: HttpManager, docId: String){
        this.httpManager = httpManager
        DeleteDocument(docId).hitApi()

    }

    inner class DeleteDocument(private var docId: String) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleDeleteResponse(this, result, error)
        }

        override fun hitApi() {

            if (TrackiApplication.getApiMap().containsKey(ApiType.DELETE_DOCUMENT)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.DELETE_DOCUMENT]!!
                val api = Api()
                api.url = apiUrl.url + "?id=$docId"
                api.name = ApiType.DELETE_DOCUMENT
                api.timeOut = apiUrl.timeOut
                dataManager.deleteDocument(this, httpManager, api)
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

    inner class GetDocuments(private var data: getDocsRequest,var paginationRequest: PaginationRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {

            navigator.handleDocumentResponse(this, result, error)

        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_DOCUMENT_BY_TYPE)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.GET_DOCUMENT_BY_TYPE]!!

                val api = Api()
                if(data.type!=null&&paginationRequest!=null) {
                    api.url =
                        apiUrl.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&type=${data.type}"
                }else if(paginationRequest!=null) {
                    api.url =
                        apiUrl.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset
                }
                else if(data.type!=null){
                    api.url = apiUrl.url + "?type=${data.type}"
                }
                else{
                    api.url = apiUrl.url
                }
                Log.e("docs response", api.name.toString())
                api.name = ApiType.GET_DOCUMENT_BY_TYPE
                api.timeOut = apiUrl.timeOut

                dataManager.getDocumentByType(this, httpManager, api)
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
}