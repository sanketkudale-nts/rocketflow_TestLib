package com.tracki.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tracki.data.model.AlarmInfo;
import com.tracki.data.model.DataObject;
import com.tracki.data.model.request.SaveFilterData;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.ChannelConfig;
import com.tracki.data.model.response.config.EmergencyContact;
import com.tracki.data.model.response.config.Flavour;
import com.tracki.data.model.response.config.FormData;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.data.model.response.config.GeoFenceData;
import com.tracki.data.model.response.config.IdleTrackingInfo;
import com.tracki.data.model.response.config.OverstoppingConfig;
import com.tracki.data.model.response.config.ProfileInfo;
import com.tracki.data.model.response.config.RoleConfigData;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.WorkFlowCategories;
import com.tracki.di.PreferenceInfo;
import com.tracki.ui.addplace.Hub;
import com.tracki.ui.login.UserAccount;
import com.tracki.ui.selectorder.CatalogProduct;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.ShiftTime;
import com.tracki.utils.UserType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.tracki.utils.AppConstants.ALWAYS_NEW_SHIFT_MAP;
import static com.tracki.utils.AppConstants.CONFIG_CHANNEL_MAP;
import static com.tracki.utils.AppConstants.HEARTBEAT;
import static com.tracki.utils.AppConstants.IS_HUB_CHANGED;
import static com.tracki.utils.AppConstants.IS_MAP_CHANGED;
import static com.tracki.utils.AppConstants.OLD_SHIFT_MAP;
import static com.tracki.utils.AppConstants.PREF_KEY_FLAVOUR_MAP;


/**
 * Created by rahul.
 */

public class AppPreferencesHelper implements PreferencesHelper {

