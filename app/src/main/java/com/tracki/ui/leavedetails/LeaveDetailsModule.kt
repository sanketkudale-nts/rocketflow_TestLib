package com.tracki.ui.leavedetails

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
open class LeaveDetailsModule {

    @Provides
    open fun provideLeaveDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): LeaveDetailsViewModel {
        return LeaveDetailsViewModel(dataManager, schedulerProvider)
    }
}