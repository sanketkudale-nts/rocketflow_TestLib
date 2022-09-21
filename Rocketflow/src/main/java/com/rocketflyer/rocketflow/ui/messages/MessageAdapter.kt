package com.rocketflyer.rocketflow.ui.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.Buddy
import com.tracki.databinding.ItemMessageEmptyViewBinding
import com.tracki.databinding.ItemMessageViewBinding
import com.tracki.ui.base.BaseViewHolder

class MessageAdapter : RecyclerView.Adapter<BaseViewHolder> {

    private lateinit var listener: MessageListener
    private var context: Context? = null
    var mList: ArrayList<Buddy>? = null
    private var copyList: ArrayList<Buddy>? = null

    constructor(mList: ArrayList<Buddy>?) : super() {
        this.mList = mList
        copyList = mList

    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_MESSAGE = 1
    }

    fun setListener(listener: MessageListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        when (viewType) {
            VIEW_TYPE_MESSAGE -> {
                val simpleViewItemBinding: ItemMessageViewBinding = ItemMessageViewBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return SimpleViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMessageEmptyViewBinding = ItemMessageEmptyViewBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return EmptyViewHolder(emptyViewBinding)
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
        VIEW_TYPE_MESSAGE
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<Buddy>) {
        mList = list
        if (copyList==null) {
            copyList= ArrayList()
        }else{
            copyList!!.clear()
        }
        copyList!!.addAll(list)
        notifyDataSetChanged()
    }

    fun addFilter(newText: String) {
        mList?.clear()
        if (newText.isEmpty()) {
            mList?.addAll(copyList!!)
        } else {
            for (name in copyList!!) {
                if (name.name != null && name.name!!.toLowerCase().contains(newText.toLowerCase())) {
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

    interface MessageListener {
        fun onItemClick(response: Buddy)
    }

    inner class SimpleViewHolder(private val mBinding: ItemMessageViewBinding) :
            BaseViewHolder(mBinding.root), MessageItemViewModel.MessageItemClickListener {

        override fun onItemClick(response: Buddy) {
            listener.onItemClick(response)
        }

        private lateinit var simpleViewModel: MessageItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]

            /*GlideApp.with(context!!)
                    .asBitmap()
                    .load(lists.profileImage)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.ic_placeholder_pic))
                    .error(R.drawable.ic_placeholder_pic)
                    .into(mBinding.ivMessagePic)*/

            val ivStatus = mBinding.ivDriverStatus

            if (lists.chatUserStatus?.name == "ONLINE") {
                ivStatus.isSelected = true
            } else if (lists.chatUserStatus?.name == "OFFLINE") {
                ivStatus.isSelected = false
                ivStatus.isEnabled = false
            } else if (lists.chatUserStatus?.name == "IDLE" ||
                    lists.chatUserStatus?.name == "BUSY") {
                ivStatus.isEnabled = true
            }


            simpleViewModel = MessageItemViewModel(lists, this, context!!)
            mBinding.viewModel = simpleViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class EmptyViewHolder(private val mBinding: ItemMessageEmptyViewBinding) :
            BaseViewHolder(mBinding.root), MessageEmptyItemViewModel.MessageEmptyListener {

        override fun onBind(position: Int) {
            val emptyViewModel = MessageEmptyItemViewModel(this)
            mBinding.viewModel = emptyViewModel
        }

        override fun onRetryClick() {
//            listener.onRetryClick()
        }

    }

}