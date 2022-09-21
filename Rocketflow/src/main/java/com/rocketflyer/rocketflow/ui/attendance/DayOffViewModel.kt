package com.rocketflyer.rocketflow.ui.attendance

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.Data
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil
import java.util.*

class DayOffViewModel(var data: Data) {

    var date = ObservableField("")
    var statusColor = ObservableField(0)
    var status = ObservableField("")
    var time = ObservableField("")
    var day = ObservableField("")


  fun getDay(timeStamp: Long):Int{
      val c: Calendar = Calendar.getInstance()
      c.timeInMillis = timeStamp

      val dayNum: Int = c.get(Calendar.DAY_OF_WEEK)

      return dayNum
  }

    init {
        data.let {
            if (it.date != 0L) {
                date.set(DateTimeUtil.getParsedDate(it.date))
                when(getDay(it.date)){
                    1->{
                        day.set("Sunday")
                    }
                    2->{
                        day.set("Monday")
                    }
                    3->{
                        day.set("Tuesday")
                    }
                    4->{
                        day.set("Wednesday")
                    }
                    5->{
                        day.set("Thursday")
                    }
                    6->{
                        day.set("Friday")
                    }
                    7->{
                        day.set("Saturday")
                    }
                }
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

        }
    }
}