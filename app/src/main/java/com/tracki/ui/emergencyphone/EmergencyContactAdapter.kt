package com.tracki.ui.emergencyphone

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.tracki.R
import com.tracki.data.model.response.config.EmergencyContact
import com.tracki.databinding.ItemEmergencyContactEmptyViewBinding
import com.tracki.databinding.ItemEmergencyContactViewBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.CircleTransform
import com.tracki.ui.custom.GlideApp

/**
 * Created by rahul on 7/12/18
 */
class EmergencyContactAdapter(private var mList: ArrayList<EmergencyContact>?) :
        RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null
    private lateinit var listener: ContactsListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_SIMPLE -> {

                val simpleViewItemBinding: ItemEmergencyContactViewBinding =
                        ItemEmergencyContactViewBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                ContactViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemEmergencyContactEmptyViewBinding =
                        ItemEmergencyContactEmptyViewBinding.inflate(
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

    override fun getItemViewType(position: Int) = if (mList != null && !mList!!.isEmpty()) {
        VIEW_TYPE_SIMPLE
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<EmergencyContact>) {
        mList = list
        notifyDataSetChanged()
    }

    fun setListener(listener: ContactsListener) {
        this.listener = listener
    }

    interface ContactsListener {
        fun onEditContact(response: EmergencyContact, position: Int)
        fun onDeleteContact(response: EmergencyContact)
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_SIMPLE = 1
    }

    inner class ContactViewHolder(private val mBinding: ItemEmergencyContactViewBinding) :
            BaseViewHolder(mBinding.root), EmergencyContactItemViewModel.ItemClickListener {

        override fun onEditClick(response: EmergencyContact) {
            listener.onEditContact(response,adapterPosition)
        }

        override fun onDeleteClick(response: EmergencyContact) {
           /* mList!!.removeAt(adapterPosition)
            notifyDataSetChanged()*/
            listener.onDeleteContact(response)
        }

        private lateinit var simpleViewModel: EmergencyContactItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            GlideApp.with(context!!)
                    .asBitmap()
                    .load("")
                    .apply(RequestOptions()
                            .transform(CircleTransform())
                            .placeholder(R.drawable.placeholder_car))
                    .error(R.drawable.placeholder_car)
                    .into(mBinding.ivContactImage)

            simpleViewModel = EmergencyContactItemViewModel(lists, this)
            mBinding.viewModel = simpleViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }

    }

    inner class EmptyViewHolder(private val mBinding: ItemEmergencyContactEmptyViewBinding) :
            BaseViewHolder(mBinding.root), ECEmptyItemViewModel.ECEmptyListener {

        override fun onBind(position: Int) {
            val emptyViewModel = ECEmptyItemViewModel(this)
            mBinding.viewModel = emptyViewModel
        }

//        override fun onRetryClick() {
//            listener.onRetryClick()
//        }

    }
}