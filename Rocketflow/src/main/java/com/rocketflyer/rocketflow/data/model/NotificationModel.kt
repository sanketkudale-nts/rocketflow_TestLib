package com.rocketflyer.rocketflow.data.model

import com.tracki.data.model.response.config.ConfigResponse
import com.tracki.data.model.response.config.HubLocation
import com.tracki.utils.ShiftTime

/**
 * Created by Rahul Abrol on 10/7/18.
 */
class NotificationModel {
    val event = NotificationEventStatus.UNKNOWN
    var trackingAction = TrackingAction.END
    val taskId: String? = null
    val sound: String? = null
    val channel_id: String? = null
    var timeStamp:Long=0

    val hubLocations: List<HubLocation?>? = null
    val shiftTimes: Map<Int, List<ShiftTime?>?>? = null
    val configResponse: ConfigResponse? = null
    val event2: String? = null
    val categoryId: String? = null
}

enum class NotificationEventStatus {
    ARRIVED, COMPLETED, UNKNOWN,PUNCH_OUT,PUNCH_IN,TRACKING_STATE_CHANGE
}

enum class TrackingAction {
    END,START
}

