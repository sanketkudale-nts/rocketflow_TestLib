package com.tracki.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.AlarmInfo;
import com.trackthat.lib.TrackThat;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShiftHandlingUtil {

    private static final String TAG = "ShiftHandlingUtil";

    /**
     * Method used to manage the shift timing of the user and set the alarm of the matched shift
     *
     * @param context         context of calling class.
     * @param preferencesUtil preferences
     */
    public static void manageAlarm(Context context, PreferencesHelper preferencesUtil) {
        try {
            Map<Integer, List<ShiftTime>> oldShiftMap = preferencesUtil.getOldShiftMap();
            //if old map is not null that means user saved alarm before

            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

            if (oldShiftMap != null && oldShiftMap.size() > 0) {
                NewAlarmUtil alarmUtil = new NewAlarmUtil(context, preferencesUtil);
                handleDayAndAlarm(alarmUtil, oldShiftMap, currentDay, false);
            } else {
                Log.e(TAG, "Inside manageAlarm(): map size is 0");
            }


        } catch (Exception e) {
            Log.e(TAG, "Error inside manageAlarm(): " + e);
        }

    }


    /**
     * This function is used to handle current day's or next day's shift and set alarm.
     *
     * @param newAlarmUtil class for setting the alarms if any shift found
     * @param oldShiftMap  shift map
     * @param currentDay   current day
     * @param isNextDay    true if next day else false
     */
    private static void handleDayAndAlarm(NewAlarmUtil newAlarmUtil,
                                          Map<Integer, List<ShiftTime>> oldShiftMap,
                                          int currentDay, boolean isNextDay) {
        if (oldShiftMap.containsKey(currentDay)) {
            //I will get here a list of ShiftTime
            List<ShiftTime> shiftTimes = oldShiftMap.get(currentDay);
            //get the current shift or upcoming shift.
            ShiftTime shiftTime = checkForShiftTime(shiftTimes, isNextDay);

            if (shiftTime != null) {
                String[] startHourMinuteArray = splitString(shiftTime.getFrom());
                String[] endHourMinuteArray = splitString(shiftTime.getTo());

                int startHour = Integer.parseInt(startHourMinuteArray[0]);
                int startMinute = Integer.parseInt(startHourMinuteArray[1]);

                int endHour = Integer.parseInt(endHourMinuteArray[0]);
                int endMinute = Integer.parseInt(endHourMinuteArray[1]);

                newAlarmUtil.cancelAlarms();
                newAlarmUtil.setStartAlarm(currentDay, startHour, startMinute);
                newAlarmUtil.setStopAlarm(currentDay, endHour, endMinute);

                /* // if current day is same day then we need to check
                // about the timings else we just need to cancel the
                // next alarm and set new next alarm.
               if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == currentDay) {

                } else {
                    newAlarmUtil.cancelAlarms();
                    newAlarmUtil.setStartAlarm(currentDay, startHour, startMinute);
                    newAlarmUtil.setStopAlarm(currentDay, endHour, endMinute);
                }

                //case for before start time
                if (currentHour < startHour || (currentHour == startHour && currentMinute < startMinute)) {
                    //cancel next alarm and start new next alarm
                    alarmUtil.cancelAllAlarm();
                    alarmUtil.setAlarmForStart(false);
                    alarmUtil.setAlarmForStop(false);
                    Log.e(TAG, "Current alarm set for new timings");
                } else *//*Case for After end time*//*if (currentHour > endHour || (currentHour == endHour && currentMinute > endMinute)) {
                    //cancel next alarm and start new next alarm
                    alarmUtil.cancelAllAlarm();
                    alarmUtil.setAlarmForStart(true);
                    alarmUtil.setAlarmForStop(true);
                    Log.e(TAG, "Next alarm set for new timings");
                } else *//*Case for between start and end and alarm is not started.*//*
                    if (((currentHour == startHour && currentMinute == startMinute) ||
                            (currentHour == startHour && currentMinute > startMinute) ||
                            currentHour > startHour)) {
                        alarmUtil.setAlarmForStart(false);
                        alarmUtil.setAlarmForStop(false);
                        serviceStarted(context, preferencesUtil);
                        Log.e(TAG, "Start and End alarm set");
                    } else {
                        alarmUtil.setAlarmForStart(false);
                        alarmUtil.setAlarmForStop(false);
                        Log.e(TAG, "else part ---> Start and End alarm set");
                    }*/
            } else {
                //If shift time is null,No shift found or all shifts got completed for this day
                int day = getNextDay(currentDay);
                handleDayAndAlarm(newAlarmUtil, oldShiftMap, day, true);
            }
        } else {
            //If current day not present in the map then go to next day and vise versa.
            int day = getNextDay(currentDay);
            handleDayAndAlarm(newAlarmUtil, oldShiftMap, day, true);
        }
    }

    /**
     * Get the next day.
     *
     * @param currentDay current day
     * @return next day
     */
    private static int getNextDay(int currentDay) {
        Log.e(TAG, "current Day before change: " + currentDay);
        if ((currentDay == Calendar.SATURDAY)) {
            currentDay = 1;
        } else {
            currentDay += 1;
        }
        Log.e(TAG, "current Day after change: " + currentDay);
        return currentDay;
    }

    /**
     * Method is used to check for the current time and
     * match if shift is passed ,ongoing or upcoming return
     * that shift and perform operations on it.
     *
     * @param shiftTimeList list of today shift timings
     * @param isNextDay     if day is next day then get the first item from the list and return
     * @return current shift time.
     */
    public static ShiftTime checkForShiftTime(List<ShiftTime> shiftTimeList, boolean isNextDay) {
        ShiftTime shift = null;
        if (shiftTimeList != null && shiftTimeList.size() > 0) {
            //if it is next day then get the item from list at position 0 else
            // iterate through the list for current shift.
            if (isNextDay) {
                shift = shiftTimeList.get(0);
            } else {
                for (int i = 0; i < shiftTimeList.size(); i++) {
                    ShiftTime shiftTime = shiftTimeList.get(i);
                    if (shiftTime != null) {
                        String startTime = shiftTime.getFrom();
                        String endTime = shiftTime.getTo();
                        if (isFallingInCurrentTime(startTime, endTime)) {
                            shift = shiftTime;
                            Log.i(TAG, "Current Shift Time: " + shiftTime.toString());
                            break;
                        }
                        Log.i(TAG, "ShiftTime Not in use: " + shiftTime.toString());
                    }
                }
            }
        }
        return shift;
    }

    /**
     * Method used to check if current timing falls under any shifting.
     *
     * @param startTime start time of shift
     * @param endTime   end time of shift
     * @return true if current time fall under this shift or before this shift else false
     */
    private static boolean isFallingInCurrentTime(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        String[] startSplit = splitString(startTime);
        int startHour = Integer.parseInt(startSplit[0]);
        int startMinute = Integer.parseInt(startSplit[1]);

        String[] endSplit = splitString(endTime);
        int endHour = Integer.parseInt(endSplit[0]);
        int endMinute = Integer.parseInt(endSplit[1]);

        if (currentHour > startHour && currentHour < endHour) {
            return true;
        } else if (currentHour == startHour) {
            if (currentMinute >= startMinute && currentMinute < endMinute) {
                return true;

            } else return currentMinute < startMinute; // this shift considered as next shift timing
        } else if (currentHour < startHour) {
            return true;
        } else if (currentHour == endHour) {
            return currentMinute < endMinute;
        }
        return false;
    }

    /**
     * Check if alarms has been set for current day or next day.
     * 1. Alarms are set
     * 2. Service running
     * 3. Is there any change in Shift or Hub
     *
     * @param context           context of calling class
     * @param preferencesHelper preferences
     */
    static void checkAlarms(Context context, PreferencesHelper preferencesHelper) {
        boolean isMapChanged = preferencesHelper.getIsMapChanged();
        boolean isHubChanged = preferencesHelper.getIsHubChanged();
        String isSDKChanged = preferencesHelper.getUpdatedSdkConfig();

        //if any of the value is true that means there is a change in hub,shift or config.
        if (isMapChanged || isHubChanged || isSDKChanged != null) {
            //stop tracking
            if (TrackThat.isTracking()) {
                TrackThat.stopTracking();
            }
            //set alarms for matched shift
            manageAlarm(context, preferencesHelper);
            return;
        }

        // if alarms are not set for current day's shift timing then set alarms else check for service only
        if (!checkIfAlarmsSet(preferencesHelper)) {
            //set alarms for matched shift
            manageAlarm(context, preferencesHelper);
//            return;
        }
    }

    private static boolean checkIfAlarmsSet(PreferencesHelper preferencesHelper) {
        Map<Integer, List<ShiftTime>> oldShiftMap = preferencesHelper.getOldShiftMap();
        //if old map is not null that means user saved alarm before

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        if (oldShiftMap != null && oldShiftMap.size() > 0) {

            if (oldShiftMap.containsKey(currentDay)) {
                //I will get here a list of ShiftTime
                List<ShiftTime> shiftTimes = oldShiftMap.get(currentDay);
                //get the current shift or upcoming shift.
                ShiftTime shiftTime = checkForShiftTime(shiftTimes, false);

                if (shiftTime != null) {
                    String[] startHourMinuteArray = splitString(shiftTime.getFrom());
                    String[] endHourMinuteArray = splitString(shiftTime.getTo());

                    int startHour = Integer.parseInt(startHourMinuteArray[0]);
                    int startMinute = Integer.parseInt(startHourMinuteArray[1]);

                    int endHour = Integer.parseInt(endHourMinuteArray[0]);
                    int endMinute = Integer.parseInt(endHourMinuteArray[1]);


                    Gson gson = new Gson();
                    AlarmInfo startAlarmInfo = null;
                    AlarmInfo stopAlarmInfo = null;

                    startAlarmInfo = preferencesHelper.getStartAlarmInfo();

                    stopAlarmInfo = preferencesHelper.getStopAlarmInfo();

                    if (startAlarmInfo != null && stopAlarmInfo != null) {
                        return startAlarmInfo.getHour() == startHour &&
                                startAlarmInfo.getMinute() == startMinute &&
                                stopAlarmInfo.getHour() == endHour &&
                                stopAlarmInfo.getMinute() == endMinute;
                    }
                }
            }
        }

        return false;
    }

    static void setShiftTiming(PreferencesHelper preferencesHelper, Map<Integer, List<ShiftTime>> newShiftTime) {
//        Gson gson = new Gson();
        //if old map is not null that means user saved alarm before
        if (preferencesHelper.getOldShiftMap() != null) {
            Map<Integer, List<ShiftTime>> oldShiftMap = preferencesHelper.getOldShiftMap();
            //if user map is changed from server then save value in new map
            if (isMapChanged(oldShiftMap, newShiftTime)) {
                Log.e(TAG, "Shift map is changed now");
                preferencesHelper.setIsMapChanged(true);
                preferencesHelper.setOldShiftMap(newShiftTime);
//                preferencesHelper.save(AppConstants.NEW_SHIFT_MAP, gson.toJson(newShiftTime));
            } else {
                Log.e(TAG, "No change in shift map");
                preferencesHelper.setIsMapChanged(false);
//                preferencesHelper.remove(AppConstants.NEW_SHIFT_MAP);
            }
        } else {
            preferencesHelper.setOldShiftMap(newShiftTime);
        }
    }

    private static boolean isMapChanged(Map<Integer, List<ShiftTime>> oldShiftMap,
                                        Map<Integer, List<ShiftTime>> newShiftMap) {
        boolean isKeysChanged;

        if (oldShiftMap != null && newShiftMap != null) {
            isKeysChanged = checkIfMapKeysChanged(oldShiftMap.keySet(), newShiftMap.keySet());
            //if keys are not changed then check if their values are changed.
            if (!isKeysChanged) {
                for (int oldKey : oldShiftMap.keySet()) {
                    try {
                        List<ShiftTime> oldShift = oldShiftMap.get(oldKey);
                        List<ShiftTime> newShift = newShiftMap.get(oldKey);
                        //if any of the list is null the there is a change in map
                        if (oldShift == null || newShift == null) {
                            return true;
                        }
                        // if size are same then check for the list values and compare
                        if (oldShift.size() == newShift.size()) {
                            for (int i = 0; i < oldShift.size(); i++) {
                                String oldFrom = oldShift.get(i).getFrom();
                                String oldTo = oldShift.get(i).getTo();

                                for (int j = 0; j < newShift.size(); j++) {
                                    String newFrom = newShift.get(i).getFrom();
                                    String newTo = newShift.get(i).getTo();

                                    boolean isStartTimeChange = compareTime(splitString(oldFrom), splitString(newFrom));
                                    boolean isEndTimeChange = compareTime(splitString(oldTo), splitString(newTo));
                                    if (isStartTimeChange || isEndTimeChange) {
                                        return true;
                                    }
                                }
                            }
                        } else {
                            return true;
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Shift time is :" + oldShiftMap + "," + newShiftMap);
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private static boolean checkIfMapKeysChanged(Set<Integer> oldMapKeys, Set<Integer> newMapKeys) {
        try {
            // if old map contains all keys of new map and new map contains all keys of old map
            // then return false else return true that map keys changed
            return !(oldMapKeys.containsAll(newMapKeys) && newMapKeys.containsAll(oldMapKeys));
        } catch (NullPointerException e) {
            Log.e(TAG, "Inside checkIfMapKeysChanged() " + e);
        }
        return false;
    }

    private static boolean compareTime(String[] oldTime, String[] newTime) {
        return !(oldTime[0].equals(newTime[0]) && oldTime[1].equals(newTime[1]));
    }

    public static String[] splitString(@NonNull String value) {
        return value.split(":");
    }

}


