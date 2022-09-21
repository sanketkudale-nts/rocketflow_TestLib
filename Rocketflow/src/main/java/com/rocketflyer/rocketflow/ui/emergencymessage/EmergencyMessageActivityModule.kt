package com.rocketflyer.rocketflow.ui.emergencymessage

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 20/2/19
 */
@Module
class EmergencyMessageActivityModule {

    @Provides
    fun provideEmergencyContactViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            EmergencyMessageViewModel(dataManager, schedulerProvider)
}