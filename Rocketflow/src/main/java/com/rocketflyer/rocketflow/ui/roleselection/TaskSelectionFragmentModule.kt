package com.rocketflyer.rocketflow.ui.roleselection

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.ui.tasklisting.assignedtome.AssignedToMeViewModel
import com.tracki.ui.tasklisting.assignedtome.AssignedtoMeFragment
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*

@Module
class TaskSelectionFragmentModule {
    @Provides
    fun taskSelectionViewModel(dataManager: DataManager?,
                               schedulerProvider: SchedulerProvider?): TaskSelectionViewModel {
        return TaskSelectionViewModel(dataManager!!, schedulerProvider!!)
    }

    @Provides
    fun provideTaskSelectionAdapter(): TaskSelectionAdapter {
        return TaskSelectionAdapter(ArrayList())
    }

    @Provides
    fun provideTaskSelectionViewModel(taskSelectionViewModel: TaskSelectionViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(taskSelectionViewModel)
    }

    @Provides
    fun provideLinearLayoutManager(fragment: TaskSelectionFragment): LinearLayoutManager {
        return LinearLayoutManager(fragment.activity)
    }
}