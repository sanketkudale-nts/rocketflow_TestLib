package com.tracki.ui.attendance.teamattendance

import androidx.lifecycle.ViewModelProvider
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides


/**
 * Created by Vikas Kesharvani on 26/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */

@Module
class TeamAttendanceModule {
    @Provides
    fun teamAttendanceViewModel(dataManager: DataManager?,
                            schedulerProvider: SchedulerProvider?): TeamAttendanceViewModel {
        return TeamAttendanceViewModel(dataManager!!, schedulerProvider!!)
    }

    @Provides
    fun punchInOutViewModelFactory(teamAttendanceViewModel: TeamAttendanceViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(teamAttendanceViewModel)
    }

    @Provides
    fun provideTeamAttendanceAdapter(): TeamAttendanceAdapter{
        return TeamAttendanceAdapter(ArrayList())
    }

}