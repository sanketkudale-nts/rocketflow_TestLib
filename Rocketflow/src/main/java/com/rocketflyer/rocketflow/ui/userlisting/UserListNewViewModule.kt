package com.rocketflyer.rocketflow.ui.userlisting

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.ArrayList


@Module
class UserListNewViewModule {
    @Provides
    open fun provideUserListNewViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): UserListNewViewModel {
        return UserListNewViewModel(dataManager, schedulerProvider)
    }


    @Provides
    fun provideUserListNewAdapter(): UserListNewAdapter {
        return UserListNewAdapter(ArrayList())
    }

}