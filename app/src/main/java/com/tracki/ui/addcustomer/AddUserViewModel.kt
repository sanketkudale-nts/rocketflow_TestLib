package com.tracki.ui.addcustomer

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AddEmployeeRequest
import com.tracki.data.model.request.UpdateFileRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.Field
import com.tracki.data.model.response.config.RoleConfigData
import com.tracki.data.model.response.config.UserData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.Log
import com.tracki.utils.rx.SchedulerProvider

class AddUserViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<AddUserNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api

    enum class ROLE_FIELD {
        FIRST_NAME, MIDDLE_NAME, LAST_NAME, MOBILE, EMAIL, PASSWORD, DATE_OF_BIRTH, DATA_OF_JOINING, PERSON_ID, ADDRESS, PROFILE_IMG,
        PAN_NUMBER,
        AADHAR_NUMBER, UPLOAD_PAN, UPLOAD_AADHAR_FRONT,
        AADHAR_BACK, MOTHER_NAME, FATHER_NAME,
        HIGHEST_QUALIFICATION
    }

    fun getUserDetail(httpManager: HttpManager, userData: UserData) {
        this.httpManager = httpManager
        GetUserDetails(userData).hitApi()
    }

    inner class GetUserDetails(var userData: UserData) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            Log.e("checkrespDetails","$result")
            if (navigator != null)
                Log.e("checkrespDetails"," insider = $result")
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

    fun uploadProfilePic(data: UpdateFileRequest, httpManager: HttpManager?, api: Api?) {
        this.httpManager = httpManager!!
        this.api = api!!
        UpdateProfilePic(data).hitApi()
    }

    inner class UpdateProfilePic(var data: UpdateFileRequest) : ApiCallback {
        override fun onResponse(result: Any, error: APIError?) {
            if (navigator != null)
                navigator.handleProfilePicResponse(this, result, error)
        }

        override fun hitApi() {
            if (dataManager != null)
                dataManager.updateProfilePic(this, httpManager, data, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}
    }


    fun updateEmployee(httpManager: HttpManager, addEmployeeRequest: AddEmployeeRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        UpdateUser(addEmployeeRequest).hitApi()
    }

    inner class UpdateUser(var addEmployeeRequest: AddEmployeeRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleAddUserResponse(this@UpdateUser, result, error)
        }

        override fun hitApi() {
            if (dataManager != null)
                dataManager.updateUser(this@UpdateUser, addEmployeeRequest, httpManager, api)
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

    fun selectTime(view: View) {
        navigator.selectDateTime(view)
    }

    fun addEmployee(httpManager: HttpManager, addEmployeeRequest: AddEmployeeRequest, api: Api) {
        this.httpManager = httpManager
        this.api = api
        AddUser(addEmployeeRequest).hitApi()
    }

    inner class AddUser(var addEmployeeRequest: AddEmployeeRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleAddUserResponse(this@AddUser, result, error)
        }

        override fun hitApi() {
            if (dataManager != null)
                dataManager.addUser(this@AddUser, addEmployeeRequest, httpManager, api)
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

    var isFirstNameVisible = ObservableField(false)
    var isFirstNameRequired = ObservableField(false)
    var firstNameLabel = ObservableField("")
    var isLastNameVisible = ObservableField(false)
    var isLastNameRequired = ObservableField(false)
    var lastNameLabel = ObservableField("")
    var isMiddleNameVisible = ObservableField(false)
    var isMiddleNameRequired = ObservableField(false)
    var middleNameLabel = ObservableField("")
    var isEmailVisible = ObservableField(false)
    var isEmailRequired = ObservableField(false)
    var emailLabel = ObservableField("")
    var isPasswordVisible = ObservableField(false)
    var isPasswordRequired = ObservableField(false)
    var passwordLabel = ObservableField("")
    var isMobileVisible = ObservableField(false)
    var isMobileRequired = ObservableField(false)
    var mobileLabel = ObservableField("")
    var isDobVisible = ObservableField(false)
    var isDobRequired = ObservableField(false)
    var dobLabel = ObservableField("")
    var isDojVisible = ObservableField(false)
    var isDojRequired = ObservableField(false)
    var dojLabel = ObservableField("")
    var isPidVisible = ObservableField(false)
    var isPidRequired = ObservableField(false)
    var podLabel = ObservableField("")
    var isAddress = ObservableField(false)
    var isAddressRequired = ObservableField(false)
    var addressLabel = ObservableField("")
    var isProfilePicVisible = ObservableField(false)
    var isProfilePicRequired = ObservableField(false)
    var profilePicLabel = ObservableField("")
    var isMotherNameVisible = ObservableField(false)
    var isMotherNameRequired = ObservableField(false)
    var motherNameLabel = ObservableField("")
    var isFatherNameVisible = ObservableField(false)
    var isFatherNameRequired = ObservableField(false)
    var fatherNameLabel = ObservableField("")

    fun performShowHideView(context: Context, pref: PreferencesHelper, roleId: String) {
        var listRoleConfigList = pref.roleConfigDataList
        if (!listRoleConfigList.isNullOrEmpty()) {
            var roleConfigData = RoleConfigData(roleId = roleId)
            if (listRoleConfigList.contains(roleConfigData)) {
                var index = listRoleConfigList.indexOf(roleConfigData)
                if (index != -1) {
                    var listFields = listRoleConfigList[index].fields
                    if (!listFields.isNullOrEmpty()) {
                        var PROFILE_IMG = Field(field = ROLE_FIELD.PROFILE_IMG.name)
                        if (listFields.contains(PROFILE_IMG)) {
                            var position = listFields.indexOf(PROFILE_IMG)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isProfilePicVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isProfilePicRequired.set(true)
                                }else{
                                    isProfilePicRequired.set(false)
                                }
                            } else {
                                isProfilePicVisible.set(false)
                                isProfilePicRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                profilePicLabel.set(listFields[position]!!.label!!)
                            else {
                                profilePicLabel.set(context.getString(R.string.profile_pic))
                            }
                        } else {
                            isProfilePicVisible.set(false)
                            isProfilePicRequired.set(false)
                        }

                        var FIRST_NAME = Field(field = ROLE_FIELD.FIRST_NAME.name)
                        if (listFields.contains(FIRST_NAME)) {
                            var position = listFields.indexOf(FIRST_NAME)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isFirstNameVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isFirstNameRequired.set(true)
                                }else{
                                    isFirstNameRequired.set(false)
                                }
                            } else {
                                isFirstNameVisible.set(false)
                                isFirstNameRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                firstNameLabel.set(listFields[position]!!.label!!)
                            else {
                                firstNameLabel.set(context.getString(R.string.first_name))
                            }
                        } else {
                            isFirstNameVisible.set(false)
                            isFirstNameRequired.set(false)
                        }
                        var MIDDLE_NAME = Field(field = ROLE_FIELD.MIDDLE_NAME.name)
                        if (listFields.contains(MIDDLE_NAME)) {
                            var position = listFields.indexOf(MIDDLE_NAME)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isMiddleNameVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isMiddleNameRequired.set(true)
                                }else{
                                    isMiddleNameRequired.set(false)
                                }

                            } else {
                                isMiddleNameVisible.set(false)
                                isMiddleNameRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                middleNameLabel.set(listFields[position]!!.label!!)
                            else {
                                middleNameLabel.set(context.getString(R.string.middle_name))
                            }
                        } else {
                            isMiddleNameVisible.set(false)
                            isMiddleNameRequired.set(false)
                        }
                        var LAST_NAME = Field(field = ROLE_FIELD.LAST_NAME.name)
                        if (listFields.contains(LAST_NAME)) {
                            var position = listFields.indexOf(LAST_NAME)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isLastNameVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isLastNameRequired.set(true)
                                }else{
                                    isLastNameRequired.set(false)
                                }
                            } else {
                                isLastNameVisible.set(false)
                                isLastNameRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                lastNameLabel.set(listFields[position]!!.label!!)
                            else {
                                lastNameLabel.set(context.getString(R.string.last_name))
                            }
                        } else {
                            isLastNameVisible.set(false)
                            isLastNameRequired.set(false)
                        }
                        var MOBILE = Field(field = ROLE_FIELD.MOBILE.name)
                        if (listFields.contains(MOBILE)) {
                            var position = listFields.indexOf(MOBILE)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isMobileVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isMobileRequired.set(true)
                                }else{
                                    isMobileRequired.set(false)
                                }

                            } else {
                                isMobileVisible.set(false)
                                isMobileRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                mobileLabel.set(listFields[position]!!.label!!)
                            else {
                                mobileLabel.set(context.getString(R.string.mobile_number))
                            }
                        } else {
                            isMobileVisible.set(false)
                            isMobileRequired.set(false)
                        }
                        var EMAIL = Field(field = ROLE_FIELD.EMAIL.name)
                        if (listFields.contains(EMAIL)) {
                            var position = listFields.indexOf(EMAIL)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isEmailVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isEmailRequired.set(true)
                                }else{
                                    isEmailRequired.set(false)
                                }
                            } else {
                                isEmailVisible.set(false)
                                isEmailRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                emailLabel.set(listFields[position]!!.label!!)
                            else {
                                emailLabel.set(context.getString(R.string.email))
                            }
                        } else {
                            isEmailVisible.set(false)
                            isEmailRequired.set(false)
                        }
                        var FATHER_NAME = Field(field = ROLE_FIELD.FATHER_NAME.name)
                        if (listFields.contains(FATHER_NAME)) {
                            var position = listFields.indexOf(FATHER_NAME)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isFatherNameVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isFatherNameRequired.set(true)
                                }else{
                                    isFatherNameRequired.set(false)
                                }
                            } else {
                                isFatherNameVisible.set(false)
                                isFatherNameRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                fatherNameLabel.set(listFields[position]!!.label!!)
                            else {
                                fatherNameLabel.set(context.getString(R.string.father_name))
                            }
                        } else {
                            isFatherNameVisible.set(false)
                            isFatherNameRequired.set(false)
                        }
                        var MOTHER_NAME = Field(field = ROLE_FIELD.MOTHER_NAME.name)
                        if (listFields.contains(MOTHER_NAME)) {
                            var position = listFields.indexOf(MOTHER_NAME)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isMotherNameVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isMotherNameRequired.set(true)
                                }else{
                                    isMotherNameRequired.set(false)
                                }
                            } else {
                                isMotherNameVisible.set(false)
                                isMotherNameRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                motherNameLabel.set(listFields[position]!!.label!!)
                            else {
                                motherNameLabel.set(context.getString(R.string.mother_name))
                            }
                        } else {
                            isMotherNameVisible.set(false)
                            isMotherNameRequired.set(false)
                        }

                        var PASSWORD = Field(field = ROLE_FIELD.PASSWORD.name)
                        if (listFields.contains(PASSWORD)) {
                            var position = listFields.indexOf(PASSWORD)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isPasswordVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isPasswordRequired.set(true)
                                }else{
                                    isPasswordRequired.set(false)
                                }
                            } else {
                                isPasswordVisible.set(false)
                                isPasswordRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                passwordLabel.set(listFields[position]!!.label!!)
                            else {
                                passwordLabel.set(context.getString(R.string.password))
                            }
                        } else {
                            isPasswordVisible.set(false)
                            isPasswordRequired.set(false)
                        }

                        var DATE_OF_BIRTH = Field(field = ROLE_FIELD.DATE_OF_BIRTH.name)
                        if (listFields.contains(DATE_OF_BIRTH)) {
                            var position = listFields.indexOf(DATE_OF_BIRTH)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isDobVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isDobRequired.set(true)
                                }else{
                                    isDobRequired.set(false)
                                }
                            } else {
                                isDobVisible.set(false)
                                isDobRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                dobLabel.set(listFields[position]!!.label!!)
                            else {
                                passwordLabel.set(context.getString(R.string.date_of_birth))
                            }
                        } else {
                            isDobVisible.set(false)
                            isDobRequired.set(false)
                        }

                        var DATA_OF_JOINING = Field(field = ROLE_FIELD.DATA_OF_JOINING.name)
                        if (listFields.contains(DATA_OF_JOINING)) {
                            var position = listFields.indexOf(DATA_OF_JOINING)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isDojVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isDojRequired.set(true)
                                }else{
                                    isDojRequired.set(false)
                                }
                            } else {
                                isDojVisible.set(false)
                                isDojRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                dojLabel.set(listFields[position]!!.label!!)
                            else {
                                dojLabel.set(context.getString(R.string.date_of_joining))
                            }
                        } else {
                            isDojVisible.set(false)
                            isDojRequired.set(false)

                        }

                        var PERSON_ID = Field(field = ROLE_FIELD.PERSON_ID.name)
                        if (listFields.contains(PERSON_ID)) {
                            var position = listFields.indexOf(PERSON_ID)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isPidVisible.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isPidRequired.set(true)
                                }else{
                                    isPidRequired.set(false)
                                }
                            } else {
                                isPidVisible.set(false)
                                isPidRequired.set(false)
                            }
                            if (position != -1 && listFields[position].label != null && listFields[position].label!!.isNotEmpty())
                                podLabel.set(listFields[position]!!.label!!)
                            else {
                                podLabel.set(context.getString(R.string.person_id))
                            }
                        } else {
                            isPidVisible.set(false)
                            isPidRequired.set(false)
                        }

                      /*  var ADDRESS = Field(field = ROLE_FIELD.ADDRESS.name)
                        if (listFields.contains(ADDRESS)) {
                            var position = listFields.indexOf(ADDRESS)
                            if (position != -1 && !listFields[position].skipVisibilty) {
                                isAddress.set(true)
                                if (position != -1 && listFields[position].required) {
                                    isAddressRequired.set(true)
                                }else{
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
                        }*/


                    }else{
                        setFieldNullOrEmpty()
                    }
                }else{
                   setFieldNullOrEmpty()
                }
            }
        }


    }

    private fun setFieldNullOrEmpty() {
        isProfilePicVisible.set(false)
        isProfilePicRequired.set(false)
        isFirstNameVisible.set(false)
        isFirstNameRequired.set(false)
        isMiddleNameVisible.set(false)
        isMiddleNameRequired.set(false)
        isLastNameVisible.set(false)
        isLastNameRequired.set(false)
        isMobileVisible.set(false)
        isMobileRequired.set(false)
        isEmailVisible.set(false)
        isEmailRequired.set(false)
        isFatherNameVisible.set(false)
        isFatherNameRequired.set(false)
        isMotherNameVisible.set(false)
        isMotherNameRequired.set(false)
        isPasswordVisible.set(false)
        isPasswordRequired.set(false)
        isDobVisible.set(false)
        isDobRequired.set(false)
        isDojVisible.set(false)
        isDojRequired.set(false)
        isPidVisible.set(false)
        isPidRequired.set(false)
    }

    fun deleteUser(httpManager: HttpManager, userId:String?) {
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


}