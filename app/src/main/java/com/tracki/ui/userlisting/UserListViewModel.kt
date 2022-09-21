package com.tracki.ui.userlisting

import android.text.InputType
import android.util.Log
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.AttendanceReq
import com.tracki.data.model.request.UserGetRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class UserListViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<UserListNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getUserList(httpManager: HttpManager,roleId:String?,type:String?,attendanceReq: AttendanceReq?) {
        this.httpManager = httpManager
        GetUserList(roleId,type,attendanceReq).hitApi()
    }

    inner class GetUserList(var roleId: String?,var type: String?,var attendanceReq: AttendanceReq?, var new: Boolean = false) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_USERS)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.GET_USERS]!!
                val userGetRequest = UserGetRequest()
                val api = oldApi
                Log.e("urlCheck", "$new")
                if (new) {
                    Log.e("urlCheck", "${oldApi.url}")
                    val array = roleId?.split(",")?.toTypedArray()
                    val arrayList = ArrayList<String>()
                    if (array != null) {

                        for (item in array){
                            arrayList.add(item)
                        }
                    }
                    userGetRequest.roleIds = arrayList
                } else {

                    if (roleId != null && roleId != "-1") {
                        //api.url = oldApi.url+"?roleId="+roleId
                        //https://qa2.rocketflyer.in/rfapi/secure/tracki/user/get?roleId=ad14fa67-d27f-4762-b3e3-48d58a45365f&offset=0&limit=10
                        userGetRequest.limit = attendanceReq!!.limit
                        userGetRequest.offset = attendanceReq!!.offset
                        userGetRequest.roleId = roleId
                    } else if (type != null) {
                        // api.url = oldApi.url+"?type="+type
                        userGetRequest.limit = attendanceReq!!.limit
                        userGetRequest.offset = attendanceReq!!.offset
                        userGetRequest.type = type
                        //  api.url = oldApi.url+"?type="+type
                    } else {
                        api.url = oldApi.url
                    }
                }
                api.url = oldApi.url
                api.name = ApiType.GET_USERS
                api.timeOut = oldApi.timeOut
                if(dataManager!=null)
                dataManager.getUserList(this, httpManager, oldApi, userGetRequest)
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

    fun deleteUser(httpManager: HttpManager,userId:String?) {
        this.httpManager = httpManager
        DeleteUser(userId).hitApi()
    }

    inner class DeleteUser(var userId: String?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleDeleteResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.DELETE_USER)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.DELETE_USER]!!
                val api = Api()
                if(userId!=null) {
                    api.url = oldApi.url+"?userId="+userId
                }else{
                    api.url=oldApi.url
                }
                api.name = ApiType.DELETE_USER
                api.timeOut = oldApi.timeOut
                if(dataManager!=null)
                    dataManager.deleteUser(this, httpManager, api)
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


    fun searchUser(httpManager: HttpManager,searchBy:String?,value:String?, attendanceReq: AttendanceReq) {
        this.httpManager = httpManager
        SearchUser(searchBy,value,attendanceReq).hitApi()
    }

    inner class SearchUser(var searchBy: String?,var value: String?,var attendanceReq: AttendanceReq) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.USER_SEARCH)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.USER_SEARCH]!!
                val api = Api()
                api.url = oldApi.url+"?limit="+attendanceReq.limit+"&offset="+attendanceReq.offset+"&searchBy="+searchBy+"&value="+value+"&fetchAddress=false"
               // api.url = oldApi.url+"?searchBy="+searchBy+"&value="+value+"&fetchAddress=false"
                api.name = ApiType.USER_SEARCH
                api.timeOut = oldApi.timeOut
                if(dataManager!=null)
                    dataManager.searchUser(this, httpManager, api)
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