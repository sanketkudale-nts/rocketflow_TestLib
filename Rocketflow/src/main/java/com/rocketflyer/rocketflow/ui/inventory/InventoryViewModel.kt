package com.rocketflyer.rocketflow.ui.inventory

import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.InventoryRequest
import com.tracki.data.model.request.LinkInventoryRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 16/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class InventoryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<InventoryNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager

    fun checkInventoryData(httpManager: HttpManager, request: InventoryRequest, isFirst: Boolean) {
        this.httpManager = httpManager
        CheckInventory(request, isFirst).hitApi()
    }

    inner class CheckInventory(private var data: InventoryRequest, private var isFirst: Boolean) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (isFirst)
                navigator.checkInventory(this, result, error)
            else
                navigator.handleInventoryResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiApplication.getApiMap()[ApiType.GET_TASK_INVENTORIES]!!
//            val api = Api()
//            api.url = "https://qa2.rocketflyer.in/rfapi/secure/tracki/inventories"
//            api.name = ApiType.CHECK_INVENTORY
//            api.timeOut = 50
            dataManager.checkInventory(this, httpManager, data, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }


    fun getCategoryGroup(httpManager: HttpManager, categoryId: String) {
        this.httpManager = httpManager
        CategoryGroup(categoryId).hitApi()
    }

    inner class CategoryGroup(private val categoryId: String) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleCategoryGroupResponse(this, result, error)
        }

        override fun hitApi() {
            val apiMain = TrackiApplication.getApiMap()[ApiType.GET_CATEGORY_BY_GROUP]!!
            val api = Api()
            api.name = ApiType.GET_CATEGORY_BY_GROUP
            api.timeOut = apiMain.timeOut
            api.url = "${apiMain.url}?categoryId=${categoryId}"
            //    api.url = "https://qa2.rocketflyer.in/rfapi/secure/tracki/getCategoryGroups?categoryId=${categoryId}"
            dataManager.getCategoryGroup(this, httpManager, null, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun linkInventory(httpManager: HttpManager, request: LinkInventoryRequest) {
        this.httpManager = httpManager
        LinkInventory(request).hitApi()
    }

    inner class LinkInventory(private var data: LinkInventoryRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.linkInventoryResponse(this, result, error)
        }

        override fun hitApi() {
            val api = TrackiApplication.getApiMap()[ApiType.LINK_INVENTORY]!!
//            val api = Api()
//            api.url = "https://qa2.rocketflyer.in/rfapi/secure/tracki/linkInventory"
//            api.name = ApiType.LINK_INVENTORY
//            api.timeOut = 50
            dataManager.linkInventory(this, httpManager, data, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }
}