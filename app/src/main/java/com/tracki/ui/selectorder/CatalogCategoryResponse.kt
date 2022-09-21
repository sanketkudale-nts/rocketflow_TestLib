package com.tracki.ui.selectorder

import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tracki.R
import com.tracki.ui.category.ProductDescription
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CatalogCategoryResponse(
    var `data`: ArrayList<CataLogProductCategory>?,
    var count: Int=0,
    var loginToken: String?,
    var responseCode: String?,
    var responseMsg: String?,
    var successful: Boolean
): Parcelable

@Parcelize
data class CataLogProductCategory(
    var active: Boolean?=null,
    var cid: String?,
    var img: String?,
    var name: String?,
    var subCat: Boolean?,
    var descriptions: ArrayList<ProductDescription>? = null

): Parcelable {
    var selected: Boolean?=false
    companion object {

        @JvmStatic
        @BindingAdapter("load_catalog")
        fun loadCatalog(view: ImageView, url: String?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            view.setImageResource(R.drawable.ic_picture)
            if (url != null && url.isNotEmpty()) {
                Glide.with(view.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.ic_picture)
                    .error(R.drawable.ic_picture)
                    .into(view)
            }
        }

    }

}