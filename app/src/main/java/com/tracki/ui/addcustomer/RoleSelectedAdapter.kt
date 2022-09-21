package com.tracki.ui.addcustomer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.RoleConfigData
import com.tracki.data.model.response.config.UserData
import com.tracki.databinding.LayoutRolesBinding
import com.tracki.ui.base.BaseViewHolder

class RoleSelectedAdapter() : RecyclerView.Adapter<BaseViewHolder>() {
    public var mList=ArrayList<RoleConfigData>()
        get() = field
    private lateinit var listener: RoleSelectedListener
    private var context: Context? = null


    fun setListener(listener: RoleSelectedListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        val simpleViewItemBinding: LayoutRolesBinding =
                LayoutRolesBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)

        return SimpleViewHolder(simpleViewItemBinding)
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            0
        }
    }

    public fun clearList() {
        if (mList.isNotEmpty()) {
            mList.clear()
            notifyDataSetChanged()
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<RoleConfigData>) {
        addZeroIndex()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    private fun addZeroIndex() {
        var rollConfigData=RoleConfigData()
        rollConfigData.roleName="ALL"
        rollConfigData.roleId="-1"
        rollConfigData.isSelected=true
        mList.add(0,rollConfigData)

    }


    interface RoleSelectedListener {
        fun onItemClick(response: RoleConfigData)
    }


    inner class SimpleViewHolder(private val mBinding: LayoutRolesBinding) :
            BaseViewHolder(mBinding.root) {


        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            if (lists.isSelected) {
                mBinding.rlMain.setBackgroundResource(R.drawable.selected_roles)
                mBinding.tvStageName.setTextColor(ContextCompat.getColor(context!!,R.color.white))
            } else {
                mBinding.rlMain.setBackgroundResource(R.drawable.unselected_roles)
                mBinding.tvStageName.setTextColor(ContextCompat.getColor(context!!,R.color.black))
            }
            mBinding.rlMain.setOnClickListener(View.OnClickListener {
                for (i in mList!!.indices) {
                    mList!![i].isSelected = i == adapterPosition

                }
                listener.onItemClick(lists)
                notifyDataSetChanged()
            })

            mBinding.executePendingBindings()
        }
    }


}