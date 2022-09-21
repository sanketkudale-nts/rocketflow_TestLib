package com.rocketflyer.rocketflow.ui.addcustomer

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

class CustomerViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<AddUserNavigator>(dataManager, schedulerProvider) {
}