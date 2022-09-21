package com.rocketflyer.rocketflow.ui.leave.leave_approval;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class LeaveApprovalFragmentProvider {

    @ContributesAndroidInjector(modules = LeaveApprovalFragmentModule.class)
    abstract LeaveApprovalFragment provideLeaveApprovalFragmentFactory();

}
