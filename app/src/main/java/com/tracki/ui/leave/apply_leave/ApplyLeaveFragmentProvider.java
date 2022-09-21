package com.tracki.ui.leave.apply_leave;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ApplyLeaveFragmentProvider {

    @ContributesAndroidInjector(modules = ApplyLeaveFragmentModule.class)
    abstract ApplyLeaveFragment provideApplyLeaveFragmentFactory();

}
