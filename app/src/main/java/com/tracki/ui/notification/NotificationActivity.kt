package com.tracki.ui.notification

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.model.NotificationEvent
import com.tracki.data.model.NotificationResponse
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.NotificationListResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityNotificationBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.buddyrequest.BuddyRequestActivity
import com.tracki.ui.tasklisting.TaskActivity
import com.tracki.utils.ApiType
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import javax.inject.Inject


/**
 * Created by rahul on 11/10/18
 */
open class NotificationActivity : BaseActivity<ActivityNotificationBinding, NotificationViewModel>(),
        NotificationNavigator, NotificationAdapter.NotificationListener {

    @Inject
    lateinit var mNotificationViewModel: NotificationViewModel
    @Inject
    lateinit var adapter: NotificationAdapter
    @Inject
    lateinit var httpManager: HttpManager

    private var list: List<NotificationResponse>? = null
    private lateinit var mActivityNotificationBinding: ActivityNotificationBinding
    private lateinit var toolbar: Toolbar
    private var rvNotification: RecyclerView? = null
    private lateinit var api: Api

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_notification
    override fun getViewModel() = mNotificationViewModel
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityNotificationBinding.coordinator, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityNotificationBinding = viewDataBinding
        mNotificationViewModel.navigator = this
        adapter.setListener(this)

        api = TrackiApplication.getApiMap()[ApiType.NOTIFICATIONS]!!
        setUp()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.notifications, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawable = item?.icon
        if (drawable is Animatable) {
            drawable.start()
        }
        when (item?.itemId) {
            R.id.action_clear -> {
                if (list != null && list!!.isNotEmpty()) {
                    showLoading()
                    val api = TrackiApplication.getApiMap()[ApiType.CLEAR_NOTIFICATIONS]
                    mNotificationViewModel.clearAll(httpManager, api!!)
                }
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(response: NotificationResponse) {
        var intent: Intent? = null
        when (response.event) {
            NotificationEvent.RECEIVE_INVITATION -> {
                if (response.actionable) {
                    intent = BuddyRequestActivity.newIntent(this@NotificationActivity)
                }
            }
            NotificationEvent.REJECT_INVITATION -> {
                intent = null
            }
            NotificationEvent.ACCEPT_INVITATION -> {
                intent = null
            }
            NotificationEvent.END_TASK -> {
                if (response.actionable) {
                    intent = TaskActivity.newIntent(this@NotificationActivity)
                            .putExtra(AppConstants.Extra.EXTRA_TASK_ID, response.data.taskId)
                }
            }
            NotificationEvent.START_TASK -> {
                intent = TaskActivity.newIntent(this@NotificationActivity)
                        .putExtra(AppConstants.Extra.EXTRA_TASK_ID, response.data.taskId)
            }
            NotificationEvent.ACCEPT_TASK -> {
                if (response.actionable) {
                    intent = TaskActivity.newIntent(this@NotificationActivity)
                            .putExtra(AppConstants.Extra.EXTRA_TASK_ID, response.data.taskId)
                }
            }
            NotificationEvent.ASSIGN_TASK -> {
                if (response.actionable) {
                    intent = TaskActivity.newIntent(this@NotificationActivity)
                            .putExtra(AppConstants.Extra.EXTRA_TASK_ID, response.data.taskId)
                }
            }
            NotificationEvent.CANCEL_TASK -> {
                if (response.actionable) {
                    intent = TaskActivity.newIntent(this@NotificationActivity)
                            .putExtra(AppConstants.Extra.EXTRA_TASK_ID, response.data.taskId)
                }
            }
            NotificationEvent.REJECT_TASK -> {
                if (response.actionable) {
                    intent = TaskActivity.newIntent(this@NotificationActivity)
                            .putExtra(AppConstants.Extra.EXTRA_TASK_ID, response.data.taskId)
                }
            }
            NotificationEvent.CANCEL_TRACKING_REQUEST -> {
                intent = null
            }
        }
        if (intent != null) {
            startActivity(intent)
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@NotificationActivity)) {
            val notificationListResponse = Gson().fromJson<NotificationListResponse>(result.toString(), NotificationListResponse::class.java)
            list = notificationListResponse.notifications
            setUpRecyclerView()
            if (list != null && list!!.isNotEmpty()) {
                adapter.addItems(list!!)
            }
        }
    }

    override fun handleClearNotificationResponse(apiCallback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this@NotificationActivity)) {
            adapter.addItems(ArrayList())
        }
    }

    private fun setUp() {
        toolbar = mActivityNotificationBinding.toolbar
        //set toolbar taskName and enable home button
        setToolbar(toolbar, getString(R.string.notifications))

        showLoading()
        mNotificationViewModel.getNotificationList(httpManager, api)
    }

    private fun setUpRecyclerView() {
        if (rvNotification == null) {
            rvNotification = mActivityNotificationBinding.rvNotificationList
            rvNotification!!.layoutManager = LinearLayoutManager(this)
        }
        if (rvNotification!!.adapter == null) {
            rvNotification!!.adapter = adapter
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, NotificationActivity::class.java)
        }
    }

}