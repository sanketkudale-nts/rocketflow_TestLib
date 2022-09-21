package com.tracki.ui.taskdetails.timeline;



import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Vikas Kesharvani on 16/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */


@Module
public abstract class TaskDetailsFragmentProvider {

    @ContributesAndroidInjector(modules = TaskDetailsFragmentModule.class)
    abstract TaskDetailsFragment provideTaskDetailsFragmentFactory();
}