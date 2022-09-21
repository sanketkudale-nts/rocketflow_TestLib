package com.tracki.ui.login

import android.os.Parcelable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.RequestOptions
import com.tracki.R
import com.tracki.data.model.BaseResponse
import com.tracki.ui.custom.GlideApp
import com.tracki.utils.NextScreen
import kotlinx.android.parcel.Parcelize


/**
 * Created by Vikas Kesharvani on 16/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
@Parcelize
data class SendOtpResponse(
    var refreshConfig: Boolean? = null,
    var draftId: String? = null,
    var userAccounts: List<UserAccount>? = null
): BaseResponse(),Parcelable{

}

@Parcelize
data class UserAccount(
    var accessId: String? = null,
    var defaultAcc: Boolean? = null,
    var logoUrl: String? = null,
    var merchantName: String? = null,
    var isSelected: Boolean? = false
):Parcelable
{
    companion object{

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, url: String?) { // This methods should not have any return type, = declaration would make it return that object declaration.
           if(url!=null && url.isNotEmpty()) {
               GlideApp.with(view.context)
                       .load(url)
                       .apply(RequestOptions().circleCrop()
                               .placeholder(R.drawable.ic_my_profile))
                       .error(R.drawable.ic_my_profile)
                       .into(view)
           }
        }
        @JvmStatic
        @BindingAdapter("bgactive")
        fun bgActive(view: LinearLayout, defaultAcc: Boolean?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if(defaultAcc!=null && defaultAcc) {
                view.background=ContextCompat.getDrawable(view.context,R.drawable.bg_user_list_item_active)
            }else{
                view.background=ContextCompat.getDrawable(view.context,R.drawable.bg_user_list_item_normal)
            }
        }
        @JvmStatic
        @BindingAdapter("accounttextcolor")
        fun selectedTextColor(view: TextView, defaultAcc: Boolean?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if(defaultAcc!=null && defaultAcc) {
                view.setTextColor(ContextCompat.getColor(view.context,R.color.white))
            }else{
                view.setTextColor(ContextCompat.getColor(view.context,R.color.black))
            }
        }
    }
}