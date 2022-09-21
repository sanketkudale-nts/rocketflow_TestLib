package com.rocketflyer.rocketflow.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast


/**
 * Created by rahul on 13/11/18
 */
class TrackiToast {
    object Message {
        @JvmStatic
        fun showLong(context: Context?, message: String?) {
            if(message!=null&&context!=null)
            {
                val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                try {

                    if(toast.view!=null) {
                        val toastLayout:LinearLayout = toast.view as LinearLayout
                        val toastTV = toastLayout.getChildAt(0) as TextView
                        //toastTV.textSize = 30f
                        val font =
                            Typeface.createFromAsset(context.getAssets(), "fonts/campton_book.ttf")
                        toastTV.setTypeface(font)
                    }
                    toast.show()
                }catch (e:Exception){
                    toast.show()
                }
//                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                Log.e("message", message)
            }

        }
        @JvmStatic
        fun showShort(context: Context?, message: String?) {
            if(message!=null&&context!=null){
                val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                try {
                    if(toast.view!=null) {
                        val toastLayout:LinearLayout = toast.view as LinearLayout


                        val toastTV = toastLayout.getChildAt(0) as TextView
                        //toastTV.textSize = 30f
                        val font =
                            Typeface.createFromAsset(context.getAssets(), "fonts/campton_book.ttf")
                        toastTV.setTypeface(font)
                    }
                    toast.show()
                }catch (e:Exception){
                    toast.show()
                }

              //  Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                Log.e("message", message)
            }

        }
    }
}