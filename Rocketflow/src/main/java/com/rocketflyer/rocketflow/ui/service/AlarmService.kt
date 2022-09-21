package com.rocketflyer.rocketflow.ui.service//package com.tracki.ui.service
//
//import android.annotation.SuppressLint
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Build
//import android.os.Handler
//import android.os.IBinder
//import android.os.Looper
//import android.widget.Toast
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.tracki.*
//import com.tracki.data.local.db.ApiEventModel
//import com.tracki.data.local.db.IAlarmTable
//import com.tracki.data.model.AlarmData
//import com.tracki.data.model.BaseResponse
//import com.tracki.data.network.APIError
//import com.tracki.data.network.HttpManager
//import com.tracki.ui.geofencing.Constants
//import com.tracki.utils.AppConstants
//import com.tracki.utils.Log
//import com.tracki.utils.PreferencesUtil
//import com.trackthat.driver.data.model.config.HubLocation
//import com.trackthat.driver.ui.broadcast.RestartAlarmsReceiver
//import com.trackthat.lib.TrackThatConstants
//import com.trackthat.lib.internal.util.GeofenceUtils
//import com.trackthat.lib.internal.util.TripType
//
//import java.util.*
//
///**
// * Created by rahul on 29/12/18
// */
//class AlarmService : Service(), Runnable {
//
//    companion object {
//        const val ACTION_ENTER = "com.trackthat.driver.Action.Enter"
//        const val ACTION_EXIT = "com.trackthat.driver.Action.Exit"
//        const val VISIT_ATM_ID = "com.trackthat.driver.Action.VisitID"
//        const val VISIT_ID = "visit_id"
//        const val TAG = "AlarmService"
//        private const val ID_SERVICE = 104
//    }
//
//    private var delayed = 10000L
//
//    private var preferencesUtil: PreferencesUtil? = null
//    private var httpManager: HttpManager? = null
//    //    private var hubLocation: HubLocation? = null
//    private lateinit var gson: Gson
//    private var alarmData: AlarmData? = null
//    private var handler: Handler? = null
//    private var isHandlerRunning: Boolean = false
//    private var alarmDBHelper: IAlarmTable? = null
//    private var geofenceUtils: GeofenceUtils? = null
//    private var trackThatPrefs:TrackThatPrefs?=null
//    private var trackThatCore:TrackThatCore?=null
//    private var hubLocationList:List<HubLocation>?=null
//
//
//    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            try {
//                if (intent != null) {
//                    val geofenceId = intent.getStringExtra("hubId")
//                    val intenre = Intent(VISIT_ATM_ID)
//                    intenre.putExtra(VISIT_ID, geofenceId)
//
//                    if (intent.action == ACTION_ENTER) {
//                        //get geofence id if already stored into the preferences
//                        val visitID = preferencesUtil?.getString(VISIT_ID, null)
//                        // If visit id is null then user enter into another geofence and save the id into the preferences
//                        // and broadcast an intent
//                        if (visitID == null || visitID != geofenceId) {
//                            //sent if app is open
//                            preferencesUtil?.save(VISIT_ID, geofenceId)
//                            Log.e(TAG, "Geofence Id $geofenceId Saved")
//                            var putExtra = intenre.putExtra("isEnter", true)
//                            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intenre)
//                        }
//                        val isInEventEnteredOnce = preferencesUtil?.getBoolean(AppConstants.IS_START, false)
//                        if (!(isInEventEnteredOnce!!)) {
//                            preferencesUtil?.save(AppConstants.IS_END, false)
//                            preferencesUtil?.save(AppConstants.IS_START, true)
//                            Toast.makeText(applicationContext, EVENT.IN.name, Toast.LENGTH_SHORT).show()
//                            completeTrip(false)
//                            Log.e(TAG, "Action Enter")
//                            startTrip(null) //start new tripId
//                            Handler().postDelayed({
//                                val tripId = trackThatPrefs?.getString(Keys.Prefs.CURRENT_TRACKING_ID, "")
//                                saveToDB(EVENT.IN, tripId!!, System.currentTimeMillis(), false, geofenceId)
//                            }, 3000)
//                        }
//                    } else if (intent.action == ACTION_EXIT) {
//                        // if inspection form is filled then remove this field from preferences
//                        // when out event detected
//                        preferencesUtil?.remove(StringConstant.FORM_FILLED)
//                        //remove geofence id and update to screen
//                        preferencesUtil?.remove(VISIT_ID)
//                        Log.e(TAG, "Geofence Id $geofenceId removed")
//                        intenre.putExtra("isExit", true)
//                        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intenre)
//
//                        val isExitedOnce = preferencesUtil?.getBoolean(Constants.IS_END, false)
//                        if (!(isExitedOnce!!)) {
//                            preferencesUtil?.save(AppConstants.IS_START, false)
//                            preferencesUtil?.save(AppConstants.IS_END, true)
//                            completeTrip(false)
//                            Toast.makeText(applicationContext, EVENT.OUT.name, Toast.LENGTH_SHORT).show()
//                            Log.e(TAG, "Action Exit")
////                      stop current trip and start new
//                            startTrip(null)
//                            Handler().postDelayed({
//                                val trackingId = trackThatPrefs?.getString(Keys.Prefs.CURRENT_TRACKING_ID, "")
//                                saveToDB(EVENT.OUT, trackingId!!, System.currentTimeMillis(), true, geofenceId)
//                            }, 3000)
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
////    private var mWakeLock: PowerManager.WakeLock? = null
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun run() {
//        Log.i(TAG, "Inside run")
//        if (NetworkUtil.isNetworkAvailable(applicationContext)) {
//            val list = alarmDBHelper?.checkIfRecordExists() as List<ApiEventModel>
//            if (list.isNotEmpty()) {
//                alarmDBHelper?.syncWithServer(list[0], 1)
//                Log.i(TAG, "Record exists")
//                hitApi(list, object : InteractorCallback {
//                    override fun onSuccess(result: Any?) {
//                        if (result != null) {
//                            val gson = Gson()
//                            try {
//                                val baseResponse = gson.fromJson(result as String, BaseResponse::class.java)
//                                when {
//                                    baseResponse.isSuccessful -> alarmDBHelper?.syncWithServer(list[0], 2)
//                                    baseResponse.responseCode == "404" -> alarmDBHelper?.syncWithServer(list[0], 2)
//                                    else -> alarmDBHelper?.syncWithServer(list[0], 0)
//                                }
//                            } catch (e: Exception) {
//                                alarmDBHelper?.syncWithServer(list[0], 0)
//                                Log.e(TAG, "Exception inside hitApi() : $e")
//                            }
//                        } else {
//                            alarmDBHelper?.syncWithServer(list[0], 0)
//                        }
//                        handler?.postDelayed(this@AlarmService, delayed)
//                    }
//
//                    override fun onError(error: APIError?) {
//                        alarmDBHelper?.syncWithServer(list[0], 0)
//                        Log.e(TAG, "Inside onError() ${error?.errorType}")
//                        handler?.postDelayed(this@AlarmService, delayed)
//                    }
//                })
//            } else {
//                Log.i(TAG, "Record doesn't exists")
//                if (alarmDBHelper?.checkIfEndSynced()!! && fromStop != null && fromStop!!) {
//                    clearAllData()
//                    return
//                }
//                handler?.postDelayed(this@AlarmService, delayed)
//            }
//        } else {
//            Log.e(TAG, "No internet connection found.")
//            handler?.postDelayed(this@AlarmService, delayed)
//        }
//        isHandlerRunning = true
//    }
//
//    private fun clearAllData() {
//        Log.e(TAG, "Deleting all data...")
//        alarmDBHelper?.deleteData()
//        stopAlarmService()
//    }
//
//    private var receiver: RestartAlarmsReceiver? = null
//
//    override fun onCreate() {
//        super.onCreate()
//        try {
//            Log.e(TAG, "Inside onCreate()")
//            trackThatCore = TrackThatCore.getInstance(applicationContext)
//            trackThatPrefs = TrackThatPrefs.with(applicationContext)
//            gson = Gson()
//            handler = Handler(Looper.getMainLooper())
//            httpManager = HttpManager.getInstance(applicationContext)
//            alarmDBHelper = AlarmDatabaseHelper.getInstance(applicationContext)
//            preferencesUtil = PreferencesUtil.with(applicationContext)
//            httpManager?.setLoginToken(preferencesUtil)
//            ping = gson.fromJson(preferencesUtil?.getString(Constants.PING, null), TrackThatUrl::class.java)
//            val location = preferencesUtil?.getString(Constants.OLD_HUB_LOCATION_LIST, null)
//            if (location != null) {
//                hubLocationList = gson.fromJson<ArrayList<HubLocation>>(location, object : TypeToken<ArrayList<HubLocation>>() {}.type)
//                Log.e(TAG, "Saved hub locations are: " + hubLocationList.toString())
//            }
//            val da = preferencesUtil?.getString(Constants.RESTART, null)
//            alarmData = if (da != null) {
//                gson.fromJson(da, AlarmData::class.java)
//            } else {
//                AlarmData()
//            }
//            //checkForSdkVersion()
//            geofenceUtils = GeofenceUtils(applicationContext)
//            val intentFilter = IntentFilter()
//            intentFilter.addAction(ACTION_ENTER)
//            intentFilter.addAction(ACTION_EXIT)
//            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
//            //register boot receiver
//            registerBoorReceiver()
//        } catch (e: Exception) {
//            Log.e(TAG, "Exception Inside onCreate() : $e")
//        }
//    }
//
//    private var fromStop: Boolean? = null
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        try {
//            if (!Util.isServiceRunningInForeground(this, this.javaClass.name)) {
//                Util.createAndShowForegroundNotification(this, ID_SERVICE)
//            }
//            Log.i(TAG, "Inside onStartCommand()")
//            if (intent != null) {
//                if (hubLocationList == null || hubLocationList!!.isEmpty()) {
//                    Log.e(TAG, "Hub location list cannot be null")
//                    stopAlarmService()
//                    return START_STICKY
//                }
//
//                if (intent.hasExtra("start") || intent.hasExtra("stop")) {
//                    if (!isHandlerRunning) {
//                        handler?.post(this@AlarmService)
//                    }
//                    val fromStart = intent.getBooleanExtra("start", false)
//                    fromStop = intent.getBooleanExtra("stop", false)
//
//                    if (fromStart) {
//                        registerAlarmForEveryTwoMin(applicationContext)
//                        Log.e(TAG, "inside service-------start---->>$fromStart")
//
//                        val isTrackingLiveTrip = trackThatPrefs?.getBoolean(Keys.Prefs.IS_TRACKING_LIVE_TRIP, false)
//                        val cTripId = trackThatPrefs?.getString(Keys.Prefs.CURRENT_TRACKING_ID, "")
//                        if (!isTrackingLiveTrip!! && cTripId == "") {
//                            //check if tracking is on and tracking service is running
//                            //if not running then start Idle tracking
//                            trackThatCore?.addTrackingIdToPreferences()
//                        }
//                        alarmData?.isStart = true
//                        alarmData?.currentTripId = cTripId
//                        preferencesUtil?.save(Constants.RESTART, gson.toJson(alarmData))
//                        trackThatCore?.startServices()
//                        geofenceUtils?.startGeofenceMonitoring(hubLocationList)
//                    } else if (fromStop!!) {
//                        cancelAlarm(applicationContext)
//                        alarmData?.isStart = false
//                        val tripId = trackThatPrefs?.getString(Keys.Prefs.CURRENT_TRACKING_ID, "")
//                        alarmData?.currentTripId = tripId
//                        preferencesUtil?.save(Constants.RESTART, gson.toJson(alarmData))
//                        Log.e(TAG, "inside service------stop----->>$tripId")
//                        if (tripId != null && tripId != "") {
//                            saveToDB(EVENT.END, tripId, System.currentTimeMillis(), false, null)
//                        }
//                        completeTrip(true)
//                        preferencesUtil?.save(Constants.IS_START, false)
//                        preferencesUtil?.save(Constants.IS_END, false)
//                        trackThatCore?.stopServices()
//                        geofenceUtils?.stopGeofenceMonitoring()
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "exception inside start method: " + e.message)
//        }
//        return START_STICKY
//    }
//
//    private fun registerBoorReceiver() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            receiver = RestartAlarmsReceiver()
//            val bootIntentFilter = IntentFilter()
//            bootIntentFilter.addAction(RestartAlarmsReceiver.BOOT_COMPLETE)
//            bootIntentFilter.addAction(RestartAlarmsReceiver.QUICKBOOT_POWERON)
//            bootIntentFilter.addAction(RestartAlarmsReceiver.PROVIDERS_CHANGED)
//            registerReceiver(receiver, bootIntentFilter)
//        }
//    }
//
//    override fun onDestroy() {
//        cancelAlarm(applicationContext)
//        super.onDestroy()
//        handler = null
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
////        LocalBroadcastManager.getInstance(this).unregisterReceiver(resetGeofenceReceiver)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (receiver != null) {
//                unregisterReceiver(receiver)
//            }
//        }
//    }
//
//    private fun hitApi(list: List<ApiEventModel>, param: InteractorCallback) {
//        val apiModel = list[0]
//        var event: EVENT? = null
//        when {
//            apiModel.eventName == EVENT.IN -> event = EVENT.IN
//            apiModel.eventName == EVENT.OUT -> event = EVENT.OUT
//            apiModel.eventName == EVENT.END -> event = EVENT.END
//        }
//
//        pingApi(ping!!, apiModel, event!!, param)
//    }
//
// /*   private fun checkForSdkVersion() {
//        val oldVersion = trackThatPrefs?.getString(Keys.Prefs.SDK_CURRENT_VERSION, null)
//        val newVersion = trackThatPrefs?.getString(Keys.Prefs.SDK_NEW_VERSION, null)
//        if (oldVersion != null) {
//            if (oldVersion != newVersion) {
//                val newConfig = trackThatPrefs?.getString(Keys.Prefs.UPDATED_SDK_CONFIG, null)
//                if (newConfig != null) {
//                    val configResponse = gson.fromJson<ConfigResponse>(newConfig, ConfigResponse::class.java)
//                    trackThatCore?.init(configResponse)
//                    //update the current version with new version
//                    trackThatPrefs?.save(Keys.Prefs.SDK_CURRENT_VERSION, newVersion)
//                    Log.e(TAG, "checkForSdkVersion(): sdk config changed")
//                }
//            }
//        }
//    }*/
//
//    private fun stopAlarmService() {
//        Log.e(TAG, "Service Stops")
//        //remove preferences
//        preferencesUtil?.remove(Constants.IS_END)
//        preferencesUtil?.remove(Constants.IS_START)
//        handler?.removeCallbacks(this@AlarmService)
//        preferencesUtil?.remove(Constants.RESTART)
//        stopForeground(true)
//        stopSelf()
//    }
//
//    private fun saveToDB(eventName: EVENT, tripId: String, time: Long,
//                         createNewTrip: Boolean?, geofenceId: String?) {
//        Log.e(TAG, "Db check for save data inside event: " +
//                alarmDBHelper?.isTrackingIdAlreadyExist(tripId))
//        if (tripId.isNotEmpty() && (eventName == EVENT.END || !alarmDBHelper?.isTrackingIdAlreadyExist(tripId)!!)) {
//            Log.i(TAG, "Inside saveToDB() $eventName , $tripId , $time $geofenceId")
//            val mApiEventModel = ApiEventModel()
//            mApiEventModel.hubId = geofenceId
//            mApiEventModel.eventName = eventName
//            mApiEventModel.tripId = tripId
//            mApiEventModel.time = time
//            mApiEventModel.createNewTrip = createNewTrip
//            alarmDBHelper?.addPendingApiEvent(mApiEventModel)
//        } else {
//            Log.e(TAG, "Inside saveToDB() empty TripId.")
//        }
//    }
//
//    private fun completeTrip(stopSdkService: Boolean) {
//        trackThatCore?.changeState(TrackThatConstants.State.COMPLETED, stopSdkService)
//        trackThatPrefs?.save(Keys.Prefs.IS_TRACKING_LIVE_TRIP, false)
//    }
//
//    private fun startTrip(tripId: String?) {
//        //checkForSdkVersion()
//        Log.e(TAG, "Starting Trip...")
//        //set trip id for db reference.
//        trackThatPrefs?.save(Keys.Prefs.IS_TRACKING_LIVE_TRIP, true)
//        trackThatPrefs?.save(Keys.Prefs.TRIP_TYPE, TripType.ON_TRIP.name)
//        trackThatCore?.createTrackingId(false, tripId)
//        // start capturing location of user.
//        trackThatCore?.changeState(TrackThatConstants.State.START, false)
//    }
//
//    private fun pingApi(startApiUrl: TrackThatUrl, model: ApiEventModel?, event: EVENT,
//                        param: InteractorCallback) {
//        Log.e(TAG, "HUB ID: " + model?.hubId)
//        if (NetworkUtil.isNetworkAvailable(this)) {
//            val request = TripRequest()
//            if (model != null) {
//                request.time = model.time
//                request.tripId = model.tripId
//                request.event = event
//                request.hubId = model.hubId
//            }
//            TrackthatManager.getInstance().ping(
//                    httpManager, startApiUrl, request, param)
//        }
//    }
//
//    /**
//     * Method used to register alarm for every two
//     * minute to check if service is running or not.
//     *
//     * @param context context of application
//     */
//    private fun registerAlarmForEveryTwoMin(context: Context) {
//
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = System.currentTimeMillis()
//        calendar.add(Calendar.MINUTE, 2)
//
//        val restartService = Intent(context, RestartAlarmsReceiver::class.java)
//        restartService.action = RestartAlarmsReceiver.ACTION_RESTART_ALARM
//        val s = preferencesUtil?.getString(Constants.RESTART, null)
//        if (s != null) {
//            val alarm = Gson().fromJson(s, AlarmData::class.java)
//            if (alarm?.isStart != null) {
//                if (alarm.isStart!!) {
//                    restartService.putExtra("start", true)
//                } else {
//                    restartService.putExtra("stop", false)
//                }
//            } else {
//                restartService.putExtra("start", true)
//            }
//        } else {
//            restartService.putExtra("start", true)
//        }
//        val pendingIntent = PendingIntent.getBroadcast(context, 10320, restartService,
//                PendingIntent.FLAG_UPDATE_CURRENT)
//        val alarmMgr1 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmMgr1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
//                    pendingIntent)
//            Log.e(TAG, "Alarm set for AlarmService() at : " + calendar.timeInMillis)
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmMgr1.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//            Log.e(TAG, "Alarm set for AlarmService() at : " + calendar.timeInMillis)
//        } else {
//            alarmMgr1.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//            Log.e(TAG, "Alarm set for AlarmService() at : " + calendar.timeInMillis)
//        }
//    }
//
//    /**
//     * Cancel the already set alarm
//     *
//     * @param context application context
//     */
//    private fun cancelAlarm(context: Context) {
//        val intents = Intent(context, RestartAlarmsReceiver::class.java)
//        intents.action = RestartAlarmsReceiver.ACTION_RESTART_ALARM
//        val alarmMgr1 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val pendingIntent = PendingIntent.getBroadcast(context, 10320,
//                intents, PendingIntent.FLAG_UPDATE_CURRENT)
//        alarmMgr1.cancel(pendingIntent)
//    }
//
//    class TripRequest {
//        var tripId: String? = null
//        var time: Long? = null
//        var event: EVENT? = null
//        var hubId: String? = null
//    }
//
//    enum class EVENT {
//        IN, OUT, END
//    }
//}