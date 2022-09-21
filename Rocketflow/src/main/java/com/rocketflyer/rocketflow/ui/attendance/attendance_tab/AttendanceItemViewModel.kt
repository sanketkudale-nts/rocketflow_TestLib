package com.rocketflyer.rocketflow.ui.attendance.attendance_tab


import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.Data
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil

class AttendanceItemViewModel(var data: Data) {

    var date = ObservableField("")
    var statusColor = ObservableField(0)
    var status = ObservableField("")
    var time = ObservableField("")

    init {
        data.let {
            if (it.date != 0L) {
                date.set(DateTimeUtil.getParsedDate(it.date))
            }

            if (it.status!=null) {
                it.status.let { stat ->
                    statusColor.set(CommonUtils.getAttendanceStatusColor((stat)))
                    status.set(CommonUtils.getStatusString(stat))
                }
                if (it.hours != null) {
                    time.set(it.hours)
//                    val splited = it.hours?.split(":".toRegex())
//                    splited.let { inner ->
//                        time.set("${inner!![0]} hrs ${inner[1]} mins")
                }else if(status.get() != "Present"){
                    time.set("N/A")
                }else if(it.hours==null){
                    time.set("N/A")
                }
            }
        }
        }
    }

