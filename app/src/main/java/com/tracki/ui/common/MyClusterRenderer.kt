package com.tracki.ui.common

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer


/**
 * Created by rahul on 21/2/19
 */
class MyClusterRenderer : DefaultClusterRenderer<MyClusterItem> {

//    private var mIconGenerator: IconGenerator
//    private var mClusterIconGenerator: IconGenerator

    constructor(context: Context, map: GoogleMap, clusterManager: ClusterManager<MyClusterItem>) :
            super(context, map, clusterManager) {
//        mIconGenerator = IconGenerator(context)
//        mClusterIconGenerator = IconGenerator(context)
//
//        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val multiProfile = layoutInflater.inflate(R.layout.custom_marker, null)
//        mClusterIconGenerator.setContentView(multiProfile)

    }

    override fun onBeforeClusterItemRendered(item: MyClusterItem?, markerOptions: MarkerOptions?) {
        super.onBeforeClusterItemRendered(item, markerOptions)
        markerOptions?.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                ?.title(item?.title)?.snippet(item?.snippet)
    }
}

