package com.tracki.ui.products
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tracki.data.model.response.config.DynamicFormData
import com.tracki.ui.category.ProductDescription
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AddProductRequest(
    @SerializedName("cid")
    var cid: String?=null,
    @SerializedName("pid")
    var pid: String?=null,
    @SerializedName("description")
    var description: String?=null,
    @SerializedName("flavorId")
    var flavorId: String?=null,
    @SerializedName("img")
    var img: String?=null,
    @SerializedName("price")
    var price: Double?=null,
    @SerializedName("productName")
    var productName: String?=null,
    @SerializedName("upcNumber")
    var upcNumber: String?=null,
    @SerializedName("sellingPrice")
    var sellingPrice: Double?=null,
    @SerializedName("unitInfo")
    var unitInfo: UnitInfo?=null,
    @SerializedName("dfData")
    var dfData: List<DynamicFormData>?=null,
    @SerializedName("dfId")
    var dfId: String?=null,
    @SerializedName("dfdId")
    var dfdId: String?=null,
    @SerializedName("maxOrderLimit")
    var maxOrderLimit: Int?=null,
    @SerializedName("minStockQuantity")
    var minStockQuantity: Int?=null,
    @SerializedName("specifications")
    var specifications: ArrayList<ProductDescription>?=null

    ):Parcelable

@Parcelize
data class UnitInfo(
    var packInfo: PackInfo?=null,
    var quantity: Float?=null,
    var type: String?=null
):Parcelable

@Parcelize
data class PackInfo(
    var from: Long?=null,
    var to: Long?=null,
    var unitType: String?=null
):Parcelable

