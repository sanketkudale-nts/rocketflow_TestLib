package com.tracki.ui.trackingbuddy;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 3/10/18
 */
@Module
public class TrackingBuddyActivityModule {

    @Provides
    TrackingBuddyViewModel provideTrackingBuddyViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new TrackingBuddyViewModel(dataManager, schedulerProvider);
    }

    @Provides
    TrackingBuddyPagerAdapter provideTrackingBuddyPagerAdapter(TrackingBuddyActivity activity) {
        return new TrackingBuddyPagerAdapter(activity.getSupportFragmentManager());
    }

}
