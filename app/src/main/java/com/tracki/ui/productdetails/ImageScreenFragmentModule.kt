package com.tracki.ui.productdetails

import androidx.lifecycle.ViewModelProvider
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class ImageScreenFragmentModule {

    @Provides
    fun provideImageScreenVewModel(
        dataManager: DataManager,
        schedulerProvider: SchedulerProvider
    ): ImageScreenVewModel {
        return ImageScreenVewModel(dataManager, schedulerProvider)
    }

    @Provides
    fun provideImageScreenViewModelFactory(iamTrackingViewModel: ImageScreenVewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory<ImageScreenVewModel>(iamTrackingViewModel)
    }
}