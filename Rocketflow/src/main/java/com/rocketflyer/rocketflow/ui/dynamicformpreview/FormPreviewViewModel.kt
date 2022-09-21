package com.rocketflyer.rocketflow.ui.dynamicformpreview

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.rx.SchedulerProvider

class FormPreviewViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<FormPreviewNavigator>(dataManager, schedulerProvider)