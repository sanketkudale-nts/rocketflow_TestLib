package com.rocketflyer.rocketflow.ui.userattendancedetails;

import com.tracki.data.DataManager;
import com.tracki.ui.userattendancedetails.UserAttendanceDetailsViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;


@Module
public class UserAttendanceActivityModule {
    @Provides
    UserAttendanceDetailsViewModel provideUserAttendanceDetailsViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new UserAttendanceDetailsViewModel(dataManager, schedulerProvider);
    }
}
