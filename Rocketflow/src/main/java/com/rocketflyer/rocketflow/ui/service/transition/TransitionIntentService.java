package com.rocketflyer.rocketflow.ui.service.transition;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.tracki.data.local.db.DatabaseHelper;
import com.tracki.data.local.db.ITransitionTable;
import com.tracki.utils.Log;

/**
 * Created by rahul on 7/3/19
 */
public class TransitionIntentService extends IntentService {
    public static String TAG = TransitionIntentService.class.getSimpleName();
    private ITransitionTable alarmDBHelper;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * name Used to name the worker thread, important only for debugging.
     */
    public TransitionIntentService() {
        super("TransitionIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmDBHelper = DatabaseHelper.getInstance(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Log.i(TAG, "Activity Transition Intent: Received");
            if (ActivityTransitionResult.hasResult(intent)) {
                Log.i(TAG, "Activity Transition Intent: Has Result");
                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                if (result != null && result.getTransitionEvents() != null) {
                    Log.i(TAG, "Activity Transition Event Count: " +
                            result.getTransitionEvents().size());
                    for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                        if (event != null) {
                            final UserActivity userActivity = new UserActivity(event.getActivityType(),
                                    100, event.getTransitionType());
                            //  chronological sequence of events....
                            Log.i(TAG, "Activity Transition Events: " +
                                    userActivity.getActivityString() + " is " + userActivity.getTransition());
                            alarmDBHelper.addTransition(userActivity);
                        }
                    }
                    TransitionServiceUtil.handleTransitionResult(
                            result.getTransitionEvents(), getApplicationContext());
                }
            } else {
                Log.i(TAG, "Activity Transition Intent: No Result");
            }
        } catch (Exception e) {
            Log.e(TAG, "Inside OnHandle: " + e);
        }
    }
}
