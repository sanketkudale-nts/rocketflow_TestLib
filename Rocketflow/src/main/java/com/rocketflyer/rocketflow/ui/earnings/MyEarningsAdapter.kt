package com.rocketflyer.rocketflow.ui.earnings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.MyEarning
import com.tracki.databinding.ItemEarningListBinding
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.payouts.AdminUserPayoutsActivity

/**
 * Created by Rahul Abrol on 29/12/19.
 */
class MyEarningsAdapter(private var mList: List<MyEarning>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null
    private var listener: OnItemClickListener? = null

    fun setOnItemListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NOTIFICATION_SIMPLE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_NOTIFICATION_SIMPLE -> {
                val simpleViewItemBinding: ItemEarningListBinding =
                        ItemEarningListBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                SimpleViewHolder(simpleViewItemBinding)
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
        VIEW_TYPE_NOTIFICATION_SIMPLE
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: List<MyEarning>?) {
        mList = list
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClickItem(date: Long)
    }

    inner class SimpleViewHolder(private val mBinding: ItemEarningListBinding) :
            BaseViewHolder(mBinding.root), MyEarningsItemViewModel.MyEarningsItemListener {

        private lateinit var simpleViewModel: MyEarningsItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            simpleViewModel = MyEarningsItemViewModel(lists, this)
            mBinding.viewModel = simpleViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }

        override fun onClickItem(date: Long) {
            listener?.onClickItem(date)
        }
    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptyBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            if(context is AdminUserPayoutsActivity){
                mBinding.tvMessage.text = "No Payouts"
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }
}