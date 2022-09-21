package com.rocketflyer.rocketflow.ui.uploadDocument
import com.google.gson.annotations.SerializedName


data class UploadDocumentRequest(
    @SerializedName("createdBy")
    var createdBy: String?=null,
    @SerializedName("documentId")
    var documentId: String?=null,
    @SerializedName("imageUrl")
    var imageUrl: String?=null,
    @SerializedName("issuedAt")
    var issuedAt: Long?=null,
    @SerializedName("name")
    var name: String?=null,
    @SerializedName("number")
    var number: String?=null,
    @SerializedName("type")
    var type: String?=null,
    @SerializedName("validTill")
    var validTill: Long?=null
)