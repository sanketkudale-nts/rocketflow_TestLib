package com.rocketflyer.rocketflow.ui.productdetails
import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.gson.annotations.SerializedName
import com.tracki.utils.DateTimeUtil


data class StockEntryResponse(
    @SerializedName("data")
    var `data`: ArrayList<StockEntry>?,
    @SerializedName("inputStock")
    var inputStock: Int?,
    @SerializedName("outputStock")
    var outputStock: Int?,
    @SerializedName("successful")
    var successful: Boolean?,
    var count: Int = 0
)


data class StockEntry(
    @SerializedName("closingStock")
    var closingStock: Int?,
    @SerializedName("date")
    var date: Long?,
    @SerializedName("inputStock")
    var inputStock: Int?,
    @SerializedName("openingStock")
    var openingStock: Int?,
    @SerializedName("outputStock")
    var outputStock: Int?,
    @SerializedName("totalPurchasePrice")
    var totalPurchasePrice: Double?,
    @SerializedName("totalSellingPrice")
    var totalSellingPrice: Double?
){
    companion object{
        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("set_stock_date")
        fun setStockDate(textView: TextView, date: Long?){
            textView.text=""
            if(date!=null){
                 var strDate=DateTimeUtil.getParsedDate4(date)
                textView.text=strDate
            }

        }

    }
}