package com.rocketflyer.rocketflow.ui.attendance

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.Data
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil

class AbsentViewModel(var data: Data) {

    var date = ObservableField("")
    var statusColor = ObservableField(0)
    var status = ObservableField("")
    var time = ObservableField("")



    init {
        data.let {
            if (it.date != 0L) {
                date.set(DateTimeUtil.getParsedDate(it.date))
            }

            if (it.status != null) {
                it.status.let { stat ->
                    statusColor.set(CommonUtils.getAttendanceStatusColor((stat)))
                    status.set(CommonUtils.getStatusString(stat))
                }
                time.set("0 hrs 0 Min")
            }

        }
    }
}