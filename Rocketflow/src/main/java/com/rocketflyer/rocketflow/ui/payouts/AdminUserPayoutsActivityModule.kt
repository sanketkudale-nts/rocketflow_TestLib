package com.rocketflyer.rocketflow.ui.payouts

import com.tracki.data.DataManager
import com.tracki.ui.earnings.MyEarningsAdapter
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*


/**
 * Created by Vikas Kesharvani on 11/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */

@Module
class AdminUserPayoutsActivityModule {

    @Provides
    fun provideAdminUserPayoutsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            AdminUserPayoutsViewModel(dataManager, schedulerProvider)

    @Provides
    fun provideAdminUserPayoutsAdapter(): MyEarningsAdapter {
        return MyEarningsAdapter(ArrayList())
    }
}