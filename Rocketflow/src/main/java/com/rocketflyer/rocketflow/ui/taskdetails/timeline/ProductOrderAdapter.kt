package com.rocketflyer.rocketflow.ui.taskdetails.timeline

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.tracki.R
import com.tracki.data.model.response.config.ProductMap
import com.tracki.data.model.response.config.ProductOrder
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.ItemRowProductBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.payouts.AdminUserPayoutsActivity

class ProductOrderAdapter(private var mList: ArrayList<ProductOrder>?) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_INVENTORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_INVENTORY -> {
                val simpleViewItemBinding: ItemRowProductBinding =
                    ItemRowProductBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                ProductOrdersListViewModel(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyEarningEmptyBinding =
                    ItemMyEarningEmptyBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
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

    fun addItems(list: ArrayList<ProductOrder>) {
        mList = list
    }


    inner class ProductOrdersListViewModel(private val mBinding: ItemRowProductBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            if (lists.picture != null && lists.picture!!.isNotEmpty()) {
                var sizeOfImageList = lists.picture!!.size
                if (sizeOfImageList > 1) {
                    mBinding.tvMoreImage.visibility = View.VISIBLE
                    mBinding.tvMoreImage.text = "More ${sizeOfImageList - 1} +"
                    mBinding.rlMoreImage.setOnClickListener {
                        openDialogImage(lists.picture)
                    }
                } else {
                    mBinding.tvMoreImage.visibility = View.GONE
                    mBinding.rlMoreImage.setOnClickListener {
                        openDialogShowImage(lists.picture!![0])
                    }
                }
                var mainImage = lists.picture!![0]
                GlideApp.with(context!!).load(mainImage)
                    .placeholder(R.drawable.ic_picture)
                    .into(mBinding.ivProduct)

            } else {
                mBinding.ivProduct.setImageResource(R.drawable.ic_picture)
            }
            if (lists.prodInfoMap != null && lists.prodInfoMap!!.isNotEmpty()) {
                val listOrderDetais = ArrayList<ProductMap>()
                if ( lists!!.productName != null) {
                    listOrderDetais.add(
                        ProductMap(context!!.getString(R.string.reg_no), lists!!.productName)
                    )
                }
                val hmIterator: Iterator<*> = lists.prodInfoMap!!.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val orderInnerData =
                        ProductMap(mapElement.key.toString(), mapElement.value.toString())
                    listOrderDetais.add(orderInnerData)
                }
                if (listOrderDetais.isNotEmpty()) {
                    mBinding.tvProductName.background =
                        ContextCompat.getDrawable(context!!, R.drawable.bg_blue_button_round_corner)
                    mBinding.tvProductName.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.white
                        )
                    )
                    mBinding.tvProductName.setOnClickListener {
                        openDialogShowMap(listOrderDetais, lists)
                    }
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
                mBinding.tvMessage.text = "No Orders"
            }

            mBinding.executePendingBindings()
        }
    }

    private fun openDialogShowImage(url: String) {

        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_show_image_big)
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
        val imageView = dialog.findViewById<View>(R.id.ivImages) as ImageView

        Glide.with(context!!)
            .asBitmap()
            .load(url)
            .error(R.drawable.ic_picture)
            .placeholder(R.drawable.ic_picture)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {
                    imageView.setImageBitmap(resource)
                }
            })
        /*  Glide.with(context!!)
                  .load(url)
                  .error(R.drawable.ic_picture)
                  .placeholder(R.drawable.ic_picture)
                  .into(imageView)*/
        dialog.window!!.attributes = lp
        // imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun openDialogShowMap(
        listOrderDetais: ArrayList<ProductMap>?,
        productOrder: ProductOrder?
    ) {
        if (listOrderDetais == null)
            return

        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_show_product_map)
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
        val ivBack = dialog.findViewById<View>(R.id.ivBack) as ImageView
        val tvDetails = dialog.findViewById<TextView>(R.id.tvDetails)
        val tvMoreImage = dialog.findViewById<TextView>(R.id.tvMoreImage)
        val rvProductMap = dialog.findViewById<RecyclerView>(R.id.rvProductMap)
        val productImage = dialog.findViewById<ImageView>(R.id.productImage)
        val rlMoreImage = dialog.findViewById<RelativeLayout>(R.id.rlMoreImage)
        val rvProduct = dialog.findViewById<RelativeLayout>(R.id.rvProduct)
        if (productOrder != null && productOrder!!.picture != null &&
            productOrder!!.picture!!.isNotEmpty()) {
            productImage.visibility=View.VISIBLE
            rvProduct.visibility=View.VISIBLE
            var sizeOfImageList = productOrder.picture!!.size
            if (sizeOfImageList > 1) {
                tvMoreImage.visibility = View.VISIBLE
                tvMoreImage.text = "More ${sizeOfImageList - 1} +"
                rlMoreImage.setOnClickListener {
                    openDialogImage(productOrder.picture)
                }
            } else {
                tvMoreImage.visibility = View.GONE
                rlMoreImage.setOnClickListener {
                  //  openDialogShowImage(productOrder.picture!![0])
                }
            }
            var mainImage = productOrder.picture!![0]
            GlideApp.with(context!!).load(mainImage)
                .placeholder(R.drawable.ic_picture)
                .into(productImage)

        } else {
            productImage.visibility=View.GONE
            rvProduct.visibility=View.GONE
//            productImage.setImageResource(R.drawable.ic_picture)
        }

        tvDetails.text =
            context!!.getString(R.string.inventory_details)
        if (listOrderDetais.isNotEmpty()) {
            var adapter = ProductMapAdapter(listOrderDetais)
            rvProductMap.adapter = adapter
        }

        dialog.window!!.attributes = lp
        ivBack.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }

    private fun openDialogImage(listImage: ArrayList<String>?) {
        if (listImage == null)
            return
        var cellwidthWillbe = 0
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_show_product_images)
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
        val ivBack = dialog.findViewById<View>(R.id.ivBack) as ImageView
        val tvDetails = dialog.findViewById<TextView>(R.id.tvDetails)
        val rvImages = dialog.findViewById<RecyclerView>(R.id.rvImages)
        val linearLayout: RelativeLayout = dialog.findViewById(R.id.rlMain)
        val viewTreeObserver = linearLayout.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                linearLayout.viewTreeObserver.removeGlobalOnLayoutListener(this)
                val width = linearLayout.measuredWidth
                val height = linearLayout.measuredHeight
                cellwidthWillbe = width / 3
            }
        })
        val gridLayoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        rvImages.layoutManager = gridLayoutManager
        val adapter = ShowImageGridAdapter(listImage)
        adapter.cellWidth(cellwidthWillbe)
        rvImages.setAdapter(adapter)


        dialog.window!!.attributes = lp
        ivBack.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }


}