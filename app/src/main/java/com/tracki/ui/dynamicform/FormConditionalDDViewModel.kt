package com.tracki.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

/**
 * Created by Rahul Abrol on 21/1/20.
 */
class FormConditionalDDViewModel(formData: FormData) {

    val title = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
    }
}