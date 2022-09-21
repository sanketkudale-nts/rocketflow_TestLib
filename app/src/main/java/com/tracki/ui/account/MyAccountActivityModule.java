package com.tracki.ui.account;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 3/10/18
 */
@Module
public class MyAccountActivityModule {

    @Provides
    MyAccountViewModel provideMyAccountViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new MyAccountViewModel(dataManager, schedulerProvider);
    }

}
