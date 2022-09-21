package com.tracki.ui.emergencymessage

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 20/2/19
 */
class EmergencyMessageViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<EmergencyMessageNavigator>(dataManager, schedulerProvider)