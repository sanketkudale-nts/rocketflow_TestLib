package com.tracki.ui.products

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tracki.databinding.ItemRowUnitBinding
import com.tracki.ui.base.BaseViewHolder

class UnitModelAdapter(var context: Context) :
    RecyclerView.Adapter<BaseViewHolder>(),
    Filterable {
    var mFilteredList: ArrayList<UnitModel> = ArrayList()
    var mList: ArrayList<UnitModel> = ArrayList()
    var mListener: OnUnitListener? = null


    fun setListener(context: OnUnitListener) {
        mListener = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        val simpleViewItemBinding: ItemRowUnitBinding =
            ItemRowUnitBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return UnitItemViewHolder(simpleViewItemBinding)


    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return if (mFilteredList!!.isNotEmpty()) {
            mFilteredList!!.size
        } else {
            0
        }
    }

    fun addItems(list: ArrayList<UnitModel>) {
        /*  if (mList != null && mList!!.isNotEmpty())
              mList!!.clear()
          if (mFilteredList != null && mFilteredList!!.isNotEmpty())
              mFilteredList!!.clear()*/
        mFilteredList!!.addAll(list)
        if (mList!!.isNotEmpty()) {
            notifyItemRangeInserted(mList!!.size + 1, list!!.size)
        } else {
            notifyDataSetChanged()
        }
        mList!!.addAll(list)
    }

    fun getAllList(): List<UnitModel> {
        return mFilteredList!!
    }

    fun clearList() {
        mList!!.clear()
        mFilteredList!!.clear()
        notifyDataSetChanged()
    }

    fun removeAt(position: Int, listSize: Int) {
        mFilteredList.removeAt(position)
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSize)
    }

    inner class UnitItemViewHolder(var binding: ItemRowUnitBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: UnitModel? = mFilteredList!![position]
            if (data != null) {
             binding.rvMain.setOnClickListener { 
                 if(mListener!=null)
                     mListener!!.onUnitSelected(data)
             }

                binding.data = data

            }
        }

    }

    interface OnUnitListener {
        fun onUnitSelected(data: UnitModel)
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString().lowercase()
                if (charString.isEmpty()) {
                    mFilteredList = mList
                } else {
                    var filteredList = ArrayList<UnitModel>()
                    for (contact in mList!!) {
                        if (contact.value!!.lowercase()
                                .contains(charString.lowercase()) || contact.value!!.contains(
                                charString
                            )
                        ) {
                            filteredList.add(contact)
                        }
                    }
                    mFilteredList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mFilteredList = results.values as java.util.ArrayList<UnitModel>
                notifyDataSetChanged()
            }
        }
    }


}