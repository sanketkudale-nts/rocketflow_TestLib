package com.rocketflyer.rocketflow.ui.dynamicform

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 20/3/19
 */
interface DynamicFormNavigator : BaseNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileApiResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileDisposeApiResponse(callback: ApiCallback, result: Any?, error: APIError?)
//    fun handleFormListResponse(apiCallback: ApiCallback, result: Any?, error: APIError?)
}