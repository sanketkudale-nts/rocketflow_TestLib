package com.tracki.ui.dynamicformpreview

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class FormPreviewActivityModule {

    @Provides
    fun provideFormPreviewViewModel(dataManager: DataManager,
                                    schedulerProvider: SchedulerProvider) =
            FormPreviewViewModel(dataManager, schedulerProvider)

}