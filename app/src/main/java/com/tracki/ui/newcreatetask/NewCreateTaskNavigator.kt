package com.tracki.ui.newcreatetask

import android.view.View
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface NewCreateTaskNavigator :BaseNavigator {
    fun onAssignNowClicked()
    fun selectDateTime(v: View)
    fun openMainActivity(taskId:String)
    fun openPlaceAutoComplete(view: View)
    fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun pointOfContactClicked()
    fun checkBuddyResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    fun checkFleetResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileApiResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileDisposeApiResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleRegionListResponse(callback: ApiCallback, result: Any?, error: APIError?,isStart:Boolean)
    fun handleStateListResponse(callback: ApiCallback, result: Any?, error: APIError?,isStart:Boolean)
    fun handleCityListResponse(callback: ApiCallback, result: Any?, error: APIError?,isStart:Boolean)
    fun handleHubListResponse(callback: ApiCallback, result: Any?, error: APIError?,isStart:Boolean)
}