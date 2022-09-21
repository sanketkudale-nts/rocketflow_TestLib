package com.tracki.ui.addplace

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


@Module
open class AddPlaceActivityModule {

    @Provides
    open fun provideAddPlaceViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddPlaceViewModel {
        return AddPlaceViewModel(dataManager, schedulerProvider)
    }
}