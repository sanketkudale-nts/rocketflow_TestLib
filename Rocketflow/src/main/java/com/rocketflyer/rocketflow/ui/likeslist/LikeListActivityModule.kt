package com.rocketflyer.rocketflow.ui.likeslist

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
class LikeListActivityModule {
    @Provides
    open fun provideFeedsActivityModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): LikeListViewModel {
        return LikeListViewModel(dataManager, schedulerProvider)
    }


    @Provides
    fun provideLikeListAdapter(): LikeListAdapter {
        return LikeListAdapter(ArrayList())
    }



}