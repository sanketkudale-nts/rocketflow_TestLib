package com.rocketflyer.rocketflow.data.local.prefs;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.tracki.data.local.prefs.AppPreferencesHelper;
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
import com.tracki.ui.addplace.Hub;
import com.tracki.ui.login.UserAccount;
import com.tracki.ui.selectorder.CatalogProduct;
import com.tracki.utils.ApiType;
import com.tracki.utils.ShiftTime;
import com.tracki.utils.UserType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public interface PreferencesHelper {

    void remove(String key);

    String getLoginToken();

    void setLoginToken(String accessToken);

    String getVerificationId();

    void setVerificationId(String verificationId);

    void clear(AppPreferencesHelper.PreferencesKeys key);

    String getDeviceId();

    void setDeviceId(String deviceId);

    String getFcmToken();

    void setFcmToken(String fcmToken);

    String getAccessId();

    void setAccessId(String accessId);

    ProfileInfo getUserDetail();

    void setUserDetail(@NonNull ProfileInfo profileInfo);

    IdleTrackingInfo getIdleTrackingInfo();

    void setIdleTrackingInfo(@NonNull IdleTrackingInfo idleTrackingInfo);

    ArrayList<EmergencyContact> getEmergencyContacts();

    void setEmergencyContacts(ArrayList<EmergencyContact> contactList);

    boolean isFirstTimeInstallFlag();

    void setFirstTimeInstallFlag(boolean isFirstTimeInstallFlag);

    boolean isIntroFlag();

    void setIntroFlag(boolean introFlag);

    String getTrackingId();

    void setTrackingId(String trackingId);

    String getActiveTaskIdId();

    void setActiveTaskId(String trackingId);

    String getActiveTaskCategoryId();

    void setActiveTaskCategoryId(String categoryId);

    boolean getTransitionServiceFlag();

    void setTransitionServiceFlag(boolean transitionServiceFlag);

    void saveCreateTaskApi(Api api);

    void saveEndTaskApi(Api api);

    Api getApi(ApiType apiType);

    void saveApiList(List<Api> apis);

    HashMap<ApiType, Api> getApiMap();

    boolean getAutoStartFlag();

    void setAutoStartFlag(boolean flagForService);

    void saveChatUrl(String chatUrl);

    String getChatUrl();

    void saveGeofenceIds(HashMap<String, LatLng> map);

    HashMap<String, LatLng> getGeofenceIds();

    void saveCurrentTask(Task task);

    Task getCurrentTask();

    void saveAllowArrival(boolean allowArrival);

    void saveAllowArrivalOnGeoIn(boolean allowArrivalOnGeoIn);

    void saveAutoCancelThresholdInMin(int autoCancelThresholdInMin);

    boolean getAllowArrival();

    boolean getAllowArrivalOnGeoIn();

    int getAutoCancelThresholdInMin();

    long getCurrentTime();

    void setCurrentTime(long currentTimeMillis);

    float getRadius();

    void setRadius(float radius);

    void saveAppAutoStartFlag(boolean isAutoStart);

    boolean getAppAutoStartFlag();

    boolean getPunchStatus();

    void setPunchStatus(boolean isPunchedIn);

    long getPunchInTime();

    void setPunchInTime(long punchInTime);

    long getPunchOutTime();

    void setPunchOutTime(long punchOutTime);

    long getLastPunchOutTime();

    void setLastPunchOutTime(long punchOutTime);

    String getSelfieUrl();

    void setSelfieUrl(String url);

    HashMap<String, DataObject> getTaskDetails();

    void setTaskDetails(HashMap<String, DataObject> hashMap);


    Map<String, Task> getTimerCallToAction();

    void setTimerCallToAction(Map<String, Task> taskMap);

    void setPendingTimeForTask(Map<String, Long> taskMap);

    Map<String, Long> getPendingTime();

//    boolean getIdleTripFlag();
//
//    void setIdleTripFlag(boolean isIdleTrip);

    Map<Integer, List<ShiftTime>> getOldShiftMap();

    void setOldShiftMap(Map<Integer, List<ShiftTime>> oldShiftMap);

    Map<Integer, List<ShiftTime>> getAlwaysNewShiftMap();

    void setAlwaysNewShiftMap(Map<Integer, List<ShiftTime>> oldShiftMap);

    Api getHeartBeat();

    void setHeartBeat(Api api);

    AlarmInfo getStartAlarmInfo();

    void setStartAlarmInfo(AlarmInfo alarmInfo);

    AlarmInfo getStopAlarmInfo();

    void setStopAlarmInfo(AlarmInfo alarmInfo);

    boolean getIsMapChanged();

    void setIsMapChanged(boolean isMapChanged);

    boolean getIsHubChanged();

    void setIsHubChanged(boolean isHubChanged);

    String getUpdatedSdkConfig();

    void setUpdatedSdkConfig(String updatedSdkConfig);


    void setIdleTripActive(boolean idleTripActive);

    boolean getIdleTripActive();

    boolean getIsTrackingLiveTrip();

    void setIsTrackingLiveTrip(boolean isLive);

    void saveWorkFlowCategoriesList(List<WorkFlowCategories> workFlowCategoriesList);

    List<WorkFlowCategories> getWorkFlowCategoriesList();

    void saveFlavorList(List<Flavour> flavourList);

    List<Flavour> getFlavorList();

    void saveWorkFlowCategoriesChannel(Map<String, ChannelConfig> channelConfigMap);

    Map<String, ChannelConfig> getWorkFlowCategoriesListChannel();

    void saveFlavourMap(Map<String, Flavour> flavourMap);

    Map<String, Flavour> getFlavourMap();

    void saveWorkFlowAllowCreation(Map<String, Boolean> allowCreationMap);

    Map<String, Boolean> getWorkFlowAllowCreation();

    void setIsFleetAndBuddyShow(boolean iShow);

    boolean getIsFleetAndBuddySHow();

    void setIsIdealTrackingEnable(boolean isLiveTrackingEnable);

    boolean getIsIdealTrackingEnable();

    void setLocationRequired(boolean locationRequired);

    boolean getLocationRequired();

    void setDailyIdleTaskOffEnable(boolean isDailyTask);

    boolean getDailyIdleTaskOffEnable();

    void setEnablePunchGeofencing(boolean locationRequired);

    boolean getEnablePunchGeofencing();

    void savePhoneUsageLimitInMinutesOnIdleTrip(int minutes);

    Integer getPhoneUsageLimitInMinutesOnIdleTrip();

    void savePhoneUsageLimitInMinutesOnTaskTrip(int minutes);

    Integer getPhoneUsageLimitInMinutesOnTaskTrip();

    void saveAccountsList(List<UserAccount> userAccounts);

    List<UserAccount> getAccountList();

    void saveGeoFence(List<GeoFenceData> geoFenceData);

    List<GeoFenceData> getGeoFenceList();

    String getUserType();

    void setUserType(UserType userType);

    String getStatus();

    void setStatus(String offlineOnLIne);

    void saveUserTypeList(List<UserType> userType);

    List<UserType> getUserTypeList();

    void saveOnTripOverStopping(@Nullable OverstoppingConfig overstoppingConfig);

    void saveOnIdleOverStopping(@Nullable OverstoppingConfig overstoppingConfig);

    OverstoppingConfig getOnTripOverStopping();

    OverstoppingConfig getOnIdleOverStopping();

    int getDefDateRange();

    void setDefDateRange(int dateRange);

    int getMaxDateRange();

    void setMaxDateRange(int dateRange);

    int getMaxPastDaysAllowed();

    void setMaxPastDaysAllowed(int dateRange);

    void setPunchId(String punchId);

    String getPunchId();

    boolean getVoiceAlertsTracking();

    void setVoiceAlertsTracking(boolean isAlerts);

    Map<String, SaveFilterData> getFilterMap();

    void saveFilterMap(Map<String, SaveFilterData> saveFilterData);

    boolean getEnableWalletFlag();

    void setEnableWalletFlag(boolean isEnable);

    boolean isManger();

    void setManger(boolean isEnable);

    void setFormDataMap(Map<String, ArrayList<FormData>> formDataList);

    Map<String, ArrayList<FormData>> getFormDataMap();

    void setUnits(LinkedHashMap<String, String> formDataList);

    LinkedHashMap<String, String> getUnits();

    void setPackUnits(LinkedHashMap<String, String> formDataList);

    LinkedHashMap<String, String> getPackUnits();


    boolean getTimeReminderFlag();

    void setTimeReminderFlag(boolean isEnable);

    boolean getLocationReminderFlag();

    void setLocationReminderFlag(boolean isEnable);

    void setEventIdInPref(Map<String, Long> formDataList);

    Map<String, Long> getEventIdMap();

    void setGeoFence(Map<String, GeoCoordinates> formDataList);

    Map<String, GeoCoordinates> getGeoFence();

    String getTimeBeforeTime();

    void setBeforeTime(String time);

    boolean userGeoFilters();

    void setUserGeoFilters(boolean isEnable);

    String getUserRoleId();

    void setUserRoleId(String roleId);

    boolean getBuddyListing();

    void setBuddyListing(boolean buddyListing);

    boolean getServicePref();

    void setServicePref(boolean selected);

    boolean getInsights();

    void setInsights(boolean selected);


    void saveUserHubList(List<Hub> hubs);

    List<Hub> getUserHubList();

    String getSelectedLocation();

    void setSelectedLocation(String hubId);


    void saveRoleConfigDataList(List<RoleConfigData> workFlowCategoriesList);

    List<RoleConfigData> getRoleConfigDataList();

    void saveProductInCartWRC(Map<String, Map<String, CatalogProduct>> map);

    HashMap<String,HashMap<String, CatalogProduct>> getProductInCartWRC();

}