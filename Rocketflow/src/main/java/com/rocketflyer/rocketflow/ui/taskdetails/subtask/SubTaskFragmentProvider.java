package com.rocketflyer.rocketflow.ui.taskdetails.subtask;

import com.tracki.ui.taskdetails.subtask.SubTaskFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Vikas Kesharvani on 17/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */

@Module
public abstract class SubTaskFragmentProvider {

    @ContributesAndroidInjector(modules = SubTaskFragmentModule.class)
    abstract SubTaskFragment provideSubTaskFragmentFactory();
}