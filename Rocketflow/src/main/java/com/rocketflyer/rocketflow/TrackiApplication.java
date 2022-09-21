package com.rocketflyer.rocketflow;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.core.content.ContextCompat;

import com.tracki.MyContentObserver;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.DynamicFormsNew;
import com.tracki.data.model.response.config.EmergencyContact;
import com.tracki.data.model.response.config.LookUps;
import com.tracki.data.model.response.config.Navigation;
import com.tracki.di.component.DaggerAppComponent;
import com.tracki.ui.receiver.ServiceRestartReceiver;
import com.tracki.utils.ApiType;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.JSONConverter;
import com.trackthat.lib.TrackThat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasServiceInjector;

//import com.crashlytics.android.Crashlytics;

/**
 * Created by rahul on 2/9/18
 * Application
 */
public class TrackiApplication extends Application implements HasActivityInjector,
        HasServiceInjector, HasBroadcastReceiverInjector {

    private static List<Navigation> navigationMenuList;
    private static Map<ApiType, Api> apiMap;
    private static String tncUrl, privacyPolicyUrl;
    private static HashMap<String, String> chatMap;
    private static Boolean autoStart;
    private static Boolean alertNotification;
    private static String sosMessage;
    private static ArrayList<EmergencyContact> emergencyContacts;
    private static List<DynamicFormsNew> dynamicFormsNews;
    private static List<LookUps> lookups;
    public static TrackiApplication instance;


    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Service> serviceDispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> receiverDispatchingAndroidInjector;
    private MyContentObserver contactObserver;

    public static List<DynamicFormsNew> getDynamicFormsNews() {
        return dynamicFormsNews;
    }

    public static void setDynamicFormsNews(List<DynamicFormsNew> dynamicFormsNews) {
        TrackiApplication.dynamicFormsNews = dynamicFormsNews;
    }

    public static List<LookUps> getLookups() {
        return lookups;
    }

    public static void setLookups(List<LookUps> lookups) {
        TrackiApplication.lookups = lookups;
    }



    public static Map<ApiType, Api> getApiMap() {
        if (apiMap == null) {
            return new HashMap<>();
        }
        return apiMap;
    }

    public static void setApiList(List<Api> apiList) {
        Map<ApiType, Api> hashMap = new HashMap<>();
        for (int i = 0; i < apiList.size(); i++) {
            Api list = apiList.get(i);
            if (list.getName() != null) {
                hashMap.put(list.getName(), list);
            }
        }

        TrackiApplication.apiMap = hashMap;
    }

    public static List<Navigation> getNavigationMenuList() {
        if (navigationMenuList != null)
            Collections.sort(navigationMenuList);
        return navigationMenuList;
    }

    public static void setNavigationMenuList(List<Navigation> navigationMenuList) throws JSONException {
        if (navigationMenuList != null) {
            if (navigationMenuList.size() > 0) {
                for (int i = 0; i < navigationMenuList.size(); i++) {
                    JSONConverter jsonConverter=new JSONConverter();
                    String strJson=jsonConverter.objectToJson(navigationMenuList.get(i));
                    JSONObject jsonObject=new JSONObject(strJson);
                    if(!jsonObject.isNull("title")){
                        if ( Objects.requireNonNull(navigationMenuList.get(i).getTitle()).equalsIgnoreCase("others")) {
                            Navigation nestedMenu = navigationMenuList.get(i);
                            if (nestedMenu.getNestedMenu() != null && nestedMenu.getNestedMenu().size() > 0) {
                                for (int j = 0; j < nestedMenu.getNestedMenu().size(); j++) {
                                    if (nestedMenu.getNestedMenu().get(j).getTitle().equalsIgnoreCase("privacy & policy")) {
                                        privacyPolicyUrl = nestedMenu.getNestedMenu().get(j).getActionConfig().getActionUrl();
                                    } else if (nestedMenu.getNestedMenu().get(j).getTitle().equalsIgnoreCase("terms & conditions")) {
                                        tncUrl = nestedMenu.getNestedMenu().get(j).getActionConfig().getActionUrl();
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }
        TrackiApplication.navigationMenuList = navigationMenuList;
    }

    public static String getPrivacyPolicyUrl() {
        return "https://rocketflow.in/privacypolicy";
       // return privacyPolicyUrl;
    }

    public static String getTncUrl() {
        return "https://rocketflow.in/termsandconditions";
        //return tncUrl;
    }

    public static boolean getAutoStart() {
        return autoStart;
    }

    public static void setAutoStart(boolean autoStart) {
        TrackiApplication.autoStart = autoStart;
    }

    public static Boolean getAlertNotification() {
        return alertNotification;
    }

    public static void setAlertNotification(Boolean alertNotification) {
        TrackiApplication.alertNotification = alertNotification;
    }

    public static void setEmergencyContactList(@Nullable ArrayList<EmergencyContact> emergencyContacts) {
        TrackiApplication.emergencyContacts = emergencyContacts;
    }

    public static ArrayList<EmergencyContact> getEmergencyContacts() {
        if (emergencyContacts == null) {
            emergencyContacts = new ArrayList<>();
        }
        return emergencyContacts;
    }

    public static String getSosMessage() {
        return sosMessage;
    }

    public static void setSosMessage(String sosMessage) {
        TrackiApplication.sosMessage = sosMessage;
    }

    public static HashMap<String, String> getChatMap() {
        return chatMap;
    }

    public static void setChatMap(@NotNull HashMap<String, String> map) {
        TrackiApplication.chatMap = map;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize crashlytics
        //Fabric.with(this, new Crashlytics());
        //initialize tracking SDK
        try {
            TrackThat.initialize(this,CommonUtils.getIMEINumber(this));
        }catch (Exception e){

        }

        //Stetho.initializeWithDefaults(this);
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
        if (instance == null) {
            instance = this;
        }
        contactReceiver();
        //register a receiver to check if user have a working internet connection
        registerReceiver(new ServiceRestartReceiver(), new IntentFilter(ServiceRestartReceiver.ACTION_INTERNET_CONNECTON));
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return receiverDispatchingAndroidInjector;
    }

    private void contactReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
            ) {

                registerContentObserver();
            }
        } else {
            registerContentObserver();
        }
    }

    public void registerContentObserver() {
        CommonUtils.showLogMessage("e", "registerContentObserver=>", "registerContentObserver ");
        contactObserver = new MyContentObserver();
        getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI, true,
                contactObserver);


    }
}
