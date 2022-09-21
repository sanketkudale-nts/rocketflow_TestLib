package com.rocketflyer.rocketflow.ui.attendance.attendance_tab;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AttendanceFragmentProvider {

    @ContributesAndroidInjector(modules = AttendanceFragmentModule.class)
    abstract AttendanceFragment provideAttendanceFragmentFactory();

}
