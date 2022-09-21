package com.rocketflyer.rocketflow.ui.attendance;

import com.tracki.data.DataManager;
import com.tracki.ui.attendance.AttendancePagerAdapter;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 8/10/18
 */
@Module
public class AttendanceActivityModule {

    @Provides
    AttendanceViewModel provideAtendanceViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new AttendanceViewModel(dataManager, schedulerProvider);
    }

    @Provides
    AttendancePagerAdapter provideAtendancePagerAdapter(AttendanceActivity activity) {
        return new AttendancePagerAdapter(activity.getSupportFragmentManager());
    }

}
