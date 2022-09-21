package com.rocketflyer.rocketflow.ui.leave.leave_approval

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tracki.R
import com.tracki.data.model.response.config.LeaveApprovalData
import com.tracki.databinding.ItemLeaveApprovalBinding
import com.tracki.databinding.ItemLeaveApprovalEmptyViewBinding
import com.tracki.databinding.LayoutLeaveApprovalOptionsBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.CircleTransform
import com.tracki.ui.custom.GlideApp

class LeaveApprovalAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var mResponseList: List<LeaveApprovalData>? = null
    private var leaveApprovalFragment:LeaveApprovalFragment?=null

//    fun setList(mResponseList: List<LeaveHistoryData>?, leaveHistoryFragment: LeaveHistoryFragment) {
//        this.mResponseList = mResponseList
//        notifyDataSetChanged()
//    }

    fun setList(mResponseList: List<LeaveApprovalData>?, leaveApprovalFragment: LeaveApprovalFragment) {
        this.mResponseList = mResponseList
        this.leaveApprovalFragment=leaveApprovalFragment
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
        return if (mResponseList == null || mResponseList!!.size==0) {
            VIEW_TYPE_EMPTY
        } else VIEW_TYPE_LEAVE_APPROVAL
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == VIEW_TYPE_LEAVE_APPROVAL) {
            val itemLeaveApprovalBinding = ItemLeaveApprovalBinding
                    .inflate(LayoutInflater.from(parent.context),
                            parent, false)
            LeaveApprovalViewHolder(itemLeaveApprovalBinding)
        } else {
            val itemLeaveApprovalEmptyViewBinding = ItemLeaveApprovalEmptyViewBinding
                    .inflate(LayoutInflater.from(parent.context),
                            parent, false)
            EmptyViewHolder(itemLeaveApprovalEmptyViewBinding)
        }
    }

    inner class EmptyViewHolder constructor(private val mBinding: ItemLeaveApprovalEmptyViewBinding) : BaseViewHolder(mBinding.root) {
        override fun onBind(position: Int) {
            val emptyItemViewModel = LeaveApprovalEmptyItemViewModel()
            mBinding.viewModel = emptyItemViewModel
        }

    }

    inner class LeaveApprovalViewHolder constructor(private val mBinding: ItemLeaveApprovalBinding) :
            BaseViewHolder(mBinding.root), LeaveApprovalItemViewModel.OnViewClickListener {
        override fun onBind(position: Int) {
            val leaveApprovalItemViewModel = LeaveApprovalItemViewModel(mResponseList!![position], this)
            mBinding.viewModel = leaveApprovalItemViewModel
            if(leaveApprovalItemViewModel.profileImg.get()!!.isNotEmpty()) {
                GlideApp.with(leaveApprovalFragment!!.baseActivity)
                        .load(leaveApprovalItemViewModel.profileImg.get())
                        .apply(RequestOptions().circleCrop()
                                .placeholder(R.drawable.ic_my_profile))
                        .error(R.drawable.ic_my_profile)
                        .into(mBinding.ivUser)
            }else{
                mBinding.ivUser.setImageResource(R.drawable.ic_my_profile)
            }
            mBinding.executePendingBindings()
        }

        override fun onExpand(data: LeaveApprovalData) {
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
            openDialogLeaveApprovalOptions(mBinding.viewModel!!)
        }

        override fun onClickApprove(lrId: String, remarks: String, updatedAt: Long) {
        leaveApprovalFragment!!.onClickApprove(lrId, remarks, updatedAt)

        }

        override fun onClickReject(lrId: String, updatedAt: Long) {
            leaveApprovalFragment!!.onClickReject(lrId, updatedAt)

        }

    }
    private fun openDialogLeaveApprovalOptions(viewModel: LeaveApprovalItemViewModel?) {
        val dialog = Dialog(leaveApprovalFragment!!.requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        val binding: LayoutLeaveApprovalOptionsBinding = DataBindingUtil.inflate(LayoutInflater.from(leaveApprovalFragment!!.requireContext()), R.layout.layout_leave_approval_options, null, false)
        dialog.setContentView(binding.root)
        binding.ivBack.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnApprove.setOnClickListener {
            dialog.cancel()
            viewModel!!.onClickApprove()
        }
        binding.btnReject.setOnClickListener {
            dialog.cancel()
            viewModel!!.onClickReject()
        }
        if (viewModel!!.profileImg.get() != "") {
            Glide.with(binding.ivUser.context)
                    .load(viewModel!!.profileImg.get())
                    .apply(RequestOptions()
                            .transform(CircleTransform())
                            .placeholder(R.drawable.ic_my_profile))
                    .error(R.drawable.ic_my_profile)
                    .into(binding.ivUser)
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


    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_LEAVE_APPROVAL = 1
    }
}
;
interface OnClickRejectListener
{
    fun onClickApprove(lrId: String, remarks: String, updatedAt: Long)
    fun onClickReject(lrId: String, updatedAt: Long)
}



