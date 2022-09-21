package com.tracki.ui.update

import com.tracki.ui.base.BaseNavigator


/**
 * Created by Vikas Kesharvani on 12/10/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface AppUpdateNavigator : BaseNavigator {


    fun openMainActivity()

    fun openAppInPlayStore()

    fun close()
}