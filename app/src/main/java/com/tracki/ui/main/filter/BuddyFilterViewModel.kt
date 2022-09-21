package com.tracki.ui.main.filter

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 11/10/18
 */
open class BuddyFilterViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<BuddyFilterNavigator>(dataManager, schedulerProvider) {
    fun onProceedClick() {
        navigator.applyFilter()
    }
}