package com.rocketflyer.rocketflow.ui.leave;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 8/10/18
 */
@Module
public class LeaveActivityModule {

    @Provides
    LeaveViewModel provideLeaveViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new LeaveViewModel(dataManager, schedulerProvider);
    }

    @Provides
    LeavePagerAdapter provideLeavePagerAdapter(LeaveActivity activity) {
        return new LeavePagerAdapter(activity.getSupportFragmentManager());
    }

}
