package com.tracki.ui.cart

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.data.model.response.config.LinkOptions
import com.tracki.databinding.ItemRowCartPlaceOrderBinding
import com.tracki.databinding.ItemRowProductOrderBinding
import com.tracki.databinding.LayoutEmptyCategoryViewBinding

import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.utils.CommonUtils
import com.tracki.utils.TrackiToast
import java.util.*

class CartItemAdapter(var context: Context) :
        RecyclerView.Adapter<BaseViewHolder>(), Filterable {
    var mFilteredList: ArrayList<CatalogProduct>? = ArrayList()
    var mList: ArrayList<CatalogProduct>? = ArrayList()
    var mListener: onCartProductAddListener? = null
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_NORMAL = 1
    private val VIEW_TYPE_DIRECT = 2
    private var linkOption: LinkOptions? = null
    private var dynamicPricing: Boolean = false
    init {
        mListener = context as onCartProductAddListener
    }

    public fun setLinkOption(linkOption: LinkOptions?) {
        this.linkOption = linkOption
    }
    public fun setDynamicPricing(dynamicPricing: Boolean) {
        this.dynamicPricing = dynamicPricing
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val simpleViewItemBinding: ItemRowCartPlaceOrderBinding =
                    ItemRowCartPlaceOrderBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                    )
            StoreProductItemViewHolder(simpleViewItemBinding)
        }else if (viewType == VIEW_TYPE_DIRECT) {
            val simpleViewItemBinding: ItemRowProductOrderBinding =
                ItemRowProductOrderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            StoreOrderItemViewHolder(simpleViewItemBinding)
        }  else {
            val simpleViewItemBinding: LayoutEmptyCategoryViewBinding =
                    LayoutEmptyCategoryViewBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                    )
            EmptyViewHolder(simpleViewItemBinding)
        }


    }


    override fun getItemCount(): Int {
        return if (mFilteredList != null && mFilteredList!!.isNotEmpty()) {
            mFilteredList!!.size
        } else {
            0
        }
    }

    fun addItems(list: ArrayList<CatalogProduct>) {
        /* if (mList != null *//*&& mList!!.isNotEmpty()*//*)
            mList!!.clear()
        if (mFilteredList != null *//*&& mFilteredList!!.isNotEmpty()*//*)
            mFilteredList!!.clear()*/
        mList!!.addAll(list)
        mFilteredList!!.addAll(list)
        notifyDataSetChanged()
    }

    fun getAllList(): List<CatalogProduct> {
        return mFilteredList!!
    }

    fun removeAt(position: Int, listSize: Int) {
        mFilteredList!!.removeAt(position)
        mList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSize)
    }

    private fun clearItems() {
        mFilteredList!!.clear()
    }

    fun clearList() {
        mList!!.clear()
        mFilteredList!!.clear()
        notifyDataSetChanged()

    }

    inner class StoreOrderItemViewHolder(var binding: ItemRowProductOrderBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: CatalogProduct? = mFilteredList!![position]
            if (data != null) {
                binding.data = data
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

    inner class StoreProductItemViewHolder(var binding: ItemRowCartPlaceOrderBinding) :
            BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            var data: CatalogProduct? = mFilteredList!![position]
            if (data != null) {
                binding.tvProductCounter.text = data.noOfProduct.toString()
                if(dynamicPricing){
                    binding.ivEditPrice.visibility=View.VISIBLE
                }else{
                    binding.ivEditPrice.visibility=View.GONE
                }
                binding.data = data

                if (data.price == data.sellingPrice) {
                    binding.tvMainPrice.visibility = View.GONE
                } else {
                    binding.tvMainPrice.paintFlags = binding.tvMainPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    if(dynamicPricing){
                        binding.tvMainPrice.visibility = View.GONE
                    }else{
                        binding.tvMainPrice.visibility = View.VISIBLE
                    }

                }


                binding.addProduct.setOnClickListener {
                    if (!data.addInOrder) {
                        data.addInOrder = true
                        data.noOfProduct = data.noOfProduct + 1
                        binding.tvProductCounter.text = data.noOfProduct.toString()

                    }
                    notifyDataSetChanged()
                    if (mListener != null) {
                        mListener!!.addProduct(data,position)
                    }
                }
                binding.ivAdd.setOnClickListener {
                    if (data.addInOrder) {
                        data.noOfProduct = data.noOfProduct + 1
                        data.addInOrder = data.noOfProduct > 0
                        binding.tvProductCounter.text = data.noOfProduct.toString()
                        notifyDataSetChanged()
                        if (mListener != null) {
                            mListener!!.addProduct(data,position)
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
                            mListener!!.addProduct(data,position)
                        }
                        if (data.noOfProduct == 0){
                            removeAt(position,mList!!.size)
                        }
                    }
                }
                binding.llImage.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                        mListener!!.viewProduct(data)
                    }
                }
                binding.llTxtData.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                        mListener!!.viewProduct(data)
                    }
                }
                binding.tvProductCounter.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                        openAddQuantityDialog(binding.tvProductCounter, position)
                    }
                }
                binding.ivEditPrice.setOnClickListener {
                    if (mListener != null) {
                        CommonUtils.preventTwoClick(it)
                        openChangePriceDialog(binding.tvSellingPrice, position)
                    }
                }


            }


        }

    }

    fun addFilter(newText: String) {
        clearItems()
        if (newText.isEmpty()) {
            mFilteredList!!.addAll(mList!!)
        } else {
            for (name in mList!!) {
                for (contact in mList!!) {
                    if (contact.name!!.toLowerCase().contains(newText) || contact.name!!.contains(
                                    newText
                            )) {
                        mFilteredList!!.add(contact)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString()
                clearItems()
                if (charString.isEmpty()) {
                    // mFilteredList=mList
                    mFilteredList!!.addAll(mList!!)
                } else {
                    var filteredList = ArrayList<CatalogProduct>()
                    for (contact in mList!!) {
                        if (contact.name!!.toLowerCase().contains(charString) || contact.name!!.contains(
                                        charString
                                )) {
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

    interface onCartProductAddListener {
        fun addProduct(data: CatalogProduct,position: Int)
        fun viewProduct(data: CatalogProduct)
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

    private fun openAddQuantityDialog(tvProductCounter: TextView, position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_add_quantity)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
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

        var data: CatalogProduct? = mFilteredList?.get(position)

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


    private fun openChangePriceDialog(tvSellingPrice: TextView, position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_change_price)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
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
        val btnChangePrice = dialog.findViewById<Button>(R.id.btnChangePrice)
        val edValue = dialog.findViewById<EditText>(R.id.edValue)
        btnCancel.setOnClickListener { dialog.dismiss() }
        edValue.setText(tvSellingPrice.text.toString().replace("₹",""))

        var data: CatalogProduct? = mFilteredList?.get(position)

        btnChangePrice.setOnClickListener {
            var value = edValue.text.toString().trim()
            if (value.isEmpty()) {
                TrackiToast.Message.showShort(context, "Please " + edValue.hint)
            } else {
                dialog.dismiss()
                if (data!!.addInOrder) {
                    data!!.sellingPrice = value.toFloat()
                    tvSellingPrice.text = "₹ "+data.sellingPrice.toString()
                    notifyDataSetChanged()
                    if (mListener != null) {
                        mListener!!.addProduct(data, position)
                    }

                }
            }
        }
        if (!dialog.isShowing) dialog.show()
    }


}