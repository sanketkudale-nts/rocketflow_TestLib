package com.tracki.ui.changepassword;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 3/10/18
 */
@Module
public class ChangePasswordActivityModule {

    @Provides
    ChangePasswordViewModel provideChangePasswordViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new ChangePasswordViewModel(dataManager, schedulerProvider);
    }

}
