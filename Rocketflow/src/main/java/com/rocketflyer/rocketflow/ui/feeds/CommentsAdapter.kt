package com.rocketflyer.rocketflow.ui.feeds

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.Comments
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.ItemPostCommentsBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel


/**
 * Created by Vikas Kesharvani on 04/01/21.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class CommentsAdapter(private var mList: ArrayList<Comments>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null


    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_POSTS = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_POSTS -> {
                val simpleViewItemBinding: ItemPostCommentsBinding =
                        ItemPostCommentsBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                PostListViewHolder(simpleViewItemBinding)
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
        VIEW_TYPE_POSTS
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<Comments>) {
        mList!!.addAll(list)
//        val set: MutableSet<Comments> = HashSet()
//        set.addAll(mList!!)
//        mList!!.clear()
//        mList!!.addAll(set)
        notifyDataSetChanged()
    }
    fun addSingleComments(comments: Comments){
        mList!!.add(comments)
//        if(mList!!.isEmpty())
//             mList!!.add(comments)
//        else{
//            mList!!.add(0,comments)
//        }
        notifyDataSetChanged()
    }
    fun getAllList(): List<Comments> {
        return mList!!
    }

    fun clearList() {
        if (mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }


    inner class PostListViewHolder(private val mBinding: ItemPostCommentsBinding) :
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
            mBinding.imageViewEmpty.setImageResource(R.drawable.ic_no_posts)
            mBinding.tvMessage.text = "No comments yet"
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }


}