package com.rocketflyer.rocketflow.ui.userdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.AttendanceStatusData
import com.tracki.databinding.ItemUserOptionBinding
import com.tracki.ui.base.BaseViewHolder

class UserOptionsSelectedAdapter (private var mList: ArrayList<AttendanceStatusData>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null
    private var listener: UserOptionsSelectedListener? = null
    fun setListener(context: Context){
        listener=context as UserOptionsSelectedListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context

        val simpleViewItemBinding: ItemUserOptionBinding =
                ItemUserOptionBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
        return UserOptionsViewHolder(simpleViewItemBinding)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<AttendanceStatusData>) {
        if(mList!=null&&mList!!.isNotEmpty())
            mList!!.clear()
        mList!!.addAll(list)
        notifyDataSetChanged()
    }

    fun getAllList(): List<AttendanceStatusData> {
        return mList!!
    }

    fun updateCount( status:String,count:Int){
        for(data in mList!!){
            if(status == data.status){
                data.count=count
            }


        }
        notifyDataSetChanged()

    }


    inner class UserOptionsViewHolder(private val mBinding: ItemUserOptionBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            mBinding.cardViewMain.setOnClickListener {
                if(listener!=null) {
                    for (data in mList!!) {
                        data.isSelected = data.status.equals(lists.status)

                    }
                    notifyDataSetChanged()
                    listener!!.onItemSelected(lists)
                }
            }

        }

    }
    interface UserOptionsSelectedListener{
        fun onItemSelected(status: AttendanceStatusData)
    }


}