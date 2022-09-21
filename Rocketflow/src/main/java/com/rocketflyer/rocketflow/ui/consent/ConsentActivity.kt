package com.rocketflyer.rocketflow.ui.consent

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.model.response.config.ActionConfig
import com.tracki.data.model.response.config.Navigation
import com.tracki.databinding.ActivityConsentBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.login.LoginActivity
import com.tracki.ui.webview.WebViewActivity
import com.tracki.utils.AppConstants
import javax.inject.Inject


/**
 * Created by rahul on 16/5/19
 */
class ConsentActivity : BaseActivity<ActivityConsentBinding, ConsentViewModel>(), ConsentNavigator {

    @Inject
    lateinit var mConsentViewModel: ConsentViewModel
    private lateinit var mActivityConsentBinding: ActivityConsentBinding

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_consent
    override fun getViewModel() = mConsentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityConsentBinding = viewDataBinding
        mConsentViewModel.navigator = this

        customTextView(mActivityConsentBinding.tvPrivacyPolicy)
        setCustomTextView(mActivityConsentBinding.tvChoose)
    }

    companion object {
        @JvmStatic
        fun newIntent(context: Context): Intent {
            return Intent(context, ConsentActivity::class.java)
        }
    }

    override fun onDeclineClick() {
        finish()
    }

    override fun onAgreeClick() {
        val intent = LoginActivity.newIntent(this@ConsentActivity)
        setFlags(intent)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
    }

    /**
     * Set the text as custom text
     *
     * @param view view that needs to be set
     */
    private fun customTextView(view: TextView) {
//        CommonUtils.setCustomSpannable(mActivityConsentBinding.tvPrivacyPolicy,"",0,0 )
//        For more information, please check Tracki \n  Privacy Policy and Terms & Conditions
        val spanTxt = SpannableStringBuilder("For more information, please check RocketFlow \n ")
        spanTxt.append("Privacy Policy")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val navigation = Navigation()
                val actionConfig = ActionConfig()
                actionConfig.actionUrl = TrackiApplication.getPrivacyPolicyUrl()
                navigation.actionConfig = actionConfig
                navigation.title = "Privacy Policy"
                startActivity(WebViewActivity.newIntent(this@ConsentActivity)
                        .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation))
            }
        }, spanTxt.length - "Privacy Policy".length, spanTxt.length, 0)
        spanTxt.setSpan(ForegroundColorSpan(Color.BLACK), spanTxt.length - "Privacy Policy".length, spanTxt.length, 0)
        spanTxt.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                spanTxt.length - "Privacy Policy".length, spanTxt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanTxt.append(" and ")
        spanTxt.setSpan(ForegroundColorSpan(Color.BLACK), 62, spanTxt.length, 0)
        spanTxt.append(" Terms & Conditions")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val navigation = Navigation()
                val actionConfig = ActionConfig()
                actionConfig.actionUrl = TrackiApplication.getTncUrl()
                navigation.actionConfig = actionConfig
                navigation.title = "Terms & Conditions"
                startActivity(WebViewActivity.newIntent(this@ConsentActivity)
                        .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation))
            }
        }, spanTxt.length - " Terms & Conditions".length, spanTxt.length, 0)
        spanTxt.setSpan(ForegroundColorSpan(Color.BLACK),
                spanTxt.length - " Terms & Conditions".length, spanTxt.length, 0)
        spanTxt.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                spanTxt.length - " Terms & Conditions".length, spanTxt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    /**
     * Set the text as custom text
     *
     * @param view view that needs to be set
     */
    private fun setCustomTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder("By choosing ")
        spanTxt.append("I Agree")
        spanTxt.setSpan(ForegroundColorSpan(Color.BLACK), spanTxt.length - "Privacy Policy".length, spanTxt.length, 0)
        spanTxt.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                spanTxt.length - "I Agree".length, spanTxt.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanTxt.append(" you consent to RocketFlow’s use of \n your data as described above")
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }
}