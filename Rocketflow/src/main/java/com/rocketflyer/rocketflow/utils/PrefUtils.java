package com.rocketflyer.rocketflow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tracki.utils.AppConstants;


public class PrefUtils {

    public static final PrefUtils prefUtils = new PrefUtils();
    public static SharedPreferences myPrefs = null;

    public static PrefUtils getInstance(Context context) {
        if (null == myPrefs)
            myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefUtils;
    }

    public void saveSecretKey(String value) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString(AppConstants.SECRET_KEY, value);
        editor.commit();
    }

    public String getSecretKey() {
        return myPrefs.getString(AppConstants.SECRET_KEY, null);
    }
}
