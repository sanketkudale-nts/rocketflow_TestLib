package com.rocketflyer.rocketflow.ui.taskdetails

import android.location.Location
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 16/10/18
 */
interface TaskDetailNavigator : BaseNavigator {
    fun showEventDialog(showDialog: Boolean, eventName: String)
    fun onLocationChange(location: Location?)
    fun isolateEventResponse(osObject: ArrayList<Any?>, ostObject: ArrayList<Any?>,
                             hcObject: ArrayList<Any?>, hbObject: ArrayList<Any?>,
                             haObject: ArrayList<Any?>)
    fun allEventList(list:ArrayList<AlertEvent>)

    fun onCallClick()
}