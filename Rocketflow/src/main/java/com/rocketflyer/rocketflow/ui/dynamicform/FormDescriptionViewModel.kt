package com.rocketflyer.rocketflow.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

/**
 * Created by rahul on 29/3/19
 */
class FormDescriptionViewModel(val formData: FormData) {
    val title = ObservableField("")
    val hint = ObservableField("")
    val length = ObservableField(100)
    var enteredValue = ObservableField("")

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