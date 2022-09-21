package com.rocketflyer.rocketflow.ui.attendance.teamattendance

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.DashBoardBoxItem
import com.tracki.databinding.ItemLayoutEmptyDashBoardBindingBinding
import com.tracki.databinding.TeamAttendenaceBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.utils.CommonUtils


/**
 * Created by Vikas Kesharvani on 26/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TeamAttendanceAdapter(private var mList: ArrayList<DashBoardBoxItem>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var listener: TeamAttendanceListener? = null
    private var context: Context? = null

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_TEAM_ATTENDANCE = 1
    }

    fun setListener(listener: TeamAttendanceListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_TEAM_ATTENDANCE -> {

                val simpleViewItemBinding: TeamAttendenaceBinding =
                        TeamAttendenaceBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                SimpleViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemLayoutEmptyDashBoardBindingBinding =
                        ItemLayoutEmptyDashBoardBindingBinding.inflate(
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

    public fun clearList() {
        if (mList != null && mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }

    override fun getItemViewType(position: Int) = if (mList != null && !mList!!.isEmpty()) {
        VIEW_TYPE_TEAM_ATTENDANCE
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<DashBoardBoxItem>) {
        mList = list
        notifyDataSetChanged()
    }


    interface TeamAttendanceListener {
        fun onItemClick(response: DashBoardBoxItem)
    }

    inner class SimpleViewHolder(private val mBinding: TeamAttendenaceBinding) :
            BaseViewHolder(mBinding.root) {


        override fun onBind(position: Int) {
            val data = mList!![position]
            mBinding.data = data
            mBinding.tvCount.setTextColor(CommonUtils.getAttendanceStatusColor(data.stageConst!!))
            if (data.stageConst!!.name.equals("ALL")||data.count==0){
//                mBinding.cardMain.setCardBackgroundColor(Color.parseColor("#b2b2b2"))
//                mBinding.tvTitle.setTextColor(Color.parseColor("#FFFFFF"))
//                mBinding.tvTitleLebel.setTextColor(Color.parseColor("#FFFFFF"))
//                mBinding.tvCount.setTextColor(Color.parseColor("#FFFFFF"))
                mBinding.ivNext.visibility= View.GONE
            }else{
                mBinding.ivNext.visibility= View.VISIBLE
              //  mBinding.cardMain.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            mBinding.cardMain.setOnClickListener {
                if (data.stageConst!!.name != "ALL") {
                   if(listener!=null){
                       if(data.count!=0)
                       listener!!.onItemClick(data)
                   }
                }

            }

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class EmptyViewHolder(private val mBinding: ItemLayoutEmptyDashBoardBindingBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
        }


    }

}