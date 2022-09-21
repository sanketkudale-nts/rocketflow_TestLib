package com.tracki.ui.taskdetails

import java.io.Serializable

data class StageHistory(
    var stageHistory: List<StageHistoryData>?
)

data class StageHistoryData(
    var buddies: List<String>?,
    var cta: String?,
    var ctaName: String?,
    var ctaId: String?,
    var dfdId: String?,
    var id: String?,
    var properties: Any?,
    var stage: Stage?,
    var timestamp: Long?,
    var timeStamp: Long?,
    var trackingState: String?
): Serializable

data class Stage(
    var callToActions: List<CallToAction>?,
    var id: String?,
    var initial: Boolean?,
    var name: String?,
    var postActions: List<String>?,
    var preActions: Any?,
    var terminal: Boolean?
)

data class CallToAction(
    var actions: Any?,
    var allowedToRoles: Any?,
    var conditions: Any?,
    var dynamicFormEditable: Boolean?,
    var dynamicFormId: String?,
    var dynamicFormPermissions: Any?,
    var executor: String?,
    var id: String?,
    var level: String?,
    var name: String?,
    var nextStageId: String?,
    var primary: Boolean?,
    var categoryId: String?,
    var createTask: Boolean?,
    var properties: Any?,
    var tracking: String?
)