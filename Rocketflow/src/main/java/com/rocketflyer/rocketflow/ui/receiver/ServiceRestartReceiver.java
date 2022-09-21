package com.rocketflyer.rocketflow.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.tracki.TrackiApplication;
import com.tracki.data.firebase.NotificationHelper;
import com.tracki.data.firebase.TrackiFirebaseMessagingService;
import com.tracki.data.local.db.Action;
import com.tracki.data.local.db.ApiEventModel;
import com.tracki.data.local.db.DatabaseHelper;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.AlarmInfo;
import com.tracki.data.model.DataObject;
import com.tracki.data.model.request.ExecuteUpdateRequest;
import com.tracki.data.model.request.TaskData;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.CallToActions;
import com.tracki.data.model.response.config.DynamicFormData;
import com.tracki.data.model.response.config.Executor;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.TaskListing;
import com.tracki.data.model.response.config.TrackiTaskData;
import com.tracki.data.network.CacheManager;
import com.tracki.ui.service.TrackiWakeLock;
import com.tracki.ui.service.transition.TransitionService;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;
import com.tracki.utils.NetworkUtils;
import com.tracki.utils.ShiftHandlingUtil;
import com.tracki.utils.TaskStatus;
import com.trackthat.lib.TrackThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.tracki.utils.AppConstants.TRACK;

/**
 * This receiver is used to check for 2 cases:
 * 1. Internet connection:-If user have a valid internet connection
 * then check if data that needs to be synced else stop service.
 * <p>
 * 2. Restart Transition service:- After switch on the device check for
 * the transition flag if its true then then check for the service running in
 * foreground or not if running then set alarm else start service.
 * <p>
 * Created by rahul on 11/3/19
 */
public class ServiceRestartReceiver extends BroadcastReceiver {

    public static final String ACTION_RESTART_SERVICE = "com.tracki.action.RESTART_SERVICE";
    public static final String ACTION_INTERNET_CONNECTON = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String ACTION_TIMER_TASK = "com.tracki.action.COUNTDOWN_TIMER";
    public static final String ACTION_CALL_TO_ACTOIN_TIMER = "com.tracki.action.CALL_TO_ACTION_TIMER";
    public static final String ACTION_ALARM_BEFORE_X_MINUTE = "com.tracki.action.ALARM_BEFORE_X_MINUTE";
    public static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String QUICK_BOOT_COMPLETED = "android.intent.action.QUICKBOOT_POWERON";
    public static final String QUICK_BOOT_POWER_ON = "com.htc.intent.action.QUICKBOOT_POWERON";
    public static final String REBOOT = "android.intent.action.REBOOT";
    private static final String TAG = "ServiceRestartReceiver";

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            TrackiWakeLock.acquire(context);
            //android injection
            AndroidInjection.inject(this, context);

