package com.rocketflyer.rocketflow.ui.userdetails

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.ArrayList


@Module
class UserDetailsModule {
    @Provides
    open fun provideUserDetailsViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): UserDetailsViewModel {
        return UserDetailsViewModel(dataManager, schedulerProvider)
    }

    @Provides
    fun provideUserOptionsSelectedAdapter(): UserOptionsSelectedAdapter {
        return UserOptionsSelectedAdapter(ArrayList())
    }



}