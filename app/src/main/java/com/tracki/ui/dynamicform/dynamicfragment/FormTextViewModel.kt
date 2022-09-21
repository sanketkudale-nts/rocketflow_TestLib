package com.tracki.ui.dynamicform.dynamicfragment

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.DataType
import com.tracki.data.model.response.config.TaskData


/**
 * Created by Vikas Kesharvani on 29/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class FormTextViewModel (val formData: TaskData) {
    val title = ObservableField("")
    val enteredValue = ObservableField("")

    init {
        if (formData.label != null) {
            title.set(formData.label)
        }
        if (formData.value != null) {
            if(formData.type== DataType.GEO){
                if (formData.value != null && formData.value!!.isNotEmpty())
                {
//                    val jsonConverter: JSONConverter<HubLocation> = JSONConverter<HubLocation>()
//                    val hubLocation = jsonConverter.jsonToObject(formData.value, HubLocation::class.java) as HubLocation
//                    enteredValue.set(hubLocation.address)
                    enteredValue.set(formData.value)
                }
            }
            else
             enteredValue.set(formData.value)
        }
    }
}