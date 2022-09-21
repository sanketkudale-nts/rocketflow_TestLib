package com.rocketflyer.rocketflow.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

/**
 * Created by Rahul Abrol on 23/1/20.
 */
class FormCalculateViewModel(formData: FormData) {

    val title = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
    }
}