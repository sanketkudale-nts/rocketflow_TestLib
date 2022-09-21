package com.tracki.ui.selectorder

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


@Module
class SelectOrderViewModule {
    @Provides
    open fun provideSelectOrderViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): SelectOrderViewModel {
        return SelectOrderViewModel(dataManager, schedulerProvider)
    }

}