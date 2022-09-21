package com.rocketflyer.rocketflow.ui.wallet

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 18/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface TransactionNavigator : BaseNavigator {

    fun handleTransactionResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

}