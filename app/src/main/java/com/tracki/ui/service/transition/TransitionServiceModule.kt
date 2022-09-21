package com.tracki.ui.service.transition

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.tracki.utils.AppConstants
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by rahul on 11/3/19
 */
@Module
class TransitionServiceModule {

    /**
     * Provide transitions request
     */
    @Provides
    fun provideTransitionsRequest(): ActivityTransitionRequest {
        val activities = listOf(DetectedActivity.IN_VEHICLE,
                DetectedActivity.ON_BICYCLE,
                DetectedActivity.WALKING,
                DetectedActivity.RUNNING,
                DetectedActivity.STILL)
        val transitions = mutableListOf<ActivityTransition>()
        activities.forEach { activity ->
            val enterTransition = ActivityTransition.Builder()
                    .setActivityType(activity)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()

            val exitTransition = ActivityTransition.Builder()
                    .setActivityType(activity)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build()

            transitions.add(enterTransition)
            transitions.add(exitTransition)
        }
        return ActivityTransitionRequest(transitions)
    }

    /**
     * Callback for Transition events
     */
    @Provides
    @Named("transitionPendingIntent")
    fun provideTransitionPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context.applicationContext, TransitionIntentService::class.java)
        return PendingIntent.getService(context.applicationContext, AppConstants.REQUEST_CODE_ACTIVITY_RECOGNITION,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

//    @Module
//    companion object {
//        /**
//         * Get the pending intent of the alarm data
//         *
//         * @param context context of class
//         * @return @[PendingIntent]
//         */
//        @JvmStatic
//        @Provides
//        @Named("alarmPendingIntent")
//        fun getAlarmPendingIntent(context: Context): PendingIntent {
//            val intents = Intent(context, ServiceRestartReceiver::class.java)
//            intents.action = ServiceRestartReceiver.ACTION_RESTART_SERVICE
//            intents.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//            return PendingIntent.getBroadcast(context, AppConstants.REQUEST_CODE_RESTART_TRANSITION_SERVICE,
//                    intents, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
//    }


}
