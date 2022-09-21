package com.rocketflyer.rocketflow.ui.selectorder

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.annotations.SerializedName

import com.tracki.R
import com.tracki.data.model.response.config.DynamicFormData
import com.tracki.ui.category.ProductDescription
import com.tracki.ui.products.UnitInfo
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat

@Parcelize
data class CatalogProductResponse(
    var `data`: ArrayList<CatalogProduct>?,
    var count: Int = 0,
    var responseCode: String?,
    var responseMsg: String?,
    var successful: Boolean
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

@Parcelize
data class ProductStock(
    @SerializedName("minValue")
    var minValue: Int?,
    @SerializedName("raiseAlert")
    var raiseAlert: Boolean?,
    @SerializedName("value")
    var value: Int?
):Parcelable

@Parcelize
data class CatalogProduct(
    var active: Boolean? = null,
    var added: Boolean? = null,
    var img: String? = null,
    var image: String? = null,
    var categoryName: String? = null,
    var name: String? = null,
    var pid: String? = null,
    var price: Float? = null,
    var cid: String? = null,
    var cname: String? = null,
    var unitValue: Float? = null,
    var qty: Int? = null,
    var sellingPrice: Float? = null,
    var unitType: String? = null,
    var dfId: String? = null,
    var description: String? = null,
    var unitInfo: UnitInfo? = null,
    var packInfo: com.tracki.ui.products.PackInfo? = null,
    var noOfProduct: Int = 0,
    var maxOrderLimit: Int? = 0,
    var productStock: ProductStock?=null,
    var specifications: ArrayList<ProductDescription>?=null,
    var addInOrder: Boolean = false,
    var upcNumber: String?=null,
    var prodInfoMap: Map<String,String>?=null,
    var dfData: List<DynamicFormData>?=null
): Parcelable {

    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val locData: CatalogProduct = obj as CatalogProduct
        return locData.pid == this.pid
    }

    override fun hashCode(): Int {
        return this.cid.hashCode()
    }
    companion object {

        @JvmStatic
        @BindingAdapter("load_catalog_product")
        fun loadCatalogProduct(view: ImageView, url: String?) {
            view.setImageResource(R.drawable.ic_picture)// This methods should not have any return type, = declaration would make it return that object declaration.
            if (url != null && url.isNotEmpty()) {
                Glide.with(view.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.ic_picture)
                    .error(R.drawable.ic_picture)
                    .into(view)
            }
        }
        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("set_percentage")
        fun percentageOff(textView: TextView, data: CatalogProduct?){
            textView.text=""
            if(data?.price != null &&data.sellingPrice!=null){
                if(data.price!! > data.sellingPrice!!){
                    var percentage=((data.price!!-data.sellingPrice!!)*100)/data.price!!
                   // var percentage=((data.price!!-data.sellingPrice!!)/data.price!!)/100
                    if(percentage<=100){
                        val decimalFormat = DecimalFormat("#.#")
                        val twoDigitsF: Float = java.lang.Float.valueOf(decimalFormat.format(percentage))
                        textView.text="${twoDigitsF} %off"
                        textView.visibility=View.VISIBLE
                    }else{
                        textView.visibility=View.GONE
                    }

                }else{
                    textView.visibility=View.GONE
                }
            }

        }

    }

}