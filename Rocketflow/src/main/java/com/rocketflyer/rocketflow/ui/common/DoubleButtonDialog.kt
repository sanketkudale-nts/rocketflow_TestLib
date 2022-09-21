package com.rocketflyer.rocketflow.ui.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.tracki.R

/**
 * Created by rahul on 5/12/18
 */
class DoubleButtonDialog(context: Context) : Dialog(context, R.style.DialogTheme) {

    private var cancelableDialog: Boolean = true
    private var titleDialog: String? = null
    private var messageDialog: String? = null
    private var leftButtonDialog: String = "Yes"
    private var rightButtonDialog: String? = null
    private var onClickListenerDialog: OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        setCancelable(cancelableDialog)
        setContentView(R.layout.dialog_double_button)
        val btnNegative = findViewById<Button>(R.id.btnNegative)
        btnNegative.visibility = View.GONE
        if (rightButtonDialog != null) {
            btnNegative.visibility = View.VISIBLE
            btnNegative.text = rightButtonDialog
            btnNegative.setOnClickListener {
                dismiss()
                onClickListenerDialog?.onClickCancel()
            }
        }
        val btnPositive = findViewById<Button>(R.id.btnPositive)
        btnPositive.text = leftButtonDialog
        btnPositive.setOnClickListener {
            onClickListenerDialog?.onClick()
            dismiss()
        }
        (findViewById<TextView>(R.id.message)).text = messageDialog
        super.onCreate(savedInstanceState)
    }

    constructor(context: Context, cancelableDialog: Boolean, titleDialog: String?,
                messageDialog: String, leftButtonDialog: String, rightButtonDialog: String?,
                onClickListenerDialog: OnClickListener) : this(context) {
        this.cancelableDialog = cancelableDialog
        this.titleDialog = titleDialog
        this.messageDialog = messageDialog
        this.leftButtonDialog = leftButtonDialog
        this.rightButtonDialog = rightButtonDialog
        this.onClickListenerDialog = onClickListenerDialog
    }
}


interface OnClickListener {
    fun onClickCancel()
    fun onClick()
}