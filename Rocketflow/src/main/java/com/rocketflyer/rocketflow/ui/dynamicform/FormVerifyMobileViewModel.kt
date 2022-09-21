package com.rocketflyer.rocketflow.ui.dynamicform

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.tracki.data.model.response.config.FormData

class FormVerifyMobileViewModel(val formData: FormData) {
    val title = ObservableField("")
    val position = ObservableInt(0)
    val mobileHint = ObservableField("Enter Mobile")
    val mobileNumber = ObservableField("")
    val otp = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
    }
}