package com.rocketflyer.rocketflow.ui.leave.leave_history

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.LeaveHistoryData
import com.tracki.databinding.ItemLeaveHistoryBinding
import com.tracki.databinding.ItemLeaveHistoryEmptyViewBinding
import com.tracki.databinding.LayoutLeaveOptionsBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.leave.leave_history.LeaveHistoryItemViewModel.OnViewClickListener


class LeaveHistoryAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var mResponseList: List<LeaveHistoryData>? = null
    var leaveHistoryFragment: LeaveHistoryFragment? = null
    fun setList(mResponseList: List<LeaveHistoryData>?, leaveHistoryFragment: LeaveHistoryFragment) {
        this.mResponseList = mResponseList
        this.leaveHistoryFragment = leaveHistoryFragment
        notifyDataSetChanged()
    }
    fun clearList(mResponseList: List<LeaveHistoryData>?){
        this.mResponseList = mResponseList
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return if (mResponseList != null && mResponseList!!.isNotEmpty()) {
            mResponseList!!.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mResponseList == null || mResponseList!!.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else
            VIEW_TYPE_LEAVE_HISTORY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == VIEW_TYPE_LEAVE_HISTORY) {
            val itemLeaveHistoryBinding = ItemLeaveHistoryBinding
                    .inflate(LayoutInflater.from(parent.context),
                            parent, false)
            LeaveHistoryViewHolder(itemLeaveHistoryBinding)
        } else {
            val itemLeaveHistoryEmptyViewBinding = ItemLeaveHistoryEmptyViewBinding
                    .inflate(LayoutInflater.from(parent.context),
                            parent, false)
            EmptyViewHolder(itemLeaveHistoryEmptyViewBinding)
        }
    }

    inner class EmptyViewHolder constructor(private val mBinding: ItemLeaveHistoryEmptyViewBinding) : BaseViewHolder(mBinding.root) {
        override fun onBind(position: Int) {
            val emptyItemViewModel = LeaveHistoryEmptyItemViewModel()
            mBinding.viewModel = emptyItemViewModel
        }

    }

    inner class LeaveHistoryViewHolder constructor(private val mBinding: ItemLeaveHistoryBinding) :
            BaseViewHolder(mBinding.root), OnViewClickListener {
        override fun onBind(position: Int) {
            mResponseList!!.get(position).itemPos = position
            val leaveHistoryItemViewModel = LeaveHistoryItemViewModel(mResponseList!![position], this)

            mBinding.viewModel = leaveHistoryItemViewModel
            mBinding.executePendingBindings()
        }

        override fun onExpand() {
            //change the flag value.
//            mResponseList!![adapterPosition].isExpandable = !mResponseList!![adapterPosition].isExpandable
//            //mark all other items value to false
//            for (i in mResponseList!!.indices) {
//                if (i != adapterPosition) {
//                    mResponseList!![i].isExpandable = false
//                }
//            }
//            //rebind the data again on the list.
//            notifyDataSetChanged()
            openDialogLeaveOptions( mBinding.viewModel!!)
        }

        override fun onEditClick(leaveHistoryData: LeaveHistoryData) {
            leaveHistoryFragment!!.onEditClick(leaveHistoryData)
        }

        override fun onCancelClick(lrId: String, remarks: String) {
            leaveHistoryFragment!!.onLeaveCancelClick(lrId, remarks);
        }

    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_LEAVE_HISTORY = 1
    }

    private fun openDialogLeaveOptions(viewModel: LeaveHistoryItemViewModel?) {
        val dialog = Dialog(leaveHistoryFragment!!.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        val binding: LayoutLeaveOptionsBinding = DataBindingUtil.inflate(LayoutInflater.from(leaveHistoryFragment!!.requireContext()), R.layout.layout_leave_options, null, false)
        dialog.setContentView(binding.root)
        binding.ivBack.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnEdit.setOnClickListener {
            dialog.dismiss()
            viewModel!!.onEditClick()
        }
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
            viewModel!!.onLeaveCancelClick()
        }
        binding.viewModel=viewModel
//        dialog.setContentView(R.layout.layout_leave_options)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        if (!dialog.isShowing)
            dialog.show()
    }


}

interface OnViewClick {
    fun onEditClick(leaveHistoryData: LeaveHistoryData)
    fun onLeaveCancelClick(lrId: String, remarks: String)
}

