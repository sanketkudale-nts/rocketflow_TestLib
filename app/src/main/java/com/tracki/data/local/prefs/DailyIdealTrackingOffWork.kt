package com.tracki.data.local.prefs

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tracki.utils.AppConstants
import com.trackthat.lib.TrackThat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Vikas Kesharvani on 06/08/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class DailyIdealTrackingOffWork(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    var preferencesHelper: PreferencesHelper? = null
    init {
        preferencesHelper = AppPreferencesHelper(ctx, AppConstants.PREF_NAME)
    }

    override fun doWork(): Result {

        if (preferencesHelper!!.idleTripActive && TrackThat.isTracking()) {
            TrackThat.stopTracking()
            preferencesHelper!!.idleTripActive = false
        }
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        // Set Execution around 23:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 23)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val dailyWorkRequest = OneTimeWorkRequest.Builder(DailyIdealTrackingOffWork::class.java)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build()
        WorkManager.getInstance()
                .enqueue(dailyWorkRequest)

        return Result.success()
    }
}