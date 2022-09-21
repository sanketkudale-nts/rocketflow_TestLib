package com.rocketflyer.rocketflow.ui.userattendancedetails

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

open class UserAttendanceDetailsViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?) : BaseViewModel<UserAttendanceDetailsNavigator?>(dataManager, schedulerProvider)