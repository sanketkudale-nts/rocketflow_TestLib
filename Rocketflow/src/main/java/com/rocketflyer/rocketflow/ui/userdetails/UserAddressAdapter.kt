package com.rocketflyer.rocketflow.ui.userdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.request.AddressInfo
import com.tracki.databinding.ItemUserAddressBinding
import com.tracki.databinding.MyPlaceEmptyViewBinding

class UserAddressAdapter(var items: ArrayList<AddressInfo>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1
    var context: Context? = null
    private var mListener: UserDeletePlaceListener? = null
    fun setListener(context: Fragment) {
        mListener = context as UserDeletePlaceListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_NORMAL) {
            val view: ItemUserAddressBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_user_address, parent, false)
            view.context = context
            MyPlaceListViewHolder(view)
        } else {
            val view: MyPlaceEmptyViewBinding = DataBindingUtil.inflate(layoutInflater, R.layout.my_place_empty_view, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyPlaceListViewHolder) {
            var data = items[position]
            if (data != null) {
                holder.bind(data)
            }
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

    fun getList(): ArrayList<AddressInfo> {
        return items
    }


    fun addData(items: List<AddressInfo>) {
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

    inner class MyPlaceListViewHolder(var myPlaceBinding: ItemUserAddressBinding) :
            RecyclerView.ViewHolder(myPlaceBinding.root) {

        fun bind(dataModel: AddressInfo) {
            this.myPlaceBinding.data = dataModel
            this.myPlaceBinding.rlMain.setOnClickListener {
                if (mListener != null)
                    mListener!!.view(dataModel, adapterPosition)

//                var intent= Intent(context, AddLocationActivity::class.java)
//                intent.putExtra("data",dataModel)
//                (context as MyPlaceListActivity).startActivityForResult(intent, MyPlaceListActivity.LAUNCH_ADD_LOCATION_ACTIVITY)
            }
            this.myPlaceBinding.ivEdit.setOnClickListener {
                mListener!!.edit(dataModel, adapterPosition)
            }
            this.myPlaceBinding.ivDelete.setOnClickListener {
                if (mListener != null)
                    mListener!!.delete(dataModel, adapterPosition)
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

    interface UserDeletePlaceListener {
        fun delete(dataModel: AddressInfo, position: Int)
        fun edit(dataModel: AddressInfo, position: Int)
        fun view(dataModel: AddressInfo, position: Int)
    }
}