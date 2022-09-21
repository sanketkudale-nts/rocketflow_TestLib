package com.rocketflyer.rocketflow.ui.dynamicform.dynamicfragment

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.TaskData


/**
 * Created by Vikas Kesharvani on 29/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class FormSingleImageViewModel(formData: TaskData) {

    val title = ObservableField("")

    init {
        if (formData.label != null) {
            title.set(formData.label)
        }

    }

}