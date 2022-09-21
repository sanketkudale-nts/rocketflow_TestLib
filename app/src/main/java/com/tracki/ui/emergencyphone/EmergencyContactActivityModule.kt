package com.tracki.ui.emergencyphone

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 5/12/18
 */
@Module
class EmergencyContactActivityModule {
    @Provides
    fun provideEmergencyContactViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            EmergencyContactViewModel(dataManager, schedulerProvider)

    @Provides
    fun provideEmergencyContactAdapter() = EmergencyContactAdapter(ArrayList())
}