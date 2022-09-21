package com.rocketflyer.rocketflow.ui.main.filter

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


/**
 * Created by Vikas Kesharvani on 04/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */

@Module
open class TaskFilterActivityModule {

    @Provides
    open fun provideTaskFilterViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): TaskFilterViewModel {
        return TaskFilterViewModel(dataManager, schedulerProvider)
    }

}