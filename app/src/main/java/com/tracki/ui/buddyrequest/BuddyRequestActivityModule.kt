package com.tracki.ui.buddyrequest

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 6/12/18
 */
@Module
class BuddyRequestActivityModule {
    @Provides
    internal fun provideBuddyRequestViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) = BuddyRequestViewModel(dataManager, schedulerProvider)

    @Provides
    internal fun provideBuddyRequestAdapter() = BuddyRequestAdapter(ArrayList())
}