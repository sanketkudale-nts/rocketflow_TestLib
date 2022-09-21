package com.tracki.ui.rides

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.GetTaskByDateRequest
import com.tracki.data.model.response.config.PayoutHistoryResponse
import com.tracki.data.model.response.config.Task
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityRideBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.taskdetails.TaskDetailActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil
import com.tracki.utils.TaskStatus
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Rahul Abrol on 29/12/19.
 */
class RideActivity : BaseActivity<ActivityRideBinding, RideViewModel>(), RideNavigator, RideAdapter.ItemClickListener {

    @Inject
    lateinit var mRideViewModel: RideViewModel
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    @Inject
    lateinit var rideAdapter: RideAdapter
    @Inject
    lateinit var httpManager: HttpManager

    private var date = 0L
    private lateinit var mActivityRideBinding: ActivityRideBinding
    private lateinit var toolbar: Toolbar

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_ride
    override fun getViewModel() = mRideViewModel
    private var from:Long=0
    private var to:Long=0
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityRideBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityRideBinding = viewDataBinding
        mRideViewModel.navigator = this
        rideAdapter.setListener(this)

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        date = intent.getLongExtra(AppConstants.Extra.EXTRA_DATE, cal.timeInMillis)
        from = intent.getLongExtra(AppConstants.Extra.FROM, cal.timeInMillis)
        to = intent.getLongExtra(AppConstants.Extra.FROM_TO, cal.timeInMillis)

        toolbar = mActivityRideBinding.toolbar
        setToolbar(toolbar, getString(R.string.ride_of) + " " +
                DateTimeUtil.getParsedDate(date, DateTimeUtil.DATE_FORMAT_2))

        //set Adapter
        mActivityRideBinding.rvRideList.adapter = rideAdapter

        val task = GetTaskByDateRequest()
        task.date = date
        task.from=from
        task.to=to
        if(intent.hasExtra(AppConstants.Extra.EXTRA_BUDDY_ID)){
            task.userId=intent.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_ID)
        }
        //hit api to get my earning details
        showLoading()
        mRideViewModel.getMyEarnings(httpManager, task)
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@RideActivity)) {
            val buddyListResponse = Gson().fromJson(result.toString(), PayoutHistoryResponse::class.java)
            val data = buddyListResponse.data
            if (data != null && data.isNotEmpty()) {
                var list=ArrayList<Task>()
                for ( task in data){
                    if(task.taskDetail!=null) {
                        var itemData=task.taskDetail!!
                       if(task.payoutBreakup!=null){
                           itemData.payoutEligible=true
                           itemData.driverPayoutBreakUps=task.payoutBreakup
                       }
                        list.add(task.taskDetail!!)
                    }
                }
                rideAdapter.addItems(list)
                mRideViewModel.totalRides.set("${data.size}")
                var earnings = 0.0
                //count the earnings
                for (i in data.indices) {
                    earnings += data[i].payoutBreakup?.totalPayout!!
                }
                //earning
                if (earnings != 0.0) {
                    mRideViewModel.totalEarnings.set("$earnings")
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, RideActivity::class.java)
    }

    override fun onCallClick(task: Task?) {
        if (task?.contact != null) {
            val mobile = task.contact!!.mobile
            CommonUtils.openDialer(this@RideActivity, mobile)
        }
    }

    override fun onItemClick(task: Task?) {
        if (task?.status == TaskStatus.COMPLETED) {
            startActivity(TaskDetailActivity.newIntent(this@RideActivity)
                    .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.taskId))
        }
    }
}