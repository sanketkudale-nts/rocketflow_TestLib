package com.rocketflyer.rocketflow.ui.service//package com.tracki.ui.service
//
////import com.tracki.data.local.db.DatabaseHelper
//import android.annotation.TargetApi
//import android.app.*
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.os.Build
//import android.os.CountDownTimer
//import android.os.IBinder
//import androidx.core.app.NotificationCompat
//import com.tracki.R
//import com.tracki.data.local.prefs.PreferencesHelper
//import com.tracki.ui.receiver.ServiceRestartReceiver
//import com.tracki.utils.AppConstants
//import com.tracki.utils.CommonUtils
//import com.tracki.utils.Log
//import com.trackthat.lib.internal.common.Trunk
//import java.util.*
//import javax.inject.Inject
//
//
//class TimerService : Service() {
//
//
//    @Inject
//    lateinit var preferencesHelper: PreferencesHelper
//
//    var bi = Intent(ServiceRestartReceiver.ACTION_TIMER_TASK)
//    var cdt: CountDownTimer? = null
//    //private val databaseHelper: DatabaseHelper? = null
//    private var isRunning = false
//
//    override fun onDestroy() {
//        cdt!!.cancel()
//        cancelAlarm(applicationContext)
//        Log.i(TAG, "Timer cancelled")
//        super.onDestroy()
//    }
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        if (!isRunning) {
//            if (CommonUtils.isServiceRunningInForeground(this, this.javaClass.name)) {
//                startForeground(AppConstants.SERVICE_ID_3, createAndShowForegroundNotification("Countdown started"))
//            }
//            isRunning = true
//            //register alarm
//            registerAlarmForEveryTwoMin(applicationContext, 2)
//            Log.i(TAG, "Starting timer...")
//
//            cdt = object : CountDownTimer(10000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                    Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000)
//                    // update notification when this function called
//                    updateNotification("Countdown seconds remaining: " + millisUntilFinished / 1000)
//                }
//
//                override fun onFinish() {
//                    //cancel alarm
//                    cancelAlarm(applicationContext)
//                    //stop foreground service & stop service
//                    stopForeground(true)
//                    isRunning = false
//                    sendBroadcast(bi)
//                    Log.i(TAG, "Timer finished")
//                    stopSelf()
//                }
//            }
//            cdt!!.start()
//        }
//        return START_STICKY
//    }
//
//    override fun onBind(arg0: Intent): IBinder? {
//        return null
//    }
//
//    /**
//     * This is the method that can be called to update the Notification
//     */
//    private fun updateNotification(text: String) {
//
//        val notification = createAndShowForegroundNotification(text)
//
//        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        mNotificationManager.notify(AppConstants.SERVICE_ID_2, notification)
//
//        Trunk.i(TAG, this.javaClass.simpleName + " started in foreground")
//    }
//
//    private fun createAndShowForegroundNotification(text: String): Notification {
//
//        val builder = getNotificationBuilder(this, "com.tracki.notification:" + this.javaClass.simpleName)
//        builder.setOngoing(true)
//                .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
//                .setAutoCancel(false)
//                .setSmallIcon(R.mipmap.ic_notification)
//                .setColor(Color.BLACK)
//                .setContentTitle(this.getString(R.string.app_name))
//                .setContentText(text)
//
//        return builder.build()
//    }
//
//    private fun getNotificationBuilder(context: Context, channelId: String): NotificationCompat.Builder {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            prepareChannel(context, channelId)
//            NotificationCompat.Builder(context, channelId)
//        } else {
//            NotificationCompat.Builder(context)
//        }
//    }
//
//    @TargetApi(26)
//    private fun prepareChannel(context: Context, id: String) {
//        val appName = "TrackI"
//        val description = "notifications_channel_description1"
//        val nm = context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
//
//        var nChannel: NotificationChannel? = nm.getNotificationChannel(id)
//        if (nChannel == null) {
//            nChannel = NotificationChannel(id, appName, NotificationManager.IMPORTANCE_HIGH)
//            nChannel.description = description
//            nm.createNotificationChannel(nChannel)
//        }
//    }
//
//    /**
//     * Method used to register alarm for every two
//     * minute to check if service is running or not.
//     *
//     * @param context context of application
//     */
//    private fun registerAlarmForEveryTwoMin(context: Context, minute: Int) {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = System.currentTimeMillis()
//        calendar.add(Calendar.MINUTE, minute)
//
//        val pendingIntent = getAlarmPendingIntent(context)
//        val alarmMgr1 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
//                alarmMgr1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
//                        pendingIntent)
//                Log.e(TAG, "Alarm Set for TimerService: " + calendar.timeInMillis)
//            }
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
//                alarmMgr1.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//                Log.e(TAG, "Alarm Set for TimerService: " + calendar.timeInMillis)
//            }
//            else -> {
//                alarmMgr1.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//                Log.e(TAG, "Alarm Set for TimerService: " + calendar.timeInMillis)
//            }
//        }
//    }
//
//    /**
//     * Cancel the already set alarm.
//     * NOTE:- The pending intent to be canceled should be
//     * same as the original pending intent that was used to
//     * schedule alarm. The pending intent to be cancelled should
//     * have set to same action and same data fields,if any have
//     * those were used to set the alarm.
//     *
//     *
//     * Below is the command used to check already set alarm in devices:-
//     * 'adb shell dumpsys alarm'
//     * This command will show all the alarms set by this device for
//     * every application.
//     *
//     * @param context application context
//     */
//    fun cancelAlarm(context: Context) {
//        val pendingIntent = getAlarmPendingIntent(context)
//        val alarmMgr1 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmMgr1.cancel(pendingIntent)
//    }
//
//    /**
//     * Get the pending intent of the alarm data
//     *
//     * @param context context of class
//     * @return @[PendingIntent]
//     */
//    private fun getAlarmPendingIntent(context: Context): PendingIntent {
//        val intents = Intent(context, ServiceRestartReceiver::class.java)
//        intents.action = ServiceRestartReceiver.ACTION_TIMER_TASK
//        intents.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//        return PendingIntent.getBroadcast(context, 3893,
//                intents, PendingIntent.FLAG_UPDATE_CURRENT)
//    }
//
//    companion object {
//        private const val TAG = "BroadcastService"
//    }
//}