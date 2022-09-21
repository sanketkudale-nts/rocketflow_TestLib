package com.tracki.ui.leave.leave_history


import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.tracki.data.model.response.config.LeaveHistoryData
import com.tracki.data.model.response.config.LeaveStatus
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil

class LeaveHistoryItemViewModel(var data: LeaveHistoryData, val onViewClickListener: OnViewClickListener) {

    var leaveDate = ObservableField("")
    var leaveNum = ObservableField("")
    var leaveNumValue = ObservableField("")
    var leaves = ObservableField("")
    var leaveType = ObservableField("")
    var leaveStatus = ObservableField("")
    var applyDate = ObservableField("")
    var approvedCancelledOn = ObservableField("")
    var approverRejector = ObservableField("")
    var remarks = ObservableField("")
    var approverComment = ObservableField("")
    var color = ObservableInt(0)
    //    var isYellow = ObservableBoolean(false)
//    var isGreen = ObservableBoolean(false)
//    var isRed = ObservableBoolean(false)
//    var isGray = ObservableBoolean(false)
    var isExpandable = ObservableBoolean(false)
    var isApplied = ObservableBoolean(false)
    var isApproved = ObservableBoolean(false)
    var isRejected = ObservableBoolean(false)
    var isCancelled = ObservableBoolean(false)
    var approverMainItem = ObservableField("")
    var approvers = ObservableField("")
    var from = ObservableField("")
    var to = ObservableField("")
    var applyDateValue = ObservableField("")
    var remarksValue= ObservableField("")

    init {
        //Subtract 19800000 to convert from UTC to IST
//        leaveDate.set("Leave Date \n" + DateTimeUtil.getParsedDate3(data.from ) + "-" +
//                DateTimeUtil.getParsedDate3(data.to ))
        leaveDate.set(DateTimeUtil.getParsedDate3(data.from ) + "-" +
                DateTimeUtil.getParsedDate3(data.to ))
        from.set(DateTimeUtil.getParsedDate(data.from ))
        to.set(DateTimeUtil.getParsedDate(data.to ))
        var leaveNumInt = 0
        leaveNumInt = data.dayKeys!!.size
        if (data.leaveType != null) {
           // leaves.set("Leaves\n " + leaveNumInt + " " + CommonUtils.getLeaveTypeString(data.leaveType) + "s")
            leaves.set(CommonUtils.getLeaveTypeString(data.leaveType))
            leaveNum.set("Leave Days: $leaveNumInt")
            leaveNumValue.set("$leaveNumInt Days")
            leaveType.set(CommonUtils.getLeaveTypeString(data.leaveType))
        }
        if (data.status != null) {
            leaveStatus.set(CommonUtils.getLeaveStatusString(data.status))
        }
        applyDate.set("Applied On: " + DateTimeUtil.getParsedDate3(data.appliedOn) + "")
        applyDateValue.set(DateTimeUtil.getParsedDate(data.appliedOn))

        if (data.leaveApprovers != null) {
            val items = data.leaveApprovers!!
            val builder = StringBuilder()
            builder.append(AppConstants.APPROVERS)
            builder.append(" ")
            for (i in items.indices) {
                if(items[i].firstName!=null) {
                    builder.append(items[i].firstName)
                    val j = i + 1
                    if (j <= items.size - 1) {
                        builder.append(",")
                    }
                }


            }
            approvers.set(builder.toString())
        }
        if (data.approvedBy != null)
            approverMainItem.set("By " + data.approvedBy!!.firstName)
        if (data.remarks != null) {
            remarks.set("Remarks: " + data.remarks)
            remarksValue.set(data.remarks)
        }
        isExpandable.set(data.isExpandable)
        color.set(CommonUtils.getLeaveColor(data.status))
        if (data.status != null) {
            when {
                data.status!! == LeaveStatus.PENDING -> {
                    isApplied.set(true)
                }
                data.status!! == LeaveStatus.APPROVED -> {
                    isApproved.set(true)
                    approvedCancelledOn.set("Approved On: " + DateTimeUtil.getParsedDate3(data.approvedOrRejectAt))
                }
                data.status!! == LeaveStatus.REJECTED -> {
                    isRejected.set(true)
                    if(data.approvedOrRejectedBy==null) {
//                        if(data.approvedOrRejectedBy==null&&data.approverComment!=null&& data.approverComment!!.isNotEmpty()) {
//                            approverMainItem.set("System")
//                        }
                        approverMainItem.set("By System")
                    }else{
                        if (data.approvedBy != null&&data.approvedBy!!.firstName!=null)
                            approverMainItem.set("By " + data.approvedBy!!.firstName)

                    }
                    approvedCancelledOn.set("Rejected On: " + DateTimeUtil.getParsedDate3(data.approvedOrRejectAt))
                }
                else -> {
                    isCancelled.set(true)
                    approvedCancelledOn.set("Cancelled On: " + DateTimeUtil.getParsedDate3(data.cancelledAt))
                }
            }
        }
    }

    fun onViewClick() {
        onViewClickListener.onExpand()
    }

    fun onEditClick() {
        onViewClickListener.onEditClick(data)
    }

    fun onLeaveCancelClick() {
        if (data.lrId != null)
            onViewClickListener.onCancelClick(data.lrId!!, data.remarks!!)
    }

    interface OnViewClickListener {
        fun onExpand()
        fun onEditClick(leaveHistoryData: LeaveHistoryData)
        fun onCancelClick(lrId: String, remarks: String)
    }
}

