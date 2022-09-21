package com.tracki.ui.feeddetails

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface FeedDetailsNavigator : BaseNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

}