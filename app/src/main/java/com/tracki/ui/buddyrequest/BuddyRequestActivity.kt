package com.tracki.ui.buddyrequest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.db.Action
import com.tracki.data.local.db.ApiEventModel
import com.tracki.data.local.db.DatabaseHelper
import com.tracki.data.model.BaseResponse
import com.tracki.data.model.request.AcceptRejectBuddyRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.Buddy
import com.tracki.data.model.response.config.BuddyListResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityBuddyRequestBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil
import com.tracki.utils.NetworkUtils
import javax.inject.Inject

/**
 * Created by rahul on 6/12/18
 */
class BuddyRequestActivity : BaseActivity<ActivityBuddyRequestBinding, BuddyRequestViewModel>(),
        BuddyRequestNavigator, BuddyRequestAdapter.ItemClickListener {

    private var snackBar: Snackbar?=null

    @Inject
    lateinit var mBuddyRequestViewModel: BuddyRequestViewModel
    @Inject
    lateinit var httpManager: HttpManager
    @Inject
    lateinit var buddyRequestAdapter: BuddyRequestAdapter
    lateinit var api: Api

    private lateinit var mActivityBuddyRequestBinding: ActivityBuddyRequestBinding

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_buddy_request
    override fun getViewModel() = mBuddyRequestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBuddyRequestBinding = viewDataBinding
        mBuddyRequestViewModel.navigator = this
        setUp()
    }

    private fun setUp() {
        setToolbar(mActivityBuddyRequestBinding.toolbar, getString(R.string.buddy_request))
        api = TrackiApplication.getApiMap()[ApiType.INVITATIONS]!!
        showLoading()
        mBuddyRequestViewModel.getInvitations(httpManager, api)
    }

    private lateinit var rvBuddyRequest: RecyclerView

    private fun setUpRecyclerView() {
        rvBuddyRequest = mActivityBuddyRequestBinding.rvNotificationList
        rvBuddyRequest.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvBuddyRequest.adapter = buddyRequestAdapter
        buddyRequestAdapter.setListener(this)
    }

    override fun onAcceptRequest(response: Buddy) {
        showLoading()
        val acceptApi = TrackiApplication.getApiMap()[ApiType.ACCEPT_INVITATION]!!
        val request = AcceptRejectBuddyRequest("ACCEPT", response.buddyId!!)
        mBuddyRequestViewModel.acceptRequest(httpManager, request, acceptApi)
    }

    override fun onRejectRequest(response: Buddy) {
        val request = AcceptRejectBuddyRequest("REJECT", response.buddyId!!)
        if (NetworkUtils.isNetworkConnected(this@BuddyRequestActivity)) {
            showLoading()
            val rejectApi = TrackiApplication.getApiMap()[ApiType.REJECT_INVITATION]!!
            mBuddyRequestViewModel.acceptRequest(httpManager, request, rejectApi)
        } else {
            val apiEventModel = ApiEventModel()
            apiEventModel.action = Action.REJECT_INVITATION
            apiEventModel.data = request //AcceptRejectBuddyRequest
            apiEventModel.time = DateTimeUtil.getCurrentDateInMillis()
            DatabaseHelper.getInstance(this).addPendingApiEvent(apiEventModel)
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val buddyListResponse = Gson().fromJson(result.toString(), BuddyListResponse::class.java)
            setUpRecyclerView()
            buddyRequestAdapter.addItems(buddyListResponse.buddies!!)
            val tvBuddyRequest = mActivityBuddyRequestBinding.tvBuddyRequest
            val v = "${buddyListResponse.buddies?.size} buddy"
            val s = "You have $v Request"
            tvBuddyRequest.text = CommonUtils.setCustomFontTypeSpan(this, s, 10, 10 + v.length, R.font.campton_semi_bold)
        }
    }

    override fun acceptRejectResponse(apiCallback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            val buddyListResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
            mBuddyRequestViewModel.getInvitations(httpManager, api)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, BuddyRequestActivity::class.java)
        }
    }

    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityBuddyRequestBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
}