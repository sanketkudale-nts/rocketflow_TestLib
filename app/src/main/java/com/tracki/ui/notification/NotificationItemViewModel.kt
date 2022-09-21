package com.tracki.ui.notification

import android.content.Context
import androidx.databinding.ObservableField
import com.tracki.data.model.NotificationResponse
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil


/**
 * Created by rahul on 11/10/18
 */
open class NotificationItemViewModel(private val response: NotificationResponse,
                                     val listener: NotificationSimpleListener,
                                     context: Context) {

    var message: ObservableField<String>
    var messageDateTime: ObservableField<String>
    var image: ObservableField<String> = ObservableField(CommonUtils.getShortName(response.event,
            response.data.taskAssignedTo,
            response.data.taskAssignedBy))

    init {
//        val name = CommonUtils.setCustomFontTypeSpan(context,
//                CommonUtils.getMessage(response.event, response.data),
//                0, 6, R.font.campton_semi_bold)
        message = ObservableField(CommonUtils.getMessage(response.event, response.data))
        messageDateTime = ObservableField("${DateTimeUtil.getParsedDate(response.time)} at ${DateTimeUtil.getParsedTime(response.time)}")
    }

    fun onItemClick() {
        listener.onItemClick(response)
    }

    interface NotificationSimpleListener {
        fun onItemClick(response: NotificationResponse)
    }
}