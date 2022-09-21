package com.tracki.ui.stockhistorydetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.databinding.ItemRowStockHistoryDetailsBinding
import com.tracki.databinding.LayoutEmptyCategoryViewBinding
import com.tracki.ui.base.BaseViewHolder
import java.util.ArrayList

class StockDetailsAdapter (var context: Context) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var mFilteredList: ArrayList<StockHistoryDetails>? = ArrayList()
    var mList: ArrayList<StockHistoryDetails>? = ArrayList()
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1
    private var emptyDataMessage: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val simpleViewItemBinding: ItemRowStockHistoryDetailsBinding =
                ItemRowStockHistoryDetailsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            StoreProductItemViewHolder(simpleViewItemBinding)
        } else {
            val simpleViewItemBinding: LayoutEmptyCategoryViewBinding =
                LayoutEmptyCategoryViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            EmptyViewHolder(simpleViewItemBinding)
        }


    }


    override fun getItemCount(): Int {
        return if (mFilteredList != null && mFilteredList!!.isNotEmpty()) {
            mFilteredList!!.size
        } else {
            1
        }
    }

    fun addItems(list: ArrayList<StockHistoryDetails>) {
        /* if (mList != null *//*&& mList!!.isNotEmpty()*//*)
            mList!!.clear()
        if (mFilteredList != null *//*&& mFilteredList!!.isNotEmpty()*//*)
            mFilteredList!!.clear()*/
        mFilteredList!!.addAll(list)
        if (mList != null && mList!!.isNotEmpty()) {
            notifyItemRangeInserted(mList!!.size + 1, list!!.size)
        } else {
            notifyDataSetChanged()
        }
        mList!!.addAll(list)
    }

    fun getAllList(): ArrayList<StockHistoryDetails> {
        return mFilteredList!!
    }

    fun removeAt(position: Int, listSize: Int) {
        mFilteredList!!.removeAt(position)
        mList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSize)
    }

    private fun clearItems() {
        mFilteredList!!.clear()
    }

    fun clearList() {
        mList!!.clear()
        mFilteredList!!.clear()
        notifyDataSetChanged()

    }


    inner class StoreProductItemViewHolder(var binding: ItemRowStockHistoryDetailsBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: StockHistoryDetails? = mFilteredList!![position]
            if (data != null) {
                binding.data = data
                if(data.userInfo==null)
                    binding.tvClosingStock.text="N/A"

            }


        }

    }


    inner class EmptyViewHolder(var binding: LayoutEmptyCategoryViewBinding) :
        BaseViewHolder(binding.root) {

        override fun onBind(position: Int) {
            binding.btnAddNow.visibility = View.GONE
            if (emptyDataMessage == null)
                binding.tvDesc.text = context.getString(R.string.seems_like_no_stock_found)
            else {
                binding.tvDesc.text = emptyDataMessage
            }
            binding.tvTitle.text = context.getString(R.string.no_stock_found)
            binding.ivMain.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_shopping_cart
                )
            )

            binding.executePendingBindings()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (mFilteredList!!.isNotEmpty()) {
            VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(holder.adapterPosition)
    }


}