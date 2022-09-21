package com.tracki.ui.adduserAddress

import android.view.View
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface AddAddressNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun openPlaceAutoComplete(view: View)

}