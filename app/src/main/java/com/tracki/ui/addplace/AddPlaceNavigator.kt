package com.tracki.ui.addplace

import android.view.View
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface AddPlaceNavigator:BaseNavigator {
    fun openMainActivity()
    fun openPlaceAutoComplete(view: View)
    fun addLocation(view: View)
    fun handleAddPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleStateCityResponse(callback: ApiCallback, result: Any?, error: APIError?)
}