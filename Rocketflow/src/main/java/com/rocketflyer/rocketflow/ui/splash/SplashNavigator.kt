package com.rocketflyer.rocketflow.ui.splash

import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 08/07/17.
 */

interface SplashNavigator : BaseNavigator {

    fun openLoginActivity()

    fun openMainActivity()

    fun close()
}