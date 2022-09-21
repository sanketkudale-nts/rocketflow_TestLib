package com.rocketflyer.rocketflow.ui.dynamicform.dynamicfragment

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by Rahul Abrol on 13/7/19.
 */
interface DynamicNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)

   fun  handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?)

   fun  handleGetPreviousFormResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)

}