package com.tracki.ui.leavedetails

import com.tracki.data.model.response.config.Summary
import java.io.Serializable

class LeaveDetailsData:Serializable {
    var summaryList:List<Summary>?=null
    var remainingLeave = 0
    var takenLeave = 0
    var totalLeave = 0
}