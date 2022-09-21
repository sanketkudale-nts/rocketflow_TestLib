package com.rocketflyer.rocketflow.ui.products

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.gson.annotations.SerializedName
import com.tracki.data.model.response.config.DynamicFormData
import com.tracki.data.model.response.config.FormData
import com.tracki.ui.category.ProductDescription
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.ui.selectorder.ProductStock
import java.io.Serializable
import java.text.DecimalFormat


data class ProductDetailsResponse(
    @SerializedName("data")
    var `data`: ProductDetails?,
    @SerializedName("responseCode")
    var responseCode: String?,
    @SerializedName("responseMsg")
    var responseMsg: String?,
    @SerializedName("successful")
    var successful: Boolean
) : Serializable

data class ProductDetails(
    @SerializedName("active")
    var active: Boolean?,
    @SerializedName("stock")
    var stock: Boolean=false,
    @SerializedName("added")
    var added: Boolean?,
    @SerializedName("cid")
    var cid: String?,
    @SerializedName("cname")
    var cname: String?,
    @SerializedName("geoTagging")
    var geoTagging: Boolean? = false,
    @SerializedName("geoMap")
    var geoMap: Map<String,String>?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("dfData")
    var dfData: ArrayList<DynamicFormData>?,
    @SerializedName("dfId")
    var dfId: String?,
    @SerializedName("dfdId")
    var dfdId: String?,
    @SerializedName("discount")
    var discount: Int?,
    @SerializedName("images")
    var images: ArrayList<String>?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("packInfo")
    var packInfo: PackInfo?,
    @SerializedName("pid")
    var pid: String?,
    @SerializedName("price")
    var price: Float?,
    @SerializedName("prodInfoMap")
    var prodInfoMap: Map<String, String>?,
    @SerializedName("qty")
    var qty: Long?,
    @SerializedName("sellingPrice")
    var sellingPrice: Float?,
    @SerializedName("unitType")
    var unitType: String?,
    @SerializedName("unitValue")
    var unitValue: Double?,
    @SerializedName("maxOrderLimit")
    var maxOrderLimit: Int? = 0,
    @SerializedName("productStock")
    var productStock: ProductStock? = null,
    @SerializedName("specifications")
    var specifications: ArrayList<ProductDescription>? = null
) : Serializable {
    companion object {
        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("percentage_off")
        fun offPercentage(textView: TextView, data: ProductDetails?) {
            textView.text = ""
            if (data?.price != null && data.sellingPrice != null) {
                if (data.price!! > data.sellingPrice!!) {
                    var percentage = ((data.price!! - data.sellingPrice!!) * 100) / data.price!!
                    // var percentage=((data.price!!-data.sellingPrice!!)/data.price!!)/100
                    if (percentage <= 100) {
                        val decimalFormat = DecimalFormat("#.#")
                        val twoDigitsF: Float =
                            java.lang.Float.valueOf(decimalFormat.format(percentage))
                        textView.text = " ${twoDigitsF} %off"
                        textView.visibility = View.VISIBLE
                    } else {
                        textView.visibility = View.GONE
                    }

                } else {
                    textView.visibility = View.GONE
                }
            }

        }

    }
}

