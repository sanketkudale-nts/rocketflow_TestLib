package com.tracki.ui.dynamicform

import android.view.View
import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData
import com.tracki.utils.DateTimeUtil

/**
 * Created by rahul on 26/3/19
 */
class FormDateRangeViewModel(formData: FormData, var listener: DateRangeListener) {

    val title = ObservableField("")
    val date1 = ObservableField("")
    val date2 = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
        var date12 = DateTimeUtil.getCurrentDate()
        var date21 = DateTimeUtil.getCurrentDate()
        if (formData.maxRange != 0L) {
            date12 = DateTimeUtil.getParsedDate(formData.maxRange)
            date1.set(date12)
        }
        if (formData.minRange != 0L) {
            date21 = DateTimeUtil.getParsedDate(formData.minRange)
            date2.set(date21)
        }
    }

    fun dateClick(view: View) {
        listener.dateViewClick(view)
    }

    interface DateRangeListener {
        fun dateViewClick(x: View)
    }
}