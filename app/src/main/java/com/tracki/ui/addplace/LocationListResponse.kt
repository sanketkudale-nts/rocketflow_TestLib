package com.tracki.ui.addplace

import android.content.Context
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.gms.maps.model.LatLng
import com.tracki.utils.CommonUtils
import kotlinx.android.parcel.Parcelize

data class LocationListResponse(
        var hubs: List<Hub>?,
        var regionCityMap: List<RegionCityMap>?,
        var successful: Boolean?
)

 enum class PLACETYPE{
     USER,ADMIN
 }
@Parcelize
data class Hub(
        var cityId: String?=null,
        var hubId: String?=null,
        var hubLocation: HubLocation?=null,
        var name: String?=null,
        var regionId: String?=null,
        var type: String?=null
) : Parcelable {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["bind:latitude", "bind:longitude","bind:Context"], requireAll = false)
        fun getAddress(textView: TextView, latitude: Double?, longitude: Double?,context:Context) {
            if (latitude != null && longitude !=null) {
                  var latlong=LatLng(latitude,longitude)
                textView.text = CommonUtils.getAddress(context,latlong)
            }
        }
        @JvmStatic
        @BindingAdapter("type")
        fun setVisibility(imageView: ImageView, type: String?) {
            if (type != null) {
                if(type==PLACETYPE.USER.name){
                    imageView.visibility= View.VISIBLE
                }else if(type==PLACETYPE.ADMIN.name){
                    imageView.visibility= View.GONE
                }else{
                    imageView.visibility= View.GONE
                }

            }
        }
    }

}

data class RegionCityMap(
        var cities: List<City>?,
        var regionId: String?,
        var regionName: String?
)


data class City(
        var cityId: String?,
        var name: String?,
        var status: String?
)