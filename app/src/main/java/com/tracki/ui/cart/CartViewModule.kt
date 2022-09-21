package com.tracki.ui.cart

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class CartViewModule {
    @Provides
    open fun provideCartViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): CartViewModel {
        return CartViewModel(dataManager, schedulerProvider)
    }

}