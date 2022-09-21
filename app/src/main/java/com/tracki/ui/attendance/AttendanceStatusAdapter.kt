package com.tracki.ui.attendance

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.AttendanceStatusData
import com.tracki.databinding.ItemAttendanceStatusBinding
import com.tracki.ui.attendance.attendance_tab.AttendanceFragment
import com.tracki.ui.base.BaseViewHolder


class AttendanceStatusAdapter(private var mList: ArrayList<AttendanceStatusData>?) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null
    private var listener: AttendanceSelectedListener? = null
    fun setListener(context: Fragment) {
        listener = context as AttendanceSelectedListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context

        val simpleViewItemBinding: ItemAttendanceStatusBinding =
            ItemAttendanceStatusBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return AttendanceStatusViewHolder(simpleViewItemBinding)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<AttendanceStatusData>) {
        if (mList != null && mList!!.isNotEmpty())
            mList!!.clear()
        mList!!.addAll(list)
        notifyDataSetChanged()
    }

    fun getAllList(): List<AttendanceStatusData> {
        return mList!!
    }

    fun getCountOfStatus(status: String): Int {
        var count = 0;
        for (data in mList!!) {
            if (status == data.status) {
                count = data.count
                break
            }


        }
        return count
    }


    fun updateCount(status: String, count: Int) {
        for (data in mList!!) {
            if (status == data.status) {
                data.count = count
            }


        }
        notifyDataSetChanged()

    }


    inner class AttendanceStatusViewHolder(private val mBinding: ItemAttendanceStatusBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            mBinding.cardViewMain.setOnClickListener {
                if (listener != null) {
                    for (data in mList!!) {
                        data.isSelected = data.status.equals(lists.status)

                    }
                    notifyDataSetChanged()
                    listener!!.onItemSelected(lists)
                }
            }

        }

    }

    interface AttendanceSelectedListener {
        fun onItemSelected(status: AttendanceStatusData)
    }


}