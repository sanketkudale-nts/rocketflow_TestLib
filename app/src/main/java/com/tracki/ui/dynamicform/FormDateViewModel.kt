package com.tracki.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData
import com.tracki.utils.DateTimeUtil

/**
 * Created by rahul on 28/3/19
 */
class FormDateViewModel(formData: FormData, listener: DateListener) {
    val title = ObservableField("")
    var date = ObservableField("")
    var listener: DateListener? = listener

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
        if (formData.maxRange != 0L) {
            val date21 = DateTimeUtil.getParsedDate(formData.maxRange)
            date.set(date21)
        }
    }

    fun onDateClick() {
        listener?.onDateClick()
    }

    interface DateListener {
        fun onDateClick()
    }
}