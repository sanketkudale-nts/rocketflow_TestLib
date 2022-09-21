package com.rocketflyer.rocketflow.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData
import com.tracki.utils.DateTimeUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by rahul on 26/3/19
 */
class FormDateTimeViewModel(formData: FormData, var listener: DateTimeListener) {
    val title = ObservableField("")
    val date = ObservableField("")
    val time = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
        var dateEntered = DateTimeUtil.getCurrentDate()
        var timeEntered = DateTimeUtil.getCurrentTime()
        if (formData.enteredValue != null && formData.enteredValue != "") {
            //val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm")
            val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm")
            val dateParse: Date = sdf.parse(formData.enteredValue)
            val millis: Long = dateParse.getTime()

            dateEntered = DateTimeUtil.getParsedDate(millis)
            timeEntered = DateTimeUtil.getParsedTime(millis)
            date.set(dateEntered)
            time.set(timeEntered)
        }
    }

    fun dateClick() {
        listener.dateClick()
    }

    fun timeClick() {
        listener.timeClick()
    }

    interface DateTimeListener {
        fun dateClick()
        fun timeClick()
    }

}