package com.tracki.ui.idealtrip

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


/**
 * Created by Vikas Kesharvani on 27/07/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
@Module
class IdealTripDetailActivityModule {

    @Provides
    fun provideIdealIdealTripViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            IdealTripViewModel(dataManager, schedulerProvider)

}