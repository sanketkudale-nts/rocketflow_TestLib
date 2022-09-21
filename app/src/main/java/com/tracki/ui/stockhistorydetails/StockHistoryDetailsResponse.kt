package com.tracki.ui.stockhistorydetails
import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.gson.annotations.SerializedName
import com.tracki.utils.DateTimeUtil


data class StockHistoryDetailsResponse(
    @SerializedName("data")
    var `data`: ArrayList<StockHistoryDetails>?,
    @SerializedName("successful")
    var successful: Boolean?,
    @SerializedName("totalCount")
    var totalCount: Int=0
)

data class StockHistoryDetails(
    @SerializedName("closingStock")
    var closingStock: Double?,
    @SerializedName("createdAt")
    var createdAt: Long?,
    @SerializedName("event")
    var event: String?,
    @SerializedName("openingStock")
    var openingStock: Double?,
    @SerializedName("pid")
    var pid: String?,
    @SerializedName("price")
    var price: Double?,
    @SerializedName("qty")
    var qty: Int?,
    @SerializedName("totalAmt")
    var totalAmt: Double?,
    @SerializedName("transactionId")
    var transactionId: String?,
    @SerializedName("userInfo")
    var userInfo: UserInfo?
){
    companion object{
        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("set_stock_time")
        fun setStockTime(textView: TextView, date: Long?){
            textView.text=""
            if(date!=null){
                var strDate= " "+DateTimeUtil.getParsedTime1(date)
                textView.text=strDate
            }

        }
        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("set_stock_his_date")
        fun setStockHistoryDate(textView: TextView, date: Long?){
            textView.text=""
            if(date!=null){
                var strDate= DateTimeUtil.getParsedDate4(date)
                textView.text=strDate
            }

        }
        @SuppressLint("SetTextI18n")
        @JvmStatic
        @BindingAdapter("set_stock_type")
        fun setStockType(textView: TextView, event: String?){
            textView.text=""
            if(event!=null){
                var type= event.lowercase().capitalize()
                textView.text=type
            }

        }

    }
}

data class UserInfo(
    @SerializedName("address")
    var address: Any?,
    @SerializedName("designation")
    var designation: Any?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("firstName")
    var firstName: String?,
    @SerializedName("lastName")
    var lastName: Any?,
    @SerializedName("mobile")
    var mobile: String?,
    @SerializedName("profileImg")
    var profileImg: Any?,
    @SerializedName("userId")
    var userId: String?
)