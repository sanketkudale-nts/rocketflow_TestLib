package com.rocketflyer.rocketflow.ui.main.taskdashboard;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */


@Module
public abstract class TaskDashBoardFragmentProvider {

    @ContributesAndroidInjector(modules = TaskDashBoardFragmentModule.class)
    abstract TaskDashBoardFragment provideTaskDashBoardFragmentFactory();

}