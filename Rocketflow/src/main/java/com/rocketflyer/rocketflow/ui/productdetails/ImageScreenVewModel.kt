package com.rocketflyer.rocketflow.ui.productdetails

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.ui.introscreens.introfragment.IntroScreenNavigator
import com.tracki.utils.rx.SchedulerProvider

class ImageScreenVewModel (dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseViewModel<IntroScreenNavigator>(dataManager, schedulerProvider) {
}