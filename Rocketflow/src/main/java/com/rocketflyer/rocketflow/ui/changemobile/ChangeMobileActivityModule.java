package com.rocketflyer.rocketflow.ui.changemobile;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 3/10/18
 */
@Module
public class ChangeMobileActivityModule {

    @Provides
    ChangeMobileViewModel provideChangeMobileViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new ChangeMobileViewModel(dataManager, schedulerProvider);
    }

}
