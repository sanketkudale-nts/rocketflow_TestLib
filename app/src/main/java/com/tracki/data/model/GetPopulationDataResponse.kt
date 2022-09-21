package com.tracki.data.model


/**
 * Created by Vikas Kesharvani on 22/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
data class GetPopulationDataResponse(
    var `data`: String? = null,
    var responseCode: String? = null,
    var responseMsg: String? = null,
    var successful: Boolean? = null
)