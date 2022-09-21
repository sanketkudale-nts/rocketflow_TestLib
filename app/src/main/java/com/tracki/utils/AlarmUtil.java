//package com.tracki.utils;
//
//import android.annotation.SuppressLint;
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.tracki.data.model.AlarmInfo;
//import com.trackthat.driver.ui.broadcast.AlarmReceiver;
//
//
//
//import java.util.Calendar;
//import java.util.HashMap;
//
///**
// * Created by rahul on 29/12/18
// */
//public class AlarmUtil {
//
//    private static final String TAG = "AlarmUtil";
//    private final PreferencesUtil preferencesUtil;
//    private Context context;
//    private int[] startAlarmArray = new int[]{0, 10, 20, 30, 40, 50, 60, 70};
//    private int[] stopAlarmArray = new int[]{0, 100, 200, 300, 400, 500, 600, 700};
//    private HashMap<Integer, ShiftTime> shiftMap;
//
//    @SuppressLint("UseSparseArrays")
//    public AlarmUtil(Context context) {
//        this.context = context;
//        preferencesUtil = PreferencesUtil.with(context);
//        String shiftMapString = preferencesUtil.getString(AppConstants.OLD_SHIFT_MAP, null);
//        if (shiftMapString != null) {
//            shiftMap = new Gson().fromJson(shiftMapString, new TypeToken<HashMap<Integer, ShiftTime>>() {
//            }.getType());
//        } else {
//            shiftMap = new HashMap<>();
//        }
//    }
//
//    public void setAlarmForStart(boolean isNext) {
//        Calendar calendar = Calendar.getInstance();
//        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
//        if (isNext) {
//            if (currentDay == Calendar.SATURDAY) {
//                currentDay = 1;
//            } else {
//                currentDay = currentDay + 1;
//            }
//        }
//
//        findNextDayAndSetAlarm(currentDay, true);
//    }
//
//    public void setAlarmForStop(boolean isNext) {
//        Calendar calendar = Calendar.getInstance();
//        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
//        if (isNext) {
//            if (currentDay == Calendar.SATURDAY) {
//                currentDay = 1;
//            } else {
//                currentDay = currentDay + 1;
//            }
//        }
//
//        findNextDayAndSetAlarm(currentDay, false);
//    }
//
//    /**
//     * Method used to find the current day and if current
//     * day is not in the shift map then find next day and set alarm.
//     *
//     * @param currentDay current day if not exists in shift map then next day will be here.
//     * @param isStart    alarm is start alarm or stop alarm.
//     */
//    private void findNextDayAndSetAlarm(int currentDay, boolean isStart) {
//        try {
//            if (shiftMap.size() > 0) {
//                if (shiftMap.containsKey(currentDay)) {
//                    ShiftTime shiftTime = shiftMap.get(currentDay);
//
//                    if (shiftTime != null) {
//                        String[] hourMinuteArray;
//                        if (isStart) {
//                            hourMinuteArray = Util.splitString(shiftTime.getFrom());
//                        } else {
//                            hourMinuteArray = Util.splitString(shiftTime.getTo());
//                        }
//                        int hour = Integer.parseInt(hourMinuteArray[0]);
//                        int minute = Integer.parseInt(hourMinuteArray[1]);
//                        int i;
//                        if (isStart) {
//                            i = startAlarmArray[currentDay];
//                            setAlarmForStart(context, hour, minute, currentDay, i);
//                        } else {
//                            i = stopAlarmArray[currentDay];
//                            setAlarmForStop(context, hour, minute, currentDay, i);
//                        }
//                    } else {
//                        Log.e(TAG, "Inside findNextDayAndSetAlarm(): Shift time cannot be null");
//                    }
//                } else {
//                    if ((currentDay == Calendar.SATURDAY)) {
//                        currentDay = 1;
//                    } else {
//                        currentDay += 1;
//                    }
//                    findNextDayAndSetAlarm(currentDay, isStart);
//                }
//            } else {
//                Log.e(TAG, "Inside findNextDayAndSetAlarm(): map size is 0");
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Exception inside catch block of findNextDayAndSetAlarm(): " + e);
//        }
//    }
//
//    /**
//     * Set start alarm.
//     *
//     * @param context     context of application
//     * @param hour        set hour for alarm
//     * @param minute      set minute for alarm
//     * @param dayOfWeek   set day of week for
//     *                    alarm but if dayOfWeek is less then
//     *                    current day then add days to set alarm
//     *                    for next week.
//     * @param requestCode request code for setting alarm pending intent
//     */
//    private void setAlarmForStart(Context context, int hour, int minute,
//                                  int dayOfWeek, int requestCode) {
//        Log.e(TAG, "----> " + hour + " <-----> " + minute + " <-----> " + dayOfWeek + " <-----> " +
//                requestCode);
//
//        AlarmInfo alarmInfo = new AlarmInfo();
//        alarmInfo.setCurrentDay(dayOfWeek);
//        alarmInfo.setHour(hour);
//        alarmInfo.setMinute(minute);
//        alarmInfo.setRequestCode(requestCode);
//        alarmInfo.setExecuted(false);
//
//        Calendar calendar = Calendar.getInstance();
//        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
//
//        if (dayOfWeek < currentDay) {
//            int noOfDays = (7 - currentDay) + dayOfWeek;
//            alarmInfo.setDayAdded(noOfDays);
//            calendar.add(Calendar.DATE, noOfDays);
//        }
//        if (dayOfWeek != 0) {
//            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
//        }
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        Log.e(TAG, "time--start---->>" + calendar.getTime());
//
//        AlarmManager alarmMgr1 = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Intent intents1 = new Intent(context, AlarmReceiver.class);
//        intents1.putExtra("start", true);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
//                intents1, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (alarmMgr1 != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmMgr1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                        pendingIntent);
//                alarmInfo.setAlarmSet(true);
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                alarmMgr1.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                alarmInfo.setAlarmSet(true);
//            } else {
//                alarmMgr1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                alarmInfo.setAlarmSet(true);
//            }
//            preferencesUtil.save(AppConstants.START_ALARM_INFO, new Gson().toJson(alarmInfo));
//        }
//        boolean alarmSet = PendingIntent.getBroadcast(context, requestCode, intents1, PendingIntent.FLAG_NO_CREATE) != null;
//        Log.e(TAG, requestCode + " <<RC--IS_SET>> " + alarmSet);
//    }
//
//    /**
//     * set stop alarm.
//     *
//     * @param context     context of application
//     * @param hour        set hour for alarm
//     * @param minute      set minute for alarm
//     * @param dayOfWeek   set day of week for
//     *                    alarm but if dayOfWeek is less then
//     *                    current day then add days to set alarm
//     *                    for next week.
//     * @param requestCode request code for setting alarm pending intent
//     */
//    private void setAlarmForStop(Context context, int hour, int minute,
//                                 int dayOfWeek, int requestCode) {
//        Log.e(TAG, "->> " + hour + " <-----> " + minute + " <-----> " + dayOfWeek + " <-----> " +
//                requestCode);
//
//        AlarmInfo alarmInfo = new AlarmInfo();
//        alarmInfo.setCurrentDay(dayOfWeek);
//        alarmInfo.setHour(hour);
//        alarmInfo.setMinute(minute);
//        alarmInfo.setRequestCode(requestCode);
//        alarmInfo.setExecuted(false);
//
//        Calendar calendar = Calendar.getInstance();
//        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
//
//        if (dayOfWeek < currentDay) {
//            int noOfDays = (7 - currentDay) + dayOfWeek;
//            alarmInfo.setDayAdded(noOfDays);
//            calendar.add(Calendar.DATE, noOfDays);
//        }
//        if (dayOfWeek != 0) {
//            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
//        }
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        Log.e(TAG, "time----stop time-->>" + calendar.getTime());
//
//        AlarmManager alarmMgr1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intents1 = new Intent(context, AlarmReceiver.class);
//        intents1.putExtra("stop", true);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
//                intents1, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (alarmMgr1 != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmMgr1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                        pendingIntent);
//                alarmInfo.setAlarmSet(true);
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                alarmMgr1.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                alarmInfo.setAlarmSet(true);
//            } else {
//                alarmMgr1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                alarmInfo.setAlarmSet(true);
//            }
//            preferencesUtil.save(AppConstants.STOP_ALARM_INFO, new Gson().toJson(alarmInfo));
//        }
//        boolean alarmSet = PendingIntent.getBroadcast(context, requestCode, intents1, PendingIntent.FLAG_NO_CREATE) != null;
//        Log.e(TAG, requestCode + " <<RC--IS_SET>> " + alarmSet);
//    }
//
//    /**
//     * Method used to cancel all alarms start ,stop,next start,next stop.
//     */
//    void cancelAllAlarm() {
//        try {
//            cancelCurrentAlarm();
//        } catch (Exception e) {
//            Log.e(TAG, "Exception inside catch block of cancelAllAlarm(): " + e);
//
//        }
//    }
//
//    /**
//     * Method used to cancel current alarm start and stop both.
//     */
//    private void cancelCurrentAlarm() {
//        String startAlarmString = preferencesUtil.getString(AppConstants.START_ALARM_INFO, null);
//        String endAlarmString = preferencesUtil.getString(AppConstants.STOP_ALARM_INFO, null);
//        Gson gson = new Gson();
//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        if (startAlarmString != null) {
//            AlarmInfo alarmInfo = gson.fromJson(startAlarmString, AlarmInfo.class);
//            if (alarmInfo.getRequestCode() != null) {
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmInfo.getRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                boolean alarmSet = PendingIntent.getBroadcast(context, alarmInfo.getRequestCode(), intent, PendingIntent.FLAG_NO_CREATE) != null;
//                if (alarmSet) {
//                    Log.e(TAG, "start alarm canceled with details " + alarmInfo.toString());
//                    am.cancel(pendingIntent);
//                }
//            }
//        }
//        if (endAlarmString != null) {
//            AlarmInfo endAlarmInfo = gson.fromJson(endAlarmString, AlarmInfo.class);
//            if (endAlarmInfo.getRequestCode() != null) {
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, endAlarmInfo.getRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                boolean alarmSet = PendingIntent.getBroadcast(context, endAlarmInfo.getRequestCode(), intent, PendingIntent.FLAG_NO_CREATE) != null;
//                if (alarmSet) {
//                    Log.e(TAG, "end alarm canceled with details " + endAlarmInfo.toString());
//                    am.cancel(pendingIntent);
//                }
//            }
//        }
//    }
//}
