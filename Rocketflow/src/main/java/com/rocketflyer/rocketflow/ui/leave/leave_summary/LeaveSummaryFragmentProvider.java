package com.rocketflyer.rocketflow.ui.leave.leave_summary;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class LeaveSummaryFragmentProvider {

    @ContributesAndroidInjector(modules = LeaveSummaryFragmentModule.class)
    abstract LeaveSummaryFragment provideLeaveSummaryFragmentFactory();

}
