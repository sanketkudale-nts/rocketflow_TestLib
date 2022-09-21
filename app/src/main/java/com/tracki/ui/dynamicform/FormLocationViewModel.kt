package com.tracki.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData


class FormLocationViewModel(formData: FormData) {

    val title = ObservableField("")
    val hint = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
        if (formData.placeHolder != null) {
            hint.set(formData.placeHolder)
        }
    }

}