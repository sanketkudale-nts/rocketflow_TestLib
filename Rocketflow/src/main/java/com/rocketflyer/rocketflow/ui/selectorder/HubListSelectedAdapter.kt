package com.rocketflyer.rocketflow.ui.selectorder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.LocData

class HubListSelectedAdapter ( var context: Context) : RecyclerView.Adapter<HubListSelectedAdapter.HubDropDownViewHolder>(),
    Filterable {
    var mFilteredList= ArrayList<LocData>()
    var mList= ArrayList<LocData>()
    private var mListener:SelectedHubListener?=null


    fun setHubSelectedListener(listener:SelectedHubListener){
        mListener=listener
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString().toLowerCase()
                clearItems()
                if (charString.isEmpty()) {
                    mFilteredList.addAll(mList)
                } else {
                    var filteredList = java.util.ArrayList<LocData>()
                    for (contact in mList) {
                        if (contact.name!!.toLowerCase().contains(charString)||contact.name!!.contains(charString)) {
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
                mFilteredList = results.values as java.util.ArrayList<LocData>
                notifyDataSetChanged()
            }
        }
    }

    private fun clearItems() {
        mFilteredList!!.clear()
    }

    fun addItems(list: java.util.ArrayList<LocData>) {
        /* if (mList != null *//*&& mList!!.isNotEmpty()*//*)
            mList!!.clear()
        if (mFilteredList != null *//*&& mFilteredList!!.isNotEmpty()*//*)
            mFilteredList!!.clear()*/
        mFilteredList.addAll(list)
        if(mList.isNotEmpty()) {
            notifyItemRangeInserted(mList.size+1,list.size)
        }else{
            notifyDataSetChanged()
        }
        mList.addAll(list)
    }

    inner class HubDropDownViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var textName = view.findViewById<TextView>(R.id.tvHubName)
        var rlHubName = view.findViewById<RelativeLayout>(R.id.rlHubName)
        fun bind(data: LocData) {
            if (data.name != null)
                textName.text = data.name
            rlHubName.setOnClickListener {
                if(mListener!=null)
                    mListener!!.onSelectedHub(data)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HubDropDownViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_list_hub, parent, false)
        return HubDropDownViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: HubDropDownViewHolder, position: Int) {
        var item = mFilteredList[position]
        holder.bind(item)

    }

    interface SelectedHubListener {
        fun onSelectedHub(data:LocData)
    }

    public fun getAllList(): ArrayList<LocData> {
        return mFilteredList
    }
}