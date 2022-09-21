package com.tracki.ui.scanqrcode

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.util.TypedValue
import android.view.*
import android.widget.*

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.tracki.R
import com.tracki.data.model.request.CommentsPostRequest
import com.tracki.data.model.request.GetCommentsOfPosts
import com.tracki.data.model.response.config.Comments
import com.tracki.data.model.response.config.LinkOptions
import com.tracki.databinding.ItemRowProductOrderBinding
import com.tracki.databinding.ItemRowSelectOrderBinding
import com.tracki.databinding.LayoutEmptyCategoryViewBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil
import com.tracki.utils.TrackiToast
import java.util.ArrayList

class ProductScanAdapter(var context: Context) :
    RecyclerView.Adapter<BaseViewHolder>(), Filterable {
    var mFilteredList: ArrayList<CatalogProduct> = ArrayList()
    var mList: ArrayList<CatalogProduct> = ArrayList()
    var mListener: OnProductAddListener? = null
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1
    private val VIEW_TYPE_DIRECT = 2
    private var linkOption: LinkOptions? = null

    init {
        mListener = context as OnProductAddListener
    }


    public fun setLinkOption(linkOption: LinkOptions?) {
        this.linkOption = linkOption
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val simpleViewItemBinding: ItemRowSelectOrderBinding =
                ItemRowSelectOrderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            StoreProductItemViewHolder(simpleViewItemBinding)
        } else if (viewType == VIEW_TYPE_DIRECT) {
            val simpleViewItemBinding: ItemRowProductOrderBinding =
                ItemRowProductOrderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            StoreOrderItemViewHolder(simpleViewItemBinding)
        } else {
            val simpleViewItemBinding: LayoutEmptyCategoryViewBinding =
                LayoutEmptyCategoryViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            EmptyViewHolder(simpleViewItemBinding)
        }


    }


    override fun getItemCount(): Int {
        return if (mFilteredList.isNotEmpty()) {
            mFilteredList.size
        } else {
            1
        }
    }

    fun addItem(product: CatalogProduct) {
        mFilteredList!!.add(product)
        if (mList.isNotEmpty()) {
           notifyItemInserted(mFilteredList!!.size)
        } else {
            notifyDataSetChanged()
        }
        mList.add(product)
    }

    fun addItems(list: ArrayList<CatalogProduct>) {
        mFilteredList!!.addAll(list)
        if (mList.isNotEmpty()) {
            notifyItemRangeInserted(mList.size + 1, list!!.size)
        } else {
            notifyDataSetChanged()
        }
        mList.addAll(list)
    }

    fun getAllList(): List<CatalogProduct> {
        return mFilteredList
    }

    fun removeAt(position: Int, listSize: Int) {
        mFilteredList.removeAt(position)
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSize)
    }

    private fun clearItems() {
        mFilteredList.clear()
    }

    fun clearList() {
        mList.clear()
        mFilteredList.clear()
        notifyDataSetChanged()

    }

    inner class StoreOrderItemViewHolder(var binding: ItemRowProductOrderBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: CatalogProduct? = mFilteredList[position]
            if (data != null) {
                binding.data = data
                var mainImage = data.img
                if (data.img != null && data.img!!.isNotEmpty()) {
                    GlideApp.with(context!!).load(mainImage)
                        .placeholder(R.drawable.ic_picture)
                        .into(binding.ivProducts)
                } else {
                    binding.ivProducts.setImageResource(R.drawable.ic_picture)
                }
                binding.addProduct.setOnClickListener {
                    if (!data.addInOrder) {
                        data.addInOrder = true
                        data.noOfProduct = 1

                    } else {
                        data.addInOrder = false
                        data.noOfProduct = 0
                    }
                    notifyDataSetChanged()
                    if (mListener != null) {
                        mListener!!.addProduct(data, position)
                    }
                }

                binding.llImage.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                    }
                }
                binding.llTxtData.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                    }
                }


            }


        }

    }


    inner class StoreProductItemViewHolder(var binding: ItemRowSelectOrderBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: CatalogProduct? = mFilteredList[position]
            if (data != null) {
                binding.tvProductCounter.text = data.noOfProduct.toString()
                binding.data = data
                binding.addProduct.setOnClickListener {
                    if (!data.addInOrder) {
                        data.addInOrder = true
                        data.noOfProduct = data.noOfProduct + 1
                        binding.tvProductCounter.text = data.noOfProduct.toString()

                    }
                    notifyDataSetChanged()
                    if (mListener != null) {
                        mListener!!.addProduct(data, position)
                    }
                }
                binding.ivAdd.setOnClickListener {
                    if (data.addInOrder) {
                        data.noOfProduct = data.noOfProduct + 1
                        data.addInOrder = data.noOfProduct > 0
                        binding.tvProductCounter.text = data.noOfProduct.toString()
                        notifyDataSetChanged()
                        if (mListener != null) {
                            mListener!!.addProduct(data, position)
                        }

                    }
                }
                binding.ivMinus.setOnClickListener {
                    if (data.addInOrder) {
                        if (data.noOfProduct > 0) {
                            data.noOfProduct = data.noOfProduct - 1
                        }
                        data.addInOrder = data.noOfProduct > 0
                        binding.tvProductCounter.text = data.noOfProduct.toString()
                        notifyDataSetChanged()
                        if (mListener != null) {
                            mListener!!.addProduct(data, position)
                        }

                        if (data.noOfProduct == 0){
                            mListener!!.removeProduct(data,position)
                        }
                    }
                }
                binding.llImage.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                    }
                }
                binding.llTxtData.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                    }
                }
                binding.tvProductCounter.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                        openAddQuantityDialog(binding.tvProductCounter, position)
                    }
                }


            }


        }

    }

    private fun openAddQuantityDialog(tvProductCounter: TextView, position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_add_quantity)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        val btnCancel = dialog.findViewById<ImageView>(R.id.btnCancel)
        val btnAdd = dialog.findViewById<Button>(R.id.btnAdd)
        val edValue = dialog.findViewById<EditText>(R.id.edValue)
        btnCancel.setOnClickListener { dialog.dismiss() }
        edValue.setText(tvProductCounter.text.toString())

        val data: CatalogProduct? = mFilteredList[position]

        btnAdd.setOnClickListener {
            var value = edValue.text.toString().trim()
            if (value.isEmpty()) {
                TrackiToast.Message.showShort(context, "Please " + edValue.hint)
            }else if (value.isEmpty()) {
                TrackiToast.Message.showShort(context, "Please " + edValue.hint)
            }  else {
                dialog.dismiss()
                if (data!!.addInOrder) {
                    data!!.noOfProduct = value.toInt()
                    data.addInOrder = data.noOfProduct > 0
                    tvProductCounter.text = data.noOfProduct.toString()
                    notifyDataSetChanged()
                    if (mListener != null) {
                        mListener!!.addProduct(data, position)
                    }

                }
            }
        }
        if (!dialog.isShowing) dialog.show()
    }

    fun addFilter(newText: String) {
        clearItems()
        if (newText.isEmpty()) {
            mFilteredList.addAll(mList)
        } else {
            for (name in mList) {
                for (contact in mList) {
                    if (contact.name!!.toLowerCase()
                            .contains(newText.toLowerCase()) || contact.name!!.contains(
                            newText
                        )
                    ) {
                        mFilteredList.add(contact)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString().toLowerCase()
                clearItems()
                if (charString.isEmpty()) {
                    // mFilteredList=mList
                    mFilteredList.addAll(mList)
                } else {
                    var filteredList = ArrayList<CatalogProduct>()
                    for (contact in mList) {
                        if (contact.name!!.toLowerCase()
                                .contains(charString) || contact.name!!.contains(charString)
                        ) {
                            filteredList.add(contact)
                        }
                    }
                    mFilteredList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mFilteredList = results.values as java.util.ArrayList<CatalogProduct>
                notifyDataSetChanged()
            }
        }
    }

    interface OnProductAddListener {
        fun addProduct(data: CatalogProduct, position: Int)
        fun removeProduct(data: CatalogProduct, position: Int)
    }

    inner class EmptyViewHolder(var binding: LayoutEmptyCategoryViewBinding) :
        BaseViewHolder(binding.root) {

        override fun onBind(position: Int) {
            binding.tvDesc.text = context.getString(R.string.seems_like_no_product)
            binding.tvTitle.text = context.getString(R.string.no_product_found)
            binding.ivMain.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_shopping_cart
                )
            )

            binding.executePendingBindings()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (mFilteredList!!.isNotEmpty()) {
            if (linkOption != null && linkOption == LinkOptions.DIRECT)
                VIEW_TYPE_DIRECT
            else {
                VIEW_TYPE_NORMAL
            }
        } else {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(holder.adapterPosition)
    }


}
