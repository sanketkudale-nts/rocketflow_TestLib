package com.rocketflyer.rocketflow.ui.consent

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 16/5/19
 */
class ConsentViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<ConsentNavigator>(dataManager, schedulerProvider) {

    fun onDeclineClick() {
        navigator.onDeclineClick()
    }

    fun onAgreeClick() {
        navigator.onAgreeClick()
    }
}