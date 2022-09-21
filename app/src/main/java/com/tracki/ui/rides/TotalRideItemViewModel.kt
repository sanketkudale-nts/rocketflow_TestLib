package com.tracki.ui.rides

import android.content.Context
import androidx.databinding.ObservableField
import com.tracki.R
import com.tracki.data.model.response.config.Task
import com.tracki.utils.*
import com.tracki.utils.DateTimeUtil.Companion.getFormattedTime
import com.tracki.utils.DateTimeUtil.Companion.getParsedDate
import com.tracki.utils.DateTimeUtil.Companion.getParsedTime
import java.util.*

/**
 * Created by Rahul Abrol on 29/12/19.
 */
class TotalRideItemViewModel(task: Task, listener: TotalRideItemViewModelListener?, context: Context) {
    var mListener: TotalRideItemViewModelListener? = listener
    private var task: Task? = task
    var taskName = ObservableField("")
    var assigneeNameCode = ObservableField("")
    var assigneeName = ObservableField("")
    var createdAt = ObservableField("")
    var setRadioButtonColor = ObservableField(0)
    var taskStartDateTime = ObservableField("")
    var taskEndDateTime = ObservableField("")
    var isContactAvail = ObservableField(false)
    var taskNameVisible = ObservableField(false)
    var contact = ObservableField("")
    var isAutoStart = ObservableField("")
    var taskStartLoc = ObservableField("")
    var taskEndLoc = ObservableField("")
    var fleetDetail = ObservableField("")
    var isFleetDetailVisible = ObservableField(false)
    var isCompleted = ObservableField(false)
    //payout feature values
    var price = ObservableField(AppConstants.INR + " 0")
    var isPayoutEligible = ObservableField(false)

    init {
        try {
            if (task.payoutEligible) {
                isPayoutEligible.set(task.payoutEligible)
                if (task.driverPayoutBreakUps != null) {
                    price.set(AppConstants.INR + " " + task.driverPayoutBreakUps?.totalPayout)
                }
            }

            if (task.autoCreated) {
                isAutoStart.set(context.getString(R.string.autostart))
            }
            if (task.autoCancel) {
                isAutoStart.set(context.getString(R.string.autocancel))
            }
            if (task.contact != null) {
                if (task.contact!!.name != null) {
                    contact.set(task.contact!!.name)
                } else if (task.contact!!.mobile != null) {
                    contact.set(task.contact!!.mobile)
                }
                isContactAvail.set(task.contact!!.mobile != null)
            }
            if (task.assigneeDetail != null) {
                if (!task.autoCreated && !task.autoCancel) {
                    isAutoStart.set("Assigned By: " + task.assigneeDetail!!.name)
                }
                if(task.assigneeDetail!!.name!=null&&!task.assigneeDetail!!.name!!.isEmpty()){
                    if(task.assigneeDetail!!.name!!.length>2) {
                        var shortCode = task.assigneeDetail!!.name!!.substring(0, 2)
                        task.assigneeDetail!!.shortCode=shortCode.toUpperCase()
                    }
                }
                assigneeNameCode.set(task.assigneeDetail!!.shortCode)
                assigneeName.set(CommonUtils.setCustomFontTypeSpan(context, task.assigneeDetail!!.name + " assign you a task",
                        0, Objects.requireNonNull(task.assigneeDetail!!.name)!!.length, R.font.campton_semi_bold))
            }
            createdAt.set(getParsedDate(task.createdAt) + " at " + getParsedTime(task.createdAt))
            if (task.status != null) {
                setRadioButtonColor.set(CommonUtils.getColor(task.status))
                isCompleted.set(task.status === TaskStatus.COMPLETED)
            }
            if(task.clientTaskId!=null&&task.clientTaskId!!.isNotEmpty()) {
                taskNameVisible.set(true)
                val s = "Task ID: " + task.clientTaskId
                taskName.set(s)
                //taskName.set(CommonUtils.setCustomFontTypeSpan(context, s, 12, s.length, R.font.campton_semi_bold))
            }
            taskStartDateTime.set(getFormattedTime(task.startTime, DateTimeUtil.DATE_TIME_FORMAT_2))
            taskEndDateTime.set(getFormattedTime(task.endTime, DateTimeUtil.DATE_TIME_FORMAT_2))

            if (task.invDetails != null) {
                fleetDetail.set("Fleet Details: " + task.invDetails!!.groupName + " | " + task.invDetails!!.referenceId)
                isFleetDetailVisible.set(true)
            } else {
                isFleetDetailVisible.set(false)
            }
            if (task.source != null) {
                taskStartLoc.set(task.source!!.address)
            }
            if (task.destination != null) {
                taskEndLoc.set(task.destination!!.address)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val TAG = "TotalRideItemViewModel"
            Log.e(TAG, "Exception in constructor " + e.message)
        }
    }

    fun onItemClick() {
        mListener!!.onItemClick(task)
    }

    fun onCallClick() {
        mListener!!.onCallClick(task)
    }

    interface TotalRideItemViewModelListener {
        fun onItemClick(task: Task?)
        fun onCallClick(task: Task?)
    }
}