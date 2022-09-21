package com.rocketflyer.rocketflow.ui.leave.leave_approval


import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.tracki.data.model.response.config.LeaveApprovalData
import com.tracki.data.model.response.config.LeaveApprovalStatus
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil

class LeaveApprovalItemViewModel(var data: LeaveApprovalData, val onViewClickListener: LeaveApprovalItemViewModel.OnViewClickListener) {

    var leaveDate = ObservableField("")
    var leaveNum = ObservableField("")
    var leaves = ObservableField("")
    var leaveType = ObservableField("")
    var leaveStatus = ObservableField("")
    var applyDate = ObservableField("")
    var appliedBy = ObservableField("")
    var appliedByName = ObservableField("")
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
    var isPending = ObservableBoolean(false)
    var isApproved = ObservableBoolean(false)
    var isRejected = ObservableBoolean(false)
    var isCancelled = ObservableBoolean(false)
    var approverMainItem = ObservableField("")
    var approvers: String = "Approver(s): "
    var approvedOrRejectAt: String = ""
    var from = ObservableField("")
    var to = ObservableField("")
    var applyDateValue = ObservableField("")
    var remarksValue= ObservableField("")
    var leaveNumValue= ObservableField("")
    var profileImg= ObservableField("")

    init {

        var from: Long
        var to: Long

        if (data.status != null) {
            if (data.status!!.equals(LeaveApprovalStatus.PENDING))
                isPending.set(true)
            else if (data.status!!.equals(LeaveApprovalStatus.APPROVED))
                isApproved.set(true)
            else
                isRejected.set(true)
        }

        if (data.from != null) {
            from = data.from!!
            this.from.set(DateTimeUtil.getParsedDate(data.from !!))

        } else from = 0

        if (data.to != null)
            to = data.to
        else to = 0
        this.to.set(DateTimeUtil.getParsedDate(data.to ))
        //leaveDate.set("Leave Date \n" + DateTimeUtil.getParsedDate3(from) + " - " + DateTimeUtil.getParsedDate3(to))
        leaveDate.set(DateTimeUtil.getParsedDate3(from) + " - " + DateTimeUtil.getParsedDate3(to))

        if (data.leaveType != null) {
            if (data.dayKeys != null) {
              //  leaves.set("Leaves\n " + data.dayKeys!!.size + " " + CommonUtils.getLeaveTypeString(data.leaveType))
                leaves.set(CommonUtils.getLeaveTypeString(data.leaveType))
                leaveNum.set("Leave Days: " + data.dayKeys!!.size)
                leaveNumValue.set("${data.dayKeys!!.size} Days")
            }
        }
        if (data.leaveType != null)
            leaveType.set(CommonUtils.getLeaveTypeString(data.leaveType))

        if (data.status != null)
            leaveStatus.set(CommonUtils.getLeaveApprovalStatusString(data.status))


//        if (data.leaveApprovers != null)
//            for (i in data.leaveApprovers!!)
//            {
//                approvers+=i.firstName.toString() + ", "
//            }
//
//
//
//        approvers = approvers!!.substring(0, approvers!!.length - 2);

        if (data.appliedOn != null) {
            applyDate.set("Applied On: " + DateTimeUtil.getParsedDate3(data.appliedOn!!))
            applyDateValue.set(DateTimeUtil.getParsedDate(data.appliedOn!!))
        }

        if (data.appliedBy != null) {
            var fName: String
            var lName: String
            if(data.appliedBy!!.profileImg!=null)
                profileImg.set(data.appliedBy!!.profileImg)

            if (data.appliedBy!!.firstName != null)
                fName = data.appliedBy!!.firstName!!
            else
                fName = ""

            if (data.appliedBy!!.lastName != null)
                lName = data.appliedBy!!.lastName!!
            else
                lName = ""

            appliedBy.set("By: " + fName + " " + lName)
            appliedByName.set(fName + " " + lName)
        }

        if (data.remarks != null) {
            remarks.set("Remarks: " + data.remarks)
            remarksValue.set(data.remarks)
        }
        // approverComment.set("Approver's Comments: " + data.comment)

        if (data.isExpandable != null)
            isExpandable.set(data.isExpandable)

        color.set(CommonUtils.getLeaveApprovalColor(data.status))
//
//        when {
//            data.status!! == LeaveApprovalStatus.PENDING -> isApplied.set(true)
//            data.status!! == LeaveApprovalStatus.APPROVED -> {
//                isApproved.set(true)
//                approvedCancelledOn.set("Approved On: " + data.approvedOrRejectAt)
//            }
//            data.status!! == LeaveApprovalStatus.REJECTED -> isRejected.set(true)
//            else -> {
//                isCancelled.set(true)
//                approvedCancelledOn.set("Cancelled On: " + data.approvedOrRejectAt)
//            }
//        }

//        when (color.get()) {
//            "Red" -> isRed.set(true)
//            "Yellow" -> isYellow.set(true)
//            "Green" -> isGreen.set(true)
//            "Gray" -> isGray.set(true)
//        }
    }

    fun onClickApprove() {
        isApproved.set(true)
        onViewClickListener.onClickApprove(data.lrId!!, data.remarks!!, System.currentTimeMillis())
    }

    fun onClickReject() {
        isRejected.set(true)
        onViewClickListener.onClickReject(data.lrId!!, System.currentTimeMillis())
    }

    fun onViewClick() {
        onViewClickListener.onExpand(data)
    }


    interface OnViewClickListener {
        fun onExpand(data: LeaveApprovalData)
        fun onClickApprove(lrId: String, remarks: String, updatedAt: Long)
        fun onClickReject(lrId: String, updatedAt: Long)
    }

}

