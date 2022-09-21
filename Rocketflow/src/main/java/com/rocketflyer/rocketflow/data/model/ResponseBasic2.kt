package com.rocketflyer.rocketflow.data.model

import java.io.Serializable

open class ResponseBasic2: Serializable {
    var successful: Boolean? = false
    var responseMsg: String? = null
    var responseCode: String? = null
}