package com.rocketflyer.rocketflow.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.DashBoardBoxItem
import com.tracki.databinding.ItemCardWorkFlowBinding
import com.tracki.databinding.ItemLayoutEmptyDashBoardBindingBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.utils.CommonUtils


/**
 * Created by Vikas Kesharvani on 19/08/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class DashBoardStageCountAdapter(private var mList: ArrayList<DashBoardBoxItem>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var cellwidthWillbe: Int = 0
    private var listener: DashBoardListener? = null
    private var context: Context? = null

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NOTIFICATION_SIMPLE = 1
    }

    fun setListener(listener: DashBoardListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_NOTIFICATION_SIMPLE -> {

                val simpleViewItemBinding: ItemCardWorkFlowBinding =
                        ItemCardWorkFlowBinding.inflate(
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
        VIEW_TYPE_NOTIFICATION_SIMPLE
        /* if (mList!![position].type == NotificationType.SIMPLE) {
           VIEW_TYPE_NOTIFICATION_SIMPLE
         } else {
             VIEW_TYPE_NOTIFICATION_WITH_BUTTON
         }*/
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<DashBoardBoxItem>) {
        mList = list
        notifyDataSetChanged()
    }

    fun cellWidth(cellwidthWillbe: Int) {
        this.cellwidthWillbe = cellwidthWillbe;
    }

    interface DashBoardListener {
        fun onItemClick(response: DashBoardBoxItem)
    }

    inner class SimpleViewHolder(private val mBinding: ItemCardWorkFlowBinding) :
            BaseViewHolder(mBinding.root) {


        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            if (cellwidthWillbe != 0) {
                CommonUtils.showLogMessage("e", "cellwidthWillbe", "" + cellwidthWillbe)
                mBinding.cardMain.setLayoutParams(FrameLayout.LayoutParams(
                        cellwidthWillbe - 20, CommonUtils.dpToPixel(context, 85)))
            }
            mBinding.cardMain.setOnClickListener(View.OnClickListener {
                if (listener != null)
                    listener!!.onItemClick(lists)
            })

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