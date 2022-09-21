package com.tracki.data.local.db


/**
 * Created by rahul on 2/1/19
 */
class ApiEventModel {
    var id: Int? = null
    var time = 0L
    var tripId: String? = null
    var action: Action? = null
    var data: Any? = null
    var formList: Any? = null
    var taskId: String? = null
    var taskAction: String? = null
    var isAutoStart: Boolean? = null
    var isAutoEnd: Boolean? = null
    var isAutoCancel: Boolean = false
    var formId:String?=null
    var ctaId:String?=null
}