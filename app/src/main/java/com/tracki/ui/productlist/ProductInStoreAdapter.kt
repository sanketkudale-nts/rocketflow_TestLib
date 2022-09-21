package com.tracki.ui.productlist

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.tracki.R
import com.tracki.databinding.ItemRowProductStoreBinding
import com.tracki.databinding.LayoutEmptyCategoryViewBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.selectorder.CatalogProduct
import java.util.*


//item_row_product_store

class ProductInStoreAdapter(var context: Context) :
    RecyclerView.Adapter<BaseViewHolder>(), Filterable {
    var mFilteredList: ArrayList<CatalogProduct>? = ArrayList()
    var mList: ArrayList<CatalogProduct>? = ArrayList()
    var mListener: OnProductSelectedListener? = null
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1
    private var emptyDataMessage:String?=null
    init {
        mListener=context as OnProductSelectedListener
    }
    fun setEmptyDataMessage(emptyDataMessage:String?){
       this.emptyDataMessage=emptyDataMessage
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val simpleViewItemBinding: ItemRowProductStoreBinding =
                ItemRowProductStoreBinding.inflate(
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

    fun addItems(list: ArrayList<CatalogProduct>) {
       /* if (mList != null *//*&& mList!!.isNotEmpty()*//*)
            mList!!.clear()
        if (mFilteredList != null *//*&& mFilteredList!!.isNotEmpty()*//*)
            mFilteredList!!.clear()*/
        mFilteredList!!.addAll(list)
        if(mList!=null&&mList!!.isNotEmpty()) {
            notifyItemRangeInserted(mList!!.size+1,list!!.size)
        }else{
            notifyDataSetChanged()
        }
        mList!!.addAll(list)
    }

    fun getAllList(): ArrayList<CatalogProduct> {
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


    inner class StoreProductItemViewHolder(var binding: ItemRowProductStoreBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: CatalogProduct? = mFilteredList!![position]
            if (data != null) {

                binding.rbChecked.setOnCheckedChangeListener(null)
                binding.rbChecked.isChecked=data.active!!
                binding.data = data
                if(data.price==data.sellingPrice){
                    binding.tvMainPrice.visibility=View.GONE
                    binding.tvPercentageOff.visibility=View.GONE
                }else{
                    binding.tvMainPrice.paintFlags = binding.tvMainPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    binding.tvMainPrice.visibility=View.VISIBLE
                }

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
                    if(mListener!=null)
                        mListener!!.onProductClicked(data)
                }


            }


        }

    }

    fun addFilter(newText: String) {
        clearItems()
        if (newText.isEmpty()) {
            mFilteredList!!.addAll(mList!!)
        } else {
            for (name in mList!!) {
                for (contact in mList!!) {
                    if (contact.name!!.lowercase().contains(newText.lowercase())||contact.name!!.contains(newText)) {
                        mFilteredList!!.add(contact)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString().lowercase()
                clearItems()
                if (charString.isEmpty()) {
                    mFilteredList!!.addAll(mList!!)
                } else {
                    var filteredList = ArrayList<CatalogProduct>()
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
                mFilteredList = results.values as java.util.ArrayList<CatalogProduct>
                notifyDataSetChanged()
            }
        }
    }

    interface OnProductSelectedListener {
        fun onEditProduct(product: CatalogProduct, position: Int)
        fun onRemovedProduct(product: CatalogProduct, position: Int)
        fun onStatusChange(product: CatalogProduct, position: Int)
        fun onProductClicked(product: CatalogProduct)
        fun addNewProduct()
    }

    inner class EmptyViewHolder(var binding: LayoutEmptyCategoryViewBinding) :
        BaseViewHolder(binding.root) {

        override fun onBind(position: Int) {
            binding.btnAddNow.visibility = View.VISIBLE
            if(emptyDataMessage==null)
            binding.tvDesc.text = context.getString(R.string.seems_like_no_product)
            else{
                binding.tvDesc.text = emptyDataMessage
            }
            binding.tvTitle.text = context.getString(R.string.no_product_found)
            binding.ivMain.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_shopping_cart
                )
            )
            binding.btnAddNow.setOnClickListener {
                if (mListener != null) {
                   mListener!!.addNewProduct()
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

    fun showPopUpMenu(ivThreeDot: ImageView, data: CatalogProduct, position: Int) {
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
                    mListener!!.onRemovedProduct(data, position)
                }

            }

            true
        })



        popup.show();
    }

}
