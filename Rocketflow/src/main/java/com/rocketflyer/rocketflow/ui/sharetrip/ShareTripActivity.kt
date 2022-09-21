package com.rocketflyer.rocketflow.ui.sharetrip

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.model.BaseResponse
import com.tracki.data.model.request.ShareTrip
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityShareTripBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.ApiType
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.TrackiToast
import javax.inject.Inject


/**
 * Class used to share live trip to social network.
 *
 * Created by rahul on 1/3/19
 */
class ShareTripActivity : BaseActivity<ActivityShareTripBinding, ShareTripViewModel>(),
        ShareTripNavigator, AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var mShareTripViewModel: ShareTripViewModel
    @Inject
    lateinit var httpManager: HttpManager

    private lateinit var mActivityShareTripBinding: ActivityShareTripBinding
    var time = arrayOf("Select", "1 Hrs", "2 Hrs", "3 Hrs", "5 Hrs")
    var selectedPosition = 0
    private lateinit var trackingId: String

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_share_trip
    override fun getViewModel() = mShareTripViewModel

    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityShareTripBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityShareTripBinding = viewDataBinding
        mShareTripViewModel.navigator = this
        setUp()
    }

    private fun setUp() {
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
            trackingId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)!!
        }
        setToolbar(mActivityShareTripBinding.toolbar, getString(R.string.share_trip_detail))
        val timeSpinner = mActivityShareTripBinding.timeSpinner
        timeSpinner.onItemSelectedListener = this

        //Creating the ArrayAdapter instance having the country hashMap
        //val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, time)
        var aa  = object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, time) {
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
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        timeSpinner.adapter = aa

//        val tvLink = mActivityShareTripBinding.tvLink
        val btnShareLink = mActivityShareTripBinding.btnShareLink
        btnShareLink.setOnClickListener {
            if (selectedPosition != 0 && trackingId != "") {
                val api = TrackiApplication.getApiMap()[ApiType.SHARE_TRIP]
                var timepstap=System.currentTimeMillis()+selectedPosition*3600000
                val shareTrip = ShareTrip(timepstap , trackingId)
                showLoading()
                mShareTripViewModel.getSharableLink(httpManager, api!!, shareTrip)
            } else {
                TrackiToast.Message.showShort(this@ShareTripActivity, "Please select time")
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> selectedPosition = 0
            1 -> selectedPosition = 1
            2 -> selectedPosition = 2
            3 -> selectedPosition = 3
            4 -> selectedPosition == 4
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val baseResponse = Gson().fromJson("$result", BaseResponse::class.java)
            CommonUtils.shareLinkOnSocialMedia(this@ShareTripActivity, baseResponse.trackingUrl)
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ShareTripActivity::class.java)
    }

}