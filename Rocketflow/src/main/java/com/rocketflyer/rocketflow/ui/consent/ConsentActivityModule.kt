package com.rocketflyer.rocketflow.ui.consent

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 16/5/19
 */
@Module
class ConsentActivityModule {

    @Provides
    fun provideConsentViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): ConsentViewModel {
        return ConsentViewModel(dataManager, schedulerProvider)
    }
}