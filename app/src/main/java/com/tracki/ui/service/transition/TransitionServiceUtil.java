package com.tracki.ui.service.transition;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.tracki.utils.Log;

import java.util.List;

/**
 * Created by rahul on 7/3/19
 */
class TransitionServiceUtil {
    private static final String TAG = TransitionServiceUtil.class.getSimpleName();

    static void handleTransitionResult(
            List<ActivityTransitionEvent> transitionEvents, Context applicationContext) {

        if (transitionEvents.isEmpty()) {
            Log.e(TAG, "Activity Transition Events has no result.");
            return;
        }

        onTransitionEventReceived(transitionEvents, applicationContext);
    }

    private static void onTransitionEventReceived(
            List<ActivityTransitionEvent> transitionEvents, Context applicationContext) {

        UserActivity activity = null;

        if (transitionEvents.size() > 1) {
            activity = new UserActivity(transitionEvents.get(1).getActivityType(),
                    100, transitionEvents.get(1).getTransitionType());
            Log.e(TAG, activity.toString());
        } else if (transitionEvents.size() == 1) {
            activity = new UserActivity(transitionEvents.get(0).getActivityType(),
                    100, transitionEvents.get(0).getTransitionType());
            Log.e(TAG, "else " + activity.toString());
        }

        Intent intent = new Intent(TransitionService.ACTION_TRANSITION);
        intent.putExtra(TransitionService.ACTIVITY_TRANSITION, activity);
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
    }
}
