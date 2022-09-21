package com.rocketflyer.rocketflow.ui.products

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


@Module
class AddProductViewModule {
    @Provides
    open fun provideAddProductViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddProductViewModel {
        return AddProductViewModel(dataManager, schedulerProvider)
    }

}