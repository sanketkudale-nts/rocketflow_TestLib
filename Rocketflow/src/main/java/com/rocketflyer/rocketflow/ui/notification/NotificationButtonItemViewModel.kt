package com.rocketflyer.rocketflow.ui.notification//package com.tracki.ui.notification
//
//import android.databinding.ObservableField
//import android.view.View
//import com.tracki.data.model.NotificationResponse
//
///**
// * Created by rahul on 11/10/18
// */
//open class NotificationButtonItemViewModel {
//
//    val listener: NotificationButtonListener
//    val message: ObservableField<String>
//    val messageDateTime: ObservableField<String>
//    val image: ObservableField<String>
//    private val response: NotificationResponse
//
//    constructor(response: NotificationResponse, listener: NotificationButtonListener) {
//        this.response = response
//        this.listener = listener
//        val name = response.name
//        val spilt = name.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
//        val buffer = StringBuilder()
//        buffer.append(Character.toUpperCase(spilt[0].get(0)))
//        try {
//            if (spilt.size > 1) {
//                buffer.append(Character.toUpperCase(spilt[1].get(0)))
//            }
//        } catch (e: ArrayIndexOutOfBoundsException) {
//            e.printStackTrace()
//        }
//        image = ObservableField(buffer.toString())
//        message = ObservableField(name)
//        messageDateTime = ObservableField("${response.date}  ${response.time}")
//    }
//
//
//    fun onAcceptClicked(view: View) {
//        listener.onAcceptClick(view, response)
//    }
//
//    fun onRejectClicked(view: View) {
//        listener.onRejectClick(view, response)
//    }
//
//    interface NotificationButtonListener {
//        fun onAcceptClick(view: View, response: NotificationResponse)
//        fun onRejectClick(view: View, response: NotificationResponse)
//    }
//}