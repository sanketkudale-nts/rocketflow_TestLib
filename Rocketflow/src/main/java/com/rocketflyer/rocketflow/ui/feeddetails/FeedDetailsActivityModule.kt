package com.rocketflyer.rocketflow.ui.feeddetails

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides




@Module
class FeedDetailsActivityModule {
    @Provides
    open fun provideFeedDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): FeedDetailsViewModel {
        return FeedDetailsViewModel(dataManager, schedulerProvider)
    }



}