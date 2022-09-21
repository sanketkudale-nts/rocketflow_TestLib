package com.rocketflyer.rocketflow.ui.accountlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.tracki.databinding.ItemMyAccountsBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.login.UserAccount
import com.tracki.utils.CommonUtils


/**
 * Created by Vikas Kesharvani on 16/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class AccountListAdapter : RecyclerView.Adapter<BaseViewHolder> {

    private var cellwidthWillbe: Int=0
    private var mListAccounts: ArrayList<UserAccount>? = null
    private var mListener:OnclickUserAccounts?=null
    private var context: Context? = null
    constructor(list: ArrayList<UserAccount>) : super()
    {
        this.mListAccounts=list
    }
    fun cellWidth(cellwidthWillbe: Int) {
        this.cellwidthWillbe = cellwidthWillbe;
    }
    fun setListener(listener:OnclickUserAccounts)
    {
        mListener=listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
         context=parent.context
        val itemMyAccountsBinding: ItemMyAccountsBinding = ItemMyAccountsBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return AccountListViewHolder(itemMyAccountsBinding)
    }

    override fun getItemCount(): Int {
        return if (mListAccounts != null && mListAccounts!!.isNotEmpty()) {
            mListAccounts!!.size
        } else {
            0
        }
    }

    fun addItems(userAccountList: List<UserAccount>?) {
        clearItems()
        userAccountList?.let { mListAccounts!!.addAll(it) }
        notifyDataSetChanged()
    }
    private fun clearItems() {
        mListAccounts!!.clear()
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }
    inner class AccountListViewHolder(var binding:ItemMyAccountsBinding):BaseViewHolder(binding.root){
        override fun onBind(position: Int) {
            var userAccount=mListAccounts?.get(position)
            binding.data= userAccount
//            if(userAccount!!.defaultAcc!!){
//                binding.cardViewMain.setBackgroundColor(Color.WHITE)
//            }
            if (cellwidthWillbe != 0) {
                CommonUtils.showLogMessage("e", "cellwidthWillbe", "" + cellwidthWillbe)
                binding.cardViewMain.setLayoutParams(FrameLayout.LayoutParams(
                        cellwidthWillbe-100, CommonUtils.dpToPixel(context, 150)))
            }
            binding.cardViewMain.setOnClickListener {
                userAccount!!.defaultAcc=true
                for (index in mListAccounts!!.indices){
                    if(index!=position){
                        mListAccounts!![index].defaultAcc=false
                    }

                }
                notifyDataSetChanged()
                mListener?.let {
                    it.onClickUser(userAccount)
                }
            }
        }

    }
    interface OnclickUserAccounts{
       fun onClickUser(userAccount: UserAccount?)
    }
}