package com.tracki.ui.productdetails
import com.google.gson.annotations.SerializedName


data class StockEntryRequest(
    @SerializedName("action")
    var action: String?=null,
    @SerializedName("pid")
    var pid: String?=null,
    @SerializedName("end")
    var end: Long?=null,
    @SerializedName("day")
    var day: Long?=null,
    @SerializedName("limit")
    var limit: Int?=null,
    @SerializedName("offset")
    var offset: Int?=null,
    @SerializedName("start")
    var start: Long?=null,
    @SerializedName("geoId")
    var geoId: String?=null
)