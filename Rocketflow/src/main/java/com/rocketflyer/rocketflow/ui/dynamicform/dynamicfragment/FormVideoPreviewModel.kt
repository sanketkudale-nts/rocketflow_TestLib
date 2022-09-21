package com.rocketflyer.rocketflow.ui.dynamicform.dynamicfragment

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.tracki.data.model.response.config.TaskData


/**
 * Created by Vikas Kesharvani on 30/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */

class FormVideoPreviewModel(val formData: TaskData) {
    val title = ObservableField("")
    val position = ObservableInt(0)

    init {
        if (formData.label != null) {
            title.set(formData.label)
        }
    }
}