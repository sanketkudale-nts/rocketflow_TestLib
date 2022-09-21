package com.tracki.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

class FormToggleViewModel(val formData: FormData) {
    val title = ObservableField("")

    init {
        if (formData.title!=null) {
            title.set(formData.title)
        }
    }

}