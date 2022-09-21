package com.tracki.ui.common

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by rahul on 27/12/18
 */
class MyClusterItem : ClusterItem {

    private var mPosition: LatLng? = null
    private var mTitle: String? = null
    private var mSnippet: String? = null //brief info


    constructor(lat: LatLng, title: String, snippet: String) {
        mPosition = lat
        mTitle = title
        mSnippet = snippet
    }

    override fun getSnippet() = mSnippet
    override fun getTitle() = mTitle
    override fun getPosition() = mPosition
}