package com.rocketflyer.rocketflow.ui.dynamicformpreview

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

class FormPreviewItemViewModel(val formData: FormData) {
    val title = ObservableField("")
    val enteredValue = ObservableField("")

    init {
        if (formData.name != null) {
            title.set(formData.name)
        }
        if (formData.enteredValue != null) {
            enteredValue.set(formData.enteredValue)
        }
    }
}