package com.rocketflyer.rocketflow.ui.addbuddy;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 14/9/18
 */
@Module
public class AddBuddyActivityModule {

    @Provides
    AddBuddyViewModel provideAddDriverViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new AddBuddyViewModel(dataManager, schedulerProvider);
    }

    @Provides
    AddBuddyAdapter provideAddDriverAdapter() {
        return new AddBuddyAdapter(new ArrayList<>());
    }
}
