package com.tracki.ui.taskdetails

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 16/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TaskDetailsActivityViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<NewTaskDetailNavigator>(dataManager, schedulerProvider) {
}