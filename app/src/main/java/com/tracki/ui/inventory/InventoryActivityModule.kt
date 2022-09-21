package com.tracki.ui.inventory

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*


/**
 * Created by Vikas Kesharvani on 16/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
@Module
class InventoryActivityModule {
    @Provides
    open fun provideInventoryActivityModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): InventoryViewModel {
        return InventoryViewModel(dataManager, schedulerProvider)
    }


    @Provides
    fun provideInventoryListingAdapter(): InventoryListAdapter {
        return InventoryListAdapter(ArrayList())
    }

}