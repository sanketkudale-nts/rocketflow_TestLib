package com.rocketflyer.rocketflow.data.local.prefs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tracki.utils.AppConstants
import com.trackthat.lib.TrackThat


/**
 * Created by Vikas Kesharvani on 25/08/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class StopIdleTracking: BroadcastReceiver() {
    var preferencesHelper: PreferencesHelper? = null
    override fun onReceive(context: Context?, intent: Intent?) {
     //   TrackiToast.Message.showShort(context!!,"Stop Tracking")
        preferencesHelper = AppPreferencesHelper(context, AppConstants.PREF_NAME)
        if (preferencesHelper!!.idleTripActive ) {
            preferencesHelper!!.idleTripActive = false
            TrackThat.stopTracking()


        }
    }
}