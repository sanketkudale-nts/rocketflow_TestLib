package com.rocketflyer.rocketflow.ui.tasklisting.assignedtome;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AssignedtoMeFragmentProvider {

    @ContributesAndroidInjector(modules = AssignedtoMeFragmentModule.class)
    abstract AssignedtoMeFragment provideAssignedtoMeFragmentFactory();
}
