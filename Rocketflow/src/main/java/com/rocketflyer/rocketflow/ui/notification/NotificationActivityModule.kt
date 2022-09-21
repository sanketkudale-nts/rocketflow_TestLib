package com.rocketflyer.rocketflow.ui.notification

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*

/**
 * Created by rahul on 11/10/18
 */
@Module
open class NotificationActivityModule {

    @Provides
    open fun provideNotificationViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): NotificationViewModel {
        return NotificationViewModel(dataManager, schedulerProvider)
    }

    @Provides
    open fun provideNotificationAdapter(): NotificationAdapter {
        return NotificationAdapter(ArrayList())
    }
}