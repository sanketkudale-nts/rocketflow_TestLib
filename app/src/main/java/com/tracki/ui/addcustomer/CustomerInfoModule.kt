package com.tracki.ui.addcustomer

import androidx.lifecycle.ViewModelProvider
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.ui.tasklisting.TaskPagerAdapter
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides




@Module
open class CustomerInfoModule {

    @Provides
    open fun provideCustomerViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): CustomerViewModel {
        return CustomerViewModel(dataManager, schedulerProvider)
    }

    @Provides
    fun provideWorkPagerAdapter(activity: AddCustomerActivity): TaskPagerAdapter {
        return TaskPagerAdapter(activity.supportFragmentManager)
    }


}