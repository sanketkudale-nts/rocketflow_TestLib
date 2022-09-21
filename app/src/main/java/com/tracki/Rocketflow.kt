package com.tracki

import com.tracki.ui.splash.SplashActivity

object Rocketflow {
    open {
        var splashActivity: SplashActivity = SplashActivity()
        splashActivity.openMainActivity()
    }
}
