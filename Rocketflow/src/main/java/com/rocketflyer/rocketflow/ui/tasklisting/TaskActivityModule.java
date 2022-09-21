package com.rocketflyer.rocketflow.ui.tasklisting;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 8/10/18
 */
@Module
public class TaskActivityModule {

    @Provides
    TaskViewModel provideTaskViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new TaskViewModel(dataManager, schedulerProvider);
    }

    @Provides
    TaskPagerAdapter provideTrackingBuddyPagerAdapter(TaskActivity activity) {
        return new TaskPagerAdapter(activity.getSupportFragmentManager());
    }

}
