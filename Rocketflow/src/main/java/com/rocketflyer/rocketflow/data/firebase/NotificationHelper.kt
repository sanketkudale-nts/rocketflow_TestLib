package com.rocketflyer.rocketflow.data.firebase

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.TaskStackBuilder
import com.tracki.R
import com.tracki.ui.feeddetails.FeedDetailsActivity
import com.tracki.ui.splash.SplashActivity
import com.tracki.utils.CommonUtils


/**
 * Helper class to manage notification channels, and create notifications.
 *
 * Created by rahul on 26/2/19
 */
@TargetApi(Build.VERSION_CODES.O)
internal class NotificationHelper
/**
 * Registers notification channels, which can be used later by individual notifications.
 * @param ctx The application context
 */
(ctx: Context) : ContextWrapper(ctx) {

    private val sound: Uri by lazy {
        return@lazy Uri.parse(
                "android.resource://" +
                        applicationContext.packageName +
                        "/" +
                        R.raw.close_door)
    }

    val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

    private val manager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            val chan1 = NotificationChannel(PRIMARY_CHANNEL,
                    getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_HIGH)

            chan1.lightColor = Color.GREEN
            chan1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            chan1.vibrationPattern = pattern
            chan1.setSound(sound, audioAttributes)
            // delete channel if any
            manager.deleteNotificationChannel(chan1.id)
            // create channel
            manager.createNotificationChannel(chan1)

            /*val chan2 = NotificationChannel(SECONDARY_CHANNEL,
                    getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH)
            chan2.lightColor = Color.BLUE
            chan2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            chan2.vibrationPattern = pattern
            chan2.setSound(sound, audioAttributes)
            // delete channel if any
            manager.deleteNotificationChannel(chan2.id)
            // create channel
            manager.createNotificationChannel(chan2)*/
        }
    }

    /**
     * Get a notification of type 1
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     * @param title the title of the notification
     * *
     * @param body the body text for the notification
     * *
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    fun getNotification1(title: String, body: String, whichClass: Int,customId:String?=null): Notification.Builder {
        val notification: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, PRIMARY_CHANNEL).setSound(sound, audioAttributes)
        } else {
            Notification.Builder(applicationContext)
                    .setSound(sound, audioAttributes)
                    .setPriority(Notification.PRIORITY_MAX)

        }

        val bigTextStyle = Notification.BigTextStyle()
        bigTextStyle.bigText(body)
        CommonUtils.showLogMessage("e", "textservice", body)

        return notification
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(smallIcon)
                .setColor(Color.BLACK)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent(whichClass,customId))
                .setStyle(bigTextStyle)
    }

    private fun getPendingIntent(value: Int,customId:String?=null): PendingIntent? {
        var resultIntent = Intent(this, SplashActivity::class.java)
        if (value == 0) {
            resultIntent = Intent(this, SplashActivity::class.java)
        } else if (value == 1) {
            resultIntent = Intent(this, SplashActivity::class.java)
        }
        else if (value == 2) {
            if(customId!=null)
            resultIntent = FeedDetailsActivity.getInstance(this,customId)
            else
                resultIntent = Intent(this, SplashActivity::class.java)
        }
        /*resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK*/

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        val stackBuilder = TaskStackBuilder.create(this)
        // All the parents of SecondActivity will be added to task stack.
//        stackBuilder.addParentStack(MainActivity::class.java)
        // Add a SecondActivity intent to the task stack.
        stackBuilder.addNextIntentWithParentStack(resultIntent)

        // Get the PendingIntent containing the entire back stack
        return stackBuilder.getPendingIntent(REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
    }

//    /**
//     * Build notification for secondary channel.
//     * @param title Title for notification.
//     * *
//     * @param body MessageResponse for notification.
//     * *
//     * @return A Notification.Builder configured with the selected channel and details
//     */
//    fun getNotification2(title: String, body: String, value: Int): Notification.Builder {
//        val notification: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Notification.Builder(applicationContext, SECONDARY_CHANNEL)
//        } else {
//            Notification.Builder(applicationContext)
//                    .setSound(sound, audioAttributes)
//                    .setPriority(Notification.PRIORITY_MAX)
////                    .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
//        }
//        val bigTextStyle = Notification.BigTextStyle()
////            bigTextStyle.setBigContentTitle(title)
//        bigTextStyle.bigText(body)
//        return notification
//                .setContentTitle(title)
//                .setContentText(body)
//                .setSmallIcon(smallIcon)
//                .setColor(Color.BLACK)
//                .setAutoCancel(true)
//                .setContentIntent(getPendingIntent(value))
//                .setStyle(bigTextStyle)
//    }

    /**
     * Send a notification.
     * @param id The ID of the notification
     * *
     * @param notification The notification object
     */
    fun notify(id: Int, notification: Notification.Builder) {
        manager.notify(id, notification.build())
    }

    /**
     * Get the small icon for this app
     * @return The small icon resource id
     */
    private val smallIcon: Int
        get() = R.mipmap.ic_launcher_foreground


    companion object {
        const val PRIMARY_CHANNEL = "default"
        const val SECONDARY_CHANNEL = "second"
        const val REQUEST_CODE = 120987621
    }
}