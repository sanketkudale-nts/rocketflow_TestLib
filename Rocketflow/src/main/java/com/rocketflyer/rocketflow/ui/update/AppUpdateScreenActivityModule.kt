package com.rocketflyer.rocketflow.ui.update

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


/**
 * Created by Vikas Kesharvani on 12/10/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
@Module
class AppUpdateScreenActivityModule {

    @Provides
    fun provideSplashViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?): AppUpdateScreenViewModel{
        return AppUpdateScreenViewModel(dataManager, schedulerProvider)
    }
}