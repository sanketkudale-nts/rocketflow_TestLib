package com.tracki.ui.feeds

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeInfo(
    var title: String?=null,
    var from: Long?=null,
    var to: Long?=null,
    var isSelected:Boolean=false
): Parcelable