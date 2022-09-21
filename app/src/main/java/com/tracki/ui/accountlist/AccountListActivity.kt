package com.tracki.ui.accountlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.RelativeLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.AppPreferencesHelper
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAccountListBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.login.SendOtpResponse
import com.tracki.ui.login.UserAccount
import com.tracki.ui.otp.OtpActivity
import com.tracki.ui.splash.SplashActivity
import com.tracki.utils.ApiType
import com.tracki.utils.AppConstants.Extra
import com.tracki.utils.CommonUtils
import com.tracki.utils.NextScreen
import com.trackthat.lib.TrackThat
import kotlinx.android.synthetic.main.activity_account_list.*
import javax.inject.Inject

class AccountListActivity : BaseActivity<ActivityAccountListBinding, AccountListViewModel>(), AccountListNavigator, AccountListAdapter.OnclickUserAccounts {

    private var cellwidthWillbe: Int=0
    private var snackBar: Snackbar?=null
    private var from: String? = null

    @Inject
    lateinit var mAccountListViewModel: AccountListViewModel

    @Inject
    lateinit var adapter: AccountListAdapter

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private var mobile: String? = null
    lateinit var accountListBinding: ActivityAccountListBinding
    private var listUserAccounts: List<UserAccount>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountListBinding = viewDataBinding
        mAccountListViewModel.navigator = this
        accountListBinding.adapter = adapter
//        if(intent.hasExtra("listaccounts"))
//        {
//            listUserAccounts=intent.getParcelableArrayListExtra("listaccounts")
//            if(listUserAccounts!=null && listUserAccounts!!.isNotEmpty())
//            {
//                adapter!!.addItems(listUserAccounts)
//                adapter.setListener(this)
//            }
//        }
        setToolbar(accountListBinding.toolbar, "Account Selection")
        listUserAccounts = preferencesHelper.accountList
        val viewTreeObserver = rlMain.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rlMain.viewTreeObserver.removeGlobalOnLayoutListener(this)
                val width = rlMain.measuredWidth
                val height = rlMain.measuredHeight
                cellwidthWillbe = width / 2
                if (listUserAccounts != null && listUserAccounts!!.isNotEmpty()) {
                    adapter.cellWidth(cellwidthWillbe)
                    adapter!!.addItems(listUserAccounts)

                }
            }
        })
        adapter.setListener(this)

        if (intent.hasExtra("from")) {
            from = intent.getStringExtra("from")
        }

        if (intent.hasExtra("mobile")) {
            mobile = intent.getStringExtra("mobile")
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_account_list
    }

    override fun getViewModel(): AccountListViewModel {
        return mAccountListViewModel!!
    }

    override fun login() {
        showLoading()
        val apiUrl = TrackiApplication.getApiMap()[ApiType.LOGIN]
        mAccountListViewModel!!.login(mobile, NextScreen.LOGIN, httpManager, apiUrl)
    }

    override fun handleConfigResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
//            if (sdkAccessId != null) {
//                // send accessId to SDK
//                TrackThat.setAccessId(sdkAccessId)
//            }
//            if (result != null) {
//                val gson = Gson()
//                val configResponse = gson.fromJson(result.toString(), ConfigResponse::class.java)
//                CommonUtils.saveConfigDetails(this@AccountListActivity, configResponse, preferencesHelper, "1", null)
//            }
        }

    }

    override fun handleResponse(apiCallback: ApiCallback, result: Any?, error: APIError?) {

        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            var sendOtpResponse = Gson().fromJson(result.toString(), SendOtpResponse::class.java)
            if (sendOtpResponse.nextScreen != null) {
                if (sendOtpResponse.nextScreen === NextScreen.VERIFY_MOBILE) {
                    preferencesHelper.saveAccountsList(listUserAccounts)
                    val intent = OtpActivity.newIntent(this)
                    intent.putExtra(Extra.NUMBER_EXTRA, mobile)
                    if (getIntent() != null && getIntent().hasExtra(Extra.EXTRA_LOGOUT)) {
                        intent.putExtra(Extra.EXTRA_LOGOUT, true)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onClickUser(userAccount: UserAccount?) {
        userAccount?.let { account ->
            account.accessId?.let {
                preferencesHelper!!.accessId = it
            }
        }
        if (from.equals(NextScreen.LOGIN.name))
            login()
        else {
            val intent = Intent(this, SplashActivity::class.java)
          //  val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Extra.EXTRA_LOGOUT, true)

            preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.NOT_ALL)
           // preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.NOT_ALL_EXCEPT_ACCESS_ID_LOGIN_TOKEN)
            CommonUtils.showLogMessage("e", "login model", preferencesHelper.getUserType())
            //CommonUtils.showLogMessage("e", "loginToken", preferencesHelper.loginToken)

            if (TrackThat.isTracking()) {
                TrackThat.stopTracking()
            }
            setFlags(intent)
            startActivity(intent)
            finish()
        }
    }

    fun newIntent(context: Context?): Intent? {
        return Intent(context, AccountListActivity::class.java)
    }

    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(accountListBinding.rlMain, getString(R.string.please_check_your_internet_connection))
    }

}