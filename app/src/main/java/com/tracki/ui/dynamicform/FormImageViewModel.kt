package com.tracki.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData


/**
 * Created by Vikas Kesharvani on 19/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class FormImageViewModel(formData: FormData) {

    val title = ObservableField("")

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }

    }

}