    public static final String PREF_KEY_GEOFENCE_IDS = "PREF_KEY_GEOFENCE_IDS";
    public static final String PREF_KEY_TASK = "PREF_KEY_TASK";
    private static final String PREF_KEY_LOGIN_TOKEN = "PREF_KEY_LOGIN_TOKEN";
    private static final String PREF_KEY_VERIFICATION_ID = "PREF_KEY_VERIFICATION_ID";
    private static final String PREF_KEY_DEVICE_ID = "PREF_KEY_DEVICE_ID";
    private static final String PREF_KEY_ACCESS_ID = "PREF_KEY_ACCESS_ID";
    private static final String PREF_KEY_FCM_TOKEN = "PREF_KEY_FCM_TOKEN";
    private static final String PREF_KEY_USER_INFO = "PREF_KEY_USER_INFO";
    private static final String PREF_KEY_INTRO = "PREF_KEY_INTRO";
    private static final String PREF_KEY_FIRST_TIME_INSTALL = "PREF_KEY_FIRST_TIME_INSTALL";
    private static final String PREF_KEY_EMERGENCY_CONTACTS = "PREF_KEY_EMERGENCY_CONTACTS";
    private static final String PREF_KEY_TRACKING_ID = "PREF_KEY_TRACKING_ID";
    private static final String PREF_KEY_FLAG = "PREF_KEY_SERVICE_FLAG";
    private static final String PREF_KEY_CREATE_TASK_API = "PREF_KEY_CREATE_TASK";
    private static final String PREF_KEY_END_TASK_API = "PREF_KEY_END_TASK";
    private static final String PREF_KEY_API_MAP = "PREF_KEY_API_MAP";
    private static final String PREF_KEY_AUTO_START_FLAG = "PREF_KEY_AUTO_START_FLAG";
    private static final String PREF_KEY_CHAT_SERVER_URL = "PREF_KEY_CHAT_SERVER_URL";
    private static final String PREF_KEY_ALLOW_ARRIVAL = "PREF_KEY_ALLOW_ARRIVAL";
    private static final String PREF_KEY_ALLOW_ARRIVAL_ON_GEOFENCE = "PREF_KEY_ALLOW_ARRIVAL_ON_GEOFENCE";
    private static final String PREF_KEY_AUTO_CANCEL_TASK_VALUE = "PREF_KEY_AUTO_CANCEL_TASK_VALUE";
    private static final String PREF_KEY_TIME = "PREF_KEY_TIME";
    private static final String PREF_KEY_RADIUS = "PREF_KEY_RADIUS";
    private static final String PREF_KEY_APP_AUTO_START = "PREF_KEY_APP_AUTO_START";
    private static final String PREF_KEY_PUNCH_STATUS = "PREF_KEY_PUNCH_STATUS";
    private static final String PREF_KEY_PUNCH_IN_TIME = "PREF_KEY_PUNCH_IN_TIME";
    private static final String PREF_KEY_PUNCH_OUT_TIME = "PREF_KEY_PUNCH_OUT_TIME";
    private static final String PREF_KEY_LAST_PUNCH_OUT_TIME = "PREF_KEY_LAST_PUNCH_OUT_TIME";
    private static final String PREF_KEY_TIME_PUNCHED_IN = "PREF_KEY_TIME_PUNCHED_IN";
    private static final String PREF_KEY_SELFIE_URL = "PREF_KEY_SELFIE_URL";
    private static final String PREF_KEY_EDIT_DATA = "PREF_KEY_EDIT_DATA";
    private static final String PREF_KEY_TASK_DETAILS = "PREF_KEY_TASK_DETAILS";
    private static final String PREF_KEY_TIMER_TASK_TIME = "PREF_KEY_TIMER_TASK_TIME";
    private static final String PREF_KEY_IDLE_TRIP = "PREF_KEY_IDLE_TRIP";
    private static final String PREF_KEY_LIVE_TRIP = "PREF_KEY_LIVE_TRIP";
    private static final String PREF_KEY_ACTIVE_TASK_ID = "PREF_KEY_ACTIVE_TASK_ID";
    private static final String PREF_KEY_ACTIVE_TASK_CATEGORY_ID = "PREF_KEY_ACTIVE_TASK_CATEGORY_ID";
    private static final String PREF_KEY_IS_FLEET_AND_BUDDY_SHOW = "PREF_KEY_IS_FLEET_AND_BUDDY_SHOW";
    private static final String PREF_KEY_IS_LAST_PUNCH_ID = "PREF_KEY_IS_LAST_PUNCH_ID";
    private static final String PREF_KEY_IS_VOICE_ALERTS = "PREF_KEY_IS_VOICE_ALERTS";
    private static final String PREF_KEY_IS_MANAGER = "PREF_KEY_IS_MANAGER";
    private static final String PREF_KEY_IS_TIME_REMINDER = "PREF_KEY_IS_TIME_REMINDER";
    private static final String PREF_KEY_IS_LOCATION_REMINDER = "PREF_KEY_IS_LOCATION_REMINDER";
    private static final String PREF_KEY_IS_WALLET_ENABLE = "PREF_KEY_IS_WALLET_ENABLE";
    private static final String PREF_UPDATED_SDK_CONFIG
            = "PREF_UPDATED_SDK_CONFIG";
    private static final String PREF_IDLE_TRIP_FLAG = "PREF_IDLE_TRIP_FLAG";
    private static final String PREF_IS_MAP_CHANGED = "PREF_IS_MAP_CHANGED";
    private static final String PREF_IS_HUB_CHANGED = "PREF_IS_HUB_CHANGED";
    private static final String PREF_KEY_WORKFLOW_CATEGORIES = "PREF_KEY_WORKFLOW_CATEGORIES";
    private static final String PREF_KEY_FLAVOR = "PREF_KEY_FLAVOR";
    private static final String PREF_KEY_USER_TYPE = "PREF_KEY_USER_TYPE";
    private static final String PREF_KEY_USER_ONLINE_OFFLINE_STATUS = "PREF_KEY_USER_ONLINE_OFFLINE_STATUS";
    private static final String PREF_KEY_USER_ACCOUNTS = "PREF_KEY_USER_ACCOUNTS";
    private static final String PREF_KEY_USER_GEO_FENCE = "PREF_KEY_USER_GEO_FENCE";
    private static final String PREF_KEY_USER_TYPE_LIST = "PREF_KEY_USER_TYPE_LIST";
    private static final String PREF_KEY_IS_IDEAL_TRACKING_ENABLE = "PREF_KEY_IS_IDEAL_TRACKING_ENABLE";
    private static final String PREF_KEY_IS_LOCATION_REQUIRED = "PREF_KEY_IS_LOCATION_REQUIRED";
    private static final String PREF_KEY_IS_DAILY_IDLE_TRACK_OFF = "PREF_KEY_IS_DAILY_IDLE_TRACK_OFF";
    private static final String PREF_KEY_IS_GEO_FENCE_REQUIRED = "PREF_KEY_IS_GEO_FENCE_REQUIRED";
    private static final String PREF_KEY_IS_PHONE_USAGE_LIMIT_ON_IDLE_TRIP = "PREF_KEY_IS_PHONE_USAGE_LIMIT_ON_IDLE_TRIP";
    private static final String PREF_KEY_IS_PHONE_USAGE_LIMIT_ON_TASK_TRIP = "PREF_KEY_IS_PHONE_USAGE_LIMIT_ON_TASK_TRIP";
    private static final String PREF_KEY_IS_OVER_STOPPING_LIMIT_ON_TASK_TRIP = "PREF_KEY_IS_OVER_STOPPING_LIMIT_ON_TASK_TRIP";
    private static final String PREF_KEY_IS_OVER_STOPPING_LIMIT_ON_IDLE_TRIP = "PREF_KEY_IS_OVER_STOPPING_LIMIT_ON_IDLE_TRIP";
    private static final String PREF_KEY_IS_DEFAULT_DATE_RANGE = "PREF_KEY_IS_DEFAULT_DATE_RANGE";
    private static final String PREF_KEY_IS_MAX_DATE_RANGE = "PREF_KEY_IS_MAX_DATE_RANGE";
    private static final String PREF_KEY_IS_MAX_PAST_DAYS_ALLOWED = "PREF_KEY_IS_MAX_PAST_DAYS_ALLOWED";
    private static final String PREF_KEY_IS_ALW_CREATION_MAP = "PREF_KEY_IS_ALW_CREATION_MAP";
    private static final String PREF_KEY_IS_FILTER_MAP = "PREF_KEY_IS_FILTER_MAP";
    public static final String PREF_KEY_IS_FORM_DATA_MAP = "PREF_KEY_IS_FORM_DATA_MAP";
    public static final String PREF_KEY_PACK_UNIT_MAPS = "PREF_KEY_PACK_UNIT_MAPS";
    public static final String PREF_KEY_UNITS_ID_MAPS = "PREF_KEY_UNITS_ID_MAPS";
    public static final String PREF_KEY_EVENT_ID_MAPS = "PREF_KEY_EVENT_ID_MAPS";
    public static final String PREF_KEY_GEO_FENCE = "PREF_KEY_GEO_FENCE";
    public static final String PREF_KEY_TIME_BEFORE_FOR_REMINDER = "PREF_KEY_TIME_BEFORE_FOR_REMINDER";
    public static final String PREF_KEY_TIME_USER_GEO_FILTERS = "PREF_KEY_TIME_USER_GEO_FILTERS";
    public static final String PREF_KEY_USER_ROLE_ID="PREF_KEY_USER_ROLE_ID";
    public static final String PREF_KEY_USER_IDLE_TRACKING_INFO="PREF_KEY_USER_IDLE_TRACKING_INFO";
    public static final String PREF_KEY_USER_BUDDY_LISTING="PREF_KEY_USER_BUDDY_LISTING";
    public static final String PREF_KEY_USER_CHNAGE_SERVICE_PREFRANCE="PREF_KEY_USER_CHNAGE_SERVICE_PREFRANCE";
    public static final String PREF_KEY_USER_INSIGHTS="PREF_KEY_USER_INSIGHTS";
    private static final String PREF_KEY_USER_HUBS= "PREF_KEY_USER_HUBS";
    public static final String PREF_KEY_SELECTED_HUB_ID="PREF_KEY_SELECTED_HUB_ID";
    public static final String PREF_KEY_ROLE_CONFIG="PREF_KEY_ROLE_CONFIG";
    public static final String PREF_KEY_SAVED_CART="PREF_KEY_SAVED_CART";


    private final SharedPreferences mPrefs;

