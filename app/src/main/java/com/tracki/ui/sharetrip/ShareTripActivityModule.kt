package com.tracki.ui.sharetrip

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 1/3/19
 */
@Module
open class ShareTripActivityModule {

    @Provides
    open fun provideShareTripViewModel(dataManager: DataManager,
                                       schedulerProvider: SchedulerProvider) =

            ShareTripViewModel(dataManager, schedulerProvider)

}