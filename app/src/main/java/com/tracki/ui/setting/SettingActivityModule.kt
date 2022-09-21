package com.tracki.ui.setting

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 16/10/18
 */
@Module
class SettingActivityModule {

    @Provides
    fun provideSettingViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) =
            SettingViewModel(dataManager, schedulerProvider)

}