package com.rocketflyer.rocketflow.data.model.response.config


/**
 * Created by Vikas Kesharvani on 02/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
data class OnLineOfflineResponse(
    var responseCode: String? = null,
    var responseMsg: String? = null,
    var status: String? = null,
    var successful: Boolean? = null
)