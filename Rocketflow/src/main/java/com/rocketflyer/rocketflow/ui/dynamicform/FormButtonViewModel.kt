package com.rocketflyer.rocketflow.ui.dynamicform

import android.os.Handler
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.FormData

class FormButtonViewModel(val formData: FormData, onButtonClickListener: OnButtonClickListener) {
    val title = ObservableField("Proceed")
    val isEnable = ObservableBoolean(true)
    private var onButtonClickListener: OnButtonClickListener? = onButtonClickListener

    init {
        if (formData.value != null) {
            title.set(formData.value)
        }
    }

    fun onButtonClick() {
        try {
            if (isEnable.get()) {
                isEnable.set(false)
            }
            Handler().postDelayed({
                isEnable.set(true)
            }, 3000)
            onButtonClickListener?.onClickButton(formData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnButtonClickListener {
        fun onClickButton(formData: FormData)
    }
}
