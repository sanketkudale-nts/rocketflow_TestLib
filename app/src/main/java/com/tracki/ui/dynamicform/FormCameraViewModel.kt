package com.tracki.ui.dynamicform

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.tracki.data.model.response.config.FormData


class FormCameraViewModel(val formData: FormData) {
    val title = ObservableField("")
    val position = ObservableInt(0)

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
    }
}