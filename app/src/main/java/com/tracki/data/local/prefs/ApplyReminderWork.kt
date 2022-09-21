package com.tracki.data.local.prefs

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.tracki.TrackiApplication
import com.tracki.data.model.response.config.TaskListing
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.tasklisting.assignedtome.AssignedToMeViewModel
import com.tracki.ui.tasklisting.assignedtome.AssignedtoMeNavigator
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.Log
import com.trackthat.lib.TrackThat
import javax.inject.Inject


/**
 * Created by Vikas Kesharvani on 15/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class ApplyReminderWork(var context: Context, workerParams: WorkerParameters) : Worker(context, workerParams), AssignedtoMeNavigator {

    @Inject
    lateinit var assignedToMeViewModel: AssignedToMeViewModel

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun doWork(): Result {
        preferencesHelper!!.idleTripActive = true
        TrackThat.startTracking(preferencesHelper!!.punchId, false)
        Log.e("StartIdealTrackWork=>", "startIdealTrack=>")
        Log.e("idleTripActive=>", preferencesHelper!!.idleTripActive.toString())
        assignedToMeViewModel.navigator=this
        var api = TrackiApplication.getApiMap()[ApiType.TASKS]
        if (api != null) {
            api.appendWithKey = "ASSIGNED_TO_ME"
        }
        // assignedToMeViewModel.getTaskList(httpManager, api, buddyRequest)
        return Result.success()
    }

    override fun showTimeOutMessage(callback: ApiCallback) {
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, context)) {
            val taskListing = Gson().fromJson(result.toString(), TaskListing::class.java)
            if (taskListing.tasks != null && taskListing.tasks!!.isNotEmpty())
                for (task in taskListing.tasks!!) {
                    var map = preferencesHelper.eventIdMap
                    if (map != null && map.isNotEmpty()) {
                        if (map.containsKey(task.taskId)) {
                            var eventId = map[task.taskId]
                            CommonUtils.deleteEventUri(context, eventId, task.taskId, preferencesHelper)
                        }

                    }
                    if(task.startTime>0){
                        var titile=""
                        var desc=""
                        var place=""
                        var startDate=task.startTime
                        var needReminder=true
                        var needMainSerVice=false
                        var taskId=task.taskId
                        var timerBefore=5
                        if(task.taskName!=null)
                            titile=task.taskName!!
                        if(task.description!=null)
                            desc=task.description!!
                        if(task.source!=null&&task.source!!.address!=null)
                            place=task.source!!.address!!
                    }

                }

        }
    }

    override fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?) {
    }


}