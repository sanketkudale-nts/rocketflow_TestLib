package com.rocketflyer.rocketflow.ui.buddylisting;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 14/9/18
 */
@Module
public class BuddyListingActivityModule {
    @Provides
    BuddyListingViewModel provideDriverViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new BuddyListingViewModel(dataManager, schedulerProvider);
    }

    @Provides
    BuddyListingAdapter provideDriverAdapter() {
        return new BuddyListingAdapter(new ArrayList<>());
    }
}
