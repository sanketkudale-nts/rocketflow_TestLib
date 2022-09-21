package com.rocketflyer.rocketflow.ui.roleselection;

import com.tracki.ui.roleselection.TaskSelectionFragment;
import com.tracki.ui.roleselection.TaskSelectionFragmentModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;



@Module
public abstract class TaskSelectionFragmentProvider {

    @ContributesAndroidInjector(modules = TaskSelectionFragmentModule.class)
    abstract TaskSelectionFragment provideTaskSelectionFragmentFactory();
}