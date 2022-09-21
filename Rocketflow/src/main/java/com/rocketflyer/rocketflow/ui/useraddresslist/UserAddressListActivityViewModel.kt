package com.rocketflyer.rocketflow.ui.useraddresslist

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider


open class UserAddressListActivityViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?) : BaseViewModel<UserAddressListActivityNavigator?>(dataManager, schedulerProvider)