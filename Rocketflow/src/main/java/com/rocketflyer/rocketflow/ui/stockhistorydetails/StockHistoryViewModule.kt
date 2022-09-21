package com.rocketflyer.rocketflow.ui.stockhistorydetails

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides



@Module
open class StockHistoryViewModule {

    @Provides
    open fun provideStockHistoryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): StockHistoryViewModel {
        return StockHistoryViewModel(dataManager, schedulerProvider)
    }
}