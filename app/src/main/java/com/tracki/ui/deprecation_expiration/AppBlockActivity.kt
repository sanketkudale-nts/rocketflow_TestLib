package com.tracki.ui.deprecation_expiration

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.tracki.BR
import com.tracki.Config
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.databinding.ActivityAppBlockBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.login.LoginActivity
import com.tracki.ui.main.MainActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.NextScreen
import javax.inject.Inject

/**
 * Created by rahul on 15/4/19
 */
class AppBlockActivity : BaseActivity<ActivityAppBlockBinding, AppBlockViewModel>(), AppBlockNavigator {

    @Inject
    lateinit var mAppBlockViewModel: AppBlockViewModel
    private var mActivityAppBlockBinding: ActivityAppBlockBinding? = null
    private var webView: WebView? = null
    private var url: String? = null

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_app_block
    override fun getViewModel() = mAppBlockViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAppBlockBinding = viewDataBinding
        mAppBlockViewModel.navigator = this

        if (intent?.hasExtra(AppConstants.Extra.EXTRA_EXPIRATION_URL)!!) {
            webView = mActivityAppBlockBinding!!.webView

            webView?.invalidate()
            webView?.settings?.javaScriptEnabled = true
            webView?.settings?.loadWithOverviewMode = true
            webView?.settings?.useWideViewPort = true
            webView?.settings?.allowFileAccess = true
            webView?.settings?.allowContentAccess = true
            webView?.isScrollbarFadingEnabled = false
            Config.enableWebDebug(webView)
            webView?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            webView?.webViewClient = WebViewClient()
            url = intent?.getStringExtra(AppConstants.Extra.EXTRA_EXPIRATION_URL)
            url?.let { webView?.loadUrl(url!!, CommonUtils.buildDeviceHeader(this@AppBlockActivity)) }
        }
    }

    override fun onBackPressed() {

    }

    /**
     * Class used to handle the urls
     */
    inner class WebViewClient : android.webkit.WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return handleUrls(request?.url.toString(), view)
//            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return handleUrls(url, view)
        }

        /**
         * Method used to handle urls for all apis levels
         */
        private fun handleUrls(url: String?, view: WebView?): Boolean {
            try {
                return if (isNetworkConnected) {
                    when {
                        url?.startsWith(SKIP_UPDATE)!! -> {
                            if (intent.hasExtra(AppConstants.Extra.EXTRA_NEXT_SCREEN)) {
                                intent.getStringExtra(AppConstants.Extra.EXTRA_NEXT_SCREEN)?.let {
                                    goToNext(
                                        it
                                    )
                                }
                            }
                        }
                        url.startsWith(STORE_UPDATED) -> openStore()
                        else -> {
                            view?.loadUrl(url)
                        }
                    }
                    true
                } else {
                    showInternetDialog()
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    }

    /**
     * Method used to go to the next screen
     * @param nextScreen net screen
     */
    private fun goToNext(nextScreen: String) {
        if (nextScreen == NextScreen.LOGIN.name) {
            val intent = LoginActivity.newIntent(this)
            startActivity(intent)
        } else if (nextScreen == NextScreen.HOME.name) {
            //start transition service if flag is true
            CommonUtils.manageTransitionService(this, TrackiApplication.getAutoStart())
            //then open main activity
            val intent = MainActivity.newIntent(this@AppBlockActivity)
            setFlags(intent)
            startActivity(intent)
        }
        finish()
    }

    private fun showInternetDialog() {
        AlertDialog.Builder(this@AppBlockActivity)
                .setMessage(AppConstants.ALERT_NO_CONNECTION)
                .setTitle(AppConstants.CONNECTION_ERROR)
                .setCancelable(false)
                .setPositiveButton(AppConstants.RETRY) { dialog, _ ->
                    dialog.dismiss()
                    //    callBack.hitApi();
                }.setNegativeButton(AppConstants.CLOSE) { dialog, _ ->
                    dialog.dismiss()
                    //callBack.onNetworkErrorClose();
                }
                .show()
    }

    private fun openStore() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }

    }

    companion object {
        fun newIntent(context: Context) = Intent(context, AppBlockActivity::class.java)
        private const val SKIP_UPDATE = "tracki://skip_update"
        private const val STORE_UPDATED = "tracki://store_update"
    }
}