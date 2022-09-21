package com.rocketflyer.rocketflow.ui.category

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProductDescription : Parcelable {
    var heading: String? = null
    var content: String? = null
}