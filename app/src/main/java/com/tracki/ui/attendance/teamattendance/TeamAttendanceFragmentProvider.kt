package com.tracki.ui.attendance.teamattendance

import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Vikas Kesharvani on 26/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */


@Module
public abstract class TeamAttendanceFragmentProvider {
    @ContributesAndroidInjector(modules = [TeamAttendanceModule::class])
    abstract fun provideTeamAttendanceFragmentFactory(): TeamAttendanceFragment?
}