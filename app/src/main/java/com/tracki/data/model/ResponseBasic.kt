package com.tracki.data.model

open class ResponseBasic {
    var successful: Boolean? = false
    var responseCode: String? = null
    var responseMsg: String? = null
    var time: Long? = 0L
}