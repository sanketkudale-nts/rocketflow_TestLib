package com.tracki.ui.webview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.core.widget.ContentLoadingProgressBar
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
//import com.tracki.Config
import com.tracki.R
import com.tracki.data.model.response.config.Navigation
import com.tracki.databinding.ActivityWebviewBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.AppConstants.Extra.EXTRA_WEB_INFO
import com.tracki.utils.CommonUtils
import com.tracki.utils.Log
import javax.inject.Inject


/**
 * Created by rahul on 22/10/18
 */
class WebViewActivity : BaseActivity<ActivityWebviewBinding, WebViewModel>(), WebViewNavigator {

    @Inject
    lateinit var mWebViewModel: WebViewModel
    private var mActivityWebviewBinding: ActivityWebviewBinding? = null

    private lateinit var webView: WebView
    private lateinit var contentProgressBar: ContentLoadingProgressBar
    private lateinit var context: Context
    private var navigation: Navigation? = null

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_webview
    override fun getViewModel() = mWebViewModel
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityWebviewBinding!!.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityWebviewBinding = viewDataBinding
        mWebViewModel.navigator = this

        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("WebView", "Error Inside OnCreate(): $e")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Throws(Exception::class)
    private fun init() {
        if (intent.hasExtra(EXTRA_WEB_INFO)) {
            navigation = intent?.getSerializableExtra(EXTRA_WEB_INFO) as Navigation
        }
        // set toolbar
        setToolbar(mActivityWebviewBinding?.toolbar, navigation?.title)

        webView = mActivityWebviewBinding?.webView!!
        contentProgressBar = mActivityWebviewBinding?.contentProgressBar!!
        contentProgressBar.visibility = View.GONE
        context = this

//        webView.isScrollbarFadingEnabled = false
        //Config.enableWebDebug(webView)
        //  webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//        webView.webChromeClient = TrackiWebViewChrome()
        webView.webViewClient = TrackiWebViewClient()
        val setting = webView.settings
        setting.domStorageEnabled = true
        setting.javaScriptEnabled = false
//        setting.setSupportMultipleWindows(true)
//        setting.javaScriptCanOpenWindowsAutomatically=true
//        setting.allowFileAccess = true
//        setting.setAppCacheEnabled(true)
//        setting.domStorageEnabled=true
//        setting.layoutAlgorithm=WebSettings.LayoutAlgorithm.NARROW_COLUMNS
//        setting.setRenderPriority(WebSettings.RenderPriority.HIGH)
//        setting.cacheMode=WebSettings.LOAD_DEFAULT
//        setting.loadWithOverviewMode = true
//        setting.useWideViewPort = true
//        setting.allowContentAccess = true
//
//        webView.settings.setAppCacheMaxSize( 10 * 1024 * 1024 ) // 10MB
//        webView.settings.setAppCachePath(applicationContext.cacheDir.absolutePath)
//
        loadUrl(webView)
    }

    private fun loadUrl(webView: WebView) {
//        navigation?.actionConfig?.actionUrl.let {
        if (navigation?.actionConfig?.actionUrl != null) {
            CommonUtils.showLogMessage("e","url",navigation?.actionConfig?.actionUrl)
            webView.loadUrl(navigation?.actionConfig?.actionUrl!!,
                    CommonUtils.buildDeviceHeader(this@WebViewActivity))
        }
//        }
    }

    /**
     * Show internet dialog
     */
    private fun showInternetDialog() {
        AlertDialog.Builder(context)
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

    companion object {
        fun newIntent(context: Context) = Intent(context, WebViewActivity::class.java)
    }

    /**
     * Class used to handle the urls
     */
    inner class TrackiWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            contentProgressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            contentProgressBar.visibility = View.GONE
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
//            super.onReceivedSslError(view, handler, error)
            val builder = AlertDialog.Builder(this@WebViewActivity)
            builder.setMessage(R.string.notification_error_ssl_cert_invalid)
            builder.setPositiveButton(AppConstants.CONTINUE) { _, _ -> handler!!.proceed() }
            builder.setNegativeButton(AppConstants.CANCEL) { _, _ -> handler!!.cancel() }
            val dialog = builder.create()
            dialog.show()
        }
    }
}