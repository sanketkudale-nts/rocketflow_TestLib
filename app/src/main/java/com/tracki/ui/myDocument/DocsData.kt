package com.tracki.ui.myDocument


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DocsData(
    @SerializedName("createdBy")
    var createdBy: String?,
    @SerializedName("documentId")
    var documentId: String?,
    @SerializedName("imageUrl")
    var imageUrl: String?,
    @SerializedName("issuedAt")
    var issuedAt: Long?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("number")
    var number: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("validTill")
    var validTill: Long?,
    @SerializedName("vendorId")
    var vendorId: String?
):Parcelable