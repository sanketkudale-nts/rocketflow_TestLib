package com.tracki.ui.register;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 5/9/18
 */
@Module
public class RegisterActivityModule {

    @Provides
    RegisterViewModel provideRegisterViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new RegisterViewModel(dataManager, schedulerProvider);
    }
}
