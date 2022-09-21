package com.tracki.ui.buddyrequest

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.Buddy
import com.tracki.databinding.ItemBuddyRequestEmptyViewBinding
import com.tracki.databinding.ItemBuddyRequestViewBinding
import com.tracki.ui.base.BaseViewHolder

/**
 * Created by rahul on 19/12/18
 */
class BuddyRequestAdapter(private var mList: List<Buddy>) : RecyclerView.Adapter<BaseViewHolder>() {
    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NOTIFICATION_SIMPLE = 1
    }

    private lateinit var listener: ItemClickListener
    private var context: Context? = null
    fun setListener(listener: ItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_NOTIFICATION_SIMPLE -> {
                val simpleViewItemBinding: ItemBuddyRequestViewBinding = ItemBuddyRequestViewBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                SimpleViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemBuddyRequestEmptyViewBinding = ItemBuddyRequestEmptyViewBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                EmptyViewHolder(emptyViewBinding)
            }
        }

    }

    override fun getItemCount(): Int {
        return if (mList.isNotEmpty()) {
            mList.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int) = if (!mList.isEmpty()) {
        VIEW_TYPE_NOTIFICATION_SIMPLE
    } else {
        VIEW_TYPE_EMPTY
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(buddies: List<Buddy>) {
        this.mList = buddies
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onAcceptRequest(response: Buddy)
        fun onRejectRequest(response: Buddy)
    }

    inner class SimpleViewHolder(private val mBinding: ItemBuddyRequestViewBinding) :
            BaseViewHolder(mBinding.root), BuddyRequestItemViewModel.ItemClickListener {

        override fun onAccept(response: Buddy) {
            listener.onAcceptRequest(response)
        }

        override fun onReject(response: Buddy) {
            listener.onRejectRequest(response)
        }

        private lateinit var simpleViewModel: BuddyRequestItemViewModel
        override fun onBind(position: Int) {
            val lists = mList[position]
            simpleViewModel = BuddyRequestItemViewModel(lists, this, context!!)
            mBinding.viewModel = simpleViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class EmptyViewHolder(private val mBinding: ItemBuddyRequestEmptyViewBinding) :
            BaseViewHolder(mBinding.root), BuddyRequestEmptyItemViewModel.BuddyRequestEmptyListener {

        override fun onBind(position: Int) {
            val emptyViewModel = BuddyRequestEmptyItemViewModel(this)
            mBinding.viewModel = emptyViewModel
        }

        override fun onRetryClick() {
//            listener.onRetryClick()
        }

    }
}