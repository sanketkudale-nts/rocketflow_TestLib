package com.rocketflyer.rocketflow.ui.inventory

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 16/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface InventoryNavigator : BaseNavigator {

    fun checkInventory(callback: ApiCallback?, result: Any?, error: APIError?)
    fun handleInventoryResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    fun handleCategoryGroupResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    fun linkInventoryResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

}