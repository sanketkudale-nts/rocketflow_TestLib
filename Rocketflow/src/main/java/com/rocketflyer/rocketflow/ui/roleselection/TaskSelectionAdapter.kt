package com.rocketflyer.rocketflow.ui.roleselection


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.Offering
import com.tracki.databinding.ItemTaskSelectionBinding
import com.tracki.databinding.MyPlaceEmptyViewBinding

class TaskSelectionAdapter(var items: ArrayList<Offering>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1
    private var signupAs: String? = null;
    fun setSignupAs(signupAs: String) {
        this.signupAs = signupAs

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_NORMAL) {
            val view: ItemTaskSelectionBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_task_selection, parent, false)
            TaskOfferingViewHolder(view)
        } else {
            val view: MyPlaceEmptyViewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.my_place_empty_view, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TaskOfferingViewHolder) {
            var data = items[position]
            holder.bind(data)
        } else if (holder is EmptyViewHolder) {
            holder.bind()
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isNotEmpty()) {
            VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_EMPTY
        }
    }

    fun getList(): ArrayList<Offering> {
        return items
    }


    fun addData(items: List<Offering>) {
        if (this.items.isNotEmpty()) {
            this.items.clear()
        }
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) return 1
        return items.size
    }

    inner class TaskOfferingViewHolder(var myTaskSelectionBinding: ItemTaskSelectionBinding) :
            RecyclerView.ViewHolder(myTaskSelectionBinding.root) {

        fun bind(dataModel: Offering) {
            this.myTaskSelectionBinding.data = dataModel
            myTaskSelectionBinding.rlMain.setOnClickListener {
                if (signupAs != null && signupAs.equals("USER")) {
                    for (i in items.indices) {
                        items[i].selected = items[i].id == dataModel.id
                    }
                    notifyDataSetChanged()

                } else {
                    dataModel.selected = !dataModel.selected
                    notifyDataSetChanged()
                }
            }
            myTaskSelectionBinding.executePendingBindings()

        }

    }

    inner class EmptyViewHolder(var myPlaceBinding: MyPlaceEmptyViewBinding) :
            RecyclerView.ViewHolder(myPlaceBinding.root) {

        fun bind() {

            myPlaceBinding.executePendingBindings()

        }

    }


}