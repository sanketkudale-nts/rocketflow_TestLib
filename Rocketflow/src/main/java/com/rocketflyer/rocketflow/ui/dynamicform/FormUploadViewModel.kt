package com.rocketflyer.rocketflow.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

/**
 * Created by rahul on 26/3/19
 */
class FormUploadViewModel(fieldName: FormData, var listener: UploadListener) {
    val title = ObservableField("")
    val imageName = ObservableField("")
    var formData: FormData = fieldName

    init {
        title.set(fieldName.title!!)
        var image = ""
        if (formData.enteredValue != null && formData.enteredValue != "") {
            image = formData.enteredValue!!
        }
        imageName.set(image)
    }

    fun upload() {
        listener.onUploadClick(formData)
    }

    interface UploadListener {
        fun onUploadClick(formData: FormData)
    }
}