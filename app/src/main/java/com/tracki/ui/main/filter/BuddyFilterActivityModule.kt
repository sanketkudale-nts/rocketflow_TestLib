package com.tracki.ui.main.filter

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 12/10/18
 */
@Module
open class BuddyFilterActivityModule {

    @Provides
    open fun provideBuddyFilterViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): BuddyFilterViewModel {
        return BuddyFilterViewModel(dataManager, schedulerProvider)
    }

}