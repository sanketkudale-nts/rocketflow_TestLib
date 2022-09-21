package com.rocketflyer.rocketflow.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData
import com.tracki.utils.DateTimeUtil

/**
 * Created by Rahul Abrol on 23/1/20.
 */
class FormTimeViewModel(formData: FormData, var listener: TimeListener) {
    val title = ObservableField("")
    val time = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
        var timeEntered = DateTimeUtil.getCurrentTime()
        if (formData.maxRange != 0L) {
            timeEntered = DateTimeUtil.getParsedTime(formData.maxRange)
            time.set(timeEntered)
        }
    }

    fun timeClick() {
        listener.timeClick()
    }

    interface TimeListener {
        fun timeClick()
    }

}