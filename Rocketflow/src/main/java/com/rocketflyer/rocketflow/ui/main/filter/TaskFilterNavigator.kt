package com.rocketflyer.rocketflow.ui.main.filter

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 04/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface TaskFilterNavigator: BaseNavigator {

    fun handleRegionListResponse(callback: ApiCallback, result: Any?, error: APIError?,isStart:Boolean)
    fun handleStateListResponse(callback: ApiCallback, result: Any?, error: APIError?,isStart:Boolean)
    fun handleCityListResponse(callback: ApiCallback, result: Any?, error: APIError?,isStart:Boolean)
    fun handleHubListResponse(callback: ApiCallback, result: Any?, error: APIError?,isStart:Boolean)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun submitFilter()
    fun clearFilter()
}