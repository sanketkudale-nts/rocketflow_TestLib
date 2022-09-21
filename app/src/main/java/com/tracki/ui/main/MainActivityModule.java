package com.tracki.ui.main;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 6/9/18
 */
@Module
public class MainActivityModule {
    @Provides
    MainViewModel provideMainViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new MainViewModel(dataManager, schedulerProvider);
    }
}
