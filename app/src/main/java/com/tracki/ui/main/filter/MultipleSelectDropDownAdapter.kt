package com.tracki.ui.main.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.LocData


/**
 * Created by Vikas Kesharvani on 04/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class MultipleSelectDropDownAdapter( var context: Context) : RecyclerView.Adapter<MultipleSelectDropDownAdapter.MultipleDropDownViewHolder>(),Filterable {
    var mFilteredList= ArrayList<LocData>()
    var mList= ArrayList<LocData>()

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

    inner class MultipleDropDownViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var textName = view.findViewById<TextView>(R.id.tvHubName)
        var checkBox = view.findViewById<CheckBox>(R.id.cbSelect)
        fun bind(data: LocData) {
            if (data.name != null)
                textName.text = data.name
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = data.isSelected
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                mFilteredList[adapterPosition].isSelected = isChecked
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleDropDownViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.list_multiselect_hub, parent, false)
        return MultipleDropDownViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: MultipleDropDownViewHolder, position: Int) {
        var item = mFilteredList[position]
        holder.bind(item)

    }

    interface SelectedHubListener {
        fun onSelectedHub(mFilteredList: List<LocData>)
    }

    public fun getAllList(): ArrayList<LocData> {
        return mFilteredList
    }
}