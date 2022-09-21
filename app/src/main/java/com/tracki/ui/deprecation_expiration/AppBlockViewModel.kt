package com.tracki.ui.deprecation_expiration

import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

open class AppBlockViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<AppBlockNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager
    private lateinit var api: Api

    fun onAssignNowClicked() {

    }
}
