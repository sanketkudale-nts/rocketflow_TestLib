package com.rocketflyer.rocketflow.ui.taskdetails.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.ProductMap
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.ItemRowProductMapBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.payouts.AdminUserPayoutsActivity

class ProductMapAdapter(private var mList: ArrayList<ProductMap>?) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_INVENTORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_INVENTORY -> {
                val simpleViewItemBinding: ItemRowProductMapBinding =
                    ItemRowProductMapBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                ProductOrdersListViewModel(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyEarningEmptyBinding =
                    ItemMyEarningEmptyBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int) = if (mList != null && mList!!.isNotEmpty()) {
        VIEW_TYPE_INVENTORY
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<ProductMap>) {
        mList = list
    }


    inner class ProductOrdersListViewModel(private val mBinding: ItemRowProductMapBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
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

            mBinding.executePendingBindings()
        }
    }
}