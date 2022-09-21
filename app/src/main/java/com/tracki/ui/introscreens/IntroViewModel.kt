package com.tracki.ui.introscreens

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 22/2/19
 */
class IntroViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<IntroNavigator>(dataManager, schedulerProvider) {

    fun onNextClicked() {
        navigator.nextClicked()
    }

    fun onSkipClicked() {
        navigator.skipClicked()
    }
}