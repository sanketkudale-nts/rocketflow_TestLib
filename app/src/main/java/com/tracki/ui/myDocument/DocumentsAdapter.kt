package com.tracki.ui.myDocument

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.databinding.ItemMyDocumentEmptyBinding
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.ItemRowDocumentsBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.utils.DateTimeUtil
import com.tracki.utils.Log
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


/**
 * Created by Vikas Kesharvani on 18/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class DocumentsAdapter() : RecyclerView.Adapter<BaseViewHolder>() {

    private var mList: ArrayList<DocsData> = ArrayList()
    private var context: Context? = null
    private lateinit var listener: onPopupDeleteSelected


    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_DOCUMENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        return when (viewType) {
            VIEW_TYPE_DOCUMENT -> {
                val simpleViewItemBinding: ItemRowDocumentsBinding =
                    ItemRowDocumentsBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                DocumentViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyDocumentEmptyBinding =
                    ItemMyDocumentEmptyBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if ( mList.isNotEmpty()) {
            mList.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int) = if ( mList.isNotEmpty()) {
        VIEW_TYPE_DOCUMENT
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)


    @SuppressLint("NotifyDataSetChanged")
    fun addItems(context: Context,list: ArrayList<DocsData>) {

        this.context = context
        listener = context as onPopupDeleteSelected

        mList.addAll(list)
        if(mList.isNotEmpty()) {
            notifyItemRangeInserted(mList.size+1,list.size)
        }else{
            notifyDataSetChanged()
        }

//        val set: MutableSet<DocsData> = HashSet()
//        set.addAll(mList)
//        mList.clear()
//        mList.addAll(set)
//        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(docData: DocsData){
        val pos = mList.indexOf(docData)
        mList.remove(docData)
        notifyItemRemoved(pos)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        if (mList.isNotEmpty()) {
            mList.clear()
            notifyDataSetChanged()
        }

    }

    fun getAllList(): List<DocsData> {
        return mList!!
    }


    inner class DocumentViewHolder(private val mBinding: ItemRowDocumentsBinding) :
        BaseViewHolder(mBinding.root) {

        private val ivMoreMenu = mBinding.ivMoreMenu

        @SuppressLint("DiscouragedPrivateApi")
        override fun onBind(position: Int) {
            val docData = mList[position]

            ivMoreMenu.setOnClickListener {
                popup(it,docData)
            }


            val expiry = docData.validTill!!
            val current = Calendar.getInstance().timeInMillis

            if (expiry <= current){
                mBinding.tvDateTime.setTextColor(ContextCompat.getColor(context!!,R.color.red_dark3))
            }
            else{
                mBinding.tvDateTime.setTextColor(ContextCompat.getColor(context!!,R.color.text_gray))
            }

            mBinding.dateData = DateTimeUtil.getParsedDate5(expiry)

            mBinding.data = docData

            mBinding.executePendingBindings()
        }


    }

    @SuppressLint("DiscouragedPrivateApi")
    fun popup(view: View, docData: DocsData) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.view_delete_menu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_view -> {
                    Log.e("popup", "View")
                    listener.onPopupViewSelected(docData)
                    true
                }
                R.id.action_delete -> {
                    Log.e("popup", "Delete")
                    listener.onPopupDeleteSelected(docData)
                    true
                }

                else -> {
                    Log.e("popup", "None")
                    true
                }
            }
        }

        popupMenu.show()

        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val menu = popup.get(popupMenu)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)
    }



    inner class EmptyViewHolder(private val mBinding: ItemMyDocumentEmptyBinding) :
        BaseViewHolder(mBinding.root) {

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            if (context is MyDocumentActivity) {
                mBinding.tvMessage.text = "No Documents"
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    interface onPopupDeleteSelected{
        fun onPopupDeleteSelected(docData: DocsData)
        fun onPopupViewSelected(docData: DocsData)
    }
}