            if (intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_CALL_TO_ACTOIN_TIMER:
                        Map<String, Task> mapTask = preferencesHelper.getTimerCallToAction();
                        String taskid = intent.getStringExtra(AppConstants.TASK_ID);
                        if (mapTask != null && mapTask.containsKey(taskid)) {
                            Task task = mapTask.get(taskid);
                            if (task.getTaskId() != null) {
                                // handle task's call to action for the SYSTEM executor to execute timer based tasks.
                                if (task.getCurrentStage() != null) {
                                    List<CallToActions> callToActionList = task.getCurrentStage().getCallToActions();
                                    if (callToActionList != null && !callToActionList.isEmpty()) {
                                        // get the actions which are performed by users only.
                                        List<CallToActions> systemCallToActions = CommonUtils.extractCallToActionsWithTypeTask(callToActionList, Executor.SYSTEM, AppConstants.TIMED);
                                        TaskData taskData = new TaskData();
                                        List<TrackiTaskData> taskDataList = task.getTaskData();
                                        if (taskDataList!=null&&taskDataList.size() > 0) {
                                            List<DynamicFormData> dynamicFormDataList = taskDataList.get(0).getTaskData();
                                            taskData.setTaskData(dynamicFormDataList);
                                        }
                                       // Long upLoadedAt = taskDataList.get(0).getUploadedAt();

                                        String ctaId = null;
                                        String dfId = null;

                                        if (systemCallToActions.size() > 0 && systemCallToActions.get(0) != null &&
                                                systemCallToActions.get(0).getName() != null) {

                                            taskData.setCtaId(systemCallToActions.get(0).getId());
                                            if (systemCallToActions.get(0).getId() != null)
                                                ctaId = systemCallToActions.get(0).getId();

                                            if (systemCallToActions.get(0).getDynamicFormId() != null)
                                                dfId = systemCallToActions.get(0).getDynamicFormId();
                                            ExecuteUpdateRequest request = new ExecuteUpdateRequest(taskid,
                                                    ctaId, DateTimeUtil.getCurrentDateInMillis(), taskData);
                                            request.setDfId(dfId);
                                            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
                                            ApiEventModel cancelAction = new ApiEventModel();
                                            cancelAction.setAction(Action.EXECUTE_UPDATE);
                                            cancelAction.setTripId(taskid);
                                            cancelAction.setData(request);
                                            cancelAction.setTime(DateTimeUtil.getCurrentDateInMillis());

                                            //add the cancel task event into the database.
                                            databaseHelper.addPendingApiEvent(cancelAction);

                                            //start the service and delete the map entry if
                                            // service sync the item with the server.
                                            CommonUtils.checkSyncService(context);

                                            //send the broadcast when entry of cancel task is added into the database and
                                            //service is started for uploading the data.
//                                            LocalBroadcastManager.getInstance(context).sendBroadcast(
//                                                    new Intent(AppConstants.ACTION_REFRESH_TASK_LIST));
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case ACTION_RESTART_SERVICE:
                        boolean stopTrackingService = preferencesHelper.getTransitionServiceFlag();
                        if (!CommonUtils.isServiceRunningInForeground(context, TransitionService.class.getName())) {
                            Log.i(TAG, "Service is not running. Starting...");

                            Intent serviceIntent = new Intent(context.getApplicationContext(), TransitionService.class);
                            serviceIntent.putExtra(AppConstants.Extra.EXTRA_STOP_SERVICE, stopTrackingService);
                            ContextCompat.startForegroundService(context.getApplicationContext(), serviceIntent);
                        } else {
                            if (!stopTrackingService) {
                                Log.i(TAG, "Service is running already. Setting alarm for next 2 minute.");
                                TransitionService.registerAlarmForEveryTwoMin(context, 2);
                            } else {
                                TransitionService.cancelAlarm(context);
                                Log.e(TAG, "Service is running already. " +
                                        "Not setting alarm as stopTrackingService " +
                                        "flag is: true Canceling alarm");
                            }
                        }
                        break;
                    case ACTION_INTERNET_CONNECTON:
                        // CommonUtils.checkSyncService(context);
                        break;
                    case ACTION_TIMER_TASK:
                        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
                        ApiEventModel cancelAction = new ApiEventModel();
                        cancelAction.setAction(Action.CANCEL_TASK);
                        cancelAction.setTripId(preferencesHelper.getTrackingId());
                        cancelAction.setAutoCancel(true);
                        cancelAction.setTime(DateTimeUtil.getCurrentDateInMillis());

                        //add the cancel task event into the database.
                        databaseHelper.addPendingApiEvent(cancelAction);

                        //start the service
                        CommonUtils.checkSyncService(context);

                        //update the lists if user is offline
                        if (!NetworkUtils.isNetworkConnected(context)) {
                            //refresh task hashMap.
                            refreshTaskListWhenOffline(context, preferencesHelper.getTrackingId());
                        }

                        //clear taskId
                        preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.TASKID);
                        preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.CURRENT_TIME);

                        //send the broadcast when entry of cancel task is added into the database and
                        //service is started for uploading the data.
                        LocalBroadcastManager.getInstance(context).sendBroadcast(
                                new Intent(AppConstants.ACTION_REFRESH_TASK_LIST));
                        break;
                    case ACTION_ALARM_BEFORE_X_MINUTE:

                        //Get Map from preferences and remove current task details
                        HashMap<String, DataObject> taskDetails = preferencesHelper.getTaskDetails();
                        if (taskDetails != null) {
                            for (Map.Entry<String, DataObject> stringDataObjectEntry : taskDetails.entrySet()) {
                                if (((Map.Entry) stringDataObjectEntry).getKey() == intent.getStringExtra("taskId"))
                                    taskDetails.remove(((Map.Entry) stringDataObjectEntry).getKey().toString());
                            }

                            //Update preference with updated Map
                            preferencesHelper.setTaskDetails(taskDetails);
                        }

                        NotificationHelper helper = new NotificationHelper(context);
                        helper.notify(TrackiFirebaseMessagingService.NOTI_PRIMARY1,
                                helper.getNotification1(AppConstants.TASK_START_NOTI_TITLE,
                                        AppConstants.TASK_START_NOTI_BODY, 0,null));


                        break;
                    case QUICK_BOOT_COMPLETED:
                    case BOOT_COMPLETED:
                    case QUICK_BOOT_POWER_ON:
                    case REBOOT:
                        //Get task details for pending alarm tasks and set alarm for them
                        HashMap<String, DataObject> map = preferencesHelper.getTaskDetails();
                        if (map != null) {
                            for (Map.Entry<String, DataObject> stringLongEntry : map.entrySet()) {
                                String taskId = String.valueOf(((Map.Entry) stringLongEntry).getKey());
                                DataObject dataObject = stringLongEntry.getValue();
                                if (System.currentTimeMillis() < dataObject.getStartTime() - 1800000) {
                                    CommonUtils.registerAlarm(dataObject.getStartTime() - 1800000,
                                            context.getApplicationContext(),
                                            ServiceRestartReceiver.ACTION_ALARM_BEFORE_X_MINUTE, dataObject.getRequestCode(), taskId);
                                }
                            }
                        }
                        break;
                    case TRACK:
                        Log.e(TAG, "onReceive: ------TRACK ACTION---- ");
                        if (intent.getBooleanExtra(AppConstants.Extra.START, false)) {
                            Log.e(TAG, "onReceive: ------start true---- ");
                            if (!TrackThat.isTracking())
                                TrackThat.startTracking(null, false);
                            AlarmInfo startAlarmInfo = preferencesHelper.getStartAlarmInfo();
                            if (startAlarmInfo != null) {
                                startAlarmInfo.setExecuted(true);
                                preferencesHelper.setStartAlarmInfo(startAlarmInfo);
                            }
                        } else if (intent.getBooleanExtra(AppConstants.Extra.STOP, false)) {
                            Log.e(TAG, "onReceive: ------End true---- ");
                            if (TrackThat.isTracking())
                                TrackThat.stopTracking();
                            AlarmInfo stopAlarmInfo = preferencesHelper.getStopAlarmInfo();
                            if (stopAlarmInfo != null) {
                                stopAlarmInfo.setExecuted(true);
                                preferencesHelper.setStopAlarmInfo(stopAlarmInfo);
                            }
                            ShiftHandlingUtil.manageAlarm(context, preferencesHelper);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception occur inside ServiceRestartReceiver:onReceive(): " + e.getMessage());
        } finally {
            TrackiWakeLock.release();
        }
    }


    /**
     * Method used to refresh the task-hashMap.
     *
     * @param taskId item that need to be replaced.
     */
    private void refreshTaskListWhenOffline(Context context, String taskId) {
        Api api = TrackiApplication.getApiMap().get(ApiType.TASKS);
        if (api != null) {
            api.setAppendWithKey("ASSIGNED_TO_ME");
        }
        CacheManager cacheManager = CacheManager.Companion.getInstance(context);
        //refresh hashMap
        String apiCachedData = cacheManager.getFromCache(api.getName().name() + "_" + api.getAppendWithKey(), api.getVersion());
        Log.e(TAG, apiCachedData);
        if (apiCachedData != null) {
            TaskListing taskListing = new Gson().fromJson(apiCachedData, TaskListing.class);
            List<Task> taskList = taskListing.getTasks();
            if (taskList != null && taskList.size() > 0) {
                for (int i = 0; i < taskList.size(); i++) {
                    if (taskId.equals(taskList.get(i).getTaskId())) {
                        taskList.get(i).setStatus(TaskStatus.CANCELLED);
                        break;
                    }
                }
                //update cache with new model item
                cacheManager.putIntoCache(api.getName().name() + "_" + api.getAppendWithKey(), new Gson().toJson(taskListing), api.getVersion());
            }
        }
    }
}
