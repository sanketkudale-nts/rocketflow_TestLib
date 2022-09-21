package com.rocketflyer.rocketflow.ui.fleetlisting;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 14/9/18
 */
@Module
public class FleetListingActivityModule {
    @Provides
    FleetListingViewModel provideFleetListingViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new FleetListingViewModel(dataManager, schedulerProvider);
    }

    @Provides
    FleetListingAdapter provideFleetListingAdapter() {
        return new FleetListingAdapter(new ArrayList<>());
    }
}
