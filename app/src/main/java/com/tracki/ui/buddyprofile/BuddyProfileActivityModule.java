package com.tracki.ui.buddyprofile;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 9/10/18
 */
@Module
public class BuddyProfileActivityModule {
    @Provides
    BuddyProfileViewModel provideBuddyDetailActivityViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new BuddyProfileViewModel(dataManager, schedulerProvider);
    }
}
