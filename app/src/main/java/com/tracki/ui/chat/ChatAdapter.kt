package com.tracki.ui.chat

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.tracki.R
import com.tracki.data.model.response.config.ChatResponse
import com.tracki.databinding.*
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.dynamicform.dynamicfragment.PlayVideoActivity
import com.tracki.utils.Log
import com.tracki.utils.ZoomableImageView
import java.io.IOException
import java.util.*


class ChatAdapter(private var mList: ArrayList<ChatResponse>?) : RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null
    private var listener: ChatListener? = null

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_MESSAGE_TYPE_ME = 1
        private const val VIEW_MESSAGE_TYPE_OTHER = 2
        private const val VIEW_LOAD_MORE = 3
        private const val VIEW_MESSAGE_TYPE_IMAGE_ME = 4
        private const val VIEW_MESSAGE_TYPE_IMAGE_OTHER = 5
        private const val VIEW_MESSAGE_TYPE_DOC_ME = 6
        private const val VIEW_MESSAGE_TYPE_DOC_OTHER = 7
        private const val VIEW_MESSAGE_TYPE_VIDEO_ME = 8
        private const val VIEW_MESSAGE_TYPE_VIDEO_OTHER = 9
        private const val VIEW_MESSAGE_TYPE_AUDIO_ME = 10
        private const val VIEW_MESSAGE_TYPE_AUDIO_OTHER = 11
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        when (viewType) {
            VIEW_MESSAGE_TYPE_ME -> {
                val simpleViewItemBinding: ItemMyMessageBinding = ItemMyMessageBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return MeViewHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_OTHER -> {
                val simpleViewItemBinding: ItemTheirMessageBinding = ItemTheirMessageBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return OtherViewHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_IMAGE_ME -> {
                val simpleViewItemBinding: ItemCustomOutcomingImageMessageBinding = ItemCustomOutcomingImageMessageBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return MeImageViewHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_IMAGE_OTHER -> {
                val simpleViewItemBinding: ItemCustomIncomingImageMessageBinding = ItemCustomIncomingImageMessageBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return OtherImageViewHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_DOC_ME -> {
                val simpleViewItemBinding: ItemMyDocViewBinding = ItemMyDocViewBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return MeDocHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_DOC_OTHER -> {
                val simpleViewItemBinding: ItemTheirDocBinding = ItemTheirDocBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return TheirDocHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_VIDEO_ME -> {
                val simpleViewItemBinding: ItemMyVideoBinding = ItemMyVideoBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return MeVideoHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_VIDEO_OTHER -> {
                val simpleViewItemBinding: ItemTheirVideoBinding = ItemTheirVideoBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return OtherVideoHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_AUDIO_ME -> {
                val simpleViewItemBinding: ItemMyAudioBinding = ItemMyAudioBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return MeAudioHolder(simpleViewItemBinding)
            }
            VIEW_MESSAGE_TYPE_AUDIO_OTHER -> {
                val simpleViewItemBinding: ItemTheirAudioBinding = ItemTheirAudioBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return OtherAudioHolder(simpleViewItemBinding)
            }
            VIEW_LOAD_MORE -> {
                val emptyViewBinding: ItemViewLoadMoreBinding = ItemViewLoadMoreBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return LoadMoreHolder(emptyViewBinding)
            }
            else -> {
                val emptyViewBinding: ItemEmptyMessageBinding = ItemEmptyMessageBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false)
                return EmptyViewHolder(emptyViewBinding)
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

    override fun getItemViewType(position: Int): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            if (mList!![position].isLoadMore == null) {
                if (mList!![position].isMe!!) {

                    when (mList!![position].type) {
                        "TEXT" -> {
                            VIEW_MESSAGE_TYPE_ME
                        }
                        "IMAGE" -> {
                            VIEW_MESSAGE_TYPE_IMAGE_ME
                        }
                        "DOCUMENT" -> {
                            VIEW_MESSAGE_TYPE_DOC_ME
                        }
                        "VIDEO" -> {
                            VIEW_MESSAGE_TYPE_VIDEO_ME
                        }
                        "AUDIO" -> {
                            VIEW_MESSAGE_TYPE_AUDIO_ME
                        }

                        else ->
                            VIEW_TYPE_EMPTY
                    }

                } else {
                    when (mList!![position].type) {
                        "TEXT" -> {
                            VIEW_MESSAGE_TYPE_OTHER
                        }
                        "IMAGE" -> {
                            VIEW_MESSAGE_TYPE_IMAGE_OTHER
                        }
                        "DOCUMENT" -> {
                            VIEW_MESSAGE_TYPE_DOC_OTHER
                        }
                        "VIDEO" -> {
                            VIEW_MESSAGE_TYPE_VIDEO_OTHER
                        }
                        "AUDIO" -> {
                            VIEW_MESSAGE_TYPE_AUDIO_OTHER
                        }
                        else ->
                            VIEW_TYPE_EMPTY
                    }

                }
            } else {
                VIEW_LOAD_MORE
            }
        } else {
            VIEW_TYPE_EMPTY
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun setListener(listener: ChatListener) {
        this.listener = listener
    }

    /**
     * Add or load new items into the chat adapter.
     */
    fun addItems(list: ArrayList<ChatResponse>?, isReplace: Boolean) {
        if (list != null) {
            mList = list
            notifyDataSetChanged()
        }
    }
    fun getList():ArrayList<ChatResponse>?{
        return mList
    }
    fun addSingleItems(chat:ChatResponse?) {
        if (chat != null) {
            mList!!.add(chat)
            notifyDataSetChanged()
        }
    }

    interface ChatListener {
        fun onLoadMore()
    }

    inner class OtherImageViewHolder(private val mBinding: ItemCustomIncomingImageMessageBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var otherItemViewModel: OtherItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            Log.e("urlImage", "=> " + lists.message)
            GlideApp.with(context!!)
                    .load(lists.message)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.ic_picture))
                    .error(R.drawable.ic_picture)
                    .into(mBinding.image)

            otherItemViewModel = OtherItemViewModel(lists, context!!)
            mBinding.viewModel = otherItemViewModel
            mBinding.image.setOnClickListener {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(lists.message))
//                context!!.startActivity(browserIntent)
                openDilougeShowImage(lists.message)
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class MeImageViewHolder(private val mBinding: ItemCustomOutcomingImageMessageBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var meViewModel: MeItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            Log.e("urlImage", "=> " + lists.message)
            GlideApp.with(context!!)
                    .load(lists.message)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.ic_picture))
                    .error(R.drawable.ic_picture)
                    .into(mBinding.imageView)

            meViewModel = MeItemViewModel(lists, context!!)
            mBinding.viewModel = meViewModel
            mBinding.imageView.setOnClickListener {
                openDilougeShowImage(lists.message)
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(lists.message))
//                context!!.startActivity(browserIntent)
            }

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    private fun openDilougeShowImage(url: String?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_show_image_big)
        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
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

                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?) {
                        imageView.setImageBitmap(resource)
                    }
                })
        dialog.window!!.attributes = lp
        //imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }

    inner class TheirDocHolder(private val mBinding: ItemTheirDocBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var meViewModel: MeItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            Log.e("urlImage", "=> " + lists.message)
            var url = lists.message
            val fileName: String = url!!.substring(url.lastIndexOf('/') + 1, url.length)
            mBinding.fileName.text = fileName

            meViewModel = MeItemViewModel(lists, context!!)
            mBinding.viewModel = meViewModel
            mBinding.rlMain.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context!!.startActivity(browserIntent)
            }

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class MeDocHolder(private val mBinding: ItemMyDocViewBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var meViewModel: MeItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            Log.e("urlImage", "=> " + lists.message)
            var url = lists.message
            val fileName: String = url!!.substring(url.lastIndexOf('/') + 1, url.length)
            mBinding.fileName.text = fileName
//            GlideApp.with(context!!)
//                    .load(url)
//                    .apply(RequestOptions()
//                            .placeholder(R.drawable.ic_picture))
//                    .error(R.drawable.ic_picture)
//                    .into(mBinding.ivThumbNel)

            meViewModel = MeItemViewModel(lists, context!!)
            mBinding.viewModel = meViewModel
            mBinding.rlMain.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(lists.message))
                context!!.startActivity(browserIntent)
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class MeVideoHolder(private val mBinding: ItemMyVideoBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var meViewModel: MeItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            Log.e("urlImage", "=> " + lists.message)
            var url = lists.message
            Glide.with(context!!).load(url)
                    .into(mBinding.imageView)
            mBinding.ivPlay.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, PlayVideoActivity::class.java)
                intent.putExtra("url", url)
                context!!.startActivity(intent)
            })

            meViewModel = MeItemViewModel(lists, context!!)
            mBinding.viewModel = meViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class MeAudioHolder(private val mBinding: ItemMyAudioBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var meViewModel: MeItemViewModel
        var mp: MediaPlayer? = MediaPlayer()
        var isPLAYING: Boolean = false
        override fun onBind(position: Int) {
            val lists = mList!![position]
            Log.e("urlImage", "=> " + lists.message)
            var url = lists.message
            val fileName: String = url!!.substring(url.lastIndexOf('/') + 1, url.length)
            //mBinding.fileName.text = fileName

            meViewModel = MeItemViewModel(lists, context!!)
            mBinding.viewModel = meViewModel
            mBinding.ivPlayRec.setOnClickListener {
                val a = Uri.parse(url)

                val viewMediaIntent = Intent()
                viewMediaIntent.action = Intent.ACTION_VIEW
                viewMediaIntent.setDataAndType(a, "audio/*")
                viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context!!.startActivity(viewMediaIntent)
                //onRadioClick(url)
            }
            mBinding.seekBar.setOnTouchListener(OnTouchListener { v, event -> true })
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }

        fun onRadioClick(url: String) {
            if (!isPLAYING) {
                isPLAYING = true
                if(mp==null)
                    mp= MediaPlayer()
                try {
                    mp!!.setDataSource(url)
                    mp!!.prepare()
                    mp!!.start()
                    val timer = Timer()
                    timer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (mp != null)
                                mBinding.seekBar.progress = mp!!.currentPosition
                        }
                    }, 0, 100)
                } catch (e: IOException) {
                }
            } else {
                isPLAYING = false
                stopPlaying()
            }
        }

        private fun stopPlaying() {
            mp!!.release()
            mp = null
        }
    }

    inner class OtherAudioHolder(private val mBinding: ItemTheirAudioBinding) :
            BaseViewHolder(mBinding.root) {
        var mp: MediaPlayer? = MediaPlayer()
        var isPLAYING: Boolean = false
        private lateinit var meViewModel: MeItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            Log.e("urlImage", "=> " + lists.message)
            var url = lists.message
            val fileName: String = url!!.substring(url.lastIndexOf('/') + 1, url.length)
            // mBinding.fileName.text = fileName

            meViewModel = MeItemViewModel(lists, context!!)
            mBinding.viewModel = meViewModel
            mBinding.ivPlayRec.setOnClickListener {
                val a = Uri.parse(url)

                val viewMediaIntent = Intent()
                viewMediaIntent.action = Intent.ACTION_VIEW
                viewMediaIntent.setDataAndType(a, "audio/*")
                viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context!!.startActivity(viewMediaIntent)
                // onRadioClick(url)
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }

        fun onRadioClick(url: String) {
            if (!isPLAYING) {
                isPLAYING = true

                try {
                    mp!!.setDataSource(url)
                    mp!!.prepare()
                    mp!!.start()
                } catch (e: IOException) {
                }
            } else {
                isPLAYING = false
                stopPlaying()
            }
        }

        private fun stopPlaying() {
            mp!!.release()
            mp = null
        }
    }

    inner class OtherVideoHolder(private val mBinding: ItemTheirVideoBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var meViewModel: MeItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]
            Log.e("urlImage", "=> " + lists.message)
            var url = lists.message
            Glide.with(context!!).load(url)
                    .into(mBinding.imageView)
            mBinding.ivPlay.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, PlayVideoVerticallyActivity::class.java)
                intent.putExtra("url", url)
                context!!.startActivity(intent)
            })

            meViewModel = MeItemViewModel(lists, context!!)
            mBinding.viewModel = meViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class MeViewHolder(private val mBinding: ItemMyMessageBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var meViewModel: MeItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]

            /* GlideApp.with(context!!)
                     .asBitmap()
                     .load(lists.profileImage)
                     .apply(RequestOptions()
                             .placeholder(R.drawable.ic_placeholder_pic))
                     .error(R.drawable.ic_placeholder_pic)
                     .into(mBinding.ivMessagePic)*/

            meViewModel = MeItemViewModel(lists, context!!)
            mBinding.viewModel = meViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }


    inner class OtherViewHolder(private val mBinding: ItemTheirMessageBinding) :
            BaseViewHolder(mBinding.root) {

        private lateinit var otherItemViewModel: OtherItemViewModel
        override fun onBind(position: Int) {
            val lists = mList!![position]

//            GlideApp.with(context!!)
//                    .asBitmap()
//                    .load(lists.imageUrl)
//                    .apply(RequestOptions()
//                            .placeholder(R.drawable.ic_placeholder_pic))
//                    .error(R.drawable.ic_placeholder_pic)
//                    .into(mBinding.avatar)

            otherItemViewModel = OtherItemViewModel(lists, context!!)
            mBinding.viewModel = otherItemViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }

    inner class EmptyViewHolder(private val mBinding: ItemEmptyMessageBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = EmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
        }
    }

    inner class LoadMoreHolder(private val mBinding: ItemViewLoadMoreBinding) :
            BaseViewHolder(mBinding.root), LoadMoreItemViewModel.LoadMoreListener {

        override fun onLoadMoreClick() {
//            val lastItem = mList?.size!! - 1
            val lastItem = 0
            //remove the element from the hashMap and
            mList?.removeAt(lastItem)
            notifyItemChanged(lastItem)//notify the adapter for position 0
            listener?.onLoadMore()
        }

        override fun onBind(position: Int) {
            val emptyViewModel = LoadMoreItemViewModel(mList!![position], this@LoadMoreHolder)
            mBinding.viewModel = emptyViewModel
        }
    }


}