package com.tracki.data.model.request

import java.io.Serializable


/**
 * Created by Vikas Kesharvani on 20/10/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class UserGetRequest: Serializable {
    var fetchAddress: Boolean? = true
    var limit: Int? = 0
    var offset: Int? = 0
    var roleId: String? = ""
    var roleIds: ArrayList<String>? = ArrayList()
    var type:String? = "INTERNAL"
}