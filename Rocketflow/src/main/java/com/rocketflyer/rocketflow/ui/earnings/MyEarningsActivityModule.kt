package com.rocketflyer.rocketflow.ui.earnings

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*

/**
 * Created by Rahul Abrol on 27/12/19.
 */
@Module
class MyEarningsActivityModule {

    @Provides
    fun provideMyEarningsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            MyEarningsViewModel(dataManager, schedulerProvider)

    @Provides
    fun provideMyEarningsAdapter(): MyEarningsAdapter {
        return MyEarningsAdapter(ArrayList())
    }
}