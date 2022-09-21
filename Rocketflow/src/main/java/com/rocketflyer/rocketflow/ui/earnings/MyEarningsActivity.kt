package com.rocketflyer.rocketflow.ui.earnings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.MyEarningRequest
import com.tracki.data.model.response.config.MyEarningResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityMyEarningsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.rides.RideActivity
import com.tracki.utils.*
import java.util.*
import javax.inject.Inject


/**
 * Created by Rahul Abrol on 27/12/19.
 */
class MyEarningsActivity : BaseActivity<ActivityMyEarningsBinding, MyEarningsViewModel>(),
        MyEarningsNavigator, MyEarningsAdapter.OnItemClickListener {

    private lateinit var earningRequest: MyEarningRequest

    @Inject
    lateinit var mMyEarningsViewModel: MyEarningsViewModel
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    @Inject
    lateinit var myEarningsAdapter: MyEarningsAdapter
    @Inject
    lateinit var httpManager: HttpManager

    private lateinit var mActivityMyEarningsBinding: ActivityMyEarningsBinding
    private lateinit var toolbar: Toolbar
    private var startTime = 0L
    private var endTime = 0L
    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_my_earnings
    override fun getViewModel() = mMyEarningsViewModel
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityMyEarningsBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMyEarningsBinding = viewDataBinding
        mMyEarningsViewModel.navigator = this
        //set listener
        myEarningsAdapter.setOnItemListener(this)
        toolbar = mActivityMyEarningsBinding.toolbar
        setToolbar(toolbar, getString(R.string.my_earnings))

        //set Adapter
        mActivityMyEarningsBinding.rvEarningList.adapter = myEarningsAdapter
        //start time
        val cal = Calendar.getInstance()
        // set end time as today's date and current time.
        endTime = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        //set start time as today's date and today's time from 12:00 am.
        startTime = cal.timeInMillis

//        Log.e(TAG, "$startTime <-----------> $endTime")
         earningRequest = MyEarningRequest()
        earningRequest.from = startTime
        earningRequest.to = endTime
        //update date range
        updateDateRange(startTime, endTime)
        //hit api to get my earning details
        showLoading()
        mMyEarningsViewModel.getMyEarnings(httpManager, earningRequest)


    }

    override fun selectDateRange() {
        val now = Calendar.getInstance()

        CommonUtils.openDatePicker(this@MyEarningsActivity,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                0, now.timeInMillis) { _, year, month, dayOfMonth ->
            val calStart = Calendar.getInstance()
            // setTimeZone(calStart)
            calStart.set(Calendar.YEAR, year)
            calStart.set(Calendar.MONTH, month)
            calStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calStart.set(Calendar.HOUR_OF_DAY, 0)
            calStart.set(Calendar.MINUTE, 0)
            calStart.set(Calendar.SECOND, 0)
            startTime = calStart.timeInMillis

            CommonUtils.openDatePicker(this@MyEarningsActivity,
                    year,
                    month,
                    dayOfMonth,
                    startTime, now.timeInMillis) { _, yearEnd, monthEnd, dayOfMonthEnd ->
                val calEnd = now.clone() as Calendar
                // setTimeZone(calEnd)
                calEnd.set(Calendar.YEAR, yearEnd)
                calEnd.set(Calendar.MONTH, monthEnd)
                calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd)
                calEnd.set(Calendar.HOUR_OF_DAY, 23)
                calEnd.set(Calendar.MINUTE, 59)
                calEnd.set(Calendar.SECOND, 0)
                endTime = calEnd.timeInMillis
                //update on server
                updateDateRange(startTime, endTime)
            }
        }
    }

    /**
     * Update observable for date range after successful set value.
     * @param startTime start time
     * @param endTime end time
     */
    private fun updateDateRange(startTime: Long, endTime: Long) {
        mMyEarningsViewModel.setDateRange("${DateTimeUtil.getParsedDate(startTime,
                DateTimeUtil.DATE_FORMAT_2)} To ${DateTimeUtil.getParsedDate(endTime,
                DateTimeUtil.DATE_FORMAT_2)}")
    }

    override fun search() {
        if (startTime != 0L && endTime != 0L) {
            val earningRequest = MyEarningRequest()
            earningRequest.from = startTime
            earningRequest.to = endTime
            //hit api to get my earning details
            showLoading()
            mMyEarningsViewModel.getMyEarnings(httpManager, earningRequest)
        } else {
            TrackiToast.Message.showShort(this, "Select Date")
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@MyEarningsActivity)) {
            val notificationListResponse = Gson().fromJson<MyEarningResponse>(result.toString(), MyEarningResponse::class.java)
            if (notificationListResponse.earnings != null && notificationListResponse.earnings!!.isNotEmpty()) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val sTime = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                val eTime = cal.timeInMillis
                //Log.e(TAG, "$sTime <----------> $eTime")
                val earning = notificationListResponse.earnings!![0]
                //get current date value for show total rides and earnings
                if (earning.date in (sTime + 1)..eTime) {
                    mMyEarningsViewModel.isTodayEarning.set(true)
                    mMyEarningsViewModel.totalRides.set("${earning.totalRide}")
                    mMyEarningsViewModel.totalEarnings.set("${AppConstants.INR} ${earning.totalAmount}")
                } else {
                    mMyEarningsViewModel.isTodayEarning.set(false)
                }
            } else {
                mMyEarningsViewModel.isTodayEarning.set(false)
                TrackiToast.Message.showShort(this@MyEarningsActivity, "No Data found for requested date")
            }
            myEarningsAdapter.addItems(notificationListResponse.earnings)
        }
    }

    override fun onClickItem(date: Long) {
        openRideActivity(date)
    }

    override fun viewDetails() {
        val cal = Calendar.getInstance()
        openRideActivity(cal.timeInMillis)
    }

    /**
     * Function used to open ride activity with date of rides
     */
    private fun openRideActivity(date: Long) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val intent = RideActivity.newIntent(this)
        intent.putExtra(AppConstants.Extra.EXTRA_DATE, cal.timeInMillis)
        intent.putExtra(AppConstants.Extra.FROM, startTime)
        intent.putExtra(AppConstants.Extra.FROM_TO, endTime)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "MyEarningsActivity"
        fun newIntent(context: Context) = Intent(context, MyEarningsActivity::class.java)
    }
}
