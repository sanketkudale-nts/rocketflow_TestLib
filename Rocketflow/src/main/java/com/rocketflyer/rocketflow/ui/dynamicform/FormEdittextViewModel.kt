package com.rocketflyer.rocketflow.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

class FormEdittextViewModel(val formData: FormData) {
    var enteredValue = ObservableField("")
    var title = ObservableField("")
    var hint = ObservableField("")
    var length = ObservableField(20)

    init {
        if (formData.enteredValue != null) {
            enteredValue.set(formData.enteredValue)
        }
        if (formData.title != null) {
            title.set(formData.title)
        }
        if (formData.placeHolder != null) {
            hint.set(formData.placeHolder)
        }
        if (formData.maxLength != 0) {
            length.set(formData.maxLength)
        }
    }

}