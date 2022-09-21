package com.rocketflyer.rocketflow.ui.trackingbuddy;

import com.tracki.data.DataManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.ui.trackingbuddy.TrackingBuddyNavigator;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 3/10/18
 */
public class TrackingBuddyViewModel extends BaseViewModel<TrackingBuddyNavigator> {
    public TrackingBuddyViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }
}
