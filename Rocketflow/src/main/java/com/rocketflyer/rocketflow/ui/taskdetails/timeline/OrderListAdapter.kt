package com.rocketflyer.rocketflow.ui.taskdetails.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.OrderData
import com.tracki.data.model.response.config.OrderDetails
import com.tracki.data.model.response.config.OrderStatus
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.ItemRowOrderBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.payouts.AdminUserPayoutsActivity
import com.tracki.utils.TrackiToast
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Vikas Kesharvani on 17/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class OrderListAdapter(private var mList: ArrayList<OrderDetails>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null


    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_INVENTORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_INVENTORY -> {
                val simpleViewItemBinding: ItemRowOrderBinding =
                        ItemRowOrderBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                OrdersViewModel(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyEarningEmptyBinding =
                        ItemMyEarningEmptyBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int) = if (mList != null && mList!!.isNotEmpty()) {
        VIEW_TYPE_INVENTORY
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<OrderDetails>) {
        mList = list
    }


    inner class OrdersViewModel(private val mBinding: ItemRowOrderBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]

            val input = lists!!.date
            try {
                val parser = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date: Date = parser.parse(input)
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                val formattedDate: String = formatter.format(date)
                lists.date = formattedDate
            } catch (e: Exception) {

            }
            if(lists.approver==null)
                lists.approver="N/A"
            mBinding.data = lists
            var count = position + 1
            //mBinding.tvOrderNumber.text = "Order $count"
            if(lists.status!=null){
                mBinding.tvStatus.text=lists.status!!.name
                when(lists.status!!){
                    OrderStatus.PENDING->{
                        mBinding.tvStatus.background=ContextCompat.getDrawable(context!!, R.drawable.status_pending)
                    }
                    OrderStatus.CONFIRMED->{
                        mBinding.tvStatus.background=ContextCompat.getDrawable(context!!,R.drawable.status_confirmed)
                    }
                    OrderStatus.CANCELLED->{
                        mBinding.tvStatus.background=ContextCompat.getDrawable(context!!,R.drawable.status_canceled)
                    }
                    OrderStatus.CLOSED->{
                        mBinding.tvStatus.background=ContextCompat.getDrawable(context!!,R.drawable.status_closed)
                    }
                }
            }else{
                mBinding.tvStatus.visibility= View.GONE
            }
            mBinding.tvOrder.text = "Order $count"
            if (lists.orderItemsInfo != null && lists.orderItemsInfo!!.isNotEmpty() ) {
                var adapter = OrderInventoryListAdapter((lists.orderItemsInfo)!!)
                mBinding.adapter = adapter
            }
            mBinding.tvOrderBy.setOnClickListener {
                if( lists.customerName!=null)
                TrackiToast.Message.showShort(context, lists.customerName!!)
            }
            mBinding.executePendingBindings()
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptyBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            if (context is AdminUserPayoutsActivity) {
                mBinding.tvMessage.text = "No Orders"
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }
}