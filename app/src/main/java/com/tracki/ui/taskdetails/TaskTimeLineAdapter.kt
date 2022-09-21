package com.tracki.ui.taskdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.databinding.ItemTimeLineBinding
import com.tracki.ui.taskdetails.timeline.TaskDetailsFragment
import com.tracki.utils.DateTimeUtil

class TaskTimeLineAdapter(var context: TaskDetailsFragment) :
        RecyclerView.Adapter<TaskTimeLineAdapter.TimeLineViewHolder>() {
    var items: ArrayList<StageHistoryData> = ArrayList()
    public var mListener:PreviousFormListener?=null
    init {
        mListener=context as PreviousFormListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        val view: ItemTimeLineBinding= DataBindingUtil.inflate(layoutInflater, R.layout.item_time_line, parent, false)
       return  TimeLineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        var data = items[position]
        holder.bind(data)



    }

    fun addData(items: List<StageHistoryData>){
        if(this.items.isNotEmpty()) {
            this.items.clear()
        }
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (items.isEmpty()) return 0
        return items.size
    }

    inner class TimeLineViewHolder(var timeLineBinding: ItemTimeLineBinding) :
            RecyclerView.ViewHolder(timeLineBinding.root) {

        fun bind(dataModel: StageHistoryData) {
            if(dataModel!!.timeStamp!=null) {
                var timeDate = DateTimeUtil.getParsedDate(dataModel!!.timeStamp!!) + " , " + DateTimeUtil.getParsedTime(dataModel!!.timeStamp!!)
                timeLineBinding.tvTimeDate.text = timeDate
            }
            if(dataModel.dfdId!=null&& dataModel.dfdId!!.isNotEmpty())
            {
                timeLineBinding.btnDetails.visibility= View.VISIBLE
                timeLineBinding.rlTimeLine.setOnClickListener {
                    if(mListener!=null)
                        mListener!!.openForm(dataModel)
                }
            }
            else{
                timeLineBinding.btnDetails.visibility= View.GONE
            }
            timeLineBinding.data=dataModel
            timeLineBinding.executePendingBindings()

        }

    }

    interface PreviousFormListener{
        fun openForm(dataModel: StageHistoryData)
    }

}