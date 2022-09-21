package com.tracki.ui.userdetails

import android.content.Context
import androidx.databinding.ObservableField
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.Field
import com.tracki.data.model.response.config.RoleConfigData
import com.tracki.data.model.response.config.UserData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

class UserDetailsViewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<UserDetailsNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager



    fun deleteUser(httpManager: HttpManager, userId:String?) {
        this.httpManager = httpManager
        DeleteUser(userId).hitApi()
    }

    fun getUserDetail(httpManager: HttpManager, userData: UserData) {
        this.httpManager = httpManager
        GetUserDetails(userData).hitApi()
    }

    inner class GetUserDetails(var userData: UserData) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUserDetailsResponse(this@GetUserDetails, result, error)
        }

        override fun hitApi() {
            if (dataManager != null) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.USER_DETAIL]!!
                val api = Api()
                if (userData.userId != null) {
                    api.url = oldApi.url + "?fetchAddress=false&userId=${userData.userId}"
                } else {
                    api.url = oldApi.url
                }
                api.name = ApiType.USER_DETAIL
                api.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getUserDetails(this, httpManager, api)
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
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


    fun searchUser(httpManager: HttpManager, searchBy:String?, value:String?) {
        this.httpManager = httpManager
        SearchUser(searchBy,value).hitApi()
    }

    inner class SearchUser(var searchBy: String?,var value: String?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.USER_SEARCH)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.USER_SEARCH]!!
                val api = Api()
                api.url = oldApi.url+"?searchBy="+searchBy+"&value="+value+"&fetchAddress=false"
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

    var isAddress = ObservableField(false)
    var isAddressRequired = ObservableField(false)
    var addressLabel = ObservableField("")
    enum class ROLE_FIELD {
        ADDRESS
    }

    fun performShowHideView(context: Context, pref: PreferencesHelper, roleId: String) {
        var listRoleConfigList = pref.roleConfigDataList
        if (!listRoleConfigList.isNullOrEmpty()) {
            var roleConfigData = RoleConfigData(roleId = roleId)
            if (listRoleConfigList.contains(roleConfigData)) {
                var index = listRoleConfigList.indexOf(roleConfigData)
                if (index != -1) {
                    var listFields = listRoleConfigList[index].fields
                    if (!listFields.isNullOrEmpty()) {
                        var ADDRESS = Field(field = ROLE_FIELD.ADDRESS.name)
                        if (listFields.contains(ADDRESS)) {
                            var position = listFields.indexOf(ADDRESS)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isAddress.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isAddressRequired.set(true)
                                } else {
                                    isAddressRequired.set(false)
                                }
                            } else {
                                isAddress.set(false)
                                isAddressRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                addressLabel.set(listFields[position]!!.label!!)
                            else {
                                addressLabel.set(context.getString(R.string.person_id))
                            }
                        } else {
                            isAddress.set(false)
                            isAddressRequired.set(false)
                        }


                    }
                }
            }
        }

    }

}