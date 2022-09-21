package com.rocketflyer.rocketflow.ui.productdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.databinding.ItemPlaceBinding
import com.tracki.databinding.ItemRowProductAuditBinding
import com.tracki.databinding.LayoutStagesBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.selectorder.CataLogProductCategory

class StockPlacesAdapter (var context: Context) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var mPlacesList :ArrayList<GeoMappingPlaces>? = null
    var mListener: OnPlaceSelected?=null

    init {
        mListener = context as OnPlaceSelected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemPlaceBinding:ItemPlaceBinding =
            ItemPlaceBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return StorePlacesItemViewHolder(itemPlaceBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(holder.adapterPosition)
    }

    fun addItems(list: ArrayList<GeoMappingPlaces>){
        mPlacesList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mPlacesList != null && mPlacesList!!.isNotEmpty()) {
            mPlacesList!!.size
        } else {
            0
        }
    }

    fun getAllList(): List<GeoMappingPlaces> {
        return mPlacesList!!
    }

    inner class StorePlacesItemViewHolder(var binding: ItemPlaceBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data : GeoMappingPlaces? = mPlacesList!![position]
            if(data != null ){
                binding.data = data
                binding.cardPlace.setOnClickListener{

                    if(mListener!=null){
                        data.selected = true
                        for(item in mPlacesList!!){
                            if(item.geoId != data.geoId){
                                item.selected =false
                            }
                        }
                        notifyDataSetChanged()
                        mListener!!.onPlaceSelected(data)

                    }
                }

            }
        }
    }

    interface OnPlaceSelected{
        fun onPlaceSelected(data: GeoMappingPlaces)
    }
}