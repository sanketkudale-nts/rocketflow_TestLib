package com.tracki.data.model.response.config

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Vikas Kesharvani on 23/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
data class EmployeeListResponse(
    var `data`: List<EmpData>? = null,
    var successful: Boolean? = null,
    var totalCount: Int? = null
)


@Parcelize
data class EmpData(
    var email: String? = null,
    var mobile: String? = null,
    var name: String? = null,
    var status: String? = null,
    var userId: String? = null,
    var userImg: String? = null,
    var roleId: String? = null,
    var punchInLoc: PunchLocationData? = null,
    var punchOutLoc: PunchLocationData? = null,
    var punchIn: Long = 0L,
    var punchOut: Long = 0L,
    var subordinate:Boolean? = false
): Parcelable