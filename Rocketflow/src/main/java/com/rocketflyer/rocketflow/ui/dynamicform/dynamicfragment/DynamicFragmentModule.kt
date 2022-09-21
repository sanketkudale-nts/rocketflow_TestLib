package com.rocketflyer.rocketflow.ui.dynamicform.dynamicfragment


import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*

/**
 * Created by Rahul Abrol on 15/7/19.
 */

@Module
class DynamicFragmentModule {
    @Provides
    fun dynamicViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): DynamicViewModel {
        return DynamicViewModel(dataManager, schedulerProvider)
    }

    @Provides
    internal fun provideDynamicViewModel(dynamicViewModel: DynamicViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(dynamicViewModel)
    }

    @Provides
    internal fun provideLinearLayoutManager(fragment: DynamicFragment): LinearLayoutManager {
        return LinearLayoutManager(fragment.activity)
    }

    @Provides
    open fun provideNotificationAdapter(): DynamicAdapter {
        return DynamicAdapter(ArrayList())
    }
}