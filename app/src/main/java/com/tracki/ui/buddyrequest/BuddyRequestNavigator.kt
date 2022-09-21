package com.tracki.ui.buddyrequest

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 6/12/18
 */
interface BuddyRequestNavigator : BaseNavigator {
    fun acceptRejectResponse(apiCallback: ApiCallback, result: Any?, error: APIError?)
}