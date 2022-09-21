package com.tracki.ui.myDocument


import com.google.gson.annotations.SerializedName

data class DocDeleteResponse(
    @SerializedName("responseCode")
    var responseCode: String?,
    @SerializedName("responseMsg")
    var responseMsg: String?,
    @SerializedName("successful")
    var successful: Boolean?
)