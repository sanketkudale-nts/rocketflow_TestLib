package com.rocketflyer.rocketflow.ui.service.transition;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.gson.Gson;
import com.tracki.data.DataManager;
import com.tracki.data.local.db.Action;
import com.tracki.data.local.db.ApiEventModel;
import com.tracki.data.local.db.DatabaseHelper;
import com.tracki.data.local.db.IAlarmTable;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.request.CreateTaskRequest;
import com.tracki.data.model.request.EndAutoTaskRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.data.model.response.config.Place;
import com.tracki.data.model.response.config.ProfileInfo;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.TaskListing;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.CacheManager;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.receiver.ServiceRestartReceiver;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;
import com.tracki.utils.NetworkUtils;
import com.tracki.utils.TaskStatus;
import com.tracki.utils.TrackiToast;
import com.trackthat.lib.TrackThat;
import com.trackthat.lib.internal.network.TrackThatCallback;
import com.trackthat.lib.models.ErrorResponse;
import com.trackthat.lib.models.SuccessResponse;
import com.trackthat.lib.models.TrackthatLocation;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;

import static com.tracki.utils.AppConstants.REQUEST_CODE_RESTART_TRANSITION_SERVICE;

/**
 * Class @{@link TransitionService} used to monitor user activity transition
 * and start trip if user transition is detected as walk.
 * <p>
 * Created by rahul on 7/3/19
 */
public class TransitionService extends IntentService {

    public static final String ACTIVITY_TRANSITION = "activity_transition";
    public static final String ACTION_TRANSITION = "com.tracki.Action.Transition";
    private static final String TAG = TransitionService.class.getSimpleName();
    @Inject
    HttpManager httpManager;
    @Inject
    DataManager dataManager;
    @Inject
    PreferencesHelper preferencesHelper;
    @Inject
    ActivityTransitionRequest request;
    @Inject
    @Named("transitionPendingIntent")
    PendingIntent pendingIntent;

