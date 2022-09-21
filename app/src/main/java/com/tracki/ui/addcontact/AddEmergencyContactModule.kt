package com.tracki.ui.addcontact

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 19/2/19
 */
@Module
class AddEmergencyContactModule {

    @Provides
    fun provideCreateTaskViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            AddEmergencyContactViewModel(dataManager, schedulerProvider)
}