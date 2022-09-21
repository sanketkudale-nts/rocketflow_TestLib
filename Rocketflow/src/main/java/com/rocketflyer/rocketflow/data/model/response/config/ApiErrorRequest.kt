package com.rocketflyer.rocketflow.data.model.response.config
import com.google.gson.annotations.SerializedName


data class ApiErrorRequest(
    @SerializedName("appVersion")
    var appVersion: String?=null,
    @SerializedName("deviceMeta")
    var deviceMeta: HashMap<String,String>?=null,
    @SerializedName("error")
    var error: String?=null
)

