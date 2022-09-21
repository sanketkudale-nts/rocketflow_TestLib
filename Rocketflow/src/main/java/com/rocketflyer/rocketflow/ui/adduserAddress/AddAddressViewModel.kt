package com.rocketflyer.rocketflow.ui.adduserAddress

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AddressInfo
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.Field
import com.tracki.data.model.response.config.RoleConfigData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

class AddAddressViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<AddAddressNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api




    fun updateAddress(httpManager: HttpManager, address: AddressInfo, api: Api) {
        this.httpManager = httpManager
        this.api = api
        UpdateAddress(address).hitApi()
    }

    inner class UpdateAddress(var address: AddressInfo) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUpdateResponse(this@UpdateAddress, result, error)
        }

        override fun hitApi() {
            if (dataManager != null)
                dataManager.updateAddress(this@UpdateAddress, address, httpManager, api)
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


    fun selectLocation(view: View) {
        navigator.openPlaceAutoComplete(view)
    }


    fun addAddAddress(httpManager: HttpManager, data: AddressInfo, api: Api) {
        this.httpManager = httpManager
        this.api = api
        AddAddress(data).hitApi()
    }

    inner class AddAddress(var data: AddressInfo) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this@AddAddress, result, error)
        }

        override fun hitApi() {
            if (dataManager != null)
                dataManager.addAddress(this@AddAddress, data, httpManager, api)
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