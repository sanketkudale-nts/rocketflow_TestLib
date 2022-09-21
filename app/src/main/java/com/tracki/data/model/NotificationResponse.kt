package com.tracki.data.model

import java.io.Serializable

/**
 * Created by rahul on 11/10/18
 */
data class NotificationResponse(
        var notificationId: String,
        var event: NotificationEvent,
        var visited: Boolean,
        var time: Long,
        var actionable: Boolean,
        var data: NotificationData) : Serializable

enum class NotificationType {
    EMPTY, SIMPLE, BUTTONS
}

enum class NotificationEvent {
    RECEIVE_INVITATION,
    ACCEPT_INVITATION,
    REJECT_INVITATION,
    ASSIGN_TASK,
    ACCEPT_TASK,
    REJECT_TASK,
    CANCEL_TASK,
    START_TASK,
    END_TASK,
    CANCEL_TRACKING_REQUEST,
    AUTO_CANCEL_TASK
}

class NotificationData {
    var invitationId: String? = null
    var taskId: String? = null
    var taskName: String? = null
    //    var shift: Shift? = null
    var invitorName: String? = null
    var inviteeName: String? = null
    var taskAssignedBy: String? = null
    var taskAssignedTo: String? = null
}
