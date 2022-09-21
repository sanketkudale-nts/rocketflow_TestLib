package com.rocketflyer.rocketflow.ui.adjusttime

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.BaseResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAdjustTimeBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.*
import kotlinx.android.synthetic.main.activity_adjust_time.*
import javax.inject.Inject
import kotlin.math.abs

class AdjustTimeActivity : BaseActivity<ActivityAdjustTimeBinding, AdjustTimeViewModel>(),AdjustTimeNavigator {
    lateinit var binding: ActivityAdjustTimeBinding
    @Inject
    lateinit var mAdjustTimeViewModel: AdjustTimeViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=viewDataBinding
        mAdjustTimeViewModel.navigator=this
        btnAdjust.setOnClickListener {
            startActivity(Intent(Settings.ACTION_DATE_SETTINGS));

        }
    }

    override fun onResume() {
        super.onResume()
        var stringTime = DateTimeUtil.getParsedDate(System.currentTimeMillis(), DateTimeUtil.DATE_TIME_FORMAT_2)
        tvDateTime.text = stringTime
        showLoading()
        mAdjustTimeViewModel.getServerTime(httpManager)
    }

    override fun onBackPressed() {
    }

    override fun getBindingVariable(): Int {
       return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_adjust_time
    }

    override fun getViewModel(): AdjustTimeViewModel {
       return mAdjustTimeViewModel
    }

    override fun handleServerTimeResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result,this)) {
            val jsonConverter: JSONConverter<BaseResponse> = JSONConverter<BaseResponse>()
            val baseResponse = jsonConverter.jsonToObject(result.toString(), BaseResponse::class.java)
            if (baseResponse != null) {
                val now = baseResponse.time!!
                if (abs(now.toLong() - System.currentTimeMillis()) >= AppConstants.TIME_DIFF) {

//                    Log.e(SNTPClient.TAG, "timeiswrong")
                } else {
                    var intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                  //  Log.e(SNTPClient.TAG, "timeisright")
                }
            }
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}