package com.rocketflyer.rocketflow.ui.scanqrcode
import com.google.gson.annotations.SerializedName
import com.tracki.data.model.BaseResponse


data class QrCodeResponse(

    @SerializedName("id")
    var id: String?,
    @SerializedName("type")
    var type: QrCodeValueType?
):BaseResponse()
enum class QrCodeValueType{
    TASK,USER,PRODUCT
}