package com.rocketflyer.rocketflow.data;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.AlarmInfo;
import com.tracki.data.model.DataObject;
import com.tracki.data.model.request.AddEmployeeRequest;
import com.tracki.data.model.request.AddressInfo;
import com.tracki.data.model.request.PlaceRequest;
import com.tracki.data.model.request.SaveFilterData;
import com.tracki.data.model.request.UserGetRequest;
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
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.data.network.NetworkManager;
import com.tracki.ui.addplace.AddPlaceRequest;
import com.tracki.ui.addplace.Hub;
import com.tracki.ui.category.AddCategoryRequest;
import com.tracki.ui.login.UserAccount;
import com.tracki.ui.productdetails.StockEntryRequest;
import com.tracki.ui.products.AddProductRequest;
import com.tracki.ui.selectorder.CataLogProductCategory;
import com.tracki.ui.selectorder.CatalogProduct;
import com.tracki.utils.ApiType;
import com.tracki.utils.ShiftTime;
import com.tracki.utils.UserType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by rahul on 2/9/18
 */
@Singleton
public class AppDataManager implements DataManager {

    private final PreferencesHelper mPreferencesHelper;
    private NetworkManager networkManager;

    @Inject
    public AppDataManager(Context mContext, Gson mGson,
                   PreferencesHelper mPreferencesHelper,
                   NetworkManager networkManager) {
        this.mPreferencesHelper = mPreferencesHelper;
        this.networkManager = networkManager;
    }

    @Override
    public void remove(String key) {
        mPreferencesHelper.remove(key);
    }

    @Override
    public String getLoginToken() {
        return mPreferencesHelper.getLoginToken();
    }

    @Override
    public void setLoginToken(String accessToken) {
        mPreferencesHelper.setLoginToken(accessToken);
    }

    @Override
    public String getVerificationId() {
        return mPreferencesHelper.getVerificationId();
    }

    @Override
    public void setVerificationId(String verificationId) {
        mPreferencesHelper.setVerificationId(verificationId);
    }

    @Override
    public void clear(AppPreferencesHelper.PreferencesKeys key) {
        mPreferencesHelper.clear(key);
    }

    @Override
    public String getDeviceId() {
        return mPreferencesHelper.getDeviceId();
    }

    @Override
    public void setDeviceId(String deviceId) {
        mPreferencesHelper.setDeviceId(deviceId);
    }

    @Override
    public String getFcmToken() {
        return mPreferencesHelper.getFcmToken();
    }

    @Override
    public void setFcmToken(String fcmToken) {
        mPreferencesHelper.setFcmToken(fcmToken);
    }

    @Override
    public String getAccessId() {
        return mPreferencesHelper.getAccessId();
    }

    @Override
    public void setAccessId(String accessId) {
        mPreferencesHelper.setAccessId(accessId);
    }

    @Override
    public ProfileInfo getUserDetail() {
        return mPreferencesHelper.getUserDetail();
    }

    @Override
    public void setUserDetail(@NonNull ProfileInfo profileInfo) {
        mPreferencesHelper.setUserDetail(profileInfo);
    }

    @Override
    public IdleTrackingInfo getIdleTrackingInfo() {
        return mPreferencesHelper.getIdleTrackingInfo();
    }

    @Override
    public void setIdleTrackingInfo(@NonNull IdleTrackingInfo idleTrackingInfo) {
        mPreferencesHelper.setIdleTrackingInfo(idleTrackingInfo);
    }

    @Override
    public ArrayList<EmergencyContact> getEmergencyContacts() {
        return mPreferencesHelper.getEmergencyContacts();
    }

    @Override
    public void setEmergencyContacts(ArrayList<EmergencyContact> contactList) {
        mPreferencesHelper.setEmergencyContacts(contactList);
    }

    @Override
    public boolean isFirstTimeInstallFlag() {
        return mPreferencesHelper.isFirstTimeInstallFlag();
    }

    @Override
    public void setFirstTimeInstallFlag(boolean isFirstTimeInstallFlag) {
        mPreferencesHelper.setFirstTimeInstallFlag(isFirstTimeInstallFlag);
    }

    @Override
    public boolean isIntroFlag() {
        return mPreferencesHelper.isIntroFlag();
    }

    @Override
    public void setIntroFlag(boolean introFlag) {
        mPreferencesHelper.setIntroFlag(introFlag);
    }

    @Override
    public String getTrackingId() {
        return mPreferencesHelper.getTrackingId();
    }

    @Override
    public void setTrackingId(String trackingId) {
        mPreferencesHelper.setTrackingId(trackingId);
    }

    @Override
    public String getActiveTaskIdId() {
        return mPreferencesHelper.getActiveTaskIdId();
    }

    @Override
    public void setActiveTaskId(String trackingId) {
        mPreferencesHelper.setActiveTaskId(trackingId);
    }

    @Override
    public String getActiveTaskCategoryId() {
        return mPreferencesHelper.getActiveTaskCategoryId();
    }

    @Override
    public void setActiveTaskCategoryId(String categoryId) {
        mPreferencesHelper.setActiveTaskCategoryId(categoryId);
    }

    @Override
    public boolean getTransitionServiceFlag() {
        return mPreferencesHelper.getTransitionServiceFlag();
    }

