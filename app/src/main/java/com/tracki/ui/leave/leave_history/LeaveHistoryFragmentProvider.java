package com.tracki.ui.leave.leave_history;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class LeaveHistoryFragmentProvider {

    @ContributesAndroidInjector(modules = LeaveHistoryFragmentModule.class)
    abstract LeaveHistoryFragment provideLeaveHistoryFragmentFactory();

}