    private IAlarmTable alarmDBHelper;
    private ApiEventModel endRequest, createRequest;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.e(TAG, intent.toString());
                if (intent.hasExtra(TransitionService.ACTIVITY_TRANSITION)) {
                    UserActivity activity = intent.getParcelableExtra(TransitionService.ACTIVITY_TRANSITION);
                    //Whenever we detect user transition as Drive with enter then
                    //start tracking if user is not not tracking is live.
                    if (activity.isEnter()) {
                        if ((activity.type == UserActivity.ActivityType.DRIVE) ||
                                (activity.type == UserActivity.ActivityType.RUN) ||
                                (activity.type == UserActivity.ActivityType.CYCLE)) {

                            Log.i(TAG, "Inside Transition Receiver: Activity Enter detected for " + activity.type);

                            starts();

                            //if walk is detected when user's auto start is
                            // true then stop tracking.
                        } else if (activity.type == UserActivity.ActivityType.WALK) {
                            end();
                        }
                    } else if (activity.isExit()) {
                        if (activity.type == UserActivity.ActivityType.DRIVE ||
                                activity.type == UserActivity.ActivityType.RUN ||
                                activity.type == UserActivity.ActivityType.CYCLE) {
                            end();
                        }
                    }

                  /*  if (activity.isEnter() && activity.type == UserActivity.ActivityType.WALK) {
                        startLocationService();
                        *//*if (!TrackThat.isTracking() && !isAutoStart) {
                            Log.e(TAG, "Walk is Enter detected.Starting process...");
                            String trackingId = UUID.randomUUID().toString();
                            Log.e(TAG, "Generated TrackingId: " + trackingId);
                            isAutoStart = true;
                            preferencesHelper.setTrackingId(trackingId);
                            TrackThat.startTracking(trackingId, true);
                            createTask(trackingId);
                        }*//*
                        //if walk is detected when user's auto start is
                        // true then stop tracking.
                    } else if (activity.isExit() && activity.type == UserActivity.ActivityType.WALK) {
                        if (TrackThat.isTracking() && isAutoStart) {
                            Log.e(TAG, "Walk is Exit detected.Stopping process...");
                            isAutoStart = false;
                            TrackThat.stopTracking();
                            endTask(preferencesHelper.getTrackingId());
                        }
                    }*/
                }
            }
        }
    };

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * name Used to name the worker thread, important only for debugging.
     */
    public TransitionService() {
        super(TransitionService.class.getSimpleName());
    }

    /**
     * Method used to register alarm for every two
     * minute to check if service is running or not.
     *
     * @param context context of application
     */
    public static void registerAlarmForEveryTwoMin(Context context, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, minute);

        PendingIntent pendingIntent = getAlarmPendingIntent(context);
        AlarmManager alarmMgr1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr1.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    pendingIntent);
            Log.e(TAG, "Alarm Set for TransitionService: " + calendar.getTimeInMillis());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr1.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.e(TAG, "Alarm Set for TransitionService: " + calendar.getTimeInMillis());
        } else {
            alarmMgr1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.e(TAG, "Alarm Set for TransitionService: " + calendar.getTimeInMillis());
        }
    }

    /**
     * Cancel the already set alarm.
     * NOTE:- The pending intent to be canceled should be
     * same as the original pending intent that was used to
     * schedule alarm. The pending intent to be cancelled should
     * have set to same action and same data fields,if any have
     * those were used to set the alarm.
     * <p>
     * Below is the command used to check already set alarm in devices:-
     * 'adb shell dumpsys alarm'
     * This command will show all the alarms set by this device for
     * every application.
     *
     * @param context application context
     */
    public static void cancelAlarm(Context context) {
        PendingIntent pendingIntent = getAlarmPendingIntent(context);
        AlarmManager alarmMgr1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr1.cancel(pendingIntent);
    }

    /**
     * Get the pending intent of the alarm data
     *
     * @param context context of class
     * @return @{@link PendingIntent}
     */
    private static PendingIntent getAlarmPendingIntent(Context context) {
        Intent intents = new Intent(context, ServiceRestartReceiver.class);
        intents.setAction(ServiceRestartReceiver.ACTION_RESTART_SERVICE);
        intents.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        return PendingIntent.getBroadcast(context, REQUEST_CODE_RESTART_TRANSITION_SERVICE,
                intents, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        try {
            alarmDBHelper = DatabaseHelper.getInstance(getApplicationContext());

            /*mContext = getApplicationContext();
            First = FirstSensor = true;
            currentValues = new float[3];
            prevValues = new float[3];
            currentVelocity = new float[3];
            speed = new float[3];
            totalDistance = 0.0F;
            Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//used for measuring the physical position of a device.
            //mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mLinearAccelertion = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

            sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            //sensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, mLinearAccelertion, SensorManager.SENSOR_DELAY_NORMAL);*/


            //Register or UnRegister your broadcast receiver here
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                    new IntentFilter(TransitionService.ACTION_TRANSITION));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        try {
            // if service is not running in foreground then run it else leave it as it is.
            if (!CommonUtils.isServiceRunningInForeground(this, this.getClass().getName())) {
                CommonUtils.createAndShowForegroundNotification(this, AppConstants.SERVICE_ID_1);
            }
            // get intent to check if user wants to stop the service.
            if (intent != null && intent.hasExtra(AppConstants.Extra.EXTRA_STOP_SERVICE)) {
                preferencesHelper.setTransitionServiceFlag(
                        intent.getBooleanExtra(AppConstants.Extra.EXTRA_STOP_SERVICE, false));
            }
            if (!preferencesHelper.getTransitionServiceFlag()) {
                //register alarm
                registerAlarmForEveryTwoMin(getApplicationContext(), 2);
                //request for activity updates
                requestActivityTransitionUpdates();
            } else {
                //cancel alarm
                cancelAlarm(getApplicationContext());
                //remove activity updates
                removeActivityTransitionUpdates();
                //stop tracking if tracking is on.
                if (TrackThat.isTracking()) {
                    TrackThat.stopTracking();
                }
                stopForeground(true);
                stopSelf();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception inside buildTransitionRequest(): " + e);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        try {
            /*sensorManager.unregisterListener(this);*/

            cancelAlarm(getApplicationContext());
            //Register or UnRegister your broadcast receiver here
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
            //remove activity transition update
            removeActivityTransitionUpdates();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception inside onDestroy(): " + e);
        } finally {
            super.onDestroy();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG, "onTaskRemoved() called-----");
        if (!preferencesHelper.getTransitionServiceFlag()) {
            registerAlarmForEveryTwoMin(getApplicationContext(), 1);
        }
    }

    /**
     * Method used to request for Activity transition updates.
     */
    private void requestActivityTransitionUpdates() {
        try {

            Log.i(TAG, "Setting Task to request Activity Transition Updates: ");
            /*Task<Void> task = */
            ActivityRecognition.getClient(getApplicationContext()).requestActivityTransitionUpdates(request, pendingIntent)
                    .addOnSuccessListener(aVoid -> Log.e(TAG, "Request Activity Transition Updates: Successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Request Activity Transition Updates: Failure :" + e));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception inside requestActivityTransitionUpdates(): " + e.getMessage());
        }
    }

    /**
     * Method used to remove Activity transition updates.
     */
    private void removeActivityTransitionUpdates() {
        try {
            Log.e(TAG, "Setting Task to Remove Activity Transition Updates: ");

            ActivityRecognition.getClient(getApplicationContext())
                    .removeActivityTransitionUpdates(pendingIntent)
                    .addOnSuccessListener(result -> Log.e(TAG, "Remove Activity Transition Updates: Successfully "))
                    .addOnFailureListener(e -> Log.e(TAG, "Remove Activity Transition Updates: Failure: " + e));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception inside removeActivityTransitionUpdates(): " + e.getMessage());
        }
    }

    /**
     * Method used to create task when activity is detected.
     *
     * @param trackingId tracking id.
     */
    private void createTask(final String trackingId) {
        try {
            ProfileInfo userDetails = preferencesHelper.getUserDetail();
            if (userDetails != null && userDetails.getName() != null) {
                Log.i(TAG, "inside user detail condition");
                TrackThatCallback thatCallback = new TrackThatCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse successResponse) {
                        TrackthatLocation loc = (TrackthatLocation) successResponse.getResponseObject();
                        if (loc != null) {
                            Log.i(TAG, "CreateTask: Got the location....");
                            GeoCoordinates coordinates = new GeoCoordinates();
                            coordinates.setLatitude(loc.getLatitude());
                            coordinates.setLongitude(loc.getLongitude());

                            Place source = new Place();
                            source.setLocation(coordinates);
                            source.setAddress(TrackThat.getAddress(loc.getLatitude(), loc.getLongitude()));
                            Log.e(TAG, "trackingId: " + trackingId);
                            CreateTaskRequest createTaskRequest = new CreateTaskRequest(true, "",
                                    "", userDetails.getName(), "", new Place(), source,
                                    System.currentTimeMillis(), "Auto Start Trip at " +
                                    DateTimeUtil.getFormattedTime(System.currentTimeMillis(), DateTimeUtil.DATE_TIME_FORMAT_2),
                                    trackingId, 0, null, null);

                            Api api = preferencesHelper.getApi(ApiType.CREATE_TASK);

                            createRequest = new ApiEventModel();
                            createRequest.setAction(Action.CREATE_TASK);
                            createRequest.setTripId(trackingId);
                            createRequest.setData(createTaskRequest);
                            createRequest.setTime(DateTimeUtil.getCurrentDateInMillis());
                            createRequest.setAutoStart(true);

                            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                                new CreateTask(createTaskRequest, api).hitApi();
                            } else {
                                alarmDBHelper.addPendingApiEvent(createRequest);
                            }

                        } else {
                            Log.e(TAG, "CreteTask: Location is null..again getting loc:--");
                            TrackThat.getCurrentLocation(this);
                        }
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        Log.e(TAG, "Exception inside createTask(): onError(): " + errorResponse.getErrorMessage());
                    }
                };
                //get start(current) location
                TrackThat.getCurrentLocation(thatCallback);
            } else {
                Log.e(TAG, "Error inside CreateTask(): username is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception inside CreateTask(): " + e);
        }
    }

    /**
     * Method used to run if the task is in offline state and
     * get hashMap form the cache and update the hashMap with the current
     * status of the trip and again save into the cache.
     *
     * @param api        api model
     * @param trackingId tracking id of the trip
     */
    private void updateTaskList(Api api, String trackingId) {
        try {
            if (api.getName() != null && api.getName().name() != null && api.getVersion() != null) {
                CacheManager cacheManager = CacheManager.Companion.getInstance(getApplicationContext());
                String apiCachedData = cacheManager.getFromCache(api.getName().name() + "_" + api.getAppendWithKey(), api.getVersion());
                if (apiCachedData != null) {
                    TaskListing taskListing = new Gson().fromJson(apiCachedData, TaskListing.class);
                    List<Task> taskList = taskListing.getTasks();
                    if (taskList != null && taskList.size() > 0) {
                        for (int i = 0; i < taskList.size(); i++) {
                            if (trackingId.equals(taskList.get(i).getTaskId())) {
                                Task t = taskList.get(i);
                                t.setStatus(TaskStatus.COMPLETED);
                                break;
                            }
                        }
                        TaskListing taskListing1 = new TaskListing();
                        taskListing1.setTasks(taskList);
                        //update cache with new model item
                        cacheManager.putIntoCache(api.getName().name() + "_" + api.getAppendWithKey(), new Gson().toJson(taskListing1), api.getVersion());
                    }
                }
            } else {
                Log.e(TAG, "Cannot update cache: api object null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception inside updateTaskList(): " + e.getMessage());
        }
    }

    /**
     * End the trip whenever user activity drive exit or walk
     * enter detected.
     */
    private void end() {
        Log.e(TAG, "end() autoStart pref val: " + preferencesHelper.getAutoStartFlag());
        if (preferencesHelper.getAutoStartFlag() && TrackThat.isTracking()) {
            preferencesHelper.setAutoStartFlag(false);
            TrackThat.stopTracking();
            endTask(preferencesHelper.getTrackingId());
        }
    }

    /**
     * Start checking the user trip and create task if drive is detected.
     */
    private void starts() {
        Log.e(TAG, "start() autoStart pref val: " + preferencesHelper.getAutoStartFlag());
        if (!TrackThat.isTracking() && !preferencesHelper.getAutoStartFlag()) {
            String trackingId = UUID.randomUUID().toString();
            //save tracking id
            preferencesHelper.setTrackingId(trackingId);
            preferencesHelper.setAutoStartFlag(true);
            //start tracking
            TrackThat.startTracking(trackingId, true);
            createTask(trackingId);
        }
    }

    /**
     * Method used to get the current location and hit the required api.
     *
     * @param trackingId tracking id of trip
     */
    private void endTask(String trackingId) {
        try {
            ProfileInfo userDetails = preferencesHelper.getUserDetail();
            if (userDetails != null) {
                Place destination = new Place();
                if (TrackThat.isLastLocationAvailable() && TrackThat.getLastLocation() != null) {
                    TrackthatLocation loc = TrackThat.getLastLocation();
                    GeoCoordinates dest = new GeoCoordinates();
                    dest.setLatitude(loc.getLatitude());
                    dest.setLongitude(loc.getLongitude());

                    destination.setLocation(dest);
                    destination.setAddress(TrackThat.getAddress(loc.getLatitude(), loc.getLongitude()));
                    Log.e(TAG, "End Location is: " + loc.getLatitude() + " "
                            + loc.getLongitude() + " " + destination.getAddress());
                }

                EndAutoTaskRequest endAutoTaskRequest = new EndAutoTaskRequest(trackingId, destination, DateTimeUtil.getCurrentDateInMillis());
                Api api = preferencesHelper.getApi(ApiType.END_TASK);
                if (api != null) {
                    api.setAppendWithKey("ASSIGNED_TO_ME");

                    endRequest = new ApiEventModel();
                    endRequest.setAction(Action.END_TASK);
                    endRequest.setTripId(trackingId);
                    endRequest.setData(endAutoTaskRequest);
                    endRequest.setTime(DateTimeUtil.getCurrentDateInMillis());
                    endRequest.setAutoStart(true);
                    endRequest.setAutoEnd(true);

                    if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                        new EndTask(endAutoTaskRequest, api).hitApi();
                    } else {
                        alarmDBHelper.addPendingApiEvent(endRequest);
                        //update the task into the cache if present.
                        updateTaskList(api, trackingId);
                        endRequest = null;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception inside endTask(): " + e);
        }
    }

    /**
     * Class used to hit the api and receive the callback
     */
    public class EndTask implements ApiCallback {
        private EndAutoTaskRequest endAutoTaskRequest;
        private Api api;

        EndTask(EndAutoTaskRequest endAutoTaskRequest, Api api) {
            this.endAutoTaskRequest = endAutoTaskRequest;
            this.api = api;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            if (CommonUtils.handleResponse(this, error, result, getApplicationContext())) {
                String s = "Task end Successfully";
                TrackiToast.Message.showShort(getApplicationContext(), s);
                Log.i(TAG, s);
            } else {
                if (endRequest != null) {
                    Log.i(TAG, "Error Inside end Api...saving in to database");
                    alarmDBHelper.addPendingApiEvent(endRequest);
                }
            }
        }

        @Override
        public void hitApi() {
            dataManager.endTask(this, httpManager, endAutoTaskRequest, api);
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public void onNetworkErrorClose() {

        }

        @Override
        public void onRequestTimeOut(ApiCallback callBack) {

        }

        @Override
        public void onLogout() {

        }
    }

    /**
     * Class used to hit the api and receive the callback
     */
    public class CreateTask implements ApiCallback {
        private CreateTaskRequest createTaskRequest;
        private Api api;

        CreateTask(CreateTaskRequest createTaskRequest, Api api) {
            this.createTaskRequest = createTaskRequest;
            this.api = api;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            if (CommonUtils.handleResponse(this, error, result, getApplicationContext())) {
                String s = "Task created Successfully";
                TrackiToast.Message.showShort(getApplicationContext(), s);
                Log.i(TAG, s);
            } else {
                if (createRequest != null) {
                    Log.i(TAG, "Error Inside create task Api...saving in to database");
                    alarmDBHelper.addPendingApiEvent(createRequest);
                }
            }
        }

        @Override
        public void hitApi() {
            Log.i(TAG, "Hit api for create task");
            dataManager.createTask(this, httpManager, createTaskRequest, api);
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public void onNetworkErrorClose() {

        }

        @Override
        public void onRequestTimeOut(ApiCallback callBack) {

        }

        @Override
        public void onLogout() {

        }
    }

    //    private int maxTimeInSec = 10;
//    private ArrayList<Float> speedList;
//    private FusedLocationProviderClient mFusedLocationClient;
//    private Handler handler;
//    /**
//     * Stores parameters for requests to the FusedLocationProviderApi.
//     */
//    private LocationRequest mLocationRequest;
//    private LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            if (locationResult != null) {
//                for (Location location : locationResult.getLocations()) {
//                    //If user register for speed check the notify it.
//                    if (location != null) {
//                        speedList.add(location.getSpeed());
//                    }
//                }
//            }
//        }
//    };
//    @Override
//    public void run() {
//        try {
//            //if hashMap is not null && speed size is greater then 10 and less
//            // then and equal to 2 minute then find the avg speed.
//            if (speedList != null) {
//                if (speedList.size() >= 10) {
//                    int totalSpeed = 0;
//                    for (int i = 0; i < speedList.size(); i++) {
//                        //convert m/sec to km/hr
//                        int sp = TrackThatUtils.metersPerSecToKmPerHr(speedList.get(i));
//                        totalSpeed += sp;
//                    }
//                    Log.e(TAG, "Total speed is: " + totalSpeed);
//                    int avgSpeed = totalSpeed / speedList.size();
//                    Log.e(TAG, "Avg. speed is: " + avgSpeed);
//                    //check if avg speed > 8 kmph then create task else reset service
//                    if (avgSpeed > 5) {
//                        Log.e(TAG, "Speed is greater then 5 kmph, Creating Task... ");
//                        starts();
//                    } else {
//                        if (speedList.size() >= maxTimeInSec) {
//                            isAutoStart = false;
//                            //reset activity transition updates.
//                            removeActivityTransitionUpdates();
//                            //request again for activity transition updates.
//                            requestActivityTransitionUpdates();
//                            // stop location and empty unused variables.
//                            stopLocationService();
//                        } else {
//                            if (handler != null) {
//                                handler.postDelayed(this, 5000);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "Exception inside speed run() " + e.getMessage());
//        }
//    }
//    /**
//     * Start location service to double check if user is
//     * really driving by monitoring speed.
//     */
//    @SuppressLint("MissingPermission")
//    private void startLocationService() {
//        try {
//            handler = new Handler();
//            speedList = new ArrayList<>();
//            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//
//            //Create Location Request object
//            createLocationRequest();
//
//            // Check if Location is enabled
//            if (TrackThatUtils.isLocationEnabled(getApplicationContext())) {
//
//                // Check if Location Permission is available
//                if (!TrackThatUtils.isLocationPermissionAvailable(getApplicationContext())) {
//                    Log.e(TAG, "Location Permission unavailable, " +
//                            "startLocationPolling failed");
//                    return;
//                }
//                com.google.android.gms.tasks.Task<Void> task = mFusedLocationClient.requestLocationUpdates(
//                        mLocationRequest, mLocationCallback, null);
//                task.addOnSuccessListener(aVoid -> {
//                    Log.i(TAG, "FusedLocation Updates Initiated for Transition Service!");
//                    handler.post(TransitionService.this);
//                    Log.i(TAG, "Handler starts for Transition Service!");
//                });
//            } else {
//                Log.e(TAG, "Could not initiate startLocationPolling: " +
//                        "locationEnabled: " +
//                        TrackThatUtils.isLocationEnabled(getApplicationContext()));
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Exception occurred while startLocationPolling: " +
//                    e.getMessage());
//        }
//    }
//
//    /**
//     * Stop location service when user complete it.
//     */
//    private void stopLocationService() {
//        try {
//            com.google.android.gms.tasks.Task<Void> task = mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//            task.addOnSuccessListener(aVoid -> {
//                Log.i(TAG, "FusedLocation Updates Initiated for Transition Service!");
//                handler.removeCallbacks(TransitionService.this);
//                Log.i(TAG, "Handler starts for Transition Service!");
//                mFusedLocationClient = null;
//                mLocationRequest = null;
//                speedList = null;
//                handler = null;
//            });
//
//        } catch (Exception e) {
//            Log.e(TAG, "Exception occurred while stopLocationPolling: " + e);
//        }
//    }
//
//    /**
//     * Sets up the location request. Android has two location request settings:
//     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
//     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
//     * the AndroidManifest.xml.
//     * <p/>
//     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
//     * interval (5 seconds), the Fused Location Provider API returns location updates that are
//     * accurate to within a few feet.
//     * <p/>
//     * These settings are appropriate for mapping applications that show real-time location
//     * updates.
//     */
//    private void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//
//        mLocationRequest.setInterval(1000);
//
//        // Sets the fastest rate for active location updates. This interval is exact, and your
//        // application will never receive updates faster than this value.
//        mLocationRequest.setFastestInterval(1000);
//        /*If priority is high then use high accuracy else balance power*/
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        // Sets the maximum time when batched location updates are delivered. Updates may be
//        // delivered sooner than this interval.
//        mLocationRequest.setMaxWaitTime(2000);
//        mLocationRequest.setSmallestDisplacement(3L);
//    }
}