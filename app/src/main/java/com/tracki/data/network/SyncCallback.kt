package com.tracki.data.network

/**
 * Created by rahul on 11/4/19
 */
interface SyncCallback : ApiCallback {

    override fun onNetworkErrorClose() {}

    override fun onLogout() {}

    override fun onRequestTimeOut(callBack: ApiCallback) {}

    override fun hitApi() {}
}