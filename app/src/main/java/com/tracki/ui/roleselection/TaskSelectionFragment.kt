package com.tracki.ui.roleselection

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.InitiateSignUpRequest
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.FragmentLayoutTaskSelectionBinding
import com.tracki.ui.attendance.punchInOut.PunchInPunchOutData
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.facility.ServicesActivity
import com.tracki.ui.webview.WebViewActivity
import com.tracki.utils.*
import com.trackthat.lib.TrackThat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.collections.ArrayList


class TaskSelectionFragment : BaseFragment<FragmentLayoutTaskSelectionBinding, TaskSelectionViewModel>(), TaskSelectionNavigator, View.OnClickListener {

    @Inject
    lateinit var mTaskSelectionAdapter: TaskSelectionAdapter

    lateinit var mTaskSelectionViewModel: TaskSelectionViewModel


    @Inject
    lateinit var mLayoutManager: LinearLayoutManager

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    lateinit var binding: FragmentLayoutTaskSelectionBinding

    var profileDetail: ProfileDetail? = null

    var accountsData: AccountsAndOffering? = null

    var draftId: String? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_layout_task_selection
    }

    override fun getViewModel(): TaskSelectionViewModel {
        mTaskSelectionViewModel = ViewModelProviders.of(this, mViewModelFactory).get(TaskSelectionViewModel::class.java)
        return mTaskSelectionViewModel
    }

    companion object {
        fun newInstance(profileDetail: ProfileDetail?, accountsData: AccountsAndOffering, draftId: String): TaskSelectionFragment? {
            val args = Bundle()
            args.putParcelable(AppConstants.PROFILE, profileDetail)
            args.putParcelable(AppConstants.ACCOUNTS_DATA, accountsData)
            args.putString(AppConstants.DRAFT_ID, draftId)
            val fragment = TaskSelectionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTaskSelectionViewModel.navigator = this
        if (arguments != null) {
            profileDetail = requireArguments().getParcelable(AppConstants.PROFILE)
            accountsData = requireArguments().getParcelable(AppConstants.ACCOUNTS_DATA)
            draftId = requireArguments().getString(AppConstants.DRAFT_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        binding.btnContinue.setOnClickListener(this)
        if (profileDetail != null)
            binding.profileData = profileDetail
        if (accountsData != null) {
            if (accountsData!!.accDescr != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.tvDescription.text=   Html.fromHtml(accountsData!!.accDescr, Html.FROM_HTML_MODE_LEGACY);
                } else {
                    binding.tvDescription.text=  Html.fromHtml(accountsData!!.accDescr);
                }
//                accountsData!!.accDescr="By continuing you agree with service <a href=\"http://www.google.com\">Terms of use</a>"
//                val pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/campton_book.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body>"
//                val pas = "</body></html>"
//                val myHtmlString = pish + accountsData!!.accDescr.toString() + pas
//                binding.tvDescription.loadDataWithBaseURL(null, myHtmlString, "text/html", "UTF-8", null)
//                accountsData!!.accDescr="<html>"+accountsData!!.accDescr+"</html>"
//                if(accountsData!!.accDescr!!.contains("<b>")){
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        binding.tvDescription.text=   Html.fromHtml(accountsData!!.accDescr, Html.FROM_HTML_MODE_LEGACY);
//                    } else {
//                        binding.tvDescription.text=  Html.fromHtml(accountsData!!.accDescr);
//                    }
//
//
//                }else{
//                    binding.tvDescription.text=accountsData!!.accDescr
//                }
            }
            if (accountsData!!.disclaimer != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.tvDisclaimer.text=   Html.fromHtml(accountsData!!.disclaimer, Html.FROM_HTML_MODE_LEGACY);
                } else {
                    binding.tvDisclaimer.text=  Html.fromHtml(accountsData!!.disclaimer);
                }
//                accountsData!!.disclaimer="By continuing you agree with service <a href=\"http://www.google.com\">Terms of use</a>"
                //accountsData!!.disclaimer = "<html> " + accountsData!!.disclaimer + "</html>"
//                val pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/campton_book.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body>"
//                val pas = "</body></html>"
//                val myHtmlString = pish + accountsData!!.disclaimer.toString() + pas
//                binding.tvDisclaimer.setBackgroundColor(Color.parseColor("#FAF4F4"));
//                binding.tvDisclaimer.loadDataWithBaseURL(null, myHtmlString, "text/html", "UTF-8", null)
//                if(accountsData!!.disclaimer!!.contains("<b>")){
//                    customTextView(binding.tvDisclaimer,accountsData!!.disclaimer!!)
//
//                }else{
//                    binding.tvDisclaimer.text=accountsData!!.disclaimer
//                }
            }
//            binding.tvDisclaimer.loadData(accountsData!!.disclaimer, "text/html", "UTF-8")
//            binding.tvDisclaimer.setBackgroundColor(Color.parseColor("#FAF4F4"));
//            binding.tvDescription.loadData(accountsData!!.accDescr, "text/html", "UTF-8")
//            binding.tvDescription.webViewClient = object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                    val navigation = Navigation()
//                    val actionConfig = ActionConfig()
//                    actionConfig.actionUrl = url
//                    navigation.actionConfig = actionConfig
//                    navigation.title = accountsData!!.accTitle
//                    startActivity(WebViewActivity.newIntent(requireContext())
//                            .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation))
//                    return false
//                }
//            }
//            binding.tvDisclaimer.webViewClient = object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                    val navigation = Navigation()
//                    val actionConfig = ActionConfig()
//                    actionConfig.actionUrl = url
//                    navigation.actionConfig = actionConfig
//                    navigation.title = accountsData!!.accTitle
//                    startActivity(WebViewActivity.newIntent(requireContext())
//                            .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation))
//                    return false
//                }
//            }
            binding.accountData = accountsData
        }
        if (accountsData != null && accountsData!!.signupAs != null && accountsData!!.signupAs!!.isNotEmpty()) {
            mTaskSelectionAdapter.setSignupAs(accountsData!!.signupAs!!)

        }
        if (accountsData != null && accountsData!!.offerings != null && accountsData!!.offerings!!.isNotEmpty()) {
            mTaskSelectionAdapter.addData(accountsData!!.offerings!!)
            binding.adapter = mTaskSelectionAdapter
        }
        // binding.tvDescription.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun handleInitiateSignUpResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, requireContext())) {
            var jsonConverter = JSONConverter<InitiateSignUpResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), InitiateSignUpResponse::class.java)
            if (response?.nextScreen != null) {
                if (response.loginToken != null && response.loginToken!!.isNotEmpty()) {
                    preferencesHelper.loginToken = response.loginToken
                }
                if (response.accessId != null && response.accessId!!.isNotEmpty()) {
                    preferencesHelper.accessId = response.accessId
                }


                val info = ProfileInfo()
                if (profileDetail!!.profileImg != null && profileDetail!!.profileImg!!.isNotEmpty())
                    info.profileImg = profileDetail!!.profileImg
                if (profileDetail!!.name != null && profileDetail!!.name!!.isNotEmpty())
                    info.name = profileDetail!!.name
                if (profileDetail!!.email != null && profileDetail!!.email!!.isNotEmpty())
                    info.email = profileDetail!!.email
                if (response.userId != null && response.userId!!.isNotEmpty())
                    info.userId = response.userId
                preferencesHelper.userDetail = info

                if (response.roleId != null) {
                    preferencesHelper!!.userRoleId = response.roleId
                    CommonUtils.showLogMessage("e", "roll_id", response.roleId)
                }
                if (response.nextScreen == NextScreen.HOME) {

                    if (response.refreshConfig) {
                        showLoading()
                        mTaskSelectionViewModel.getConfig(httpManager, response.nextScreen, response.sdkAccessId)
                    } else {

                        if (response.sdkAccessId != null && response.sdkAccessId!!.isNotEmpty()) {
                            TrackThat.setAccessId(response.sdkAccessId)
                        }
                        CommonUtils.otpgoToNext(requireActivity(), response.nextScreen, profileDetail!!.mobile)
                    }

                } else if (response.nextScreen == NextScreen.SERVICE_PREFERENCES) {
                    var intent = Intent(requireContext(), ServicesActivity::class.java)
                    intent.putExtra(AppConstants.DRAFT_ID, draftId)
                    intent.putExtra(AppConstants.MERCHNAT_ID, accountsData!!.merchantId)
                    intent.putExtra(AppConstants.TITLE, accountsData!!.accTitle)
                    intent.putExtra(AppConstants.Extra.FROM, "direct")
                    intent.putParcelableArrayListExtra("service", response.services as java.util.ArrayList<out Parcelable>)
                    startActivity(intent)

                }
            }
        }
    }

    override fun handleConfigResponse(callback: ApiCallback, result: Any?, error: APIError?, nextScreen: NextScreen?, sdkAccessId: String?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, requireContext())) {
            if (sdkAccessId != null) {
                // send accessId to SDK
                TrackThat.setAccessId(sdkAccessId)
            }
            if (result != null) {
                val gson = Gson()
                val configResponse = gson.fromJson(result.toString(), ConfigResponse::class.java)
                CommonUtils.saveConfigDetails(requireActivity(), configResponse, preferencesHelper, "2", profileDetail!!.mobile)
                if (Objects.requireNonNull(configResponse.appConfig)!!.idleTrackingInfo != null) {
                    if (configResponse.appConfig!!.idleTrackingInfo!!.mode != null && configResponse.appConfig!!.idleTrackingInfo!!.mode == "ON_PUNCH" && Objects.requireNonNull(configResponse.appConfig!!.idleTrackingInfo)!!.enableIdleTracking) {
                        preferencesHelper!!.isIdealTrackingEnable = configResponse.appConfig!!.idleTrackingInfo!!.enableIdleTracking
                        showLoading()
                        mTaskSelectionViewModel.getPunchInPunchOutData(httpManager, nextScreen)
                    } else {
                        CommonUtils.otpgoToNext(requireActivity(), nextScreen, profileDetail!!.mobile)
                    }
                } else {
                    CommonUtils.otpgoToNext(requireActivity(), nextScreen, profileDetail!!.mobile)
                }
            }
        }
    }

    override fun handlePunchInPunchOutResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?, nextScreen: NextScreen?) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, requireContext())) {
            val jsonConverter: JSONConverter<PunchInPunchOutData> = JSONConverter<PunchInPunchOutData>()
            val punchInPunchOutData = jsonConverter.jsonToObject(result.toString(), PunchInPunchOutData::class.java) as PunchInPunchOutData
            if (punchInPunchOutData.data != null) {
                if (punchInPunchOutData.data!!.status != null && !punchInPunchOutData.data!!.status!!.isEmpty()) {
                    if (punchInPunchOutData.data!!.status == "PRESENT") {
                        if (preferencesHelper!!.isIdealTrackingEnable) {
                            if (punchInPunchOutData.data!!.punchOutAt != 0L) {
                                preferencesHelper!!.punchOutTime = punchInPunchOutData.data!!.punchOutAt
                                if (TrackThat.isTracking()) {
                                    preferencesHelper!!.idleTripActive = false
                                    TrackThat.stopTracking()
                                    preferencesHelper!!.punchId = null
                                }
                            } else {
                                if (punchInPunchOutData.data != null && punchInPunchOutData.data!!.punchInAt != 0L) {
                                    preferencesHelper!!.punchStatus = true
                                    if (!preferencesHelper!!.idleTripActive) {
                                        preferencesHelper!!.punchId = punchInPunchOutData.data!!.punchId
                                        if (!TrackThat.isTracking()) {
                                            preferencesHelper!!.idleTripActive = true
                                            TrackThat.startTracking(punchInPunchOutData.data!!.punchId, false)
                                        }
                                    }
                                    preferencesHelper!!.punchInTime = punchInPunchOutData.data!!.punchInAt
                                    if (punchInPunchOutData.data!!.punchData != null && punchInPunchOutData.data!!.punchData!!.punchInData != null) {
                                        if (punchInPunchOutData.data!!.punchData!!.punchInData!!.selfie != null) {
                                            val imageUrl = punchInPunchOutData.data!!.punchData!!.punchInData!!.selfie
                                            preferencesHelper!!.selfieUrl = imageUrl
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //CommonUtils.updateSharedContentProvider(this, preferencesHelper)
        }
        CommonUtils.otpgoToNext(requireActivity(), nextScreen, profileDetail!!.mobile)
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnContinue -> {
                var listSelected = mTaskSelectionAdapter.getList().filter { it -> it.selected }
                if (listSelected.isNullOrEmpty()) {
                    TrackiToast.Message.showShort(requireContext(), "Please select your purpose ")
                } else {
                    var idsList = ArrayList<String>()
                    for (offering in listSelected) {
                        idsList.add(offering.id)
                    }
                    var data = InitiateSignUpRequest()
                    data.selectionIds = idsList
                    data.draftId = draftId
                    if (accountsData != null && accountsData!!.merchantId != null)
                        data.merchantId = accountsData!!.merchantId
                    if (accountsData != null && accountsData!!.signupAs != null)
                        data.signupAs = accountsData!!.signupAs
                    var api = TrackiApplication.getApiMap()[ApiType.INITIATE_SIGNUP]
                    if (api != null) {
                        showLoading()
                        mTaskSelectionViewModel.initiateSignUp(httpManager, api, data)
                    }

                }


            }


        }
    }


    private fun customTextView(view: TextView, str: String) {
//        CommonUtils.setCustomSpannable(mActivityConsentBinding.tvPrivacyPolicy,"",0,0 )
//        For more information, please check Tracki \n  Privacy Policy and Terms & Conditions
        val spanTxt = SpannableStringBuilder(str)
        val p: Pattern = Pattern.compile("<b>(\\S+)</b>") //match letters or numbers after a # or @

        val m: Matcher = p.matcher(str) //get matcher, applying the pattern to caption string

        while (m.find()) { // Find each match in turn
//            val clickableSpan: ClickableSpan = object : ClickableSpan() {
//                override fun onClick(textView: View) {
//                    //Clicked word
//                }
//            }
//            spanTxt.setSpan(clickableSpan, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            spanTxt.setSpan(ForegroundColorSpan(Color.BLACK), m.start(), m.end(), 0)
            spanTxt.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        }

       /* val spanTxt = SpannableStringBuilder("For more information, please check RocketFlow \n ")


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
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)*/
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }


}