package com.rocketflyer.rocketflow.ui.deviceChange

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.ResponseBasic
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityDeviceChangeBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.main.MainActivity
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.Log
import com.tracki.utils.NextScreen
import javax.inject.Inject

class DeviceChangeActivity : BaseActivity<ActivityDeviceChangeBinding?, DeviceChangeViewModel>(),
    DeviceChangeNavigator {

    @JvmField
    @Inject
    var deviceChangeViewModel: DeviceChangeViewModel? = null

    @JvmField
    @Inject
    var httpManager: HttpManager? = null

    @JvmField
    @Inject
    var preferencesHelper: PreferencesHelper? = null

    var mobile: String? = null

    private lateinit var api: Api
    private var deviceChangeBinding: ActivityDeviceChangeBinding? = null

    private var soft = false

    companion object {
        fun newIntent(context: Context?): Intent {
            return Intent(context, DeviceChangeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceChangeBinding = viewDataBinding
        deviceChangeViewModel!!.navigator = this

        if (ApiType.INITIATE_DEVICE_CHANGE != null) {
            api = TrackiApplication.getApiMap()[ApiType.INITIATE_DEVICE_CHANGE]!!

            val tvTitle = deviceChangeBinding!!.tvTitle
            val tvChangeMessage = deviceChangeBinding!!.tvChangeMessage
            val btnProceedChange = deviceChangeBinding!!.btnProceedChange


            if (intent.hasExtra("title")) {
                tvTitle.text = intent.getStringExtra("title").toString()
            }
            if (intent.hasExtra("message")) {
                tvChangeMessage.text = intent.getStringExtra("message").toString()
            }
            if (intent.hasExtra("btnMsg")) {
                val btnMsg = intent.getStringExtra("btnMsg").toString()
                if (btnMsg != "false") {
                    Log.e("checkInvi", "$btnMsg")
                    btnProceedChange.text = btnMsg
                } else {
                    Log.e("checkInvi", "$btnMsg")
                    btnProceedChange.visibility = View.GONE
                    btnProceedChange.isClickable = false
                }
            }
            if (intent.hasExtra("soft")) {
                soft = intent.getBooleanExtra("soft", false)
            }

            btnProceedChange.setOnClickListener {
                deviceChange()
            }
        }
        else{
            Log.e("checkApi","INITIATE_DEVICE_CHANGE - Null ")
        }



    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_device_change
    }

    override fun getViewModel(): DeviceChangeViewModel {
        return deviceChangeViewModel!!
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        Log.e("resultResponse","$result")

        val gson = Gson()
        val responseBasic = gson.fromJson(
            result.toString(),
            ResponseBasic::class.java
        )
        if (responseBasic != null) {
            if (responseBasic.successful == true) {

                val prefs: SharedPreferences =
                    getSharedPreferences("DeviceChange", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("deviceChangeRequestInitiated", true).apply()
                val deviceChangeIntent = intent
                deviceChangeIntent.putExtra("title", "Device Change Under Process")
                deviceChangeIntent.putExtra(
                    "message",
                    "Your Device Change request is under review. We will update you once it's approved"
                )
                deviceChangeIntent.putExtra("btnMsg", "false")
                deviceChangeIntent.putExtra("soft", soft)

                if (!soft){
                    startActivity(deviceChangeIntent)
                }
                else{
                    CommonUtils.otpgoToNext(this, NextScreen.HOME, mobile)
                }

            }
        }
    }

    override fun deviceChange() {
        //val api = TrackiApplication.getApiMap()[ApiType.INITIATE_DEVICE_CHANGE]
        //Log.e("deviceChange1",  "${api?.name.toString()}")
        deviceChangeViewModel!!.changeDevice(httpManager,api)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        finishAffinity()
    }
}