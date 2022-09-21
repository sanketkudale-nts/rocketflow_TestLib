package com.rocketflyer.rocketflow.ui.payouts

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.MyEarningRequest
import com.tracki.data.model.response.config.Buddy
import com.tracki.data.model.response.config.ExecutiveMap
import com.tracki.data.model.response.config.MyEarningResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAdminUserPayoutsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.earnings.MyEarningsAdapter
import com.tracki.ui.rides.RideActivity
import com.tracki.utils.*
import kotlinx.android.synthetic.main.activity_admin_user_payouts.*
import java.util.*
import javax.inject.Inject

class AdminUserPayoutsActivity : BaseActivity<ActivityAdminUserPayoutsBinding, AdminUserPayoutsViewModel>(),
        AdminUserPayoutsNavigator, MyEarningsAdapter.OnItemClickListener {

    private lateinit var earningRequest: MyEarningRequest

    @Inject
    lateinit var mMyEarningsViewModel: AdminUserPayoutsViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var myEarningsAdapter: MyEarningsAdapter

    @Inject
    lateinit var httpManager: HttpManager

    private lateinit var mActivityMyEarningsBinding: ActivityAdminUserPayoutsBinding
    private lateinit var toolbar: Toolbar
    private var startTime = 0L
    private var endTime = 0L
    private var buddyId: String? = null
    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_admin_user_payouts
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
        setToolbar(toolbar, getString(R.string.payouts))

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
        if (preferencesHelper.userType != null && preferencesHelper.userType == UserType.ADMIN.name) {
            llMyBuddy.visibility = View.VISIBLE
            mMyEarningsViewModel.checkBuddy(httpManager)
        } else {
            llMyBuddy.visibility = View.GONE
            showLoading()
            mMyEarningsViewModel.getMyEarnings(httpManager, earningRequest)
        }

    }

    override fun selectDateRange() {
        val now = Calendar.getInstance()

        CommonUtils.openDatePicker(this@AdminUserPayoutsActivity,
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

            CommonUtils.openDatePicker(this@AdminUserPayoutsActivity,
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
        if (CommonUtils.handleResponse(callback, error, result, this@AdminUserPayoutsActivity)) {
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
                TrackiToast.Message.showShort(this@AdminUserPayoutsActivity, "No Data found for requested date")
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
        if (buddyId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_ID, buddyId)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "AdminUserPayoutsActivity"
        fun newIntent(context: Context) = Intent(context, AdminUserPayoutsActivity::class.java)
    }

    override fun checkBuddyResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<ExecutiveMap>(result.toString(), ExecutiveMap::class.java)
            executive?.data?.let {
                val list = ArrayList<Buddy>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = Buddy()
                    buddy.buddyId = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, buddyList) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                            (v as TextView).typeface = externalFont
                            return v
                        }
                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spnBuddy!!.adapter = arrayAdapter
                    spnBuddy!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            val selectedItem = parent.getItemAtPosition(position).toString()
                            buddyId = list[position].buddyId
                            CommonUtils.showLogMessage("e", "buddyId", buddyId);
                            earningRequest.userId = buddyId
                            showLoading()
                            mMyEarningsViewModel.getMyEarnings(httpManager, earningRequest)

                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }


                } else {
                }

            }
        }

    }
}
