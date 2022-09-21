package com.rocketflyer.rocketflow.ui.introscreens

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 22/2/19
 */
@Module
open class IntroActivityModule {

    @Provides
    open fun provideIntroViewModel(dataManager: DataManager,
                                   schedulerProvider: SchedulerProvider) =
            IntroViewModel(dataManager, schedulerProvider)

//    @Provides
//    open fun provideIntroPagerAdapter(fragmentManager: FragmentManager) =
//            IntroPagerAdapter(fragmentManager)

}