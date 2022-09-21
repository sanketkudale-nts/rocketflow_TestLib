package com.rocketflyer.rocketflow.ui.tasklisting

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import java.util.*

/**
 * Created by rahul on 5/12/18
 */
class StateAdapter(private val statusList: ArrayList<Status>, private val clickListener: StatusClickListener) : RecyclerView.Adapter<StateAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task_status, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        viewHolder.bind()
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    private fun makeAllSelected() {
        for (i in statusList.indices) {
            statusList[i].isSelected = i == 0
        }
        notifyDataSetChanged()
    }

    interface StatusClickListener {
        fun onStatusClickListener(item: String)
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val title: TextView = view.findViewById(R.id.tvStatus)
        private val shape = GradientDrawable()

        init {
            view.setOnClickListener(this)
        }

        fun bind() {
            val status = statusList[adapterPosition]
            title.text = status.statusName
            val color = Color.parseColor(status.statusColor)

            shape.shape = GradientDrawable.RECTANGLE
            shape.setStroke(1, color)
            shape.cornerRadius = 4f
            if (status.isSelected) {
                shape.setColor(color)
                title.setTextColor(Color.WHITE)
            } else {
                shape.setColor(Color.WHITE)
                title.setTextColor(color)
            }
            title.background = shape
        }

        override fun onClick(v: View) {
            clickListener.onStatusClickListener(statusList[adapterPosition].statusName)

            for ((index, _) in statusList.withIndex()) {
                statusList[index].isSelected = index == adapterPosition
            }
            notifyDataSetChanged()
        }
    }
}