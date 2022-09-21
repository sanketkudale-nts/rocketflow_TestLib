package com.tracki.data.local.prefs

import androidx.lifecycle.ViewModelProvider
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.ui.leave.apply_leave.ApplyLeaveViewModel
import com.tracki.ui.placelist.MyPlacesViewModel
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides




@Module
open class GetUserLocationWorkerModule {

    @Provides
    open fun provideMyPlacesViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?): MyPlacesViewModel {
        return MyPlacesViewModel(dataManager!!, schedulerProvider!!)
    }

    @Provides
    open fun provideViewModelProviderFactory(applyLeaveViewModel: MyPlacesViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(applyLeaveViewModel)
    }

}