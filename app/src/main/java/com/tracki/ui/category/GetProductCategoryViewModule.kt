package com.tracki.ui.category

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides



@Module
class GetProductCategoryViewModule {
    @Provides
    open fun provideGetFlavourCategoryListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): GetFlavourCategoryListViewModel {
        return GetFlavourCategoryListViewModel(dataManager, schedulerProvider)
    }

}