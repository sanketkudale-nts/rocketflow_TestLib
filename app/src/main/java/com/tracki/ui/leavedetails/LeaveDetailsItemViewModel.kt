package com.tracki.ui.leavedetails

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.Summary
import com.tracki.utils.CommonUtils

class LeaveDetailsItemViewModel(var data: Summary) {
    var leaveRemaining = ObservableField("")
    var leaveAllowed = ObservableField("")
    var leaveTaken = ObservableField("")
    var leaveType= ObservableField("")
    init {
        leaveRemaining.set(data.remaing.toString())
        leaveAllowed.set(data.allowed.toString())
        leaveTaken.set(data.taken.toString())
        if(data.type!=null)
        leaveType.set(CommonUtils.getLeaveTypeString(data.type))
    }

}