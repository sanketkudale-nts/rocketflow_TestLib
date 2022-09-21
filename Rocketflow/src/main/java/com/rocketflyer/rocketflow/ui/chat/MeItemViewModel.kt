package com.rocketflyer.rocketflow.ui.chat

import android.content.Context
import androidx.databinding.ObservableField
import com.tracki.data.model.response.config.ChatResponse
import com.tracki.utils.DateTimeUtil

class MeItemViewModel(private val response: ChatResponse, context: Context) {
    var message: ObservableField<String> = ObservableField("")
    //    var name: ObservableField<String> = ObservableField("")
    var time: ObservableField<String> = ObservableField("")

    init {
//        if (response.name != null) {
//            name.set(response.name)
//        }
        if (response.message != null) {
            var msg = response.message
            if (response.message?.length!! < 20) {
                msg = response.message + "\t\t\t\t\t\t\t"
            }
            message.set(msg)
        }

        if (response.time?.toLong() != 0L) {
            time.set(DateTimeUtil.getChatDateFormat(response.time?.toLong()!!))
        }
    }
}