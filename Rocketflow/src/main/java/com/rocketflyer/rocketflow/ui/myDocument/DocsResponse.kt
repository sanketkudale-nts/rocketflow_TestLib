package com.rocketflyer.rocketflow.ui.myDocument


import com.google.gson.annotations.SerializedName

data class DocsResponse(
    @SerializedName("data")
    var `data`: ArrayList<DocsData>?,
    @SerializedName("responseCode")
    var responseCode: String?,
    @SerializedName("responseMsg")
    var responseMsg: String?,
    @SerializedName("successful")
    var successful: Boolean?,
    @SerializedName("dataCount")
    var dataCount: Int=0
)