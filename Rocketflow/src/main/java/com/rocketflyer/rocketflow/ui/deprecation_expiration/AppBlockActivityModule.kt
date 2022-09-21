package com.rocketflyer.rocketflow.ui.deprecation_expiration

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 12/10/18
 */
@Module
open class AppBlockActivityModule {

    @Provides
    open fun provideAppBlockViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AppBlockViewModel {
        return AppBlockViewModel(dataManager, schedulerProvider)
    }
}