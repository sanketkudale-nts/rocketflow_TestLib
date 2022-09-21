package com.rocketflyer.rocketflow.ui.dynamicform.dynamicfragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.tracki.R
import com.tracki.data.model.response.config.DataType
import com.tracki.data.model.response.config.Field
import com.tracki.data.model.response.config.TaskData
import com.tracki.databinding.*
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.chat.PlayVideoVerticallyActivity
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.dynamicform.FormEmptyItemViewModel
import com.tracki.utils.CommonUtils
import com.tracki.utils.Log
import com.tracki.utils.ZoomableImageView
import java.io.IOException


/**
 * Created by Vikas Kesharvani on 29/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class ShowDynamicFormDataAdapter(var formDataList: ArrayList<TaskData>) : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        private const val VIEW_EMPTY = 0
        private const val VIEW_DISPLAY_TEXT = 1
        private const val VIEW_DISPLAY_PICS = 2
        private const val VIEW_DISPLAY_SINGLE_PIC = 3
        private const val VIEW_PLAY_VIDEO = 4
        private const val VIEW_PLAY_AUDIO = 5
        private const val TAG = "ShowDynamicFormDataAdapter"

    }

    var allowedFields = ArrayList<Field>()

    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        this.context = parent.context
        when (viewType) {
            VIEW_DISPLAY_TEXT -> {
                val itemFormPreview = ItemLayoutFormDetailsBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return FormPreviewViewHolder(itemFormPreview)
            }
            VIEW_DISPLAY_SINGLE_PIC -> {
                val itemFormPicture = ItemLayoutSingleImagesBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return FormSinglePictureViewHolder(itemFormPicture)
            }
            VIEW_DISPLAY_PICS -> {
                val itemFormPicture = ItemRowImageFormDetailsBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return FormPictureViewHolder(itemFormPicture)
            }
            VIEW_PLAY_AUDIO -> {
                val itemFormPicture = ItemLayoutPlayAudioDetailsBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return FormAudioViewHolder(itemFormPicture)
            }
            VIEW_PLAY_VIDEO -> {
                val itemFormPicture = ItemLayoutPreviewVideoBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return FormVideoViewHolder(itemFormPicture)
            }

            else -> {
                val emptyViewBinding = ItemDynamicFormEmptyViewBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                return EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    fun addData(formDataList: ArrayList<TaskData>) {
        this.formDataList = formDataList
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return formDataList.size
    }
    /* SIGNATURE, NUMBER, DATE_RANGE, DATE, DATE_TIME, TIME, DAY, EMAIL,
     FILE, TEXT, FILES, SOUND, RATING, TEXT_AREA, BOOLEAN, LIST,
     GEO, MULTI_SELECT, CAMERA, TOGGLE, BUTTON, DROPDOWN, CONDITIONAL_DROPDOWN_STATIC,
     CONDITIONAL_DROPDOWN_API, RADIO, CHECKBOX, LABLE, LABEL, DROPDOWN_API, CALCULATE,
     AUDIO, VIDEO,VERIFY_OTP,IMAGE
     case SELECT_EXECUTIVE:
                    case SELECT_EXECUTIVE_BY_PLACE:
                    case SELECT_NEAR_BY_EXECUTIVE:
                    case SELECT_GROUP:
                    case ASSIGN_SUBORDINATE:*/

    override fun getItemViewType(position: Int): Int {
        return if (formDataList.isNotEmpty()) {
            if (formDataList[position].type != null) {
                when (formDataList[position].type) {
                    DataType.IP_ADDRESS, DataType.CALCULATE, DataType.NUMBER, DataType.RATING, DataType.DATE_RANGE, DataType.TEXT,
                    DataType.DATE, DataType.DATE_TIME, DataType.DAY, DataType.TIME, DataType.EMAIL,
                    DataType.TEXT_AREA, DataType.GEO, DataType.MULTI_SELECT, DataType.TOGGLE, DataType.BUTTON,
                    DataType.DROPDOWN, DataType.CONDITIONAL_DROPDOWN_STATIC, DataType.CONDITIONAL_DROPDOWN_API, DataType.SELECT_EXECUTIVE,
                    DataType.SELECT_EXECUTIVE_BY_PLACE, DataType.SELECT_NEAR_BY_EXECUTIVE,
                    DataType.SELECT_GROUP, DataType.ASSIGN_SUBORDINATE,DataType.AMOUNT,DataType.EVENT,
                    DataType.RADIO,DataType.SCANNER, DataType.CHECKBOX, DataType.LABLE, DataType.LABEL, DataType.DROPDOWN_API, DataType.ASSIGN_SUBORDINATE, DataType.PASSWORD, DataType.USER_NAME, DataType.PORT, DataType.RTSP
                    -> VIEW_DISPLAY_TEXT
                    DataType.FILE, DataType.CAMERA -> VIEW_DISPLAY_PICS
                    DataType.IMAGE, DataType.SIGNATURE -> VIEW_DISPLAY_SINGLE_PIC
                    DataType.AUDIO -> VIEW_PLAY_AUDIO
                    DataType.VIDEO -> VIEW_PLAY_VIDEO
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
    private inner class FormPreviewViewHolder(var mBinding: ItemLayoutFormDetailsBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val formData = formDataList[position]
            val emptyItemViewModel = FormTextViewModel(formData)
            mBinding.viewModel = emptyItemViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()


        }
    }


    /**
     * Class used to handle all the pictures
     */
    private inner class FormPictureViewHolder(var mBinding: ItemRowImageFormDetailsBinding) :
            BaseViewHolder(mBinding.root) {

        private var linearLayoutAdd: LinearLayout = mBinding.root.findViewById(R.id.lLayout)

        override fun onBind(position: Int) {
            val formData = formDataList[position]
            val emptyItemViewModel = FormFileViewModel(formData)
            mBinding.viewModel = emptyItemViewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()

            if (formData.value?.isNotEmpty()!!) {
                linearLayoutAdd.removeAllViews()
                val urls = formData.value?.split(",")?.toTypedArray()
                if (urls != null) {
                    for (i in urls?.indices!!) {
                        val mInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        val view = mInflater.inflate(R.layout.add_photo_view, linearLayoutAdd, false) as ImageView
                        GlideApp.with(context!!).load(urls[i]).into(view)

                        linearLayoutAdd.addView(view)
                        view.setOnClickListener {
                            if (urls[i].isNotEmpty())
                                openDialogShowImage(urls[i])
                        }
                    }
                }
            }
        }
    }

    private fun openDialogShowImage(url: String) {

        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_show_image_big)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
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

                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?) {
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

    /**
     * Class used to handle single the pictures
     */
    private inner class FormSinglePictureViewHolder(var mBinding: ItemLayoutSingleImagesBinding) :
            BaseViewHolder(mBinding.root) {


        override fun onBind(position: Int) {
            val formData = formDataList[position]
            val viewModel = FormSingleImageViewModel(formData)
            mBinding.viewModel = viewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
            if (formData.value != null) {
                GlideApp.with(context!!).load(formData.value).into(mBinding.ivImage)
                mBinding.ivImage.setOnClickListener {
                    if (formData.value!!.isNotEmpty())
                        openDialogShowImage(formData.value!!)
                }
            }

        }
    }

    /**
     * Class used to handle single the pictures
     */
    private inner class FormVideoViewHolder(var mBinding: ItemLayoutPreviewVideoBinding) :
            BaseViewHolder(mBinding.root) {


        override fun onBind(position: Int) {
            val formData = formDataList[position]
            val viewModel = FormVideoPreviewModel(formData)
            mBinding.viewModel = viewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
            if (formData.value != null) {
                if (formData.value != null && !formData.value!!.isEmpty()) {
                    Glide.with(context!!).load(formData.value!!)
                            .into(mBinding.ivVideo)
                    mBinding.ivPlay.setOnClickListener(View.OnClickListener {
                        val intent = Intent(context, PlayVideoVerticallyActivity::class.java)
                        intent.putExtra("url", formData.value)
                        context!!.startActivity(intent)
                    })
                }
            }

        }
    }

    /**
     * Class used to handle single the pictures
     */
    private inner class FormAudioViewHolder(var mBinding: ItemLayoutPlayAudioDetailsBinding) :
            BaseViewHolder(mBinding.root) {
        private var player: MediaPlayer? = null
        private var fileName: String? = null
        private var start = true
        private var playing = false
        override fun onBind(position: Int) {
            val formData = formDataList[position]
            val viewModel = FormAudioDetailsViewModel(formData)
            mBinding.viewModel = viewModel

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
            if (formData.value != null) {
                if (formData.value!!.isNotEmpty()) {
                    fileName = formData.value
                    CommonUtils.showLogMessage("e", "value", formData.value)
                    // fileName="https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
                    mBinding.ivPlayRec.background = ContextCompat.getDrawable(context!!, R.drawable.circle_audio_play)
                    mBinding.ivPlayRec.isEnabled = true
                    mBinding.ivPlayRec.visibility = View.VISIBLE
                }
                mBinding.ivPlayRec.setOnClickListener(View.OnClickListener {
                    mBinding.tvPlay.visibility = View.VISIBLE
                    mBinding.tvTime.visibility = View.VISIBLE
                    mBinding.ivGif.visibility = View.GONE
                    mBinding.tvTime.text = ""
                    mBinding.tvTime.post(mUpdateTime)
                    onPlay(playing)
                })
            }

        }

        private val mUpdateTime: Runnable = object : Runnable {
            override fun run() {
                val currentDuration: Int
                if (player!!.isPlaying) {
                    currentDuration = player!!.currentPosition
                    updatePlayer(currentDuration)
                    mBinding.tvTime.postDelayed(this, 100)
                } else {
                    mBinding.tvTime.removeCallbacks(this)
                    mBinding.ivPlayRec.setImageResource(R.drawable.ic_multimedia_play)
                    mBinding.tvPlay.setVisibility(View.GONE)
                    mBinding.tvTime.setVisibility(View.GONE)
                }
            }
        }

        private fun onPlay(playing: Boolean) {
            if (!playing) {
                startPlaying()
                mBinding.ivPlayRec.setImageResource(R.drawable.ic_pause)
                this@FormAudioViewHolder.playing = true
            } else {
                stopPlaying()
                mBinding.ivPlayRec.setImageResource(R.drawable.ic_multimedia_play)
                this@FormAudioViewHolder.playing = false
            }
        }

        private fun startPlaying() {
            player = MediaPlayer()
            try {
                player!!.setDataSource(fileName)
                player!!.prepare()
                player!!.start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }
        }

        private fun stopPlaying() {
            if (player != null)
                player!!.stop()

            //player.release();

            //player = null;
        }

        private fun updatePlayer(currentDuration: Int) {
            mBinding.tvTime.text = "" + milliSecondsToTimer(currentDuration.toLong())
        }

        /**
         * Function to convert milliseconds time to Timer Format
         * Hours:Minutes:Seconds
         */
        fun milliSecondsToTimer(milliseconds: Long): String {
            var finalTimerString = ""
            var secondsString = ""

            // Convert total duration into time
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
            // Add hours if there
            if (hours > 0) {
                finalTimerString = "$hours:"
            }

            // Prepending 0 to seconds if it is one digit
            secondsString = if (seconds < 10) {
                "0$seconds"
            } else {
                "" + seconds
            }
            finalTimerString = "$finalTimerString$minutes:$secondsString"

            // return timer string
            return finalTimerString
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

