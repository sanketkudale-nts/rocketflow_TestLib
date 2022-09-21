package com.tracki.ui.cart

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class CreateOrderRequest(
    var addressId: String?=null,
    var categoryId: String?=null,
    var customerId: String?=null,
    var couponPromo: String?=null,
    var mode: FullFillSettingMode?=null,
    var products: Map<String,Int>?=null
)

@Parcelize
enum class FullFillSettingMode: Parcelable {
    DELIVERY, PICKUP
}


