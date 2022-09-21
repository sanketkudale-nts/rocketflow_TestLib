package com.rocketflyer.rocketflow.ui.deviceChange;

import com.tracki.data.DataManager;
import com.tracki.ui.otp.OtpViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;


@Module
public class DeviceChangeActivityModule {

    @Provides
    DeviceChangeViewModel provideDeviceChangeViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new DeviceChangeViewModel(dataManager, schedulerProvider);
    }
}
