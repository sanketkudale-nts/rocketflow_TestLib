package com.tracki.ui.dynamicformpreview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.DataType
import com.tracki.data.model.response.config.FormData
import com.tracki.databinding.ItemDynamicFormEmptyViewBinding
import com.tracki.databinding.ItemFormPicturesBinding
import com.tracki.databinding.ItemFormPreviewBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.dynamicform.FormEmptyItemViewModel

class PreviewAdapter(val formDataList: ArrayList<FormData>) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        private const val VIEW_EMPTY = 0
        private const val VIEW_DISPLAY_TEXT = 1
        private const val VIEW_DISPLAY_PICS = 2
    }

    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        this.context = parent.context
        when (viewType) {
            VIEW_DISPLAY_TEXT -> {
                val itemFormPreview = ItemFormPreviewBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return FormPreviewViewHolder(itemFormPreview)
            }
            VIEW_DISPLAY_PICS -> {
                val itemFormPicture = ItemFormPicturesBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return FormPictureViewHolder(itemFormPicture)
            }
            else -> {
                val emptyViewBinding = ItemDynamicFormEmptyViewBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return formDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (formDataList.isNotEmpty()) {
            if (formDataList[position].type != null) {
                when (formDataList[position].type) {
                    DataType.TEXT -> VIEW_DISPLAY_TEXT
                    DataType.FILE -> VIEW_DISPLAY_PICS
                    else -> VIEW_EMPTY
                }
            } else {
                VIEW_EMPTY
            }
        } else {
            VIEW_EMPTY
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    /**
     * Class used to handle all the text fields for email,text & number.
     */
    private inner class FormPreviewViewHolder(var mBinding: ItemFormPreviewBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val formData = formDataList[position]
            val emptyItemViewModel = FormPreviewItemViewModel(formData)
            mBinding.viewModel = emptyItemViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    /**
     * Class used to handle all the text fields for email,text & number.
     */
    private inner class FormPictureViewHolder(var mBinding: ItemFormPicturesBinding) :
            BaseViewHolder(mBinding.root) {

        private var linearLayoutAdd: LinearLayout = mBinding.root.findViewById(R.id.lLayout)

        override fun onBind(position: Int) {
            val formData = formDataList[position]
            val emptyItemViewModel = FormPicturesItemViewModel(formData)
            mBinding.viewModel = emptyItemViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()

            if (formData.file?.isNotEmpty()!!) {
                linearLayoutAdd.removeAllViews()
                for (i in formData.file?.indices!!) {
                    val mInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val view = mInflater.inflate(R.layout.add_photo_view, linearLayoutAdd, false) as ImageView
                    GlideApp.with(context!!).load(formData.file?.get(i)?.path).into(view)
                    linearLayoutAdd.addView(view)
                }
            }
        }
    }



    /**
     * If hashMap is empty show empty view
     */
    private inner class EmptyViewHolder(private val mBinding: ItemDynamicFormEmptyViewBinding) :
            BaseViewHolder(mBinding.root), FormEmptyItemViewModel.ClickListener {

        override fun onBind(position: Int) {
            val emptyItemViewModel = FormEmptyItemViewModel(this)
            mBinding.viewModel = emptyItemViewModel
        }
    }
}
