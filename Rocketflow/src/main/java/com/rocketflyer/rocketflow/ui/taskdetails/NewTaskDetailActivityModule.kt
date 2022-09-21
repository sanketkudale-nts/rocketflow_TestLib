package com.rocketflyer.rocketflow.ui.taskdetails

import com.tracki.data.DataManager
import com.tracki.ui.tasklisting.TaskPagerAdapter
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


@Module
class NewTaskDetailActivityModule {

    @Provides
    fun provideNewTaskDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            TaskDetailsActivityViewModel(dataManager, schedulerProvider)

    @Provides
    fun provideTaskPagerAdapter(activity: NewTaskDetailsActivity): TaskPagerAdapter {
        return TaskPagerAdapter(activity.supportFragmentManager)
    }
}