package com.rocketflyer.rocketflow.ui.myInventory

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.ItemRowDocumentsBinding
import com.tracki.databinding.ItemRowMyInventoryBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.myDocument.MyDocumentActivity

class MyInventoryAdapter (/*private var mList: ArrayList<Transaction>?*/) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null


    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_DOCUMENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_DOCUMENT -> {
                val simpleViewItemBinding: ItemRowMyInventoryBinding =
                    ItemRowMyInventoryBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                InventoryViewHolder(simpleViewItemBinding)
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
        /*return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            1
        }*/

        return 5
    }

    /*override fun getItemViewType(position: Int) = if (mList != null && mList!!.isNotEmpty()) {
        VIEW_TYPE_DOCUMENT
    } else {
        VIEW_TYPE_EMPTY
    }*/

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_DOCUMENT
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    /*fun addItems(list: ArrayList<Transaction>) {
//        clearList()
//       mList!!.addAll(list)

        //clearList()
        mList!!.addAll(list)
        val set: MutableSet<Transaction> = HashSet()
        set.addAll(mList!!)
        mList!!.clear()
        mList!!.addAll(set)
        notifyDataSetChanged()
    }

    fun clearList() {
        if (mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }

    fun getAllList(): List<Transaction> {
        return mList!!
    }*/


    inner class InventoryViewHolder(private val mBinding: ItemRowMyInventoryBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            /*val lists = mList!![position]
            mBinding.data = lists

            mBinding.executePendingBindings()*/
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptyBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            mBinding.imageViewEmpty.setImageResource(R.drawable.ic_wallet)
            if (context is MyDocumentActivity) {
                mBinding.tvMessage.text = "No Documents"
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }
}