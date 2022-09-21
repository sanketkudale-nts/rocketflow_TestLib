package com.tracki.ui.taskdetails.timeline

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.tracki.R
import com.tracki.databinding.ItemRowProductImageBinding
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.utils.CommonUtils

class ShowImageGridAdapter(private var mList: ArrayList<String>?) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private var cellwidthWillbe: Int = 0
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        val simpleViewItemBinding: ItemRowProductImageBinding =
            ItemRowProductImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return SimpleViewHolder(simpleViewItemBinding)
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            0
        }
    }

    public fun clearList() {
        if (mList != null && mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<String>) {
        mList = list
        notifyDataSetChanged()
    }

    fun cellWidth(cellwidthWillbe: Int) {
        this.cellwidthWillbe = cellwidthWillbe;
    }


    inner class SimpleViewHolder(private val mBinding: ItemRowProductImageBinding) :
        BaseViewHolder(mBinding.root) {


        override fun onBind(position: Int) {
            val lists = mList!![position]
            if (cellwidthWillbe != 0) {
                CommonUtils.showLogMessage("e", "cellwidthWillbe", "" + cellwidthWillbe)
                mBinding.ivImage.setLayoutParams(
                    FrameLayout.LayoutParams(
                        CommonUtils.dpToPixel(context, cellwidthWillbe), CommonUtils.dpToPixel(context, cellwidthWillbe)
                    )
                )
            }
            if ( lists.isNotEmpty()) {
                GlideApp.with(context!!).load(lists)
                    .placeholder(R.drawable.ic_picture)
                    .into(mBinding.ivImage)
            }
            mBinding.ivImage.setOnClickListener(View.OnClickListener {
               // openDialogShowImage(lists)

            })

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
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


}