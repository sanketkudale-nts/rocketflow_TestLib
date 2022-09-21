package com.rocketflyer.rocketflow.ui.feeds

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.tracki.R
import com.tracki.data.model.request.AddressInfo
import com.tracki.data.model.response.config.*
import com.tracki.databinding.*
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.dynamicform.dynamicfragment.PlayVideoActivity
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.feeddetails.FeedDetailsActivity
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.ui.taskdetails.timeline.ProductMapAdapter
import com.tracki.utils.JSONConverter
import com.tracki.utils.Log
import com.tracki.utils.ZoomableImageView
import java.lang.Exception
import androidx.core.content.ContextCompat.startActivity

import android.os.Bundle
import androidx.core.content.ContextCompat
import java.io.Serializable


/**
 * Created by Vikas Kesharvani on 04/01/21.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
open class FeedsAdapter(private var mList: ArrayList<Post>?) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null
    private var mListener: LikePostUser? = null

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_POST_IMAGE = 1
        private const val VIEW_TYPE_POSTS_VIDEO = 2
        private const val VIEW_TYPE_POSTS_AUDIO = 3
        private const val VIEW_TYPE_POSTS_NO_MEDIA = 4
        private const val VIEW_TYPE_DF_DATA = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        mListener = context as LikePostUser
        return when (viewType) {
            VIEW_TYPE_POST_IMAGE -> {
                val simpleViewItemBinding: ItemsLayoutFeedsBinding =
                    ItemsLayoutFeedsBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                PostListViewHolder(simpleViewItemBinding)
            }
            VIEW_TYPE_POSTS_VIDEO -> {
                val simpleViewItemBinding: ItemsLayoutFeedsVideoBinding =
                    ItemsLayoutFeedsVideoBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                VideoPostListViewHolder(simpleViewItemBinding)
            }
            VIEW_TYPE_POSTS_NO_MEDIA -> {
                val simpleViewItemBinding: ItemsLayoutNoMediaBinding =
                    ItemsLayoutNoMediaBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                NoMediaPostListViewHolder(simpleViewItemBinding)
            }
            VIEW_TYPE_DF_DATA -> {
                val simpleViewItemBinding: ItemLayoutFeedDfDataBinding =
                    ItemLayoutFeedDfDataBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                DfDataViewHolder(simpleViewItemBinding)
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
        if (mList!![position].contentType == ContentType.PLAIN_TEXT) {
            if (mList!![position].mediaType != null) {
                if (mList!![position].mediaType == MEDIATYPE.IMAGE) {
                    VIEW_TYPE_POST_IMAGE
                } else if (mList!![position].mediaType == MEDIATYPE.VIDEO) {
                    VIEW_TYPE_POSTS_VIDEO
                } else {
                    VIEW_TYPE_EMPTY
                }
            } else {
                VIEW_TYPE_POSTS_NO_MEDIA
            }

        } else {
            VIEW_TYPE_DF_DATA
        }


    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
        holder.onBind(holder.adapterPosition)

    fun addItems(list: ArrayList<Post>) {
        mList!!.addAll(list)
        val set: MutableSet<Post> = HashSet()
        set.addAll(mList!!)
        mList!!.clear()
        mList!!.addAll(set)
        notifyDataSetChanged()
    }

    fun getAllList(): List<Post> {
        return mList!!
    }

    fun clearList() {
        if (mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }


    inner class PostListViewHolder(private val mBinding: ItemsLayoutFeedsBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            mBinding.ivLike.setOnCheckedChangeListener(null)
            if (lists.loggedInUserLike) {
                mBinding.ivLike.isClickable = false
                mBinding.ivLike.isEnabled = false
            }
            if (lists.fileUrl != null && lists.fileUrl!!.isNotEmpty()) {
                mBinding.progressBar.visibility = View.VISIBLE
                GlideApp.with(mBinding.ivImageMain).load(lists.fileUrl)
                    .error(R.drawable.ic_picture)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            mBinding.progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(mBinding.ivImageMain)
            }
            mBinding.ivLike.isChecked = lists.loggedInUserLike
            mBinding.ivLike.setOnCheckedChangeListener { buttonView, isChecked ->
                mListener?.let {
                    it.likePost(isChecked, lists.postId, layoutPosition)
                }

            }
            mBinding.tvLikes.setOnClickListener {
                mListener?.let {
                    it.allLikes(lists.postId)
                }
            }
            mBinding.tvComments.setOnClickListener {
                mListener?.let {
                    it.openComment(lists.postId, adapterPosition)
                }
            }
            mBinding.ivComment.setOnClickListener {
                mListener?.let {
                    it.openComment(lists.postId, adapterPosition)
                }
            }
            mBinding.ivImageMain.setOnClickListener {
                if (lists.fileUrl != null && lists.fileUrl!!.isNotEmpty()) {
                    openDialogShowImage(lists.fileUrl!!)
                }

            }
            mBinding.cvMain.setOnClickListener {
                mListener?.let {
                    it.openDetails( lists, position)
                }
            }
            //
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
        val imageView = dialog.findViewById<View>(R.id.ivImages) as ZoomableImageView
//        Glide.with(context!!)
//                .load(url)
//                .error(R.drawable.ic_picture)
//                .placeholder(R.drawable.ic_picture)
//                .into(imageView)
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
        dialog.window!!.attributes = lp
        imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }

    inner class VideoPostListViewHolder(private val mBinding: ItemsLayoutFeedsVideoBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            mBinding.ivLike.setOnCheckedChangeListener(null)
            if (lists.loggedInUserLike) {
                mBinding.ivLike.isClickable = false
                mBinding.ivLike.isEnabled = false
            }
            if (lists.fileUrl != null && lists.fileUrl!!.isNotEmpty()) {
                mBinding.progressBar.visibility = View.VISIBLE
                GlideApp.with(mBinding.ivVideo).load(lists.fileUrl)
                    .error(R.drawable.ic_picture)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            mBinding.progressBar.visibility = View.GONE
                            mBinding.ivPlay.visibility = View.VISIBLE
                            return false
                        }
                    })
                    .into(mBinding.ivVideo)
            }

            mBinding.ivLike.isChecked = lists.loggedInUserLike
            mBinding.ivLike.setOnCheckedChangeListener { buttonView, isChecked ->
                mListener?.let {
                    it.likePost(isChecked, lists.postId, layoutPosition)
                }

            }
            mBinding.tvLikes.setOnClickListener {
                mListener?.let {
                    it.allLikes(lists.postId)
                }
            }
            mBinding.tvComments.setOnClickListener {
                mListener?.let {
                    it.openComment(lists.postId, adapterPosition)
                }
            }
            mBinding.ivComment.setOnClickListener {
                mListener?.let {
                    it.openComment(lists.postId, adapterPosition)
                }
            }

            mBinding.rlVideo.setOnClickListener {
                if (context != null && lists.fileUrl != null) {
                    var intent = Intent(context, PlayVideoActivity::class.java)
                    intent.putExtra("url", lists.fileUrl)
                    context!!.startActivity(intent)
                }
            }
            mBinding.cvMain.setOnClickListener {
                mListener?.let {
                    it.openDetails( lists, position)
                }
            }
            //
            mBinding.executePendingBindings()
        }


    }

    inner class DfDataViewHolder(private val mBinding: ItemLayoutFeedDfDataBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            try {
                val jsonConverter: JSONConverter<DfDataMain> = JSONConverter()
                var response: DfDataMain = jsonConverter.jsonToObject(
                    lists.message.toString(),
                    DfDataMain::class.java
                ) as DfDataMain
                if (response.df_data != null && response.df_data!!.isNotEmpty()) {
                    if (response.df_data!!.size > 3) {
                        var listOfThree = ArrayList<FeedDfDataMap>()
                        listOfThree.add(response.df_data!![0])
                        listOfThree.add(response.df_data!![1])
                        listOfThree.add(response.df_data!![2])
                        var adapter = DynamicFormDataFeedAdapter(listOfThree)
                        mBinding.rvDfData.adapter = adapter

                        mBinding.btnViewMore.visibility = View.VISIBLE


                        mBinding.btnViewMore.setOnClickListener {
                            openDialogShowMap(response.df_data)
                        }
                    } else {
                        var adapter = DynamicFormDataFeedAdapter(response.df_data!!)
                        mBinding.rvDfData.adapter = adapter
                        mBinding.btnViewMore.visibility = View.GONE
                    }


                }
                mBinding.cvMain.setOnClickListener {
                    mListener?.let {
                        it.openDetails( lists, position)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            //
            mBinding.executePendingBindings()
        }


    }

    private fun openDialogShowMap(df_data: ArrayList<FeedDfDataMap>?) {

        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_pop_up_feed_df_data)
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
        val rvDfData = dialog.findViewById<RecyclerView>(R.id.rvDfData)

        if (df_data!!.isNotEmpty()) {
            var adapter = DynamicFormDataFeedAdapter(df_data)
            rvDfData.adapter = adapter
        }

        dialog.window!!.attributes = lp
        ivBack.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }


    inner class NoMediaPostListViewHolder(private val mBinding: ItemsLayoutNoMediaBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            mBinding.ivLike.setOnCheckedChangeListener(null)
            if (lists.loggedInUserLike) {
                mBinding.ivLike.isClickable = false
                mBinding.ivLike.isEnabled = false
            }

            mBinding.ivLike.isChecked = lists.loggedInUserLike
            mBinding.ivLike.setOnCheckedChangeListener { buttonView, isChecked ->
                mListener?.let {
                    it.likePost(isChecked, lists.postId, layoutPosition)
                }

            }
            mBinding.tvLikes.setOnClickListener {
                mListener?.let {
                    it.allLikes(lists.postId)
                }
            }
            mBinding.tvComments.setOnClickListener {
                mListener?.let {
                    it.openComment(lists.postId, adapterPosition)
                }
            }
            mBinding.ivComment.setOnClickListener {
                mListener?.let {
                    it.openComment(lists.postId, adapterPosition)
                }
            }
            mBinding.cvMain.setOnClickListener {
                mListener?.let {
                    it.openDetails( lists, position)
                }
            }

            //
            mBinding.executePendingBindings()
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptyBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            mBinding.imageViewEmpty.setImageResource(R.drawable.ic_no_posts)
            if (context is FeedsActivity) {
                mBinding.tvMessage.text = "Nothing Posted Yet.."
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    interface LikePostUser {
        fun likePost(islike: Boolean, postId: String?, position: Int)
        fun allLikes(postId: String?)
        fun openComment(postId: String?, position: Int)
        fun openDetails(data: Post?, position: Int)
    }


}