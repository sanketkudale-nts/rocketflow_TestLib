package com.tracki.ui.attendance

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.tracki.R
import com.tracki.data.model.response.config.Buddy
import com.tracki.data.model.response.config.EmpData
import com.tracki.data.model.response.config.LeaveStatus
import com.tracki.databinding.ItemEmployeeListBinding
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.userattendancedetails.UserAttendanceDetailsActivity
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil


/**
 * Created by Vikas Kesharvani on 22/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class EmployeeListAdapter : RecyclerView.Adapter<BaseViewHolder> {

    private var context: Context? = null

    var mList: ArrayList<EmpData>? = null
    private var copyList: ArrayList<EmpData>? = null

    constructor(mList: ArrayList<EmpData>?) : super() {
        this.mList = mList
       // copyList = mList

    }
    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_TRANSACTION = 1
    }
    private var listener:OnChatListener?=null

    fun setListener(context: Context){
        listener=context as OnChatListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_TRANSACTION -> {
                val simpleViewItemBinding: ItemEmployeeListBinding =
                        ItemEmployeeListBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                EmployeeListViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyEarningEmptyBinding =
                        ItemMyEarningEmptyBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int) = if (mList != null && mList!!.isNotEmpty()) {
        VIEW_TYPE_TRANSACTION
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<EmpData>) {
        mList!!.addAll(list)
        if (copyList==null) {
            copyList= ArrayList()
        }else{
            copyList!!.clear()
        }
        if(mList!=null)
        copyList!!.addAll(mList!!)
//        val set: MutableSet<EmpData> = HashSet()
//        set.addAll(mList!!)
//        mList!!.clear()
//        mList!!.addAll(set)
        notifyDataSetChanged()
    }

    fun getAllList(): List<EmpData> {
        return mList!!
    }

    fun clearList() {
        if (mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }
    fun addFilter(newText: String) {
        mList?.clear()
        if (newText.isEmpty()) {
            mList?.addAll(copyList!!)
        } else {
            for (name in copyList!!) {
                if (name.name != null && name.name!!.toLowerCase().contains(newText.toLowerCase())
                        ||name.mobile != null && name.mobile!!.contains(newText.toLowerCase())) {
                    mList?.add(name)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun populateList() {
        if(mList!=null)
            mList!!.clear()
        mList?.addAll(copyList!!)
        notifyDataSetChanged()
    }



    inner class EmployeeListViewHolder(private val mBinding: ItemEmployeeListBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            if (lists.status != null) {
                var stageConst = LeaveStatus.valueOf(lists.status!!)
                if(stageConst==LeaveStatus.PRESENT){
                    mBinding.llPunchInData.visibility=View.VISIBLE
                    mBinding.tvStatus.visibility=View.GONE

                    if(lists.punchIn>0L){
                        mBinding.tvPunchInValue.text = DateTimeUtil.getParsedTime(lists.punchIn)

                    }else{
                        mBinding.tvPunchOutValue.text = "N/A"
                    }
                    if(lists.punchOut>0L){
                        mBinding.tvPunchOutValue.text = DateTimeUtil.getParsedTime(lists.punchOut)
                    }else{
                        mBinding.tvPunchOutValue.text = "N/A"
                    }
                }else{
                    var statusName = getStatusName(lists!!.status!!)
                    if (statusName != null && statusName.isNotEmpty()) {
                        mBinding.tvStatus.visibility = View.VISIBLE
                        mBinding.tvStatus.text = statusName
                        mBinding.tvStatus.setTextColor(CommonUtils.getAttendanceStatusColor(stageConst))
                    } else {
                        mBinding.tvStatus.visibility = View.GONE
                    }
                    mBinding.llPunchInData.visibility=View.GONE

                }

            }
            if(lists.userImg!=null && lists.userImg!!.isNotEmpty()) {
                GlideApp.with(context!!)
                        .load(lists.userImg)
                        .apply(RequestOptions().circleCrop()
                                .placeholder(R.drawable.ic_my_profile))
                        .error(R.drawable.ic_my_profile)
                        .into(mBinding.ivUser)
            }else{
                mBinding.ivUser.setImageResource(R.drawable.ic_my_profile)
            }
            mBinding.ivNext.setOnClickListener {
                if(listener!=null){
                    listener!!.openEmpAttendanceData(lists,position)
                }

            }
            if(lists.punchInLoc!=null&&!lists.punchInLoc!!.address.isNullOrBlank()){
                mBinding.tvPunchInLocation.visibility=View.VISIBLE
                mBinding.tvPunchInLocation.text=lists.punchInLoc!!.address
            }else{
                mBinding.tvPunchInLocation.visibility=View.GONE
            }
            if(lists.punchOutLoc!=null&&!lists.punchOutLoc!!.address.isNullOrBlank()){
                mBinding.tvPunchOutLocation.visibility=View.VISIBLE
                mBinding.tvPunchOutLocation.text=lists.punchOutLoc!!.address
            }else{
                mBinding.tvPunchOutLocation.visibility=View.GONE
            }
            mBinding.cardMain.setOnClickListener {
                if(listener!=null){
                    listener!!.openEmpAttendanceData(lists,position)
                }

            }
            mBinding.ivMessage.setOnClickListener {
                if(listener!=null)
                    listener!!.onChatStart(lists.userId,lists.name)
            }


            mBinding.llOptions.setOnClickListener {
                if (lists.mobile != null && lists.mobile!!.isNotEmpty())
                    CommonUtils.openDialer(context, lists.mobile)
            }
            mBinding.executePendingBindings()
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptyBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            mBinding.imageViewEmpty.setImageResource(R.drawable.ic_empty_buddy)
            if (context is EmployeeListActivity) {
                mBinding.tvMessage.text = "No Employee"
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    private fun getStatusName(status: String): String? {
        var statusName = ""
        if (status == LeaveStatus.ALL.name)
            statusName = "Active Employee"
        if (status == LeaveStatus.PRESENT.name)
            statusName = "Present"
        if (status == LeaveStatus.ABSENT.name)
            statusName = "Absent"
        if (status == LeaveStatus.ON_LEAVE.name)
            statusName = "On Leave"
        if (status == LeaveStatus.NOT_UPDATED.name)
            statusName = "Not Punched In"
        return statusName

    }
    interface OnChatListener{
        fun onChatStart(buddyId: String?, buddyName: String?)
        fun openEmpAttendanceData(data: EmpData?, position: Int)
    }
}