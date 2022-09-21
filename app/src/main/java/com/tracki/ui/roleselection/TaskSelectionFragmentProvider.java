package com.tracki.ui.roleselection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;



@Module
public abstract class TaskSelectionFragmentProvider {

    @ContributesAndroidInjector(modules = TaskSelectionFragmentModule.class)
    abstract TaskSelectionFragment provideTaskSelectionFragmentFactory();
}