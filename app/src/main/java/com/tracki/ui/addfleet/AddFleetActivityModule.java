package com.tracki.ui.addfleet;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 6/9/18
 */
@Module
public class AddFleetActivityModule {

    @Provides
    AddFleetActivityViewModel provideAddFleetViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new AddFleetActivityViewModel(dataManager, schedulerProvider);
    }
}
