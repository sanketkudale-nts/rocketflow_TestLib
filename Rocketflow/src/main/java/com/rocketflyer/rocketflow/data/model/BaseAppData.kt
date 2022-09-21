package com.rocketflyer.rocketflow.data.model

import com.google.gson.annotations.SerializedName
import com.tracki.data.model.response.config.Api
import com.tracki.utils.ShiftTime


/**
 * Created by Vikas Kesharvani on 17/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
data class BaseAppData(
        var apiInfo: Api? = null,
        @SerializedName("first-install")
        var first_install: Int? = null,
        @SerializedName("app-id")
        var app_id: String? = null,
        @SerializedName("app-type")
        var app_type: String? = null,
        @SerializedName("app-version")
        var app_version: String? = null,
        @SerializedName("device-id")
        var device_id: String? = null,
        @SerializedName("login-token")
        var login_token: String? = null,
        @SerializedName("tracki-ai")
        var tracki_ai: String? = null,
        @SerializedName("tracki-at")
        var tracki_at: String? = null,
        @SerializedName("tracki-ts")
        var tracki_ts: String? = null,
        @SerializedName("vname")
        var vname: String? = null,
        var startalrm: AlarmInfo? = null,
        var endalarm: AlarmInfo? = null,
        var userId: String? = null,
        var punchIn: String? = null,
        var punchId: String? = null,
        var islogin: String? = null,
        var shift: MutableMap<Int?, MutableList<ShiftTime>>?=null

)

