package com.tracki.ui.userlisting

import android.text.InputType
import android.util.Log
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.AttendanceReq
import com.tracki.data.model.request.ExecuteUpdateRequest
import com.tracki.data.model.request.UserGetRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class UserListNewViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<UserListNewNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getUserList(
        httpManager: HttpManager,
        roleId: String?,
        type: String?,
        attendanceReq: AttendanceReq?,
        new: Boolean = false
    ) {
        this.httpManager = httpManager
        GetUserList(roleId, type, attendanceReq, new).hitApi()
    }

    inner class GetUserList(
        var roleId: String?,
        var type: String?,
        var attendanceReq: AttendanceReq?,
        var new: Boolean
    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_USERS)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.GET_USERS]!!
                val userGetRequest = UserGetRequest()
                val api = oldApi
                if (new) {
                    Log.e("urlCheck", "${api.url}")
                    val array = roleId?.split(",")?.toTypedArray()
                    val arrayList = ArrayList<String>()
                    if (array != null) {
                        for (item in array){
                            arrayList.add(item)
                        }
                    }
                    userGetRequest.roleIds = arrayList
                } else {
                    //https://qa2.rocketflyer.in/rfapi/secure/tracki/user/get?pagination.datalimit=10&pagination.pageOffset=1&pagination.pageNumbers=1&pagination.pageIndex=1&pagination.dataCount=10&roleId=ad14fa67-d27f-4762-b3e3-48d58a45365f
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
                api.name = ApiType.GET_USERS
                api.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getUserList(this, httpManager, api,userGetRequest)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun executeUpdates(httpManager: HttpManager?, request: ExecuteUpdateRequest?) {
        this.httpManager = httpManager!!
        ExecuteUpdateTask(request).hitApi()
    }

    inner class ExecuteUpdateTask(var request: ExecuteUpdateRequest?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleExecuteUpdateResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiApplication.getApiMap()[ApiType.EXECUTE_UPDATE]
            dataManager.executeUpdateTask(this, httpManager, request, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }
}
