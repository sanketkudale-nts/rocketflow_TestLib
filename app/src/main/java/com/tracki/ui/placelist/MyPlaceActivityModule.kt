package com.tracki.ui.placelist

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


@Module
open class MyPlaceActivityModule {

    @Provides
    open fun provideMyPlaceViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): MyPlacesViewModel {
        return MyPlacesViewModel(dataManager, schedulerProvider)
    }
}