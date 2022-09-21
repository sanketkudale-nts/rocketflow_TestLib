package com.rocketflyer.rocketflow.ui.rides

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.Task
import com.tracki.databinding.ItemRideEmptyBinding
import com.tracki.databinding.ItemTotalRidesBinding
import com.tracki.ui.base.BaseViewHolder

/**
 * Created by Rahul Abrol on 29/12/19.
 */
class RideAdapter(private var mList: List<Task>?) : RecyclerView.Adapter<BaseViewHolder>() {
    private var listener: ItemClickListener? = null
    private var context: Context? = null

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_HEADER = 1
    }

    fun setListener(listener: ItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val simpleViewItemBinding: ItemTotalRidesBinding =
                        ItemTotalRidesBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                SimpleViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemRideEmptyBinding =
                        ItemRideEmptyBinding.inflate(
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
        VIEW_TYPE_HEADER
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: List<Task>?) {
        mList = list
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onCallClick(task: Task?)
        fun onItemClick(task: Task?)
    }

    inner class SimpleViewHolder(private val mBinding: ItemTotalRidesBinding) :
            BaseViewHolder(mBinding.root), TotalRideItemViewModel.TotalRideItemViewModelListener {

        private lateinit var simpleViewModel: TotalRideItemViewModel
        override fun onBind(position: Int) {
            val task = mList!![position]
            simpleViewModel = TotalRideItemViewModel(task, this, context!!)
            mBinding.viewModel = simpleViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }

        override fun onItemClick(task: Task?) {
            listener?.onItemClick(task)
        }

        override fun onCallClick(task: Task?) {
            listener?.onCallClick(task)
        }
    }

    inner class EmptyViewHolder(private val mBinding: ItemRideEmptyBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = RideEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }
}