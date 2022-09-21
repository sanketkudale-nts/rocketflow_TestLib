package com.rocketflyer.rocketflow.ui.emergencymessage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.databinding.ActivityEmergencyMessageBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil
import javax.inject.Inject

/**
 * Created by rahul on 5/12/18
 */
class EmergencyMessageActivity : BaseActivity<ActivityEmergencyMessageBinding, EmergencyMessageViewModel>(),
        EmergencyMessageNavigator {

    @Inject
    lateinit var mEmergencyMessageViewModel: EmergencyMessageViewModel
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var mActivityEmergencyMessageBinding: ActivityEmergencyMessageBinding

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_emergency_message
    override fun getViewModel() = mEmergencyMessageViewModel
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityEmergencyMessageBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityEmergencyMessageBinding = viewDataBinding
        mEmergencyMessageViewModel.navigator = this
        setUp()
    }

    private lateinit var toolbar: Toolbar
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult == null) {
                return
            }
            for (location in locationResult.locations) {
                if (location != null) {
                    removeLocationUpdates(this)
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    mActivityEmergencyMessageBinding.tvLocation.text = "Current Location " +
                            "${location.latitude},${location.longitude} |\n " +
                            "Follow the link to view in map"
                    mActivityEmergencyMessageBinding.tvTime.text = "Capture At " +
                            "${DateTimeUtil.getParsedTime(location.time)} | " +
                            "${DateTimeUtil.getParsedDate(location.time)}"
                }
            }
        }
    }

    private fun setUp() {
        toolbar = mActivityEmergencyMessageBinding.toolbar
        val edHelp = mActivityEmergencyMessageBinding.edHelp
        edHelp.setSelection(edHelp.text.length)
        setToolbar(toolbar, getString(R.string.text_message))
        // Check if the Read IEMI Permission is granted
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return
        }
        //get current location
        requestCurrentLocation(locationCallback)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, EmergencyMessageActivity::class.java)
    }
}