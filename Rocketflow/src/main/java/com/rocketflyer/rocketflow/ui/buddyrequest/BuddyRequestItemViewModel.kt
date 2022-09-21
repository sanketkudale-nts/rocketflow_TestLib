package com.rocketflyer.rocketflow.ui.buddyrequest

import android.content.Context
import androidx.databinding.ObservableField
import com.tracki.R
import com.tracki.data.model.response.config.Buddy
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil

/**
 * Created by rahul on 7/12/18
 */
class BuddyRequestItemViewModel(private val response: Buddy, val listener: ItemClickListener, context: Context) {
    val shortCode: ObservableField<String> = ObservableField(response.shortCode!!)
    val name: ObservableField<String>
    val time: ObservableField<String>

    init {
        val s = "${response.name} sent you track request"
        name = ObservableField(CommonUtils.setCustomFontTypeSpan(context, s, 0,
                response.name!!.length, R.font.campton_semi_bold))
        time = ObservableField(DateTimeUtil.getFormattedTime(response.createdAt,
                DateTimeUtil.DATE_TIME_FORMAT_2))
    }

    fun onAccept() {
        listener.onAccept(response)
    }

    fun onReject() {
        listener.onReject(response)
    }

    /* fun onItemClick() {
         listener.onItemClick(response)
     }*/

    interface ItemClickListener {
        // fun onItemClick(response: BuddyResponse)
        fun onAccept(response: Buddy)

        fun onReject(response: Buddy)
    }
}