package com.rocketflyer.rocketflow.ui.attendance;



import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
@Module
public abstract class AttendanceBaseFragmentProvider {

    @ContributesAndroidInjector(modules = AttendanceFragmentModule.class)
    abstract AttendanceBaseFragment provideAttendanceBaseFragmentFactory();

}