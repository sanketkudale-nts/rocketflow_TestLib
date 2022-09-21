package com.tracki.ui.dynamicformpreview

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

class FormPicturesItemViewModel {

    val title: ObservableField<String> = ObservableField("")
    val formData: FormData

    constructor(formData: FormData) {
        this.formData = formData
        if (formData.name != null) {
            title.set(formData.name)
        }
    }
}