package com.tracki.ui.profile;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 3/10/18
 */
@Module
public class MyProfileActivityModule {

    @Provides
    MyProfileViewModel provideMyProfileViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new MyProfileViewModel(dataManager, schedulerProvider);
    }

}
