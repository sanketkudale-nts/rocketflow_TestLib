package com.rocketflyer.rocketflow.ui.notification

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 11/10/18
 */
interface NotificationNavigator : BaseNavigator {
    fun handleClearNotificationResponse(apiCallback: ApiCallback, result: Any?, error: APIError?)
}
