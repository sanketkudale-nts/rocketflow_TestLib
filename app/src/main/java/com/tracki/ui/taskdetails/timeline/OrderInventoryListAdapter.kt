package com.tracki.ui.taskdetails.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.OrderData
import com.tracki.data.model.response.config.OrderItemsInfo
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.ItemRowOrderListBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.payouts.AdminUserPayoutsActivity


/**
 * Created by Vikas Kesharvani on 17/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class OrderInventoryListAdapter(private var mList: ArrayList<OrderItemsInfo>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null


    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_INVENTORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_INVENTORY -> {
                val simpleViewItemBinding: ItemRowOrderListBinding =
                        ItemRowOrderListBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                OrdersListViewModel(simpleViewItemBinding)
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
        VIEW_TYPE_INVENTORY
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<OrderItemsInfo>) {
        mList = list
    }


    inner class OrdersListViewModel(private val mBinding: ItemRowOrderListBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            if (lists.img != null && lists.img!!.isNotEmpty()) {
                GlideApp.with(context!!).load(lists.img)
                        .placeholder(R.drawable.ic_picture)
                        .into(mBinding.ivProduct)

            }else{
                mBinding.ivProduct.setImageResource(R.drawable.ic_picture)
            }
            mBinding.executePendingBindings()
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptyBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            if (context is AdminUserPayoutsActivity) {
                mBinding.tvMessage.text = "No Orders"
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }
}