    @Override
    public void setTransitionServiceFlag(boolean transitionServiceFlag) {
        mPreferencesHelper.setTransitionServiceFlag(transitionServiceFlag);
    }

    @Override
    public void saveCreateTaskApi(Api api) {
        mPreferencesHelper.saveCreateTaskApi(api);
    }

    @Override
    public void saveEndTaskApi(Api api) {
        mPreferencesHelper.saveEndTaskApi(api);
    }

    @Override
    public Api getApi(ApiType apiType) {
        return mPreferencesHelper.getApi(apiType);
    }

    @Override
    public void saveApiList(List<Api> apis) {
        mPreferencesHelper.saveApiList(apis);
    }

    @Override
    public HashMap<ApiType, Api> getApiMap() {
        return mPreferencesHelper.getApiMap();
    }

    @Override
    public boolean getAutoStartFlag() {
        return mPreferencesHelper.getAutoStartFlag();
    }

    @Override
    public void setAutoStartFlag(boolean flagForService) {
        mPreferencesHelper.setAutoStartFlag(flagForService);
    }

    @Override
    public void saveChatUrl(String chatUrl) {
        mPreferencesHelper.saveChatUrl(chatUrl);
    }

    @Override
    public String getChatUrl() {
        return mPreferencesHelper.getChatUrl();
    }

    @Override
    public void saveGeofenceIds(HashMap<String, LatLng> map) {
        mPreferencesHelper.saveGeofenceIds(map);
    }

    @Override
    public HashMap<String, LatLng> getGeofenceIds() {
        return mPreferencesHelper.getGeofenceIds();
    }

    @Override
    public void saveCurrentTask(Task task) {
        mPreferencesHelper.saveCurrentTask(task);
    }

    @Override
    public Task getCurrentTask() {
        return mPreferencesHelper.getCurrentTask();
    }

    @Override
    public void saveAllowArrival(boolean allowArrival) {
        mPreferencesHelper.saveAllowArrival(allowArrival);
    }

    @Override
    public void saveAllowArrivalOnGeoIn(boolean allowArrivalOnGeoIn) {
        mPreferencesHelper.saveAllowArrivalOnGeoIn(allowArrivalOnGeoIn);
    }

    @Override
    public void saveAutoCancelThresholdInMin(int autoCancelThresholdInMin) {
        mPreferencesHelper.saveAutoCancelThresholdInMin(autoCancelThresholdInMin);
    }

    @Override
    public boolean getAllowArrival() {
        return mPreferencesHelper.getAllowArrival();
    }

    @Override
    public boolean getAllowArrivalOnGeoIn() {
        return mPreferencesHelper.getAllowArrivalOnGeoIn();
    }

    @Override
    public int getAutoCancelThresholdInMin() {
        return mPreferencesHelper.getAutoCancelThresholdInMin();
    }

    @Override
    public long getCurrentTime() {
        return mPreferencesHelper.getCurrentTime();
    }

    @Override
    public void setCurrentTime(long currentTimeMillis) {
        mPreferencesHelper.setCurrentTime(currentTimeMillis);
    }

    @Override
    public float getRadius() {
        return mPreferencesHelper.getRadius();
    }

    @Override
    public void setRadius(float radius) {
        mPreferencesHelper.setRadius(radius);
    }

    @Override
    public void saveAppAutoStartFlag(boolean isAutoStart) {
        mPreferencesHelper.saveAppAutoStartFlag(isAutoStart);
    }

    @Override
    public boolean getAppAutoStartFlag() {
        return mPreferencesHelper.getAppAutoStartFlag();
    }

    @Override
    public boolean getPunchStatus() {
        return mPreferencesHelper.getPunchStatus();
    }

    @Override
    public void setPunchStatus(boolean isPunchedIn) {
        mPreferencesHelper.setPunchStatus(isPunchedIn);
    }

    @Override
    public long getPunchInTime() {
        return mPreferencesHelper.getPunchInTime();
    }

    @Override
    public void setPunchInTime(long punchInTime) {
        mPreferencesHelper.setPunchInTime(punchInTime);

    }

//    @Override
//    public int getTimeElapsedPunchedIn(){
//        return mPreferencesHelper.getTimeElapsedPunchedIn();
//
//    }

//    @Override
//    public void setTimeElapsedPunchedIn(int counter) {
//        mPreferencesHelper.setTimeElapsedPunchedIn(counter);
//    }

    @Override
    public long getPunchOutTime() {
        return mPreferencesHelper.getPunchOutTime();
    }

    @Override
    public void setPunchOutTime(long punchOutTime) {
        mPreferencesHelper.setPunchOutTime(punchOutTime);

    }

    @Override
    public long getLastPunchOutTime() {
        return mPreferencesHelper.getLastPunchOutTime();
    }

    @Override
    public void setLastPunchOutTime(long punchOutTime) {
        mPreferencesHelper.setLastPunchOutTime(punchOutTime);
    }

    @Override
    public void getConfig(HttpManager httpManager, ApiCallback callBack) {
        networkManager.getConfig(httpManager, callBack);
    }

    @Override
    public void login(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.login(apiCallback, httpManager, data, api);
    }

    @Override
    public void verifyOtp(HttpManager httpManager, ApiCallback apiCallback, Object data, Api api) {
        networkManager.verifyOtp(httpManager, apiCallback, data, api);
    }

