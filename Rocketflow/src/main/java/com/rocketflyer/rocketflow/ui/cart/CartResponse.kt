package com.rocketflyer.rocketflow.ui.cart

import android.os.Parcelable
import com.tracki.ui.selectorder.CatalogProduct
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartResponse(
        var `data`: ArrayList<CatalogProduct>?,
        var successful: Boolean,
        var responseCode: String?=null,
        var responseMsg: String?=null,
        var totalAmount: Float?,
        var totalDiscount: Float?,
        var totalQty: Int?,
        var totalSaving: Float?

):Parcelable
