package com.rocketflyer.rocketflow.ui.dynamicform.dynamicfragment

import com.tracki.data.model.response.config.DynamicActionConfig
import com.tracki.data.model.response.config.FormData

/**
 * Created by Rahul Abrol on 14/7/19.
 */
interface FormSubmitListener {

    fun onProcessClick(list: ArrayList<FormData>, dynamicActionConfig: DynamicActionConfig?, currentFormId: String?,dfdid:String?)
    fun showLoading()
}