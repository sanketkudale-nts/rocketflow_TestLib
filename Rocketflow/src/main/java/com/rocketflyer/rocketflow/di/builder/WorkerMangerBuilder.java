package com.rocketflyer.rocketflow.di.builder;

import com.tracki.data.local.prefs.ApplyReminderWork;
import com.tracki.data.local.prefs.GetUserLocationWorker;
import com.tracki.data.local.prefs.GetUserLocationWorkerModule;
import com.tracki.data.local.prefs.PostFileErrorToServerWork;
import com.tracki.data.local.prefs.SendErrorToServerViewModule;
import com.tracki.data.local.prefs.WorkerManagerModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Vikas Kesharvani on 15/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
@Module
public abstract class WorkerMangerBuilder {
    @ContributesAndroidInjector(modules = WorkerManagerModule.class)
    abstract ApplyReminderWork bindWorkerManger();

    @ContributesAndroidInjector(modules = SendErrorToServerViewModule.class)
    abstract PostFileErrorToServerWork bindPostFileErrorToServerWorkWorkerManger();

    @ContributesAndroidInjector(modules = GetUserLocationWorkerModule.class)
    abstract GetUserLocationWorker bindGetUserLocationWorkerManger();
}
