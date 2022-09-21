package com.tracki.ui.taskdetails

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 16/10/18
 */
@Module
class TaskDetailActivityModule {

    @Provides
    fun provideTaskDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            TaskDetailViewModel(dataManager, schedulerProvider)

}