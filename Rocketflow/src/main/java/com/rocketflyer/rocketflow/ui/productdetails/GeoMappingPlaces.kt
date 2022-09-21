package com.rocketflyer.rocketflow.ui.productdetails

import android.os.Parcelable
import com.tracki.ui.category.ProductDescription
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeoMappingPlaces(
    var geoId:String? = null,
    var geoName:String? =null

) : Parcelable {
    var selected: Boolean?=false
}