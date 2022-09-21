package com.tracki.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.tracki.R

/**
 * Created by rahul on 26/12/18
 *
 * Class used to show the info window on markers with custom layout.
 *
 */
class CustomInfoWindowAdapter(private var context: Context) : GoogleMap.InfoWindowAdapter {
    private lateinit var view: View

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker?): View {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.info_window, null, false)
        val tvLocationName = view.findViewById(R.id.tvLocationName) as TextView
        val tvStatus = view.findViewById(R.id.tvStatus) as TextView
        tvLocationName.text = marker?.title
        tvStatus.text = marker?.snippet
        return view
    }

}