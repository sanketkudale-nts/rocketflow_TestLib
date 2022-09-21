package com.rocketflyer.rocketflow.ui.userlisting

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.ArrayList


@Module
class UserListViewModule {
    @Provides
    open fun provideUserListViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): UserListViewModel {
        return UserListViewModel(dataManager, schedulerProvider)
    }


    @Provides
    fun provideUserListAdapter(): UserListAdapter {
        return UserListAdapter(ArrayList())
    }

}