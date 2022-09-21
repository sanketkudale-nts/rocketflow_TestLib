package com.tracki.ui.leavedetails

import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.LeaveApprovalData
import com.tracki.data.model.response.config.LeaveHistoryData
import com.tracki.data.model.response.config.Summary
import com.tracki.databinding.ItemLeaveApprovalBinding
import com.tracki.databinding.ItemLeaveApprovalEmptyViewBinding
import com.tracki.databinding.ItemRowLeaveSummaryBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.leave.leave_approval.LeaveApprovalEmptyItemViewModel
import com.tracki.ui.leave.leave_approval.LeaveApprovalItemViewModel

class LeaveDetailsAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var mResponseList: List<Summary>? = null


    fun setList(mResponseList: List<Summary>?) {
        this.mResponseList = mResponseList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return mResponseList!!.size
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val itemLeaveApprovalBinding = ItemRowLeaveSummaryBinding
                .inflate(LayoutInflater.from(parent.context),
                        parent, false)
        return LeaveDetailsViewHolder(itemLeaveApprovalBinding)

    }

    inner class EmptyViewHolder constructor(private val mBinding: ItemLeaveApprovalEmptyViewBinding) : BaseViewHolder(mBinding.root) {
        override fun onBind(position: Int) {
            val emptyItemViewModel = LeaveApprovalEmptyItemViewModel()
            mBinding.viewModel = emptyItemViewModel
        }

    }

    inner class LeaveDetailsViewHolder constructor(private val mBinding: ItemRowLeaveSummaryBinding) :
            BaseViewHolder(mBinding.root) {
        override fun onBind(position: Int) {
            val leaveApprovalItemViewModel = LeaveDetailsItemViewModel(mResponseList!![position])
            mBinding.viewModel = leaveApprovalItemViewModel
            mBinding.executePendingBindings()
        }


    }


}
