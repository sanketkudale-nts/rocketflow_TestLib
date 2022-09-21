package com.tracki.ui.referearn

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 23/11/18
 */
@Module
class ReferAndEarnActivityModule {
    @Provides
    internal fun provideReferAndEarnViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): ReferAndEarnViewModel {
        return ReferAndEarnViewModel(dataManager, schedulerProvider)
    }
}