package com.tracki.ui.feeds

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*


/**
 * Created by Vikas Kesharvani on 04/01/21.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */

@Module
class FeedsActivityModule {
    @Provides
    open fun provideFeedsActivityModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): FeedsViewModel {
        return FeedsViewModel(dataManager, schedulerProvider)
    }


    @Provides
    fun provideFeedsAdapter(): FeedsAdapter {
        return FeedsAdapter(ArrayList())
    }

    @Provides
    fun provideCommentsAdapter(): CommentsAdapter {
        return CommentsAdapter(ArrayList())
    }

}