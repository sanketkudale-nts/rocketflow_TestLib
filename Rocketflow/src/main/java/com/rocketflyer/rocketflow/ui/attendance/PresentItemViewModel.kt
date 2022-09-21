package com.rocketflyer.rocketflow.ui.attendance

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.Data
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil

class PresentItemViewModel(var data: Data) {

    var date = ObservableField("")
    var statusColor = ObservableField(0)
    var status = ObservableField("")
    var time = ObservableField("")
    var punchInTime = ObservableField("")
    var punchOutTime = ObservableField("")
    var punchInLocation = ObservableField("")
    var punchOutLocation = ObservableField("")
    var punchInLocationVisible = ObservableField(false)
    var punchOutLocationVisible = ObservableField(false)


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
                if (it.hours != null) {
                    time.set(it.hours)
//                    val splited = it.hours?.split(":".toRegex())
//                    splited.let { inner ->
//                        time.set("${inner!![0]} hrs ${inner[1]} mins")
                } else if (status.get() != "Present") {
                    time.set("N/A")
                } else if (it.hours == null) {
                    time.set("N/A")
                }
            }
            if (it.punchInAt != 0L) {
                punchInTime.set(DateTimeUtil.getParsedTime(it.punchInAt!!))
            }else{
                punchInTime.set("N/A")
            }
            if (it.punchOutAt != 0L) {
                punchOutTime.set(DateTimeUtil.getParsedTime(it.punchOutAt!!))
            }else{
                punchOutTime.set("N/A")
            }

            if (it.punchInData != null && it.punchInData!!.location != null && it.punchInData!!.location!!.address != null) {
                punchInLocation.set(it.punchInData!!.location!!.address)
                punchInLocationVisible.set(true)
            }
            if (it.punchOutData != null && it.punchOutData!!.location != null &&it.punchOutData!!.location!!.address != null) {
                punchOutLocation.set(it.punchOutData!!.location!!.address)
                punchOutLocationVisible.set(true)
            }
        }
    }
}