package com.tracki.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ProgressBar
import android.widget.TextView
import com.tracki.R

class ImageUploadDialog : Dialog {
    var titleText: TextView?=null
    var percentageText: TextView?=null
    var currentStatusText: TextView?=null
    var progressDialog:Dialog?=null
    var progressBar: ProgressBar?=null
 constructor(context: Context):super(context){
     progressDialog = Dialog(context)
     if (progressDialog!!.window != null) {
         progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
     }
     progressDialog!!.setContentView(R.layout.progress_dialog_with_percentage)
     progressDialog!!.setCancelable(false)
     progressDialog!!.setCanceledOnTouchOutside(false)
     titleText=progressDialog!!.findViewById(R.id.tvTitle)
     currentStatusText=progressDialog!!.findViewById(R.id.currentStatusText)
     percentageText=progressDialog!!.findViewById(R.id.tvPercentage)
     progressBar=progressDialog!!.findViewById(R.id.pb_loading)
     progressDialog!!.show()
 }

    fun updateValue(numberOfFiles:Int, fileUploadCounter:Int) {

        var progressUploadText="${fileUploadCounter}/${numberOfFiles}"
        var percentage =((fileUploadCounter * 100 / numberOfFiles) )
        progressBar!!.progress=percentage
        percentageText!!.text= "$percentage %"
        currentStatusText!!.text=progressUploadText

    }

    override fun cancel() {
        super.cancel()
    }

    override fun dismiss() {
        super.dismiss()
    }
}