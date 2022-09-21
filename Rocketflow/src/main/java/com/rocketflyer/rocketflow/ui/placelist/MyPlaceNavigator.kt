package com.rocketflyer.rocketflow.ui.placelist

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface MyPlaceNavigator :BaseNavigator{
    fun handleMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleDeleteMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun openAddPlaceActivity()
}