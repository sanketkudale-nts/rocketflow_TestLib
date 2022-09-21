package com.tracki.ui.productdetails

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides



@Module
open class ProductDetailsViewModule {

    @Provides
    open fun provideProductDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): ProductDetailsViewModel {
        return ProductDetailsViewModel(dataManager, schedulerProvider)
    }
}