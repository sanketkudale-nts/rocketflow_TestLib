package com.tracki.ui.trackingbuddy.buddydetail;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 9/10/18
 */
@Module
public class TrackingBuddyDetailActivityModule {
    @Provides
    TrackingBuddyDetailViewModel provideBuddyDetailActivityViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new TrackingBuddyDetailViewModel(dataManager, schedulerProvider);
    }
}
