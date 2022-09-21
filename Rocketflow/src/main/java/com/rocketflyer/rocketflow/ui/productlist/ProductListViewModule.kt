package com.rocketflyer.rocketflow.ui.productlist

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


@Module
class ProductListViewModule {
    @Provides
    open fun provideProductListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): ProductListViewModel {
        return ProductListViewModel(dataManager, schedulerProvider)
    }

}