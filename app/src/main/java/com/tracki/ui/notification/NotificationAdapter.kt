package com.tracki.ui.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.NotificationResponse
import com.tracki.databinding.ItemNotificationEmptyViewBinding
import com.tracki.databinding.ItemNotificationViewBinding
import com.tracki.ui.base.BaseViewHolder

/**
 * Created by rahul on 11/10/18
 */
class NotificationAdapter(private var mList: List<NotificationResponse>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private lateinit var listener: NotificationListener
    private var context: Context? = null

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NOTIFICATION_SIMPLE = 1
    }

    fun setListener(listener: NotificationListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_NOTIFICATION_SIMPLE -> {

                val simpleViewItemBinding: ItemNotificationViewBinding =
                        ItemNotificationViewBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                SimpleViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemNotificationEmptyViewBinding =
                        ItemNotificationEmptyViewBinding.inflate(
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

    fun addItems(list: List<NotificationResponse>) {
        mList = list
        notifyDataSetChanged()
    }

    interface NotificationListener {
        fun onItemClick(response: NotificationResponse)
    }

    inner class SimpleViewHolder(private val mBinding: ItemNotificationViewBinding) :
            BaseViewHolder(mBinding.root), NotificationItemViewModel.NotificationSimpleListener {

        override fun onItemClick(response: NotificationResponse) {
            listener.onItemClick(response)
        }

        private lateinit var simpleViewModel: NotificationItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            simpleViewModel = NotificationItemViewModel(lists, this, context!!)
            mBinding.viewModel = simpleViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class EmptyViewHolder(private val mBinding: ItemNotificationEmptyViewBinding) :
            BaseViewHolder(mBinding.root), NotificationEmptyItemViewModel.NotificationEmptyListener {

        override fun onBind(position: Int) {
            val emptyViewModel = NotificationEmptyItemViewModel(this)
            mBinding.viewModel = emptyViewModel
        }

        override fun onRetryClick() {
//            listener.onRetryClick()
        }

    }
}