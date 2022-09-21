package com.tracki.ui.useraddresslist

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides




@Module
class UserAddressListActivityModule {
    @Provides
    fun provideUserAddressListActivityViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): UserAddressListActivityViewModel {
        return UserAddressListActivityViewModel(dataManager, schedulerProvider)
    }
}