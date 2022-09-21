package com.rocketflyer.rocketflow.ui.attendance

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*


/**
 * Created by Vikas Kesharvani on 22/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */

@Module
class EmployeeActivityModule {
    @Provides
    open fun provideEmployeeListActivityModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): EmployeeListViewModel {
        return EmployeeListViewModel(dataManager, schedulerProvider)
    }


    @Provides
    fun provideEmployeeListAdapter(): EmployeeListAdapter {
        return EmployeeListAdapter(ArrayList())
    }

}