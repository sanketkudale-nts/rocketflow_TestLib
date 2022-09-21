package com.rocketflyer.rocketflow.ui.facility

import com.tracki.data.DataManager
import com.tracki.ui.roleselection.TaskSelectionAdapter
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.ArrayList


@Module
open class UpdateServiceModule {
    @Provides
    fun provideServiceViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?): ServiceViewModel {
        return ServiceViewModel(dataManager!!, schedulerProvider!!)
    }

    @Provides
    fun provideServicesAdapter(): ServicesAdapter {
        return ServicesAdapter(ArrayList())
    }


}