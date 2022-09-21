package com.tracki.ui.service;

import android.content.Context;
import android.os.PowerManager;

import com.tracki.utils.Log;

/**
 * Created by rahul on 6/5/19
 */
public final class TrackiWakeLock {
    private static final String WAKE_LOCK_TAG = "com.tracki:TrackiWakeLock";
    private static final String TAG = "TrackiWakeLock";
    private static PowerManager.WakeLock wakeLock;
    private static long time = 1000;

    public static void acquire(Context c) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, WAKE_LOCK_TAG);
        Log.i(TAG, "Lock acquired");
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) {
            wakeLock.release();
            Log.i(TAG, "Lock released");
        }
        wakeLock = null;
    }
}
