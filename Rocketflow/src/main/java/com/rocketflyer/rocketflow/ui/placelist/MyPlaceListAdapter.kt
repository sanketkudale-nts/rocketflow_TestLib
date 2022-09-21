package com.rocketflyer.rocketflow.ui.placelist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.databinding.ItemMyPlacesBinding
import com.tracki.databinding.MyPlaceEmptyViewBinding
import com.tracki.ui.addplace.AddLocationActivity
import com.tracki.ui.addplace.Hub


class MyPlaceListAdapter ( var context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: ArrayList<Hub> = ArrayList()
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1
    private var mDeleteListener: DeletePlaceListener?=null
    init {
        mDeleteListener= context as DeletePlaceListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType==VIEW_TYPE_NORMAL) {
            val view: ItemMyPlacesBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_my_places, parent, false)
            view.context = context
            MyPlaceListViewHolder(view)
        }else{
            val view: MyPlaceEmptyViewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.my_place_empty_view, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is MyPlaceListViewHolder) {
            var data = items[position]
            if(data!=null) {
                holder.bind(data)
            }
        }else if(holder is EmptyViewHolder){
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
    fun getList():ArrayList<Hub>{
        return items
    }


    fun addData(items: List<Hub>){
        if(this.items.isNotEmpty()) {
            this.items.clear()
        }
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) return 1
        return items.size
    }

    inner class MyPlaceListViewHolder(var myPlaceBinding: ItemMyPlacesBinding) :
            RecyclerView.ViewHolder(myPlaceBinding.root) {

        fun bind(dataModel: Hub) {
            this.myPlaceBinding.data = dataModel
            this.myPlaceBinding.rlMain.setOnClickListener {
                var intent= Intent(context, AddLocationActivity::class.java)
                intent.putExtra("data",dataModel)
                (context as MyPlaceListActivity).startActivityForResult(intent,MyPlaceListActivity.LAUNCH_ADD_LOCATION_ACTIVITY)
            }
            this.myPlaceBinding.ivEdit.setOnClickListener {
                var intent= Intent(context, AddLocationActivity::class.java)
                intent.putExtra("data",dataModel)
                (context as MyPlaceListActivity).startActivityForResult(intent,MyPlaceListActivity.LAUNCH_ADD_LOCATION_ACTIVITY)
            }
            this.myPlaceBinding.ivDelete.setOnClickListener {
                if(mDeleteListener!=null)
                    mDeleteListener!!.delete(dataModel,adapterPosition)
            }
            myPlaceBinding.executePendingBindings()

        }

    }
    inner class EmptyViewHolder(var myPlaceBinding: MyPlaceEmptyViewBinding) :
            RecyclerView.ViewHolder(myPlaceBinding.root) {

        fun bind() {

            myPlaceBinding.executePendingBindings()

        }

    }
    interface DeletePlaceListener{
        fun delete(dataModel: Hub,position: Int)
    }
}