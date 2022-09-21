//package com.trackthat.driver.ui.broadcast
//
//import android.annotation.SuppressLint
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.os.PowerManager
//import android.util.Log
//import com.google.gson.Gson
//import com.tracki.data.local.prefs.PreferencesHelper
//import com.tracki.data.model.AlarmInfo
//import com.tracki.utils.AppConstants
//import com.tracki.utils.ShiftHandlingUtil
//import com.trackthat.lib.TrackThat
//
//
///**
// * Created by rahul on 28/12/18
// */
//class AlarmReceiver : BroadcastReceiver() {
//    private var screenWakeLock: PowerManager.WakeLock? = null
//    private val WAKETAG = "com.traktat.driver:Wklk"
//    private val  preferencesHelper:PreferencesHelper?=null
//
//    @SuppressLint("WakelockTimeout")
//    override fun onReceive(context: Context, intent: Intent?) {
//
//        Log.e(WAKETAG, "inside on receive of AlarmReceiver")
//
//        if (screenWakeLock == null) {
//            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//            screenWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKETAG)
//            screenWakeLock!!.acquire()
//        }
//
//        if (intent!!.action.equals("start tracking", true)) {
//            if (!TrackThat.isTracking())
//            TrackThat.startTracking(null, false)
//        } else if (intent.action.equals("stop tracking", true)) {
//            if (TrackThat.isTracking())
//            TrackThat.stopTracking()
//        }
//
////        val serviceIntent = Intent(context, AlarmService::class.java)
//        val preferencesUtil = PreferencesUtil.with(context)
//        if (intent != null && (intent.hasExtra("start") || intent.hasExtra("stop"))) {
//            val fromStart = intent.getBooleanExtra("start", false)
//            val fromStop = intent.getBooleanExtra("stop", false)
//            val gson = Gson()
//            if (fromStart) {
//                Log.e(WAKETAG, "iniside start")
//                //to mark the previous set alarm execution case true
//                val startAlarmString = preferencesUtil.getString(AppConstants.START_ALARM_INFO, null)
//                if (startAlarmString != null) {
//                    val alarmInfo = gson.fromJson(startAlarmString, AlarmInfo::class.java)
//                    alarmInfo.isExecuted = true
//                    preferencesUtil?.save(AppConstants.START_ALARM_INFO, gson.toJson(alarmInfo))
//                }
//            } else if (fromStop) {
//                Log.e(WAKETAG, "iniside stop")
//                //to mark the previous set alarm execution case true
//                val stopAlarmString = preferencesUtil.getString(AppConstants.STOP_ALARM_INFO, null)
//                if (stopAlarmString != null) {
//                    val alarmInfo = gson.fromJson(stopAlarmString, AlarmInfo::class.java)
//                    alarmInfo.isExecuted = true
//                    preferencesUtil?.save(AppConstants.STOP_ALARM_INFO, gson.toJson(alarmInfo))
//                }
//                // set alarms for next timings
//                ShiftHandlingUtil.manageAlarm(context, preferencesUtil)
//            }
////            serviceIntent.putExtra("start", fromStart)
////            serviceIntent.putExtra("stop", fromStop)
////            //start service
////            ContextCompat.startForegroundService(context, serviceIntent)
//        }
//
//        if (screenWakeLock != null && screenWakeLock!!.isHeld)
//            screenWakeLock!!.release()
//    }
//}