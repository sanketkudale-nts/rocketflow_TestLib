package com.tracki.ui.tasklisting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.DashBoardBoxItem
import com.tracki.databinding.LayoutStagesBinding
import com.tracki.ui.base.BaseViewHolder


/**
 * Created by Vikas Kesharvani on 21/08/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class StageListAdapter(private var mList: ArrayList<DashBoardBoxItem>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private lateinit var listener: DashBoardListener
    private var context: Context? = null


    fun setListener(listener: DashBoardListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        val simpleViewItemBinding: LayoutStagesBinding =
                LayoutStagesBinding.inflate(
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

    fun addItems(list: ArrayList<DashBoardBoxItem>) {
        mList = list
        notifyDataSetChanged()
    }


    interface DashBoardListener {
        fun onItemClick(response: DashBoardBoxItem)
    }



    inner class SimpleViewHolder(private val mBinding: LayoutStagesBinding) :
            BaseViewHolder(mBinding.root) {


        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            if (lists.isSelected)
                mBinding.rlMain.setBackgroundResource(R.drawable.selected_stage)
            else
                mBinding.rlMain.setBackgroundResource(R.drawable.unselected_stage)
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