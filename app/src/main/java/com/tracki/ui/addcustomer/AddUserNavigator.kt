package com.tracki.ui.addcustomer

import android.view.View
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface AddUserNavigator: BaseNavigator {
    fun handleAddUserResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUserDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProfilePicResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)
    fun handleDeleteResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun openPlaceAutoComplete(view: View)
    fun selectDateTime(view: View)
}