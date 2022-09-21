package com.rocketflyer.rocketflow.ui.referearn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.databinding.ActivityReferEarnBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.CommonUtils
import javax.inject.Inject

/**
 * Created by rahul on 23/11/18
 */
class ReferAndEarnActivity : BaseActivity<ActivityReferEarnBinding, ReferAndEarnViewModel>(),
        ReferAndEarnNavigator {

    @Inject
    lateinit var mReferAndEarnViewModel: ReferAndEarnViewModel
    private lateinit var mActivityReferEarnBinding: ActivityReferEarnBinding

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_refer_earn
    override fun getViewModel() = mReferAndEarnViewModel
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityReferEarnBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityReferEarnBinding = viewDataBinding
        mReferAndEarnViewModel.navigator = this
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ReferAndEarnActivity::class.java)
        }
    }
}