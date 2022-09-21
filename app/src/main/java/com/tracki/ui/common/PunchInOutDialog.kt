package com.tracki.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.PunchInOut
import com.tracki.ui.custom.GlideApp
import com.tracki.utils.CommonUtils

/**
 * Created by rahul on 5/12/18
 */
class PunchInOutDialog(context: Context) : Dialog(context, R.style.DialogTheme) {

    private var cancelableDialog: Boolean = true
    private var titleDialog: String? = null
    private var messageDialog: String? = null
    private var buttonDialog: String = "Yes"
    private var onClickListenerDialog: OnClickViews? = null
    private var data: String? = null
    private var event: PunchInOut? = null
    private var preferencesHelper: PreferencesHelper? = null
    private var myContext: Context? = null
    lateinit var ivAddSelfie: ImageView
    lateinit var btnAddSelfie: LinearLayout
    lateinit var progressbar: ProgressBar
    lateinit var ivCancel: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(cancelableDialog)
        setContentView(R.layout.dialog_punching_in_out)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        progressbar = findViewById<ProgressBar>(R.id.progressbar)
        ivAddSelfie = findViewById<ImageView>(R.id.ivAddSelfie)
        val edComments = findViewById<EditText>(R.id.edComments)
        btnAddSelfie = findViewById<LinearLayout>(R.id.btnAddSelfie)
        ivCancel = findViewById<ImageView>(R.id.ivCancel)

        // data = edComments.text.toString()
        txtTitle.text = titleDialog
        btnSubmit.text = buttonDialog
        btnAddSelfie?.setOnClickListener {
            event?.let { it1 ->
                onClickListenerDialog?.onCameraOpen(it1)
            }
        }
        ivCancel.setOnClickListener {
            dismiss()
        }
        ivAddSelfie?.setOnClickListener {
            onClickListenerDialog?.onCameraOpen(event!!)
        }

        btnSubmit.setOnClickListener {
            dismiss()
            CommonUtils.preventTwoClick(btnSubmit)
            onClickListenerDialog?.onClickSubmit(event!!, edComments.text.toString())

        }
    }


    fun loadImage() {

        GlideApp.with(myContext!!).load(preferencesHelper!!.selfieUrl)
                .circleCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        progressbar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressbar.visibility = View.GONE
                        return false
                    }
                })
                .into(ivAddSelfie)

    }

    constructor(context: Context, preferencesHelper: PreferencesHelper, titleDialog: String?,
                messageDialog: String, buttonDialog: String, event: PunchInOut,
                onClickListenerDialog: OnClickViews) : this(context) {
        this.titleDialog = titleDialog
        this.messageDialog = messageDialog
        this.buttonDialog = buttonDialog
        this.onClickListenerDialog = onClickListenerDialog
        this.event = event
        this.myContext = context
        this.preferencesHelper = preferencesHelper
    }
}


interface OnClickViews {
    fun onClickSubmit(event: PunchInOut, data: String?)
    fun onCameraOpen(event: PunchInOut)
}