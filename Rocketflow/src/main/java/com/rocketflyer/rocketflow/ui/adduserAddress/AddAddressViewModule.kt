package com.rocketflyer.rocketflow.ui.adduserAddress
import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


@Module
class AddAddressViewModule {
    @Provides
    fun provideAddAddressViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddAddressViewModel {
        return AddAddressViewModel(dataManager, schedulerProvider)
    }

}
