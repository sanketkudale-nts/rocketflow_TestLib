package com.rocketflyer.rocketflow.ui.attendance;

import androidx.lifecycle.ViewModelProvider;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.ui.attendance.AttendancePagerAdapter;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */


@Module
public class AttendanceFragmentModule {

    @Provides
    AttendanceViewModel provideAttendanceViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new AttendanceViewModel(dataManager, schedulerProvider);
    }



    @Provides
    ViewModelProvider.Factory provideAttendanceViewModelFactory(AttendanceViewModel attendanceViewModel) {
        return new ViewModelProviderFactory<>(attendanceViewModel);
    }

    @Provides
    AttendancePagerAdapter provideAtendancePagerAdapter(AttendanceBaseFragment fragment) {
        return new AttendancePagerAdapter(fragment.getChildFragmentManager());
    }

}