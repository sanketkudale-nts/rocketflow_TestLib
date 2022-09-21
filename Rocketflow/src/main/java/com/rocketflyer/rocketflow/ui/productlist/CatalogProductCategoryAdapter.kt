package com.rocketflyer.rocketflow.ui.productlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.databinding.ItemRowCatalogProductCategoryBinding
import com.tracki.databinding.LayoutEmptyCategoryViewBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.selectorder.CataLogProductCategory


class CatalogProductCategoryAdapter(var context: Context) :
    RecyclerView.Adapter<BaseViewHolder>(), Filterable {
    var mFilteredList: ArrayList<CataLogProductCategory>? = ArrayList()
    var mList: ArrayList<CataLogProductCategory>? = ArrayList()
    var mListener: OnCatalogCategoryListener? = null
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1

    init {
        mListener = context as OnCatalogCategoryListener
    }

    fun setActivityListener(context: Context){
        mListener = context as OnCatalogCategoryListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val simpleViewItemBinding: ItemRowCatalogProductCategoryBinding =
                ItemRowCatalogProductCategoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            CatalogCategoryItemViewHolder(simpleViewItemBinding)
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

    fun addItems(list: ArrayList<CataLogProductCategory>) {
       /* if (mList != null && mList!!.isNotEmpty())
            mList!!.clear()
        if (mFilteredList != null && mFilteredList!!.isNotEmpty())
            mFilteredList!!.clear()*/

        mFilteredList!!.addAll(list)
        notifyItemRangeInserted(mList!!.size+1,list!!.size)
        mList!!.addAll(list)
      //  notifyDataSetChanged()
    }

    fun getAllList(): List<CataLogProductCategory> {
        return mFilteredList!!
    }

    fun clearList() {
        mList!!.clear()
        mFilteredList!!.clear()
        notifyDataSetChanged()
    }


    inner class CatalogCategoryItemViewHolder(var binding: ItemRowCatalogProductCategoryBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: CataLogProductCategory? = mFilteredList!![position]
            if (data != null) {
                binding.data = data
            }
            binding.cardViewMain.setOnClickListener {
                if (mListener != null) {
                    mListener!!.onCategoryRootSelected(data!!)
                }
            }
        }

    }

    interface OnCatalogCategoryListener {
        fun onCategoryRootSelected(prodCat: CataLogProductCategory)
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString().lowercase()
                if (charString.isEmpty()) {
                    mFilteredList = mList
                } else {
                    var filteredList = ArrayList<CataLogProductCategory>()
                    for (contact in mList!!) {
                        if (contact.name!!.lowercase().contains(charString)||contact.name!!.contains(charString)) {
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
                mFilteredList = results.values as java.util.ArrayList<CataLogProductCategory>
                notifyDataSetChanged()
            }
        }
    }

    inner class EmptyViewHolder(var binding: LayoutEmptyCategoryViewBinding) :
        BaseViewHolder(binding.root) {

        override fun onBind(position: Int) {
            binding.btnAddNow.visibility=View.GONE
            binding.tvDesc.text=context.getString(R.string.seems_no_category)
            binding.tvTitle.text=context.getString(R.string.no_category_found)
            binding.ivMain.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_shopping_cart))
            binding.btnAddNow.setOnClickListener {
                if (mListener != null) {

                }
            }
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