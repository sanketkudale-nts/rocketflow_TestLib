package com.rocketflyer.rocketflow.ui.dynamicform

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

/**
 * Created by Rahul Abrol on 15/7/19.
 */
class FormSignatureViewModel(val formData: FormData, onButtonClickListener: OnResetClickListener) {
    val title = ObservableField("")
    private var onButtonClickListener: OnResetClickListener? = onButtonClickListener

    init {
        if (formData.title != null) {
            title.set(formData.title)
        }
    }

    fun onResetClick() {
        onButtonClickListener?.onClickButton()
    }

    interface OnResetClickListener {
        fun onClickButton()
    }
}