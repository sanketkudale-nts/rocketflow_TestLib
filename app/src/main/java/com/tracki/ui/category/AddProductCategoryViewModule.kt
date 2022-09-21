package com.tracki.ui.category

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


@Module
class AddProductCategoryViewModule {
    @Provides
    open fun provideAddCategoryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddProductCategoryViewModel {
        return AddProductCategoryViewModel(dataManager, schedulerProvider)
    }

}