package com.tracki.ui.dynamicform

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 20/3/19
 */
@Module
class DynamicFormActivityModule {

    @Provides
    fun provideDynamicFormViewModel(dataManager: DataManager,
                                    schedulerProvider: SchedulerProvider) =
            DynamicFormViewModel(dataManager, schedulerProvider)

}