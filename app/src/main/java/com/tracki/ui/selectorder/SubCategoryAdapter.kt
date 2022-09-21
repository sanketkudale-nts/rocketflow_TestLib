package com.tracki.ui.selectorder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.databinding.ItemRowSubCategoryBinding
import com.tracki.ui.base.BaseViewHolder


class SubCategoryAdapter (var context: Context) :
    RecyclerView.Adapter<SubCategoryAdapter.SubCategoryItemViewHolder>() {
    var listItems: ArrayList<CataLogProductCategory>?=null
    var mListener:OnSubCategorySelected?=null
     init {
         mListener=context as OnSubCategorySelected
     }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryItemViewHolder {

        val simpleViewItemBinding: ItemRowSubCategoryBinding =
            ItemRowSubCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return SubCategoryItemViewHolder(simpleViewItemBinding)
    }

    override fun onBindViewHolder(holder: SubCategoryItemViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return if (listItems != null && listItems!!.isNotEmpty()) {
            listItems!!.size
        } else {
            0
        }
    }

    fun addItems(list: ArrayList<CataLogProductCategory>) {
            listItems=list
        notifyDataSetChanged()
    }

    fun getAllList(): List<CataLogProductCategory> {
        return listItems!!
    }

    fun clearList() {
        if (listItems!!.isNotEmpty()) {
            listItems!!.clear()
            notifyDataSetChanged()
        }

    }


    inner class SubCategoryItemViewHolder(var binding: ItemRowSubCategoryBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: CataLogProductCategory? = listItems!![position]
            if (data != null) {
                binding.data = data
            }
           binding.rlMain.setOnClickListener {
               data!!.selected=true
               if(mListener!=null){
                   mListener!!.onCategorySelected(data)
               }
               for (item in listItems!!){
                   if(item.cid!=data!!.cid){
                       item.selected=false
                   }

               }
               notifyDataSetChanged()
           }

        }

    }

    interface OnSubCategorySelected{
        fun onCategorySelected(data: CataLogProductCategory)
    }

}