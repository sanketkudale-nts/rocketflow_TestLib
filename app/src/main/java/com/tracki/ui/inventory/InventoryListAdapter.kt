package com.tracki.ui.inventory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.Inventory
import com.tracki.data.model.response.config.InventoryStatus
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.LayoutRowItemProductBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.payouts.AdminUserPayoutsActivity
import com.tracki.utils.TrackiToast
import java.util.*


/**
 * Created by Vikas Kesharvani on 16/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
open class InventoryListAdapter(private var mList: ArrayList<Inventory>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null


    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_INVENTORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_INVENTORY -> {
                val simpleViewItemBinding: LayoutRowItemProductBinding =
                        LayoutRowItemProductBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                InventoryViewModel(simpleViewItemBinding)
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

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(holder.adapterPosition)

    fun addItems(list: ArrayList<Inventory>) {
//        clearList()
//       mList!!.addAll(list)

        //clearList()
        Collections.sort(list, InventoryComparator())
        mList!!.addAll(list)
//        val set: HashSet<Inventory> = HashSet()
//        set.addAll(mList!!)
//        mList!!.clear()
//        mList!!.addAll(set)
        notifyDataSetChanged()
    }

    fun clearList() {
        if (mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }

    fun getAllList(): List<Inventory> {
        return mList!!
    }


    inner class InventoryViewModel(private val mBinding: LayoutRowItemProductBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            if (!lists.isAdded) {
                lists.addquantity = 0
                mBinding.tvProductCounter.text = "0"
                mBinding.ivAdded.visibility = View.GONE
                mBinding.butonAdd.text = "Add"
                mBinding.butonAdd.background = ContextCompat.getDrawable(context!!, R.drawable.bg_blue_button_round_corner)

            }else{
               var count =lists.addquantity
                mBinding.tvProductCounter.text = count.toString()
                mBinding.ivAdded.visibility = View.VISIBLE
                mBinding.butonAdd.text = "Remove"
                mBinding.butonAdd.background = ContextCompat.getDrawable(context!!, R.drawable.btn_disable_inactive)
            }
            mBinding.data = lists
            if (lists.fleetImg != null && lists.fleetImg!!.isNotEmpty()) {
                GlideApp.with(context!!).load(lists.fleetImg)
                        .placeholder(R.drawable.ic_loading)
                        .into(mBinding.ivProduct)
            }


            if (lists.status == InventoryStatus.ACTIVE) {
                mBinding.ivMinus.isEnabled = true
                mBinding.ivAdd.isEnabled = true
            } else {
                mBinding.ivMinus.isEnabled = false
                mBinding.ivAdd.isEnabled = false
            }
            mBinding.ivMinus.setOnClickListener {
                var count = lists.addquantity - 1;
                if (count > 0) {
                    lists.addquantity = count
                    mBinding.tvProductCounter.text = count.toString()
                } else {
                    mBinding.tvProductCounter.text = count.toString()
                    lists.isAdded = false
                    mBinding.ivAdded.visibility = View.GONE
                    mBinding.butonAdd.text = "Add"
                    mBinding.butonAdd.background = ContextCompat.getDrawable(context!!, R.drawable.bg_blue_button_round_corner)
                }


            }
            mBinding.ivAdd.setOnClickListener {
                var count = lists.addquantity + 1
                if (count < lists.quantity) {
                    lists.addquantity = count
                    mBinding.tvProductCounter.text = count.toString()
                } else {
                    TrackiToast.Message.showShort(context,"Out of Stock")
                }

            }
            mBinding.butonAdd.setOnClickListener {
                if (!lists.isAdded) {
                    if (lists.addquantity > 0) {
                        lists.isAdded = true
                        mBinding.ivAdded.visibility = View.VISIBLE
                        mBinding.butonAdd.text = "Remove"
                        mBinding.butonAdd.background = ContextCompat.getDrawable(context!!, R.drawable.btn_disable_inactive)
                    } else {
                        lists.isAdded = true
                        mBinding.ivAdded.visibility = View.VISIBLE
                        mBinding.butonAdd.text = "Remove"
                        mBinding.butonAdd.background = ContextCompat.getDrawable(context!!, R.drawable.btn_disable_inactive)
                        var count = lists.addquantity + 1
                        if (count < lists.quantity) {
                            lists.addquantity = count
                            mBinding.tvProductCounter.text = count.toString()
                        } else {
                            TrackiToast.Message.showShort(context,"Out of Stock")
                        }
                        //Toast.makeText(context, "Please add quantity ,current is 0", Toast.LENGTH_SHORT).show()
                    }

                } else {
//                    lists.addquantity=0
//                    mBinding.tvProductCounter.text = "0"
                    lists.isAdded = false
                    mBinding.ivAdded.visibility = View.GONE
                    mBinding.butonAdd.text = "Add"
                    mBinding.butonAdd.background = ContextCompat.getDrawable(context!!, R.drawable.bg_blue_button_round_corner)
                }
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
                mBinding.tvMessage.text = "No Inventory"
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }
}