package com.tracki.ui.accountlist

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*


/**
 * Created by Vikas Kesharvani on 16/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
@Module
class AccountListActivityModule {
    @Provides
    fun provideAccountListViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?): AccountListViewModel {
        return AccountListViewModel(dataManager, schedulerProvider)
    }

    @Provides
    fun provideDriverAdapter(): AccountListAdapter {
        return AccountListAdapter(ArrayList())
    }
}