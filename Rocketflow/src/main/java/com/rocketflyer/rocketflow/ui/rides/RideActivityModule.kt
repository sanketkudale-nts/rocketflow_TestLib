package com.rocketflyer.rocketflow.ui.rides

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*

/**
 * Created by Rahul Abrol on 29/12/19.
 */
@Module
class RideActivityModule {

    @Provides
    fun provideRideViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            RideViewModel(dataManager, schedulerProvider)

    @Provides
    fun provideRideAdapter(): RideAdapter {
        return RideAdapter(ArrayList())
    }
}