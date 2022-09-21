package com.tracki.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.tracki.R
import com.tracki.data.model.response.config.Action

/**
 * Created by rahul on 5/12/18
 */
class ApproveRejectLeaveDialog(context: Context) : Dialog(context, R.style.DialogTheme) {

    private var cancelableDialog: Boolean = true
    private var title: String? = null
    private var messageDialog: String? = null
    private var btnText: String?=null
    private var onClickListenerDialog: OnClickApproveRejectNowListener? = null
    private var action: Action?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        //requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(cancelableDialog)
        setContentView(R.layout.dialog_rejecting_leave)
        var btnReject = findViewById<Button>(R.id.btnReject)
        var tvTitle = findViewById<TextView>(R.id.tvTitle)


        btnReject.setText(btnText)
        tvTitle.setText(title)

        val comment = (findViewById<EditText>(R.id.edComments))
        val btnNegative = findViewById<Button>(R.id.btnReject)
            btnNegative.visibility = View.VISIBLE
            btnNegative.setOnClickListener {
                dismiss()
                onClickListenerDialog?.onClickApproveRejectNow(comment.text.toString(), action!!)
            }


        super.onCreate(savedInstanceState)
    }

    constructor(context: Context, onClickListenerDialog: OnClickApproveRejectNowListener, title:String, btnText:String, action: Action) : this(context) {
        this.onClickListenerDialog = onClickListenerDialog
        this.title=title
        this.btnText=btnText
        this.action=action
    }
}


interface OnClickApproveRejectNowListener {
    fun onClickApproveRejectNow(comment:String, action: Action)

}