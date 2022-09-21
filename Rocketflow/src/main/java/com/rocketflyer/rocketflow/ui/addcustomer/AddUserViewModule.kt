package com.rocketflyer.rocketflow.ui.addcustomer

import androidx.lifecycle.ViewModelProvider
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.ui.taskdetails.NewTaskDetailsActivity
import com.tracki.ui.tasklisting.TaskPagerAdapter
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
open class AddUserViewModule {

    @Provides
    open fun provideAddUserViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): AddUserViewModel {
        return AddUserViewModel(dataManager, schedulerProvider)
    }
    @Provides
    open fun provideAddUserViewModelFactory(addUserViewModel: AddUserViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(addUserViewModel)
    }
}