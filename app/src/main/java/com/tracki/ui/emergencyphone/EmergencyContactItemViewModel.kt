package com.tracki.ui.emergencyphone

import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.EmergencyContact

/**
 * Created by rahul on 7/12/18
 */
class EmergencyContactItemViewModel(private val response: EmergencyContact, val listener: ItemClickListener) {
    val name: ObservableField<String> = ObservableField(response.name!!)
    val mobile: ObservableField<String> = ObservableField(response.mobile!!)

    fun onEditClick() {
        listener.onEditClick(response)
    }

    fun onDeleteClick() {
        listener.onDeleteClick(response)
    }

    interface ItemClickListener {
        fun onEditClick(response: EmergencyContact)
        fun onDeleteClick(response: EmergencyContact)
        //fun onItemClick(response: ContactResponse)
    }
}