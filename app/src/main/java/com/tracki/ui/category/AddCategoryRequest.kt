package com.tracki.ui.category

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddCategoryRequest(
    var id: String? = null,
    var cid: String? = null,
    var parentCategoryId: String? = null,
    var img: String? = null,
    var name: String? = null,
    var flavorId: String? = null,
    var subCategory: Boolean = false,
    var descriptions: List<ProductDescription>? = null
):Parcelable