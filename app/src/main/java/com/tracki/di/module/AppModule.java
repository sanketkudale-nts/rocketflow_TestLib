package com.tracki.di.module;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tracki.data.AppDataManager;
import com.tracki.data.DataManager;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.network.HttpManager;
import com.tracki.data.network.NetworkManager;
import com.tracki.data.network.NetworkManagerImpl;
import com.tracki.di.PreferenceInfo;
import com.tracki.utils.AnalyticsHelper;
import com.tracki.utils.AppConstants;
import com.tracki.utils.rx.AppSchedulerProvider;
import com.tracki.utils.rx.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul.
 */
@Module
public class AppModule {

    @Provides
    @Singleton
    NetworkManager provideNetworkManager() {
        return new NetworkManagerImpl();
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @Provides
    @Singleton
    HttpManager provideHttpManager(Context context, PreferencesHelper preferencesHelper) {
        return HttpManager.getInstance(context, preferencesHelper);
    }

    @Provides
    @Singleton
    AnalyticsHelper provideAnalyticsHelper(Context context) {
        return new AnalyticsHelper(context);
    }


}
