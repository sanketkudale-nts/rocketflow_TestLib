package com.rocketflyer.rocketflow.ui.update

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAppUpdateScreenBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.NextScreen
import kotlinx.android.synthetic.main.activity_app_update_screen.*
import javax.inject.Inject


class AppUpdateScreenActivity : BaseActivity<ActivityAppUpdateScreenBinding, AppUpdateScreenViewModel>() ,AppUpdateNavigator{
    @Inject
    lateinit var mAppUpdateScreenViewModel: AppUpdateScreenViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    private lateinit var mActivityAppUpdateScreenBinding: ActivityAppUpdateScreenBinding

    private var nextScreen:String?=null

    private var deprication:Boolean?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAppUpdateScreenBinding = viewDataBinding
        mAppUpdateScreenViewModel.navigator = this
        mActivityAppUpdateScreenBinding.viewmodel=mAppUpdateScreenViewModel
        tvTitle.text = "Update "+getString(R.string.app_name) +"?"
        tvMessage.text = getString(R.string.app_name) +" recommends that you update to latest version .you can have the access to the application while downloading the update."
        if(intent.hasExtra(AppConstants.Extra.EXTRA_NEXT_SCREEN)){
            nextScreen=intent.getStringExtra(AppConstants.Extra.EXTRA_NEXT_SCREEN)
        }
        if(intent.hasExtra(AppConstants.Extra.EXTRA_DEPRICATION_SCREEN)){
            deprication=intent.getBooleanExtra(AppConstants.Extra.EXTRA_DEPRICATION_SCREEN, false)
            if(deprication!=null&&deprication!!){
                ivClose.visibility= View.VISIBLE
            }else{
                ivClose.visibility= View.GONE
            }
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
       return R.layout.activity_app_update_screen
    }

    override fun getViewModel(): AppUpdateScreenViewModel {
        return mAppUpdateScreenViewModel
    }

    override fun openMainActivity() {
        if(nextScreen!=null){
            var nextScreen=NextScreen.valueOf(nextScreen!!)
            if (intent != null && intent.hasExtra(AppConstants.NOTIFICATION_DATA)) {
                var message = intent.getStringExtra(AppConstants.NOTIFICATION_DATA)
                CommonUtils.goToNext(preferencesHelper, nextScreen, this, message)
            }else{
                CommonUtils.goToNext(preferencesHelper, nextScreen, this, null)
            }

//            val intent = MainActivity.newIntent(this@AppUpdateScreenActivity)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            finish()
        }

    }

    override fun openAppInPlayStore() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    override fun close() {
        finish()
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun onBackPressed() {
    }
    companion object {
        fun newIntent(context: Context) = Intent(context, AppUpdateScreenActivity::class.java)

    }
}