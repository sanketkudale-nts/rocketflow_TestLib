package com.rocketflyer.rocketflow.ui.otp;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 5/9/18
 */
@Module
public class OtpActivityModule {

    @Provides
    OtpViewModel provideLoginViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new OtpViewModel(dataManager, schedulerProvider);
    }
}
