package com.tracki.data.model

/**
 * Created by rahul on 29/12/18
 */
class AlarmData {
    var isStart: Boolean? = null
    var currentTripId: String? = null
    val hubIn: Boolean? = null
    val hubOut: Boolean? = null
}

class AlarmInfo {

    var currentDay: Int? = null
    var hour: Int = 0
    var minute: Int = 0
    var dayAdded: Int = 0
    var requestCode: Int? = null
    var isAlarmSet = false
    var isExecuted: Boolean = false

    override fun toString(): String {
        return "{" +
                "currentDay: " + currentDay +
                ", hour: " + hour +
                ", minute: " + minute +
                ", dayAdded: " + dayAdded +
                ", requestCode: " + requestCode +
                ", isAlarmSet: " + isAlarmSet +
                ", isExecuted: " + isExecuted +
                "}"
    }
}

class FileUrlsResponse : BaseResponse() {
    val filesUrl: HashMap<String, ArrayList<String>>? = null
}

class DynamicFormRequest {
    var visitData: HashMap<String, String>? = null
    var visitId: String? = null
}