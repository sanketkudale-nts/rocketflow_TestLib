package com.tracki.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.gson.Gson;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.AlarmInfo;
import com.tracki.ui.receiver.ServiceRestartReceiver;

import java.util.Calendar;

import static com.tracki.utils.AppConstants.TRACK;

class NewAlarmUtil {

    private static final String TAG = "NewAlarmUtil";
    private Context context;
    private PreferencesHelper preferencesHelper;
    private int[] startAlarmArray = new int[]{0, 10, 20, 30, 40, 50, 60, 70};
    private int[] stopAlarmArray = new int[]{0, 100, 200, 300, 400, 500, 600, 700};

    @SuppressLint("UseSparseArrays")
    NewAlarmUtil(Context context, PreferencesHelper preferencesHelper) {
        this.context = context;
        this.preferencesHelper = preferencesHelper;
    }

    /**
     * Method used to set the start alarm for the given day.
     *
     * @param dayOfWeek it will be current day but if current day not found then next day will be passed.
     * @param hour      time in hour for setting alarm
     * @param minute    time in minute for setting alarm
     */
    void setStartAlarm(int dayOfWeek, int hour, int minute) {
        try {

            int requestCode = startAlarmArray[dayOfWeek];

            AlarmInfo alarmInfo = new AlarmInfo();
            alarmInfo.setCurrentDay(dayOfWeek);
            alarmInfo.setHour(hour);
            alarmInfo.setMinute(minute);
            alarmInfo.setRequestCode(requestCode);
            alarmInfo.setExecuted(false);

            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek < currentDay) {
                int noOfDays = (7 - currentDay) + dayOfWeek;
                alarmInfo.setDayAdded(noOfDays);
                calendar.add(Calendar.DATE, noOfDays);
            }
            if (dayOfWeek != 0) {
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Log.e(TAG, "time--start---->>" + calendar.getTime());

            AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ServiceRestartReceiver.class);
            intent.putExtra(AppConstants.Extra.START, true);
            intent.setAction(TRACK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            pendingIntent);
                    alarmInfo.setAlarmSet(true);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmInfo.setAlarmSet(true);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmInfo.setAlarmSet(true);
                }
                preferencesHelper.setStartAlarmInfo(alarmInfo);
            }
            boolean alarmSet = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null;
            Log.e(TAG, requestCode + " <----requestcode-----Alarm set for start time------alarmset----> " + alarmSet);

        } catch (Exception e) {
            Log.e(TAG, "Exception inside setStartAlarm(): " + e);
        }
    }

    /**
     * Method used to set the stop alarm for the day (current or next).
     *
     * @param dayOfWeek it would be current or next day.
     * @param hour      hour to set alarm
     * @param minute    minute to set alarm
     */
    void setStopAlarm(int dayOfWeek, int hour, int minute) {
        try {

            int requestCode = stopAlarmArray[dayOfWeek];

            AlarmInfo alarmInfo = new AlarmInfo();
            alarmInfo.setCurrentDay(dayOfWeek);
            alarmInfo.setHour(hour);
            alarmInfo.setMinute(minute);
            alarmInfo.setRequestCode(requestCode);
            alarmInfo.setExecuted(false);

            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek < currentDay) {
                int noOfDays = (7 - currentDay) + dayOfWeek;
                alarmInfo.setDayAdded(noOfDays);
                calendar.add(Calendar.DATE, noOfDays);
            }
            if (dayOfWeek != 0) {
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Log.e(TAG, "time----stop time-->>" + calendar.getTime());

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ServiceRestartReceiver.class);
            intent.putExtra(AppConstants.Extra.STOP, true);
            intent.setAction(TRACK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            pendingIntent);
                    alarmInfo.setAlarmSet(true);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmInfo.setAlarmSet(true);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmInfo.setAlarmSet(true);
                }

                preferencesHelper.setStopAlarmInfo(alarmInfo);
            }
            boolean alarmSet = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null;
            Log.e(TAG, requestCode + " <----requestcode-----Alarm set for end time------alarmset----> " + alarmSet);

        } catch (Exception e) {
            Log.e(TAG, "Exception inside setStopAlarm(): " + e);
        }
    }


//    public Intent getStartPendingIntent() {
//
//    }

    /**
     * Method used to cancel all the alarms.
     */
    void cancelAlarms() {
        try {
            Gson gson = new Gson();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (preferencesHelper.getStartAlarmInfo() != null) {
                String startAlarmString = gson.toJson(preferencesHelper.getStartAlarmInfo());
                if (startAlarmString != null) {
                    AlarmInfo alarmInfo = gson.fromJson(startAlarmString, AlarmInfo.class);
                    if (alarmInfo.getRequestCode() != null) {
                        Intent intent = new Intent(context, ServiceRestartReceiver.class);
                        intent.putExtra(AppConstants.Extra.START, true);
                        intent.setAction(TRACK);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                alarmInfo.getRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        boolean alarmSet = PendingIntent.getBroadcast(context, alarmInfo.getRequestCode(),
                                intent, PendingIntent.FLAG_NO_CREATE) != null;
                        if (alarmSet) {
                            Log.e(TAG, "start alarm canceled with details " + alarmInfo.toString());
                            alarmManager.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }
                    }
                }
            }
            if (preferencesHelper.getStopAlarmInfo() != null) {
                String endAlarmString = gson.toJson(preferencesHelper.getStopAlarmInfo());
                if (endAlarmString != null) {
                    AlarmInfo endAlarmInfo = gson.fromJson(endAlarmString, AlarmInfo.class);
                    if (endAlarmInfo.getRequestCode() != null) {
                        Intent intent = new Intent(context, ServiceRestartReceiver.class);
                        intent.putExtra(AppConstants.Extra.STOP, true);
                        intent.setAction(TRACK);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, endAlarmInfo.getRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        boolean alarmSet = PendingIntent.getBroadcast(context, endAlarmInfo.getRequestCode(), intent, PendingIntent.FLAG_NO_CREATE) != null;
                        if (alarmSet) {
                            Log.e(TAG, "end alarm canceled with details " + endAlarmInfo.toString());
                            alarmManager.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }
                    }
                }
            }

            Log.e(TAG, "Alarms are cancelled");
        } catch (Exception e) {
            Log.e(TAG, "Exception inside cancelAlarms(): " + e);
        }
    }
}
