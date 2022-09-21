package com.rocketflyer.rocketflow.ui.introscreens.introfragment

import androidx.lifecycle.ViewModelProvider
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 22/2/19
 */
@Module
class IntroScreenFragmentModule {

    @Provides
    fun provideIntroScreenViewModel(dataManager: DataManager,
                                    schedulerProvider: SchedulerProvider): IntroScreenViewModel {
        return IntroScreenViewModel(dataManager, schedulerProvider)
    }

    @Provides
    fun provideIntroScreenViewModelFactory(iamTrackingViewModel: IntroScreenViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory<IntroScreenViewModel>(iamTrackingViewModel)
    }
}