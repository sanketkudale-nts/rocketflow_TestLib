package com.rocketflyer.rocketflow.ui.feeds

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.databinding.LayoutEmptyCategoryViewBinding
import com.tracki.databinding.LayoutSelectTimeBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.utils.DateTimeUtil
import com.tracki.utils.Log
import java.util.*
import kotlin.collections.ArrayList

class SortTimeListAdapter(var context: Context) :
    RecyclerView.Adapter<BaseViewHolder>() {
    var mFilteredList: ArrayList<TimeInfo>? = ArrayList()
    var mListener: OnTimeSelectedListener? = null
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1

    init {
        mListener = context as OnTimeSelectedListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        if (viewType == VIEW_TYPE_NORMAL) {
            val simpleViewItemBinding: LayoutSelectTimeBinding =
                LayoutSelectTimeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            return TimeItemViewHolder(simpleViewItemBinding)
        } else {

            val simpleViewItemBinding: LayoutEmptyCategoryViewBinding =
                LayoutEmptyCategoryViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            return EmptyViewHolder(simpleViewItemBinding)

        }


    }


    override fun getItemCount(): Int {
        return if (mFilteredList != null && mFilteredList!!.isNotEmpty()) {
            mFilteredList!!.size
        } else {
            1
        }
    }

    fun addItems(list: ArrayList<TimeInfo>) {
        if (mFilteredList != null && mFilteredList!!.isNotEmpty())
            mFilteredList!!.clear()
        mFilteredList!!.addAll(list)
        notifyDataSetChanged()
    }

    fun getAllList(): ArrayList<TimeInfo> {
        return mFilteredList!!
    }

    fun clearList() {
        mFilteredList!!.clear()
        notifyDataSetChanged()
    }

    fun clearItems() {
        mFilteredList!!.clear()
    }


    inner class TimeItemViewHolder(var binding: LayoutSelectTimeBinding) :
        BaseViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        override fun onBind(position: Int) {
            var data: TimeInfo? = mFilteredList!![position]
            if (data != null) {
                binding.data = data
                binding.tvDay.text = data.title
                if (data.title == context.getString(R.string.till_now)) {
                    val cal = Calendar.getInstance()
                    binding.tvDateRange.text ="Till Now"
                }
                else if (data.title == context.getString(R.string.today)) {
                    val cal = Calendar.getInstance()
                    binding.tvDateRange.text = DateTimeUtil.getParsedDate4(cal.time.time)
                } else if (data.title == context.getString(R.string.yesterday)) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_MONTH, -1)
                    binding.tvDateRange.text = DateTimeUtil.getParsedDate4(cal.time.time)
                } else if (data.title == context.getString(R.string.last_week)) {
                    binding.tvDateRange.text = "${DateTimeUtil.getParsedDate4(data.from!!)} - " +
                            DateTimeUtil.getParsedDate4(data.to!!)
                } else if (data.title == context.getString(R.string.last_month)) {
                    binding.tvDateRange.text = "${DateTimeUtil.getParsedDate4(data.from!!)} - " +
                            "${DateTimeUtil.getParsedDate4(data.to!!)}"
                }

                binding.rlTile.setOnClickListener {
                    for (item in mFilteredList!!) {
                        item.isSelected = item.title == data.title
                        Log.e("Data.selected", "${item.title} : ${data.isSelected}")
                        notifyDataSetChanged()
                    }
                    if (mListener != null) {
                        mListener!!.onItemSelected(data!!)
                    }
                }
            }


        }

    }

    interface OnTimeSelectedListener {
        fun onItemSelected(timeInfo: TimeInfo)
    }


    inner class EmptyViewHolder(var binding: LayoutEmptyCategoryViewBinding) :
        BaseViewHolder(binding.root) {

        override fun onBind(position: Int) {
            /*binding.btnAddNow.visibility = View.VISIBLE
            binding.tvDesc.text = context.getString(R.string.seems_like_you_have_not_any_address_to_deliver_order)
            binding.tvTitle.text = context.getString(R.string.no_address_added)
            binding.ivMain.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_place_holder
                )
            )
            binding.btnAddNow.setOnClickListener {
                if (mListener != null) {
                    mListener!!.addNewAddress()
                }
            }
            binding.executePendingBindings()*/
        }

    }


    fun removeAt(position: Int, listSize: Int) {
        mFilteredList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSize)
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