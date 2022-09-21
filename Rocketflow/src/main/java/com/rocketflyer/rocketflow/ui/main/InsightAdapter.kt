package com.rocketflyer.rocketflow.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.ServiceDescr
import com.tracki.databinding.LayoutItemInsightsBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.utils.CommonUtils

class InsightAdapter(private var mList: ArrayList<ServiceDescr?>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null
    private var cellwidthWillbe: Int = 0
    fun cellWidth(cellwidthWillbe: Int) {
        this.cellwidthWillbe = cellwidthWillbe;
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        val simpleViewItemBinding: LayoutItemInsightsBinding =
                LayoutItemInsightsBinding.inflate(
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
        if (mList != null && mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<ServiceDescr?>) {
        mList = list
        notifyDataSetChanged()
    }


    inner class SimpleViewHolder(private val mBinding: LayoutItemInsightsBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            if(mList!=null) {
                val lists: ServiceDescr? = mList!![position]
                if (lists != null) {
                    if (cellwidthWillbe != 0) {
                        CommonUtils.showLogMessage("e", "cellwidthWillbe", "" + cellwidthWillbe)
                        mBinding.cardViewMain.layoutParams = FrameLayout.LayoutParams(
                                cellwidthWillbe - 20, CommonUtils.dpToPixel(context, 85))
                    }
                    mBinding.data = lists
                    mBinding.executePendingBindings()
                }
            }
        }
    }


}