    @Inject
    public AppPreferencesHelper(Context context, @PreferenceInfo String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    @Override
    public void remove(String key) {
        mPrefs.edit().remove(key).apply();
    }

    @Override
    public void clear(PreferencesKeys key) {
        switch (key) {
            case PREF_KEY_TASK:
                mPrefs.edit().remove(PREF_KEY_TASK).apply();
                break;
            default:
            case ALL:
                mPrefs.edit().clear().apply();
                break;
            case VERIFICATION_ID:
                mPrefs.edit().remove(PREF_KEY_VERIFICATION_ID).apply();
                break;
            case TASKID:
                mPrefs.edit().remove(PREF_KEY_TRACKING_ID).apply();
                break;
            case CURRENT_TIME:
                mPrefs.edit().remove(PREF_KEY_TIME).apply();
                break;
            case NOT_ALL:
                //Returns a map containing a hashMap of pairs key/value representing the preferences.
                Map<String, ?> keys = mPrefs.getAll();
                //Iterate through the map and delete all the keys except intro preferences.
                for (Map.Entry<String, ?> entry : keys.entrySet()) {
                    if (!entry.getKey().equalsIgnoreCase(PREF_KEY_INTRO) /*&& !entry.getKey().equalsIgnoreCase(PREF_KEY_ACCESS_ID)*/) {
                        mPrefs.edit().remove(entry.getKey()).apply();
                    }
                }
                break;
            case NOT_ALL_EXCEPT_ACCESS_ID_LOGIN_TOKEN:
                //Returns a map containing a hashMap of pairs key/value representing the preferences.
                Map<String, ?> keys4 = mPrefs.getAll();
                //Iterate through the map and delete all the keys except intro preferences.
                for (Map.Entry<String, ?> entry : keys4.entrySet()) {
                    if (!entry.getKey().equalsIgnoreCase(PREF_KEY_INTRO) && !entry.getKey().equalsIgnoreCase(PREF_KEY_ACCESS_ID)&& !entry.getKey().equalsIgnoreCase(PREF_KEY_LOGIN_TOKEN)) {
                        mPrefs.edit().remove(entry.getKey()).apply();
                    }
                }
                break;
            case NOT_ALL_FOR_EXCEPT_ACCESS_ID:
                //Returns a map containing a hashMap of pairs key/value representing the preferences.
                Map<String, ?> keys2 = mPrefs.getAll();
                //Iterate through the map and delete all the keys except intro preferences.
                for (Map.Entry<String, ?> entry : keys2.entrySet()) {
                    if (!entry.getKey().equalsIgnoreCase(PREF_KEY_INTRO)) {
                        mPrefs.edit().remove(entry.getKey()).apply();
                    }
                }
                break;
            case PUNCH:
                mPrefs.edit().remove(PREF_KEY_PUNCH_IN_TIME).apply();
                mPrefs.edit().remove(PREF_KEY_PUNCH_OUT_TIME).apply();
                break;

            case EDIT_DATA:
                mPrefs.edit().remove(PREF_KEY_EDIT_DATA).apply();
                break;

            case SELFIE_URL:
                mPrefs.edit().remove(PREF_KEY_SELFIE_URL).apply();
                break;

            case TASK_DETAILS:
                mPrefs.edit().remove(PREF_KEY_TASK_DETAILS).apply();
                break;

            case LIVE_TRIP:
                mPrefs.edit().remove(PREF_KEY_LIVE_TRIP).apply();
                break;
            case PREF_KEY_IS_FORM_DATA_MAP:
                mPrefs.edit().remove(PREF_KEY_IS_FORM_DATA_MAP).apply();

        }
    }

    @Override
    public String getLoginToken() {
        return mPrefs.getString(PREF_KEY_LOGIN_TOKEN, null);
    }

    @Override
    public void setLoginToken(String accessToken) {
        mPrefs.edit().putString(PREF_KEY_LOGIN_TOKEN, accessToken).apply();
    }

    @Override
    public String getVerificationId() {
        return mPrefs.getString(PREF_KEY_VERIFICATION_ID, null);
    }

    @Override
    public void setVerificationId(String verificationId) {
        mPrefs.edit().putString(PREF_KEY_VERIFICATION_ID, verificationId).apply();
    }

    @Override
    public String getDeviceId() {
        return mPrefs.getString(PREF_KEY_DEVICE_ID, null);
    }

    @Override
    public void setDeviceId(String deviceId) {
        mPrefs.edit().putString(PREF_KEY_DEVICE_ID, deviceId).apply();
    }

    @Override
    public String getFcmToken() {
        return mPrefs.getString(PREF_KEY_FCM_TOKEN, null);
    }

    @Override
    public void setFcmToken(String fcmToken) {
        mPrefs.edit().putString(PREF_KEY_FCM_TOKEN, fcmToken).apply();
    }

    @Override
    public String getAccessId() {
        return mPrefs.getString(PREF_KEY_ACCESS_ID, AppConstants.Extra.ACCESS_ID);
    }

    @Override
    public void setAccessId(String accessId) {
        mPrefs.edit().putString(PREF_KEY_ACCESS_ID, accessId).apply();
    }

    @Override
    public ProfileInfo getUserDetail() {
        return new Gson()
                .fromJson(mPrefs.getString(PREF_KEY_USER_INFO, null),
                        ProfileInfo.class);
    }

    @Override
    public void setUserDetail(@NonNull ProfileInfo profileInfo) {
        mPrefs.edit().putString(PREF_KEY_USER_INFO, new Gson().toJson(profileInfo)).apply();
    }

    @Override
    public IdleTrackingInfo getIdleTrackingInfo() {
        return new Gson()
                .fromJson(mPrefs.getString(PREF_KEY_USER_IDLE_TRACKING_INFO, null),
                        IdleTrackingInfo.class);
    }

    @Override
    public void setIdleTrackingInfo(@NonNull IdleTrackingInfo idleTrackingInfo) {
        mPrefs.edit().putString(PREF_KEY_USER_IDLE_TRACKING_INFO, new Gson().toJson(idleTrackingInfo)).apply();
    }

    @Override
    public ArrayList<EmergencyContact> getEmergencyContacts() {
        String c = mPrefs.getString(PREF_KEY_EMERGENCY_CONTACTS, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<ArrayList<EmergencyContact>>() {
        }.getType());
    }

    @Override
    public void setEmergencyContacts(ArrayList<EmergencyContact> contactList) {
        mPrefs.edit().putString(PREF_KEY_EMERGENCY_CONTACTS, new Gson().toJson(contactList)).apply();

    }

    @Override
    public boolean isFirstTimeInstallFlag() {
        return mPrefs.getBoolean(PREF_KEY_FIRST_TIME_INSTALL, false);
    }

    @Override
    public void setFirstTimeInstallFlag(boolean isFirstTimeInstallFlag) {
        mPrefs.edit().putBoolean(PREF_KEY_FIRST_TIME_INSTALL, isFirstTimeInstallFlag).apply();
    }

    @Override
    public boolean isIntroFlag() {
        return mPrefs.getBoolean(PREF_KEY_INTRO, false);
    }

    @Override
    public void setIntroFlag(boolean introFlag) {
        mPrefs.edit().putBoolean(PREF_KEY_INTRO, introFlag).apply();
    }

    @Override
    public String getTrackingId() {
        return mPrefs.getString(PREF_KEY_TRACKING_ID, null);
    }

    @Override
    public void setTrackingId(String trackingId) {
        mPrefs.edit().putString(PREF_KEY_TRACKING_ID, trackingId).apply();
    }

    @Override
    public String getActiveTaskIdId() {
        return mPrefs.getString(PREF_KEY_ACTIVE_TASK_ID, "");
    }

    @Override
    public void setActiveTaskId(String trackingId) {
        mPrefs.edit().putString(PREF_KEY_ACTIVE_TASK_ID, trackingId).apply();
    }

    @Override
    public String getActiveTaskCategoryId() {

         return mPrefs.getString(PREF_KEY_ACTIVE_TASK_CATEGORY_ID, "");
    }

    @Override
    public void setActiveTaskCategoryId(String categoryId) {
        mPrefs.edit().putString(PREF_KEY_ACTIVE_TASK_CATEGORY_ID, categoryId).apply();

    }

    @Override
    public boolean getTransitionServiceFlag() {
        return mPrefs.getBoolean(PREF_KEY_FLAG, false);
    }

    @Override
    public void setTransitionServiceFlag(boolean transitionServiceFlag) {
        mPrefs.edit().putBoolean(PREF_KEY_FLAG, transitionServiceFlag).apply();
    }

    @Override
    public void saveCreateTaskApi(Api api) {
        if (api != null) {
            mPrefs.edit().putString(PREF_KEY_CREATE_TASK_API, new Gson().toJson(api)).apply();
        }
    }

    @Override
    public void saveEndTaskApi(Api api) {
        if (api != null) {
            mPrefs.edit().putString(PREF_KEY_END_TASK_API, new Gson().toJson(api)).apply();
        }
    }

    @Override
    public Api getApi(ApiType apiType) {
        String s;
        if (apiType == ApiType.CREATE_TASK) {
            s = PREF_KEY_CREATE_TASK_API;
        } else if (apiType == ApiType.END_TASK) {
            s = PREF_KEY_END_TASK_API;
        } else {
            s = null;
        }

        String c = mPrefs.getString(s, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, Api.class);

    }

    @Override
    public void saveApiList(List<Api> apiList) {
        Map<ApiType, Api> hashMap = new HashMap<>();
        for (int i = 0; i < apiList.size(); i++) {
            Api list = apiList.get(i);
            if (list.getName() != null) {
                hashMap.put(list.getName(), list);
            }
        }

        mPrefs.edit().putString(PREF_KEY_API_MAP, new Gson().toJson(hashMap)).apply();
    }

    @Override
    public HashMap<ApiType, Api> getApiMap() {
        String c = mPrefs.getString(PREF_KEY_API_MAP, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<HashMap<ApiType, Api>>() {
        }.getType());

    }

    @Override
    public void saveChatUrl(String chatUrl) {
        mPrefs.edit().putString(PREF_KEY_CHAT_SERVER_URL, chatUrl).apply();
    }

    @Override
    public String getChatUrl() {
        return mPrefs.getString(PREF_KEY_CHAT_SERVER_URL, null);
    }

    @Override
    public void saveGeofenceIds(HashMap<String, LatLng> map) {
        mPrefs.edit().putString(PREF_KEY_GEOFENCE_IDS, new Gson().toJson(map)).apply();
    }

    @Override
    public HashMap<String, LatLng> getGeofenceIds() {
        String c = mPrefs.getString(PREF_KEY_GEOFENCE_IDS, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<HashMap<String, LatLng>>() {
        }.getType());
    }

    @Override
    public void saveCurrentTask(Task task) {
        mPrefs.edit().putString(PREF_KEY_TASK, new Gson().toJson(task)).apply();
    }

    @Override
    public Task getCurrentTask() {
        String task = mPrefs.getString(PREF_KEY_TASK, null);
        if (task == null) {
            return null;
        }
        return new Gson().fromJson(task, Task.class);
    }

    @Override
    public void saveAllowArrival(boolean allowArrival) {
        mPrefs.edit().putBoolean(PREF_KEY_ALLOW_ARRIVAL, allowArrival).apply();
    }

    @Override
    public void saveAllowArrivalOnGeoIn(boolean allowArrivalOnGeoIn) {
        mPrefs.edit().putBoolean(PREF_KEY_ALLOW_ARRIVAL_ON_GEOFENCE, allowArrivalOnGeoIn).apply();
    }

    @Override
    public void saveAutoCancelThresholdInMin(int autoCancelThresholdInMin) {
        mPrefs.edit().putInt(PREF_KEY_AUTO_CANCEL_TASK_VALUE, autoCancelThresholdInMin).apply();
    }

    @Override
    public boolean getAllowArrival() {
        return mPrefs.getBoolean(PREF_KEY_ALLOW_ARRIVAL, false);
    }

    @Override
    public boolean getAllowArrivalOnGeoIn() {
        return mPrefs.getBoolean(PREF_KEY_ALLOW_ARRIVAL_ON_GEOFENCE, false);
    }

    @Override
    public int getAutoCancelThresholdInMin() {
        return mPrefs.getInt(PREF_KEY_AUTO_CANCEL_TASK_VALUE, 30);
    }

    @Override
    public long getCurrentTime() {
        return mPrefs.getLong(PREF_KEY_TIME, 30);
    }

    @Override
    public void setCurrentTime(long currentTimeMillis) {
        mPrefs.edit().putLong(PREF_KEY_TIME, currentTimeMillis).apply();
    }

    @Override
    public float getRadius() {
        return mPrefs.getFloat(PREF_KEY_RADIUS, 500f);
    }

    @Override
    public void setRadius(float radius) {
        mPrefs.edit().putFloat(PREF_KEY_RADIUS, radius).apply();
    }

    @Override
    public void saveAppAutoStartFlag(boolean isAutoStart) {
        mPrefs.edit().putBoolean(PREF_KEY_APP_AUTO_START, isAutoStart).apply();
    }

    //AppConstants.Extra.ACCESS_ID
    @Override
    public boolean getAppAutoStartFlag() {
        return mPrefs.getBoolean(PREF_KEY_APP_AUTO_START, false);
    }

    @Override
    public boolean getAutoStartFlag() {
        return mPrefs.getBoolean(PREF_KEY_AUTO_START_FLAG, false);
    }

    @Override
    public void setAutoStartFlag(boolean flagForService) {
        mPrefs.edit().putBoolean(PREF_KEY_AUTO_START_FLAG, flagForService).apply();
    }

    @Override
    public boolean getPunchStatus() {
        return mPrefs.getBoolean(PREF_KEY_PUNCH_STATUS, false);
    }

    @Override
    public void setPunchStatus(boolean isPunchedIn) {
        mPrefs.edit().putBoolean(PREF_KEY_PUNCH_STATUS, isPunchedIn).apply();
    }

    @Override
    public long getPunchInTime() {
        return mPrefs.getLong(PREF_KEY_PUNCH_IN_TIME, 0);
    }

    @Override
    public void setPunchInTime(long punchInTime) {
        mPrefs.edit().putLong(PREF_KEY_PUNCH_IN_TIME, punchInTime).apply();
    }

    @Override
    public long getPunchOutTime() {
        return mPrefs.getLong(PREF_KEY_PUNCH_OUT_TIME, 0);
    }

    @Override
    public void setPunchOutTime(long punchOutTime) {
        mPrefs.edit().putLong(PREF_KEY_PUNCH_OUT_TIME, punchOutTime).apply();
    }

    @Override
    public long getLastPunchOutTime() {
        return mPrefs.getLong(PREF_KEY_LAST_PUNCH_OUT_TIME, 0);
    }

    @Override
    public void setLastPunchOutTime(long punchOutTime) {
        mPrefs.edit().putLong(PREF_KEY_LAST_PUNCH_OUT_TIME, punchOutTime).apply();
    }

//    @Override
//    public void setEditData(EditData data) {
//        mPrefs.edit().putString(PREF_KEY_EDIT_DATA, new Gson().toJson(data)).apply();
//    }
//
//    @Override
//    public EditData getEditData() {
//        return (new Gson().fromJson(mPrefs.getString(PREF_KEY_EDIT_DATA, ""), EditData.class);
//    }

    @Override
    public Map<String, Task> getTimerCallToAction() {
        String c = mPrefs.getString(PREF_KEY_TASK_DETAILS, null);
        if (c == null) {
            Map<String, Task> taskMap = new HashMap<>();
            return taskMap;
        }
        return new Gson().fromJson(c, new TypeToken<Map<String, Task>>() {
        }.getType());
    }

    @Override
    public void setTimerCallToAction(Map<String, Task> taskMap) {
        mPrefs.edit().putString(PREF_KEY_TASK_DETAILS, new Gson().toJson(taskMap)).apply();
    }


    @Override
    public void setPendingTimeForTask(Map<String, Long> taskMap) {

        mPrefs.edit().putString(PREF_KEY_TIMER_TASK_TIME, new Gson().toJson(taskMap)).apply();
    }

    @Override
    public Map<String, Long> getPendingTime() {
        String c = mPrefs.getString(PREF_KEY_TIMER_TASK_TIME, null);
        if (c == null) {
            Map<String, Long> taskMap = new HashMap<>();
            return taskMap;
        }
        return new Gson().fromJson(c, new TypeToken<Map<String, Long>>() {
        }.getType());
    }

    @Override
    public String getSelfieUrl() {
        return mPrefs.getString(PREF_KEY_SELFIE_URL, "");
    }

    @Override
    public void setSelfieUrl(String url) {
        mPrefs.edit().putString(PREF_KEY_SELFIE_URL, url).apply();
    }

    @Override
    public HashMap<String, DataObject> getTaskDetails() {
        String c = mPrefs.getString(PREF_KEY_TASK_DETAILS, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<Map<String, Task>>() {
        }.getType());
    }

    @Override
    public void setTaskDetails(HashMap<String, DataObject> hashMap) {
        mPrefs.edit().putString(PREF_KEY_TASK_DETAILS, new Gson().toJson(hashMap)).apply();

    }

    @Override
    public Map<Integer, List<ShiftTime>> getOldShiftMap() {
        String c = mPrefs.getString(OLD_SHIFT_MAP, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<Map<Integer, List<ShiftTime>>>() {
        }.getType());
    }

    @Override
    public void setOldShiftMap(Map<Integer, List<ShiftTime>> oldShiftMap) {
        mPrefs.edit().putString(OLD_SHIFT_MAP, new Gson().toJson(oldShiftMap)).apply();
    }

    @Override
    public Map<Integer, List<ShiftTime>> getAlwaysNewShiftMap() {
        String c = mPrefs.getString(ALWAYS_NEW_SHIFT_MAP, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<Map<Integer, List<ShiftTime>>>() {
        }.getType());
    }

    @Override
    public void setAlwaysNewShiftMap(Map<Integer, List<ShiftTime>> oldShiftMap) {
        mPrefs.edit().putString(ALWAYS_NEW_SHIFT_MAP, new Gson().toJson(oldShiftMap)).apply();
    }

    @Override
    public Api getHeartBeat() {
        return new Gson().fromJson(mPrefs.getString(HEARTBEAT, null), Api.class);
    }

    @Override
    public void setHeartBeat(Api api) {
        mPrefs.edit().putString(HEARTBEAT, new Gson().toJson(api)).apply();
    }

    @Override
    public AlarmInfo getStartAlarmInfo() {
        return new Gson().fromJson(mPrefs.getString(AppConstants.START_ALARM_INFO, null), AlarmInfo.class);
    }

    @Override
    public void setStartAlarmInfo(AlarmInfo alarmInfo) {
        mPrefs.edit().putString(AppConstants.START_ALARM_INFO, new Gson().toJson(alarmInfo)).apply();
    }

    @Override
    public AlarmInfo getStopAlarmInfo() {
        return new Gson().fromJson(mPrefs.getString(AppConstants.STOP_ALARM_INFO, null), AlarmInfo.class);
    }

    @Override
    public void setStopAlarmInfo(AlarmInfo alarmInfo) {
        mPrefs.edit().putString(AppConstants.STOP_ALARM_INFO, new Gson().toJson(alarmInfo)).apply();
    }

    @Override
    public boolean getIsMapChanged() {

        return mPrefs.getBoolean(IS_MAP_CHANGED, false);
    }

    @Override
    public void setIsMapChanged(boolean isMapChanged) {
        mPrefs.edit().putBoolean(IS_MAP_CHANGED, isMapChanged).apply();

    }

    @Override
    public boolean getIsHubChanged() {
        return mPrefs.getBoolean(IS_HUB_CHANGED, false);
    }

    @Override
    public void setIsHubChanged(boolean isHubChanged) {
        mPrefs.edit().putBoolean(IS_HUB_CHANGED, isHubChanged).apply();

    }

    @Override
    public String getUpdatedSdkConfig() {
        return mPrefs.getString(PREF_UPDATED_SDK_CONFIG, null);

    }

    @Override
    public void setUpdatedSdkConfig(String updatedSdkConfig) {
        mPrefs.edit().putString(PREF_UPDATED_SDK_CONFIG, updatedSdkConfig).apply();
    }

    @Override
    public void setIdleTripActive(boolean idleTripActive) {
        mPrefs.edit().putBoolean(PREF_KEY_IDLE_TRIP, idleTripActive).apply();
    }

    @Override
    public boolean getIdleTripActive() {
        return mPrefs.getBoolean(PREF_KEY_IDLE_TRIP, false);
    }

    @Override
    public void setIsTrackingLiveTrip(boolean isLive) {
        mPrefs.edit().putBoolean(PREF_KEY_LIVE_TRIP, isLive).apply();
    }

    @Override
    public boolean getIsTrackingLiveTrip() {
        return mPrefs.getBoolean(PREF_KEY_LIVE_TRIP, false);
    }

    @Override
    public List<WorkFlowCategories> getWorkFlowCategoriesList() {
        Type listType = new TypeToken<List<WorkFlowCategories>>() {
        }.getType();

        List<WorkFlowCategories> workFlowCategoriesList
                = new Gson().fromJson(mPrefs.getString(PREF_KEY_WORKFLOW_CATEGORIES, null)
                , listType);
        return workFlowCategoriesList;
    }

    @Override
    public void saveFlavorList(List<Flavour> flavourList) {
        mPrefs.edit().putString(PREF_KEY_FLAVOR, new Gson().toJson(flavourList)).apply();

    }

    @Override
    public List<Flavour> getFlavorList() {
        Type listType = new TypeToken<List<Flavour>>() {
        }.getType();

        List<Flavour> flavorList
                = new Gson().fromJson(mPrefs.getString(PREF_KEY_FLAVOR, null)
                , listType);
        return flavorList;
    }

    @Override
    public void saveWorkFlowCategoriesChannel(Map<String, ChannelConfig> channelConfigMap) {

        mPrefs.edit().putString(CONFIG_CHANNEL_MAP, new Gson().toJson(channelConfigMap)).apply();
    }



    @Override
    public Map<String, ChannelConfig> getWorkFlowCategoriesListChannel() {
        String c = mPrefs.getString(CONFIG_CHANNEL_MAP, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<Map<String, ChannelConfig>>() {
        }.getType());
    }

    @Override
    public void saveFlavourMap(Map<String, Flavour> flavourMap) {

        mPrefs.edit().putString(PREF_KEY_FLAVOUR_MAP, new Gson().toJson(flavourMap)).apply();
    }

    @Override
    public Map<String, Flavour> getFlavourMap() {
        String c = mPrefs.getString(PREF_KEY_FLAVOUR_MAP, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<Map<String, Flavour>>() {
        }.getType());
    }

    @Override
    public void saveWorkFlowAllowCreation(Map<String, Boolean> allowCreationMap) {

        mPrefs.edit().putString(PREF_KEY_IS_ALW_CREATION_MAP, new Gson().toJson(allowCreationMap)).apply();
    }

    @Override
    public Map<String, Boolean> getWorkFlowAllowCreation() {
        String c = mPrefs.getString(PREF_KEY_IS_ALW_CREATION_MAP, null);
        if (c == null) {
            return null;
        }
        return new Gson().fromJson(c, new TypeToken<Map<String, Boolean>>() {
        }.getType());
    }

    @Override
    public void setIsFleetAndBuddyShow(boolean iShow) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_FLEET_AND_BUDDY_SHOW, iShow).apply();
    }

    @Override
    public boolean getIsFleetAndBuddySHow() {
        return mPrefs.getBoolean(PREF_KEY_IS_FLEET_AND_BUDDY_SHOW, false);
    }

    @Override
    public void setIsIdealTrackingEnable(boolean isLiveTrackingEnable) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_IDEAL_TRACKING_ENABLE, isLiveTrackingEnable).apply();
    }

    @Override
    public boolean getIsIdealTrackingEnable() {
        return mPrefs.getBoolean(PREF_KEY_IS_IDEAL_TRACKING_ENABLE, false);
    }

    @Override
    public void setLocationRequired(boolean locationRequired) {

        mPrefs.edit().putBoolean(PREF_KEY_IS_LOCATION_REQUIRED, locationRequired).apply();
    }

    @Override
    public boolean getLocationRequired() {
        return mPrefs.getBoolean(PREF_KEY_IS_LOCATION_REQUIRED, false);
    }

    @Override
    public void setDailyIdleTaskOffEnable(boolean isDailyTask) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_DAILY_IDLE_TRACK_OFF, isDailyTask).apply();
    }

    @Override
    public boolean getDailyIdleTaskOffEnable() {
        return mPrefs.getBoolean(PREF_KEY_IS_DAILY_IDLE_TRACK_OFF, false);
    }

    @Override
    public void setEnablePunchGeofencing(boolean locationRequired) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_GEO_FENCE_REQUIRED, locationRequired).apply();
    }

    @Override
    public boolean getEnablePunchGeofencing() {
        return mPrefs.getBoolean(PREF_KEY_IS_GEO_FENCE_REQUIRED, false);
    }

    @Override
    public void savePhoneUsageLimitInMinutesOnIdleTrip(int minutes) {
        mPrefs.edit().putInt(PREF_KEY_IS_PHONE_USAGE_LIMIT_ON_IDLE_TRIP, minutes).apply();
    }

    @Override
    public Integer getPhoneUsageLimitInMinutesOnIdleTrip() {
        return mPrefs.getInt(PREF_KEY_IS_PHONE_USAGE_LIMIT_ON_IDLE_TRIP, 0);
    }

    @Override
    public void savePhoneUsageLimitInMinutesOnTaskTrip(int minutes) {
        mPrefs.edit().putInt(PREF_KEY_IS_PHONE_USAGE_LIMIT_ON_TASK_TRIP, minutes).apply();
    }

    @Override
    public Integer getPhoneUsageLimitInMinutesOnTaskTrip() {
        return mPrefs.getInt(PREF_KEY_IS_PHONE_USAGE_LIMIT_ON_TASK_TRIP, 0);
    }

    @Override
    public void saveAccountsList(List<UserAccount> userAccounts) {
        mPrefs.edit().putString(PREF_KEY_USER_ACCOUNTS, new Gson().toJson(userAccounts)).apply();
    }

    @Override
    public List<UserAccount> getAccountList() {
        Type listType = new TypeToken<List<UserAccount>>() {
        }.getType();

        List<UserAccount> accountList
                = new Gson().fromJson(mPrefs.getString(PREF_KEY_USER_ACCOUNTS, null)
                , listType);
        return accountList;
    }

    @Override
    public void saveGeoFence(List<GeoFenceData> geoFenceData) {
        mPrefs.edit().putString(PREF_KEY_USER_GEO_FENCE, new Gson().toJson(geoFenceData)).apply();

    }

    @Override
    public List<GeoFenceData> getGeoFenceList() {
        Type listType = new TypeToken<List<GeoFenceData>>() {
        }.getType();
        List<GeoFenceData> geoFence
                = new Gson().fromJson(mPrefs.getString(PREF_KEY_USER_GEO_FENCE, null)
                , listType);
        return geoFence;
    }

    @Override
    public String getUserType() {
        return mPrefs.getString(PREF_KEY_USER_TYPE, "");
    }

    @Override
    public void setUserType(UserType userType) {
        mPrefs.edit().putString(PREF_KEY_USER_TYPE, userType.name()).apply();
    }

    @Override
    public String getStatus() {
        return mPrefs.getString(PREF_KEY_USER_ONLINE_OFFLINE_STATUS, null);
    }

    @Override
    public void setStatus(String offlineOnLIne) {
        mPrefs.edit().putString(PREF_KEY_USER_ONLINE_OFFLINE_STATUS, offlineOnLIne).apply();
    }

    @Override
    public void saveUserTypeList(List<UserType> userType) {
        mPrefs.edit().putString(PREF_KEY_USER_TYPE_LIST, new Gson().toJson(userType)).apply();
    }

    @Override
    public List<UserType> getUserTypeList() {
        Type listType = new TypeToken<List<UserType>>() {
        }.getType();

        List<UserType> accountList
                = new Gson().fromJson(mPrefs.getString(PREF_KEY_USER_TYPE_LIST, null)
                , listType);
        return accountList;
    }

    @Override
    public void saveOnTripOverStopping(OverstoppingConfig overstoppingConfig) {
        mPrefs.edit().putString(PREF_KEY_IS_OVER_STOPPING_LIMIT_ON_TASK_TRIP, new Gson().toJson(overstoppingConfig)).apply();
    }

    @Override
    public void saveOnIdleOverStopping(OverstoppingConfig overstoppingConfig) {
        mPrefs.edit().putString(PREF_KEY_IS_OVER_STOPPING_LIMIT_ON_IDLE_TRIP, new Gson().toJson(overstoppingConfig)).apply();
    }

    @Override
    public OverstoppingConfig getOnTripOverStopping() {
        return new Gson().fromJson(mPrefs.getString(PREF_KEY_IS_OVER_STOPPING_LIMIT_ON_TASK_TRIP, null), OverstoppingConfig.class);
    }

    @Override
    public OverstoppingConfig getOnIdleOverStopping() {
        return new Gson().fromJson(mPrefs.getString(PREF_KEY_IS_OVER_STOPPING_LIMIT_ON_IDLE_TRIP, null), OverstoppingConfig.class);
    }

    @Override
    public int getDefDateRange() {
        return mPrefs.getInt(PREF_KEY_IS_DEFAULT_DATE_RANGE, 0);
    }

    @Override
    public void setDefDateRange(int dateRange) {
        mPrefs.edit().putInt(PREF_KEY_IS_DEFAULT_DATE_RANGE, dateRange).apply();
    }

    @Override
    public int getMaxDateRange() {
        return mPrefs.getInt(PREF_KEY_IS_MAX_DATE_RANGE, 0);
    }

    @Override
    public void setMaxDateRange(int dateRange) {
        mPrefs.edit().putInt(PREF_KEY_IS_MAX_DATE_RANGE, dateRange).apply();
    }

    @Override
    public int getMaxPastDaysAllowed() {
        return mPrefs.getInt(PREF_KEY_IS_MAX_PAST_DAYS_ALLOWED, 0);
    }

    @Override
    public void setMaxPastDaysAllowed(int dateRange) {
        mPrefs.edit().putInt(PREF_KEY_IS_MAX_PAST_DAYS_ALLOWED, dateRange).apply();
    }

    @Override
    public void setPunchId(String punchId) {
        mPrefs.edit().putString(PREF_KEY_IS_LAST_PUNCH_ID, punchId).apply();
    }

    @Override
    public String getPunchId() {
        return mPrefs.getString(PREF_KEY_IS_LAST_PUNCH_ID, null);
    }

    @Override
    public boolean getVoiceAlertsTracking() {
        return mPrefs.getBoolean(PREF_KEY_IS_VOICE_ALERTS, false);
    }

    @Override
    public void setVoiceAlertsTracking(boolean isAlerts) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_VOICE_ALERTS, isAlerts).apply();
    }

    @Override
    public Map<String, SaveFilterData> getFilterMap() {
        String c = mPrefs.getString(PREF_KEY_IS_FILTER_MAP, null);
        if (c == null) {
            return null;
        }
        Map<String, SaveFilterData> dataHashMap = new Gson().fromJson(c, new TypeToken<Map<String, SaveFilterData>>() {
        }.getType());
        return dataHashMap;
    }

    @Override
    public void saveFilterMap(Map<String, SaveFilterData> saveFilterData) {
        mPrefs.edit().putString(PREF_KEY_IS_FILTER_MAP, new Gson().toJson(saveFilterData)).apply();
    }

    @Override
    public boolean getEnableWalletFlag() {
        return mPrefs.getBoolean(PREF_KEY_IS_WALLET_ENABLE, false);
    }

    @Override
    public void setEnableWalletFlag(boolean isEnable) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_WALLET_ENABLE, isEnable).apply();
    }

    @Override
    public boolean isManger() {

        return mPrefs.getBoolean(PREF_KEY_IS_MANAGER, false);
    }

    @Override
    public void setManger(boolean isEnable) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_MANAGER, isEnable).apply();
    }

    @Override
    public void setFormDataMap(Map<String, ArrayList<FormData>> formDataList) {
        mPrefs.edit().putString(PREF_KEY_IS_FORM_DATA_MAP, new Gson().toJson(formDataList)).apply();
    }

    @Override
    public Map<String, ArrayList<FormData>> getFormDataMap() {
        String c = mPrefs.getString(PREF_KEY_IS_FORM_DATA_MAP, null);
        if (c == null) {
            return null;
        }
        Map<String, ArrayList<FormData>> dataHashMap = new Gson().fromJson(c, new TypeToken<Map<String, ArrayList<FormData>>>() {
        }.getType());
        return dataHashMap;
    }




    @Override
    public void setUnits(LinkedHashMap<String, String> formDataList) {
        mPrefs.edit().putString(
                PREF_KEY_UNITS_ID_MAPS,
                new Gson().toJson(formDataList)
        ).apply();
    }

    @Override
    public LinkedHashMap<String, String> getUnits() {
        String c = mPrefs.getString(PREF_KEY_UNITS_ID_MAPS, null);
        if (c == null) {
            return null;
        }
        LinkedHashMap<String, String> dataHashMap = new Gson().fromJson(c, new TypeToken<LinkedHashMap<String, String>>() {
        }.getType());
        return dataHashMap;
    }

    @Override
    public void setPackUnits(LinkedHashMap<String, String> formDataList) {
        mPrefs.edit().putString(
                PREF_KEY_PACK_UNIT_MAPS,
                new Gson().toJson(formDataList)
        ).apply();
    }

    @Override
    public LinkedHashMap<String, String> getPackUnits() {
        String c = mPrefs.getString(PREF_KEY_PACK_UNIT_MAPS, null);
        if (c == null) {
            return null;
        }
        LinkedHashMap<String, String> dataHashMap = new Gson().fromJson(c, new TypeToken<LinkedHashMap<String, String>>() {
        }.getType());
        return dataHashMap;
    }

    @Override
    public boolean getTimeReminderFlag() {
        return mPrefs.getBoolean(PREF_KEY_IS_TIME_REMINDER, false);
    }

    @Override
    public void setTimeReminderFlag(boolean isEnable) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_TIME_REMINDER, isEnable).apply();
    }

    @Override
    public boolean getLocationReminderFlag() {
        return mPrefs.getBoolean(PREF_KEY_IS_LOCATION_REMINDER, false);
    }

    @Override
    public void setLocationReminderFlag(boolean isEnable) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_LOCATION_REMINDER, isEnable).apply();
    }

    @Override
    public void setEventIdInPref(Map<String, Long> formDataList) {
        mPrefs.edit().putString(PREF_KEY_EVENT_ID_MAPS, new Gson().toJson(formDataList)).apply();
    }

    @Override
    public Map<String, Long> getEventIdMap() {
        String c = mPrefs.getString(PREF_KEY_EVENT_ID_MAPS, null);
        if (c == null) {
            return null;
        }
        Map<String, Long> dataHashMap = new Gson().fromJson(c, new TypeToken<Map<String, Long>>() {
        }.getType());
        return dataHashMap;
    }

    @Override
    public void setGeoFence(Map<String, GeoCoordinates> formDataList) {
        mPrefs.edit().putString(PREF_KEY_GEO_FENCE, new Gson().toJson(formDataList)).apply();
    }

    @Override
    public Map<String, GeoCoordinates> getGeoFence() {
        String c = mPrefs.getString(PREF_KEY_GEO_FENCE, null);
        if (c == null) {
            return null;
        }
        Map<String, GeoCoordinates> dataHashMap = new Gson().fromJson(c, new TypeToken<Map<String, GeoCoordinates>>() {
        }.getType());
        return dataHashMap;
    }

    @Override
    public String getTimeBeforeTime() {
        return mPrefs.getString(PREF_KEY_TIME_BEFORE_FOR_REMINDER, "0");
    }

    @Override
    public void setBeforeTime(String time) {
        mPrefs.edit().putString(PREF_KEY_TIME_BEFORE_FOR_REMINDER, time).apply();
    }

    @Override
    public boolean userGeoFilters() {
        return mPrefs.getBoolean(PREF_KEY_TIME_USER_GEO_FILTERS, false);
    }

    @Override
    public void setUserGeoFilters(boolean isEnable) {
        mPrefs.edit().putBoolean(PREF_KEY_TIME_USER_GEO_FILTERS, isEnable).apply();
    }

    @Override
    public String getUserRoleId() {
        return mPrefs.getString(PREF_KEY_USER_ROLE_ID,null);
    }

    @Override
    public void setUserRoleId(String roleId) {
        mPrefs.edit().putString(PREF_KEY_USER_ROLE_ID, roleId).apply();
    }

    @Override
    public boolean getBuddyListing() {
        return mPrefs.getBoolean(PREF_KEY_USER_BUDDY_LISTING, false);
    }

    @Override
    public void setBuddyListing(boolean buddyListing) {
        mPrefs.edit().putBoolean(PREF_KEY_USER_BUDDY_LISTING, buddyListing).apply();
    }

    @Override
    public boolean getServicePref() {
        return mPrefs.getBoolean(PREF_KEY_USER_CHNAGE_SERVICE_PREFRANCE, false);
    }

    @Override
    public void setServicePref(boolean selected) {

        mPrefs.edit().putBoolean(PREF_KEY_USER_CHNAGE_SERVICE_PREFRANCE, selected).apply();
    }

    @Override
    public boolean getInsights() {
        return mPrefs.getBoolean(PREF_KEY_USER_INSIGHTS, false);
    }

    @Override
    public void setInsights(boolean selected) {
        mPrefs.edit().putBoolean(PREF_KEY_USER_INSIGHTS, selected).apply();
    }

    @Override
    public void saveUserHubList(List<Hub> hubs) {
        mPrefs.edit().putString(PREF_KEY_USER_HUBS, new Gson().toJson(hubs)).apply();
    }

    @Override
    public List<Hub> getUserHubList() {
        Type listType = new TypeToken<List<Hub>>() {
        }.getType();

        List<Hub> accountList
                = new Gson().fromJson(mPrefs.getString(PREF_KEY_USER_HUBS, null)
                , listType);
        return accountList;
    }

    @Override
    public String getSelectedLocation() {
        return mPrefs.getString(PREF_KEY_SELECTED_HUB_ID,null);
    }

    @Override
    public void setSelectedLocation(String hubId) {
        mPrefs.edit().putString(PREF_KEY_SELECTED_HUB_ID, hubId).apply();
    }

    @Override
    public void saveRoleConfigDataList(List<RoleConfigData> roleList) {
        mPrefs.edit().putString(PREF_KEY_ROLE_CONFIG, new Gson().toJson(roleList)).apply();
    }

    @Override
    public List<RoleConfigData> getRoleConfigDataList() {

        Type listType = new TypeToken<List<RoleConfigData>>() {
        }.getType();

        List<RoleConfigData> roleList
                = new Gson().fromJson(mPrefs.getString(PREF_KEY_ROLE_CONFIG, null)
                , listType);
        return roleList;
    }



    @Override
    public void saveProductInCartWRC(Map<String, Map<String, CatalogProduct>> map) {
        mPrefs.edit().putString(PREF_KEY_SAVED_CART, new Gson().toJson(map)).apply();
    }

    @Override
    public HashMap<String, HashMap<String, CatalogProduct>> getProductInCartWRC() {
        String c = mPrefs.getString(PREF_KEY_SAVED_CART, null);
        if (c == null) {
            return null;
        }
        HashMap<String,HashMap<String,CatalogProduct>> dataHashMap = new Gson().fromJson(c, new TypeToken<HashMap<String,HashMap<String,CatalogProduct>>>() {
        }.getType());
        return dataHashMap;
    }

    @Override
    public void saveWorkFlowCategoriesList(List<WorkFlowCategories> workFlowCategoriesList) {
        mPrefs.edit().putString(PREF_KEY_WORKFLOW_CATEGORIES, new Gson().toJson(workFlowCategoriesList)).apply();
    }


    public enum PreferencesKeys {
        VERIFICATION_ID, ALL, NOT_ALL, PREF_KEY_TASK, TASKID, CURRENT_TIME, PUNCH, EDIT_DATA,
        SELFIE_URL, TASK_DETAILS, IDLE_TRIP_FLAG, OLD_SHIFT_MAP, IS_HUB_CHANGED, UPDATED_SDK_CONFIG, PREF_IDLE_TRIP, LIVE_TRIP,
        NOT_ALL_FOR_EXCEPT_ACCESS_ID,PREF_KEY_IS_FORM_DATA_MAP,NOT_ALL_EXCEPT_ACCESS_ID_LOGIN_TOKEN
    }
}
