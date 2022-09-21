package com.rocketflyer.rocketflow.ui.attendance

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.Data
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil

class OnLeaveViewModel(var data: Data) {

    var date = ObservableField("")
    var statusColor = ObservableField(0)
    var status = ObservableField("")
    var time = ObservableField("")
    var fromTime = ObservableField("")
    var toTime= ObservableField("")
    var leaveType = ObservableField("")
    var LeaveDays = ObservableField("")



    init {
        data.let {
            if (it.date != 0L) {
                date.set(DateTimeUtil.getParsedDate(it.date))
            }

            if (it.leaveType != null) {
                it.leaveType.let { stat ->
                    leaveType.set(CommonUtils.getLeaveTypeString(data.leaveType))
                }

            }
            if (it.from != 0L) {
                fromTime.set(DateTimeUtil.getParsedDate(it.from))
            }
            if (it.to != 0L) {
                toTime.set(DateTimeUtil.getParsedDate(it.to))
            }
            if (it.dayKeys!=null&&it.dayKeys!!.isNotEmpty()) {
                var days=""+it.dayKeys!!.size
                LeaveDays.set(days)
            }

        }
    }
}