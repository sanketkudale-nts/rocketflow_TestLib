package com.tracki.utils;

import com.tracki.BuildConfig;

/**
 * Created by rahul on 5/9/18
 */
public final class Log {
    private Log() {
        //cannot be instantiated.
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.i(tag, message);
        }
    }
    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.w(tag, message);
        }
    }
}
