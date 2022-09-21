package com.rocketflyer.rocketflow.ui.roleselection

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
open class SelectAccountModule {
    @Provides
    fun provideTaskViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?): SelectAccountViewModel {
        return SelectAccountViewModel(dataManager!!, schedulerProvider!!)
    }

    @Provides
    fun provideAccountPagerAdapter(activity: SelectionActivity): AccountPagerAdapter {
        return AccountPagerAdapter(activity.supportFragmentManager)
    }
}