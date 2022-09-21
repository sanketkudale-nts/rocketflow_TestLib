package com.tracki.ui.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.tracki.utils.Log
import java.util.regex.Matcher
import java.util.regex.Pattern

class MySMSBroadcastReceiver : BroadcastReceiver() {

    private var otpReceiver: OTPReceiveListener? = null


    fun initOTPListener(receiver: OTPReceiveListener) {
        this.otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(otpReceiver!=null){
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status

                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents
                        var otp: String? = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                        Log.d("OTP_Message", otp)
                        // Extract one-time code from the message and complete verification
                        // by sending the code back to your server for SMS authenticity.
                        // But here we are just passing it to MainActivity
                        //<#> SampleApp: Your verification code is 143567
                        //QbwSot12oP
                        if (otpReceiver != null) {
                            //val otpArray = otp.split("\\s").toTypedArray()
                            val mPattern: Pattern = Pattern.compile("(|^)\\d{6}")

                            if (otp != null) {
                                val mMatcher: Matcher = mPattern.matcher(otp)
                                if (mMatcher.find()) {
                                    val otpdigit: String = mMatcher.group(0)
                                    Log.e("otp", "Final OTP: $otpdigit")
                                   // var finalotp = otpArray[otpArray.size - 2]
                                    if (TextUtils.isDigitsOnly(otpdigit))
                                        otpReceiver!!.onOTPReceived(otpdigit)
                                } else {
                                    //something went wrong
                                    Log.e("ptp", "Failed to extract the OTP!! ")
                                }
                            }
//                            otp = otp.replace("<#> RocketFlow: Your verification code is ", "").split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

                        }
                    }

                    CommonStatusCodes.TIMEOUT ->
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        otpReceiver!!.onOTPTimeOut()
                }
            }

        }
    }

    interface OTPReceiveListener {

        fun onOTPReceived(otp: String)

        fun onOTPTimeOut()
    }
}