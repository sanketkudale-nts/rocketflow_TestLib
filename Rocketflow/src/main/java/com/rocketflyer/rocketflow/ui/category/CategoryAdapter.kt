package com.rocketflyer.rocketflow.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.databinding.ItemRowProductCategoryBinding
import com.tracki.databinding.LayoutEmptyCategoryViewBinding


import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.selectorder.CataLogProductCategory


/**
 * Created by Vikas Kesharvani on 01/02/21.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class CategoryAdapter(var context: Context) :
    RecyclerView.Adapter<BaseViewHolder>(),
    Filterable {
    var mFilteredList: ArrayList<CataLogProductCategory>? = ArrayList()
    var mList: ArrayList<CataLogProductCategory>? = ArrayList()
    var mListener: OnCategoryListener? = null
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1

    init {
        mListener = context as OnCategoryListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        if (viewType == VIEW_TYPE_NORMAL) {
            val simpleViewItemBinding: ItemRowProductCategoryBinding =
                ItemRowProductCategoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            return StoreCategoryItemViewHolder(simpleViewItemBinding)
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

    fun addItems(list: ArrayList<CataLogProductCategory>) {
      /*  if (mList != null && mList!!.isNotEmpty())
            mList!!.clear()
        if (mFilteredList != null && mFilteredList!!.isNotEmpty())
            mFilteredList!!.clear()*/
        mFilteredList!!.addAll(list)
        if(mList!=null&&mList!!.isNotEmpty()) {
            notifyItemRangeInserted(mList!!.size+1,list!!.size)
        }else{
            notifyDataSetChanged()
        }
        mList!!.addAll(list)
    }

    fun getAllList(): ArrayList<CataLogProductCategory> {
        return mFilteredList!!
    }

    fun clearList() {
        mList!!.clear()
        mFilteredList!!.clear()
        notifyDataSetChanged()
    }


    inner class StoreCategoryItemViewHolder(var binding: ItemRowProductCategoryBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: CataLogProductCategory? = mFilteredList!![position]
            if (data != null) {
                binding.data = data
                binding.rbChecked.setOnCheckedChangeListener(null)
                if(data.active!=null)
                binding.rbChecked.isChecked=data.active!!
                binding.data = data
                binding.rbChecked.setOnCheckedChangeListener { buttonView, isChecked ->
                    if(mListener!=null){
                        data.active=isChecked
                        mListener!!.onStatusChange(data, position)
                    }
                }

//                binding.addProduct.setOnClickListener {
//                    /* data.isAdded = !data.isAdded
//                     notifyDataSetChanged()*/
//                    if(mListener!=null&&!data.added!!) {
//                        data.added=true
//                        mListener!!.onProductAdded(data,adapterPosition)
//                    }
//                }
                binding.ivThreeDot.setOnClickListener {
                    showPopUpMenu(binding.ivThreeDot, data, adapterPosition)
                }
                binding.cardViewMain.setOnClickListener {
                    if (mListener != null) {
                        mListener!!.onCategorySelected(data!!)
                    }
                }
            }



        }

    }

    interface OnCategoryListener {
        fun onCategorySelected(prodCat: CataLogProductCategory)
        fun onEditProduct(product: CataLogProductCategory, position: Int)
        fun onStatusChange(product: CataLogProductCategory, position: Int)
        fun onDeleteCategory(prodCat: CataLogProductCategory,position:Int)
        fun addNewCategory()
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
            binding.btnAddNow.visibility=View.VISIBLE
            binding.tvDesc.text=context.getString(R.string.seems_like_you_have_not_added_any_category)
            binding.tvTitle.text=context.getString(R.string.no_category_found)
            binding.ivMain.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_shopping_cart))
            binding.btnAddNow.setOnClickListener {
                if(mListener!=null){
                    mListener!!.addNewCategory()
                }
            }
            binding.executePendingBindings()
        }

    }
    fun removeAt(position: Int, listSize: Int) {
        mFilteredList!!.removeAt(position)
        mList!!.removeAt(position)
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
    fun showPopUpMenu(ivThreeDot: ImageView, data: CataLogProductCategory, position: Int) {
        var popup = PopupMenu(context, ivThreeDot);
        //Inflating the Popup using xml file
        popup.menuInflater
            .inflate(R.menu.product_edit_options, popup.menu)
        val editmenu: MenuItem = popup.menu.findItem(R.id.action_edit)
        val deletemenu: MenuItem = popup.menu.findItem(R.id.action_delete)
        editmenu.title=context.getString(R.string.edit)
        deletemenu.title=context.getString(R.string.delete)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.action_edit -> {
                    if (mListener != null) {
                        mListener!!.onEditProduct(data, position)
                    }

                }
                R.id.action_delete -> {
                    mListener!!.onDeleteCategory(data, position)
                }

            }

            true
        })



        popup.show();
    }


}