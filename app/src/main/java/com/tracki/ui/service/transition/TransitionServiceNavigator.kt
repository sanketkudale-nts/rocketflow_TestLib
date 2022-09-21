package com.tracki.ui.service.transition

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

/**
 * Created by rahul on 11/3/19
 */
interface TransitionServiceNavigator : BaseNavigator {
    fun handleEndTaskResponse(endTask: ApiCallback, result: Any, error: APIError)
}