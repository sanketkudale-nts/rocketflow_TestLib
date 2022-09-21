package com.rocketflyer.rocketflow.ui.update

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider


/**
 * Created by Vikas Kesharvani on 12/10/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class AppUpdateScreenViewModel(dataManager: DataManager?, schedulerProvider: SchedulerProvider?) : BaseViewModel<AppUpdateNavigator>(dataManager,schedulerProvider)
{
    fun openMainActivity(){
        navigator.openMainActivity()

    }
    fun openAppInPlayStore(){
        navigator.openAppInPlayStore()

    }
}