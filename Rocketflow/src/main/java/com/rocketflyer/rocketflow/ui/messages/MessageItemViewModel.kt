package com.rocketflyer.rocketflow.ui.messages

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.Buddy
import com.tracki.utils.DateTimeUtil

class MessageItemViewModel(private val response: Buddy,
                           val listener: MessageItemClickListener,
                           context: Context) {

    var message: ObservableField<String> = ObservableField("")
    var name: ObservableField<String> = ObservableField("")
    var time: ObservableField<String> = ObservableField("")
    var shortCode: ObservableField<String> = ObservableField("")
    var status: ObservableBoolean = ObservableBoolean(false)

    init {
        if (response.name != null) {
            name.set(response.name)
        }
        if (response.lastMessage != null) {
            when (response.type) {
                "TEXT" -> {
                    message.set(response.lastMessage)
                }
                "IMAGE" -> {
                    message.set("Image")
                }
                "DOCUMENT" -> {
                    message.set("Document")
                }
                "VIDEO" -> {
                    message.set("Video")
                }
                "AUDIO" -> {
                    message.set("Audio")
                }
                else -> {

                }
            }

        }
//        if (response.lastMessage != null) {
//            message.set(response.lastMessage)
//        }
        if (response.lastMsgTime != 0L) {
            time.set(DateTimeUtil.getChatDateFormat(response.lastMsgTime))
        }
        if (response.shortCode != null) {
            shortCode.set(response.shortCode)
        }
        if (response.chatUserStatus != null) {
            status.set(true)
        }

    }

    fun onItemClick() {
        listener.onItemClick(response)
    }

    interface MessageItemClickListener {
        fun onItemClick(response: Buddy)
    }
}