    @Override
    public void signUp(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.signUp(apiCallback, httpManager, data, api);
    }

    @Override
    public void buddyListing(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        networkManager.buddyListing(apiCallback, httpManager, api, data);
    }

    @Override
    public void addBuddyInvitation(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.addBuddyInvitation(apiCallback, httpManager, data, api);
    }

    @Override
    public void addFleet(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.addFleet(apiCallback, httpManager, data, api);
    }

    @Override
    public void updateBuddy(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.updateBuddy(apiCallback, httpManager, data, api);
    }

    @Override
    public void getProfile(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getProfile(apiCallback, httpManager, api);
    }

    @Override
    public void updateProfile(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.updateProfile(apiCallback, httpManager, data, api);
    }

    @Override
    public void updateProfilePic(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.updateProfilePic(apiCallback, httpManager, data, api);
    }

    @Override
    public void updateDocuments(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.updateDocuments(apiCallback, httpManager, data, api);
    }

    @Override
    public void addDocuments(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.addDocuments(apiCallback, httpManager, data, api);
    }

    @Override
    public void getTasksList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        networkManager.getTasksList(apiCallback, httpManager, data, apiUrl);
    }

    @Override
    public void fleetListing(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.fleetListing(apiCallback, httpManager, data, api);
    }

    @Override
    public void createTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.createTask(apiCallback, httpManager, data, api);
    }

    @Override
    public void getUserListForSuggestion(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getUserListForSuggestion(apiCallback, httpManager, data, api);
    }

    @Override
    public void getRegionList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getRegionList(apiCallback, httpManager, data, api);
    }

    @Override
    public void getStateList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getStateList(apiCallback, httpManager, data, api);
    }

    @Override
    public void getCityList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getCityList(apiCallback, httpManager, data, api);
    }

    @Override
    public void getHubList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getHubList(apiCallback, httpManager, data, api);

    }

    @Override
    public void logout(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.logout(apiCallback, httpManager, api);
    }

    @Override
    public void acceptTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.acceptTask(apiCallback, httpManager, data, api);
    }

    @Override
    public void getTaskById(ApiCallback apiCallBack, HttpManager httpManager, Object data, Api api) {
        networkManager.getTaskById(apiCallBack, httpManager, data, api);
    }

    @Override
    public void rejectTask(@NotNull ApiCallback apiCallBack, @NotNull HttpManager httpManager, @NotNull Object request, @NotNull Api api) {
        networkManager.rejectTask(apiCallBack, httpManager, request, api);
    }

    @Override
    public void deleteBuddy(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        networkManager.deleteBuddy(apiCallback, httpManager, api, data);
    }

    @Override
    public void deleteFleet(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.deleteFleet(apiCallback, httpManager, data, api);
    }

    @Override
    public void changeFleetStatus(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.deleteFleet(apiCallback, httpManager, data, api);
    }

    @Override
    public void getInvites(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api) {
        networkManager.getInvites(apiCallback, httpManager, api);
    }

    @Override
    public void acceptRejectRequest(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object request, @NotNull Api api) {
        networkManager.acceptRejectRequest(apiCallback, httpManager, request, api);
    }

    @Override
    public void startTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.startTask(apiCallback, httpManager, data, api);
    }

    @Override
    public void cancelTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.cancelTask(apiCallback, httpManager, data, api);
    }

    @Override
    public void endTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.endTask(apiCallback, httpManager, data, api);
    }

    @Override
    public void callHomeApi(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        networkManager.callHomeApi(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateTask(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        networkManager.updateTask(apiCallback, httpManager, data, api);
    }

    @Override
    public void getNotifications(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api) {
        networkManager.getNotifications(apiCallback, httpManager, api);
    }

    @Override
    public void clearNotifications(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api) {
        networkManager.clearNotifications(apiCallback, httpManager, api);
    }

    @Override
    public void cancelTrackingRequest(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.cancelTrackingRequest(apiCallback, httpManager, data, api);
    }

    @Override
    public void callDashboardApi(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        networkManager.callDashboardApi(apiCallback, httpManager, api, data);
    }

    @Override
    public void uploadFiles(@NotNull ApiCallback apiCallback, @Nullable HttpManager httpManager, @NotNull Object data, @Nullable Api api) {
        networkManager.uploadFiles(apiCallback, httpManager, data, api);
    }

    @Override
    public void getSettings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api) {
        networkManager.getSettings(apiCallback, httpManager, api);
    }

    @Override
    public void getBalance(@NonNull ApiCallback apiCallback, @NonNull HttpManager httpManager, @NonNull Api api) {
        networkManager.getBalance(apiCallback, httpManager, api);
    }

    @Override
    public void saveSettings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        networkManager.saveSettings(apiCallback, httpManager, data, api);
    }

    @Override
    public void getSharableLink(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        networkManager.getSharableLink(apiCallback, httpManager, data, api);
    }

    @Override
    public void uploadFormList(@NotNull ApiCallback apiCallback, @Nullable HttpManager httpManager, @NotNull Object data, @Nullable Api api) {
        networkManager.uploadFormList(apiCallback, httpManager, data, api);
    }

    @Override
    public void arriveReachTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.arriveReachTask(apiCallback, httpManager, data, api);
    }

    @Override
    public void getMyEarnings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.getMyEarnings(apiCallback, httpManager, data, api);
    }

    @Override
    public void checkInventory(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.checkInventory(apiCallback, httpManager, data, api);
    }

    @Override
    public void linkInventory(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.linkInventory(apiCallback, httpManager, data, api);
    }

    @Override
    public void employeeListInEvents(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.employeeListInEvents(apiCallback, httpManager, data, api);
    }

    @Override
    public void getAllPost(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.getAllPost(apiCallback, httpManager, data, api);
    }

    @Override
    public void getFeedDetails(@NonNull ApiCallback apiCallback, @NonNull HttpManager httpManager, @NonNull Api api) {
        networkManager.getFeedDetails(apiCallback, httpManager, api);
    }

    @Override
    public void getQrCodeValue(@NonNull ApiCallback apiCallback, @NonNull HttpManager httpManager, @NonNull Api api, @NonNull Object data) {
        networkManager.getQrCodeValue(apiCallback, httpManager, api,data);
    }

    @Override
    public void getComments(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.getComments(apiCallback, httpManager, data, api);
    }

    @Override
    public void updateReaction(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.updateReaction(apiCallback, httpManager, data, api);
    }

    @Override
    public void getAllLikes(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.getAllLikes(apiCallback, httpManager, data, api);
    }


    @Override
    public void getCategoryGroup(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {

        networkManager.getCategoryGroup(apiCallback, httpManager, data, api);
    }

    @Override
    public void getTaskByDate(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        networkManager.getTaskByDate(apiCallback, httpManager, data, api);
    }

    @Override
    public void punch(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.punch(apiCallback, httpManager, data, api);
    }

    @Override
    public void markedOnlineOffLine(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.markedOnlineOffLine(apiCallback, httpManager, data, api);
    }

    @Override
    public void punchInPunchOutData(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.punchInPunchOutData(apiCallback, httpManager, api);
    }

    @Override
    public void validateGeoPunchIn(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.validateGeoPunchIn(apiCallback, httpManager, data, api);
    }

    @Override
    public void getUserLocation(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getUserLocation(apiCallback, httpManager, api);
    }

    @Override
    public void deleteUserLocation(ApiCallback apiCallback, HttpManager httpManager, Api api, Object hub) {
        networkManager.deleteUserLocation(apiCallback, httpManager, api, hub);
    }

    @Override
    public void updateUserLocation(ApiCallback apiCallback, AddPlaceRequest addPlaceRequest, HttpManager httpManager, Api api) {
        networkManager.updateUserLocation(apiCallback, addPlaceRequest, httpManager, api);
    }

    @Override
    public void getAttendance(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getAttendance(apiCallback, httpManager, data, api);

    }


    @Override
    public void applyLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.applyLeave(apiCallback, httpManager, data, api);

    }

    @Override
    public void applyLeaveEdit(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.applyLeaveEdit(apiCallback, httpManager, data, api);

    }

    @Override
    public void getServerTime(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getServerTime(apiCallback, httpManager, api);
    }

    @Override
    public void getLeaveSummary(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getLeaveSummary(apiCallback, httpManager, api);

    }

    @Override
    public void getMyLeaves(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getMyLeaves(apiCallback, httpManager, data, api);

    }


    @Override
    public void cancelLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.cancelLeave(apiCallback, httpManager, data, api);

    }

    @Override
    public void approveLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.cancelLeave(apiCallback, httpManager, data, api);

    }

    @Override
    public void rejectLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.cancelLeave(apiCallback, httpManager, data, api);

    }

    @Override
    public void getLeaveRequests(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.cancelLeave(apiCallback, httpManager, data, api);

    }

    @Override
    public void executeUpdateTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.executeUpdateTask(apiCallback, httpManager, data, api);
    }

    @Override
    public void executeMap(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @Nullable Api api) {
        networkManager.executeMap(apiCallback, httpManager, api);
    }

    @Override
    public void heartBeat(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.heartBeat(apiCallback, httpManager, data, api);

    }

    @Override
    public void taskData(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.taskData(apiCallback, httpManager, data, api);

    }

    @Override
    public void getAccountList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getAccountList(apiCallback, httpManager, data, api);
    }

    @Override
    public void initiateSignUp(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.initiateSignUp(apiCallback, httpManager, data, api);
    }

    @Override
    public void updateService(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.updateService(apiCallback, httpManager, data, api);
    }

    @Override
    public void getSavedServices(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getSavedServices(apiCallback, httpManager, api);
    }

    @Override
    public void updateSavedServices(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.updateSavedServices(apiCallback, httpManager, data, api);
    }

    @Override
    public void getCart(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.getCart(apiCallback, httpManager, data, api);
    }

    @Override
    public void applyCoupon(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.applyCoupon(apiCallback, httpManager, data, api);
    }

    @Override
    public void createOrder(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.createOrder(apiCallback, httpManager, data, api);
    }

    @Override
    public void postFileErrorToServer(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        networkManager.postFileErrorToServer(apiCallback, httpManager, data, api);
    }

    @Override
    public void getInsights(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getInsights(apiCallback, httpManager, api);
    }

    @Override
    public void addUser(ApiCallback apiCallback, AddEmployeeRequest data, HttpManager httpManager, Api api) {
        networkManager.addUser(apiCallback, data, httpManager, api);
    }

    @Override
    public void updateUser(ApiCallback apiCallback, AddEmployeeRequest data, HttpManager httpManager, Api api) {
        networkManager.updateUser(apiCallback, data, httpManager, api);
    }




    @Override
    public void getUserList(ApiCallback apiCallback, HttpManager httpManager, Api api, UserGetRequest userGetRequest) {
        networkManager.getUserList(apiCallback, httpManager, api,userGetRequest);
    }

    @Override
    public void getProductsCategory(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getProductsCategory(apiCallback, httpManager, api);
    }

    @Override
    public void getProductsSubCategory(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getProductsSubCategory(apiCallback, httpManager, api);
    }

    @Override
    public void getProductTerminalCatgeory(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getProductTerminalCatgeory(apiCallback, httpManager, api);
    }

    @Override
    public void getProductDetails(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getProductDetails(apiCallback, httpManager, api);
    }

    @Override
    public void getStockEntry(ApiCallback apiCallback, StockEntryRequest data, HttpManager httpManager, Api api) {
        networkManager.getStockEntry(apiCallback, data, httpManager, api);
    }

    @Override
    public void getStockHistoryDetails(ApiCallback apiCallback, StockEntryRequest data, HttpManager httpManager, Api api) {
        networkManager.getStockHistoryDetails(apiCallback, data, httpManager, api);
    }

    @Override
    public void getProducts(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getProducts(apiCallback, httpManager, api);
    }

    @Override
    public void getUnits(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getUnits(apiCallback, httpManager, api);
    }

    @Override
    public void searchUser(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.searchUser(apiCallback, httpManager, api);
    }

    @Override
    public void getUserDetails(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getUserDetails(apiCallback, httpManager, api);
    }

    @Override
    public void deleteUser(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.deleteUser(apiCallback, httpManager, api);
    }

    @Override
    public void getUserAddress(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getUserAddress(apiCallback, httpManager, api);
    }

    @Override
    public void deleteAddress(ApiCallback apiCallback, PlaceRequest data, HttpManager httpManager, Api api) {
        networkManager.deleteAddress(apiCallback, data, httpManager, api);
    }

    @Override
    public void updateAddress(ApiCallback apiCallback, AddressInfo data, HttpManager httpManager, Api api) {
        networkManager.updateAddress(apiCallback, data, httpManager, api);

    }

    @Override
    public void addAddress(ApiCallback apiCallback, AddressInfo data, HttpManager httpManager, Api api) {
        networkManager.addAddress(apiCallback, data, httpManager, api);
    }

    @Override
    public void addProductCategory(ApiCallback apiCallback, AddCategoryRequest data, HttpManager httpManager, Api api) {
        networkManager.addProductCategory(apiCallback, data, httpManager, api);
    }

    @Override
    public void updateProductCategory(ApiCallback apiCallback, AddCategoryRequest data, HttpManager httpManager, Api api) {
        networkManager.updateProductCategory(apiCallback, data, httpManager, api);
    }

    @Override
    public void addProduct(ApiCallback apiCallback, AddProductRequest data, HttpManager httpManager, Api api) {
        networkManager.addProduct(apiCallback, data, httpManager, api);
    }

    @Override
    public void updateProduct(ApiCallback apiCallback, AddProductRequest data, HttpManager httpManager, Api api) {
        networkManager.updateProduct(apiCallback, data, httpManager, api);
    }

    @Override
    public void deleteProductCategory(ApiCallback apiCallback, CataLogProductCategory data, HttpManager httpManager, Api api) {
        networkManager.deleteProductCategory(apiCallback, data, httpManager, api);
    }

    @Override
    public void deleteProduct(ApiCallback apiCallback, CatalogProduct data, HttpManager httpManager, Api api) {
        networkManager.deleteProduct(apiCallback, data, httpManager, api);
    }

    @Override
    public void updateStatusProductCategory(ApiCallback apiCallback, CataLogProductCategory data, HttpManager httpManager, Api api) {
        networkManager.updateStatusProductCategory(apiCallback, data, httpManager, api);
    }

    @Override
    public void getDocumentById(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getDocumentById(apiCallback,httpManager,api);
    }

    @Override
    public void getDocumentByType(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.getDocumentByType(apiCallback,httpManager,api);
    }

    @Override
    public void deleteDocument(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.deleteDocument(apiCallback, httpManager, api);
    }

    @Override
    public void initiateDeviceChange(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        networkManager.initiateDeviceChange(apiCallback,httpManager,api);
    }


    @Override
    public void teamAttendance(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        networkManager.teamAttendance(apiCallback, httpManager, data, api);
    }

    @Override
    public String getSelfieUrl() {
        return mPreferencesHelper.getSelfieUrl();
    }

    @Override
    public void setSelfieUrl(String url) {
        mPreferencesHelper.setSelfieUrl(url);
    }

    @Override
    public Map<String, Task> getTimerCallToAction() {
        return mPreferencesHelper.getTimerCallToAction();
    }

    @Override
    public void setTimerCallToAction(Map<String, Task> taskMap) {
        mPreferencesHelper.setTimerCallToAction(taskMap);
    }

    @Override
    public void setPendingTimeForTask(Map<String, Long> taskMap) {
        mPreferencesHelper.setPendingTimeForTask(taskMap);
    }

    @Override
    public Map<String, Long> getPendingTime() {
        return mPreferencesHelper.getPendingTime();
    }

    @Override
    public HashMap<String, DataObject> getTaskDetails() {
        return mPreferencesHelper.getTaskDetails();
    }

    @Override
    public void setTaskDetails(HashMap<String, DataObject> hashMap) {
        mPreferencesHelper.setTaskDetails(hashMap);
    }

    @Override
    public void setIdleTripActive(boolean idleTripActive) {
        mPreferencesHelper.setIdleTripActive(idleTripActive);
    }

    @Override
    public Map<Integer, List<ShiftTime>> getOldShiftMap() {
        return mPreferencesHelper.getOldShiftMap();
    }

    @Override
    public void setOldShiftMap(Map<Integer, List<ShiftTime>> oldShiftMap) {
        mPreferencesHelper.setOldShiftMap(oldShiftMap);
    }

    @Override
    public Map<Integer, List<ShiftTime>> getAlwaysNewShiftMap() {
        return mPreferencesHelper.getAlwaysNewShiftMap();
    }

    @Override
    public void setAlwaysNewShiftMap(Map<Integer, List<ShiftTime>> oldShiftMap) {
        mPreferencesHelper.setAlwaysNewShiftMap(oldShiftMap);
    }

    @Override
    public Api getHeartBeat() {
        return mPreferencesHelper.getHeartBeat();
    }

    @Override
    public void setHeartBeat(Api api) {
        mPreferencesHelper.setHeartBeat(api);
    }

    @Override
    public boolean getIdleTripActive() {
        return mPreferencesHelper.getIdleTripActive();
    }

    @Override
    public void setIsTrackingLiveTrip(boolean isLive) {
        mPreferencesHelper.setIsTrackingLiveTrip(isLive);
    }

    @Override
    public boolean getIsTrackingLiveTrip() {
        return mPreferencesHelper.getIsTrackingLiveTrip();
    }

    @Override
    public AlarmInfo getStartAlarmInfo() {
        return mPreferencesHelper.getStartAlarmInfo();
    }

    @Override
    public void setStartAlarmInfo(AlarmInfo alarmInfo) {
        mPreferencesHelper.setStartAlarmInfo(alarmInfo);
    }

    @Override
    public AlarmInfo getStopAlarmInfo() {
        return mPreferencesHelper.getStartAlarmInfo();
    }

    @Override
    public void setStopAlarmInfo(AlarmInfo alarmInfo) {
        mPreferencesHelper.setStartAlarmInfo(alarmInfo);
    }

    @Override
    public boolean getIsMapChanged() {
        return mPreferencesHelper.getIsMapChanged();
    }

    @Override
    public void setIsMapChanged(boolean isMapChanged) {
        mPreferencesHelper.setIsMapChanged(isMapChanged);
    }

    @Override
    public boolean getIsHubChanged() {
        return mPreferencesHelper.getIsHubChanged();
    }

    @Override
    public void setIsHubChanged(boolean isHubChanged) {
        mPreferencesHelper.setIsHubChanged(isHubChanged);
    }

    @Override
    public String getUpdatedSdkConfig() {
        return mPreferencesHelper.getUpdatedSdkConfig();
    }

    @Override
    public void setUpdatedSdkConfig(String updatedSdkConfig) {
        mPreferencesHelper.setUpdatedSdkConfig(updatedSdkConfig);
    }

    @Override
    public List<WorkFlowCategories> getWorkFlowCategoriesList() {
        return mPreferencesHelper.getWorkFlowCategoriesList();
    }

    @Override
    public void saveFlavorList(List<Flavour> flavourList) {
        mPreferencesHelper.saveFlavorList(flavourList);
    }

    @Override
    public List<Flavour> getFlavorList() {
        return mPreferencesHelper.getFlavorList();
    }

    @Override
    public void saveWorkFlowCategoriesChannel(Map<String, ChannelConfig> channelConfigMap) {
        mPreferencesHelper.saveWorkFlowCategoriesChannel(channelConfigMap);
    }

    @Override
    public Map<String, ChannelConfig> getWorkFlowCategoriesListChannel() {
        return mPreferencesHelper.getWorkFlowCategoriesListChannel();
    }

    @Override
    public void saveFlavourMap(Map<String, Flavour> flavourMap) {
        mPreferencesHelper.saveFlavourMap(flavourMap);
    }

    @Override
    public Map<String, Flavour> getFlavourMap() {
        return mPreferencesHelper.getFlavourMap();
    }

    @Override
    public void saveWorkFlowAllowCreation(Map<String, Boolean> allowCreationMap) {
        mPreferencesHelper.saveWorkFlowAllowCreation(allowCreationMap);
    }

    @Override
    public Map<String, Boolean> getWorkFlowAllowCreation() {
        return mPreferencesHelper.getWorkFlowAllowCreation();
    }

    @Override
    public void setIsFleetAndBuddyShow(boolean iShow) {
        mPreferencesHelper.setIsFleetAndBuddyShow(iShow);
    }

    @Override
    public boolean getIsFleetAndBuddySHow() {
        return mPreferencesHelper.getIsFleetAndBuddySHow();
    }

    @Override
    public void setIsIdealTrackingEnable(boolean isLiveTrackingEnable) {
        mPreferencesHelper.setIsIdealTrackingEnable(isLiveTrackingEnable);
    }

    @Override
    public boolean getIsIdealTrackingEnable() {
        return mPreferencesHelper.getIsIdealTrackingEnable();
    }

    @Override
    public void setLocationRequired(boolean locationRequired) {
        mPreferencesHelper.setLocationRequired(locationRequired);
    }

    @Override
    public boolean getLocationRequired() {
        return mPreferencesHelper.getLocationRequired();
    }

    @Override
    public void setDailyIdleTaskOffEnable(boolean isDailyTask) {
        mPreferencesHelper.setDailyIdleTaskOffEnable(isDailyTask);
    }

    @Override
    public boolean getDailyIdleTaskOffEnable() {
        return mPreferencesHelper.getDailyIdleTaskOffEnable();
    }

    @Override
    public void setEnablePunchGeofencing(boolean locationRequired) {
        mPreferencesHelper.setEnablePunchGeofencing(locationRequired);
    }

    @Override
    public boolean getEnablePunchGeofencing() {
        return mPreferencesHelper.getEnablePunchGeofencing();
    }

    @Override
    public void savePhoneUsageLimitInMinutesOnIdleTrip(int minutes) {
        mPreferencesHelper.savePhoneUsageLimitInMinutesOnIdleTrip(minutes);
    }

    @Override
    public Integer getPhoneUsageLimitInMinutesOnIdleTrip() {
        return mPreferencesHelper.getPhoneUsageLimitInMinutesOnIdleTrip();
    }

    @Override
    public void savePhoneUsageLimitInMinutesOnTaskTrip(int minutes) {
        mPreferencesHelper.savePhoneUsageLimitInMinutesOnTaskTrip(minutes);
    }

    @Override
    public Integer getPhoneUsageLimitInMinutesOnTaskTrip() {
        return mPreferencesHelper.getPhoneUsageLimitInMinutesOnTaskTrip();
    }

    @Override
    public void saveAccountsList(List<UserAccount> userAccounts) {
        mPreferencesHelper.saveAccountsList(userAccounts);
    }

    @Override
    public List<UserAccount> getAccountList() {
        return mPreferencesHelper.getAccountList();
    }

    @Override
    public void saveGeoFence(List<GeoFenceData> geoFenceData) {
        mPreferencesHelper.saveGeoFence(geoFenceData);
    }

    @Override
    public List<GeoFenceData> getGeoFenceList() {
        return mPreferencesHelper.getGeoFenceList();
    }

    @Override
    public String getUserType() {
        return mPreferencesHelper.getUserType();
    }

    @Override
    public void setUserType(UserType userType) {
        mPreferencesHelper.setUserType(userType);
    }

    @Override
    public String getStatus() {
        return mPreferencesHelper.getStatus();
    }

    @Override
    public void setStatus(String offlineOnLIne) {
        mPreferencesHelper.setStatus(offlineOnLIne);
    }

    @Override
    public void saveUserTypeList(List<UserType> userType) {
        mPreferencesHelper.saveUserTypeList(userType);
    }

    @Override
    public List<UserType> getUserTypeList() {
        return mPreferencesHelper.getUserTypeList();
    }

    @Override
    public void saveOnTripOverStopping(@androidx.annotation.Nullable OverstoppingConfig overstoppingConfig) {
        mPreferencesHelper.saveOnTripOverStopping(overstoppingConfig);
    }

    @Override
    public void saveOnIdleOverStopping(@androidx.annotation.Nullable OverstoppingConfig overstoppingConfig) {
        mPreferencesHelper.saveOnIdleOverStopping(overstoppingConfig);
    }

    @Override
    public OverstoppingConfig getOnTripOverStopping() {
        return mPreferencesHelper.getOnTripOverStopping();
    }

    @Override
    public OverstoppingConfig getOnIdleOverStopping() {
        return mPreferencesHelper.getOnIdleOverStopping();
    }

    @Override
    public int getDefDateRange() {
        return mPreferencesHelper.getDefDateRange();
    }

    @Override
    public void setDefDateRange(int dateRange) {
        mPreferencesHelper.setDefDateRange(dateRange);
    }

    @Override
    public int getMaxDateRange() {
        return mPreferencesHelper.getMaxDateRange();
    }

    @Override
    public void setMaxDateRange(int dateRange) {
        mPreferencesHelper.setMaxDateRange(dateRange);
    }

    @Override
    public int getMaxPastDaysAllowed() {
        return mPreferencesHelper.getMaxPastDaysAllowed();
    }

    @Override
    public void setMaxPastDaysAllowed(int dateRange) {
        mPreferencesHelper.setMaxPastDaysAllowed(dateRange);
    }

    @Override
    public void setPunchId(String punchId) {
        mPreferencesHelper.setPunchId(punchId);
    }

    @Override
    public String getPunchId() {
        return mPreferencesHelper.getPunchId();
    }

    @Override
    public boolean getVoiceAlertsTracking() {
        return mPreferencesHelper.getVoiceAlertsTracking();
    }

    @Override
    public void setVoiceAlertsTracking(boolean isAlerts) {
        mPreferencesHelper.setVoiceAlertsTracking(isAlerts);
    }

    @Override
    public Map<String, SaveFilterData> getFilterMap() {
        return mPreferencesHelper.getFilterMap();
    }

    @Override
    public void saveFilterMap(Map<String, SaveFilterData> saveFilterData) {
        mPreferencesHelper.saveFilterMap(saveFilterData);

    }

    @Override
    public boolean getEnableWalletFlag() {
        return mPreferencesHelper.getEnableWalletFlag();
    }

    @Override
    public void setEnableWalletFlag(boolean isEnable) {
        mPreferencesHelper.setEnableWalletFlag(isEnable);
    }

    @Override
    public boolean isManger() {
        return mPreferencesHelper.isManger();
    }

    @Override
    public void setManger(boolean isEnable) {
        mPreferencesHelper.setManger(isEnable);
    }

    @Override
    public void setFormDataMap(Map<String, ArrayList<FormData>> formDataList) {
        mPreferencesHelper.setFormDataMap(formDataList);
    }

    @Override
    public Map<String, ArrayList<FormData>> getFormDataMap() {
        return mPreferencesHelper.getFormDataMap();
    }

    @Override
    public void setUnits(LinkedHashMap<String, String> formDataList) {
        mPreferencesHelper.setUnits(formDataList);
    }

    @Override
    public LinkedHashMap<String, String> getUnits() {
        return mPreferencesHelper.getUnits();
    }

    @Override
    public void setPackUnits(LinkedHashMap<String, String> formDataList) {
        mPreferencesHelper.setPackUnits(formDataList);
    }

    @Override
    public LinkedHashMap<String, String> getPackUnits() {
        return mPreferencesHelper.getPackUnits();
    }

    @Override
    public boolean getTimeReminderFlag() {
        return mPreferencesHelper.getTimeReminderFlag();
    }

    @Override
    public void setTimeReminderFlag(boolean isEnable) {
        mPreferencesHelper.setTimeReminderFlag(isEnable);
    }

    @Override
    public boolean getLocationReminderFlag() {
        return mPreferencesHelper.getLocationReminderFlag();
    }

    @Override
    public void setLocationReminderFlag(boolean isEnable) {
        mPreferencesHelper.setLocationReminderFlag(isEnable);
    }

    @Override
    public void setEventIdInPref(Map<String, Long> eventIdInPref) {
        mPreferencesHelper.setEventIdInPref(eventIdInPref);
    }

    @Override
    public Map<String, Long> getEventIdMap() {
        return mPreferencesHelper.getEventIdMap();
    }

    @Override
    public void setGeoFence(Map<String, GeoCoordinates> formDataList) {
        mPreferencesHelper.setGeoFence(formDataList);
    }

    @Override
    public Map<String, GeoCoordinates> getGeoFence() {
        return mPreferencesHelper.getGeoFence();
    }

    @Override
    public String getTimeBeforeTime() {
        return mPreferencesHelper.getTimeBeforeTime();
    }

    @Override
    public void setBeforeTime(String time) {
        mPreferencesHelper.setBeforeTime(time);
    }

    @Override
    public boolean userGeoFilters() {
        return mPreferencesHelper.userGeoFilters();
    }

    @Override
    public void setUserGeoFilters(boolean isEnable) {
        mPreferencesHelper.setUserGeoFilters(isEnable);
    }

    @Override
    public String getUserRoleId() {
        return mPreferencesHelper.getUserRoleId();
    }

    @Override
    public void setUserRoleId(String roleId) {
        mPreferencesHelper.setUserRoleId(roleId);
    }

    @Override
    public boolean getBuddyListing() {
        return mPreferencesHelper.getBuddyListing();
    }

    @Override
    public void setBuddyListing(boolean buddyListing) {
        mPreferencesHelper.setBuddyListing(buddyListing);
    }

    @Override
    public boolean getServicePref() {
        return mPreferencesHelper.getServicePref();
    }

    @Override
    public void setServicePref(boolean selected) {
        mPreferencesHelper.setServicePref(selected);
    }

    @Override
    public boolean getInsights() {
        return mPreferencesHelper.getInsights();

    }

    @Override
    public void setInsights(boolean selected) {
        mPreferencesHelper.setInsights(selected);
    }

    @Override
    public void saveUserHubList(List<Hub> hubs) {
        mPreferencesHelper.saveUserHubList(hubs);
    }

    @Override
    public List<Hub> getUserHubList() {
        return mPreferencesHelper.getUserHubList();
    }

    @Override
    public String getSelectedLocation() {
        return mPreferencesHelper.getSelectedLocation();
    }

    @Override
    public void setSelectedLocation(String hubId) {
        mPreferencesHelper.setSelectedLocation(hubId);
    }

    @Override
    public void saveRoleConfigDataList(List<RoleConfigData> roleConfigDataList) {
        mPreferencesHelper.saveRoleConfigDataList(roleConfigDataList);
    }

    @Override
    public List<RoleConfigData> getRoleConfigDataList() {
        return mPreferencesHelper.getRoleConfigDataList();
    }

    @Override
    public void saveProductInCartWRC(Map<String, Map<String, CatalogProduct>> map) {
        mPreferencesHelper.saveProductInCartWRC(map);
    }

    @Override
    public HashMap<String, HashMap<String, CatalogProduct>> getProductInCartWRC() {
        return mPreferencesHelper.getProductInCartWRC();
    }

    @Override
    public void saveWorkFlowCategoriesList(List<WorkFlowCategories> workFlowCategoriesList) {
        mPreferencesHelper.saveWorkFlowCategoriesList(workFlowCategoriesList);
    }


}