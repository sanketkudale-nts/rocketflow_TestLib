package com.rocketflyer.rocketflow.ui.adjusttime

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides



@Module
class AdjustTimeViewModule {
    @Provides
    fun provideAdjustTimeViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AdjustTimeViewModel {
        return AdjustTimeViewModel(dataManager, schedulerProvider)
    }

}