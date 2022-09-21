package com.rocketflyer.rocketflow.ui.addcontact

import com.tracki.data.DataManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.CommonUtils
import com.tracki.utils.rx.SchedulerProvider

/**
 * Created by rahul on 18/2/19
 */
class AddEmergencyContactViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<AddEmergencyContactNavigator>(dataManager, schedulerProvider){

    fun isViewNullOrEmpty(string: String): Boolean {
        return CommonUtils.isViewNullOrEmpty(string)
    }

    fun isMobileValid(mobile: String): Boolean {
        return CommonUtils.isMobileValid(mobile)
    }


}