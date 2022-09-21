package com.tracki.data.local.prefs;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

public interface PostErrorToServerNavigator extends BaseNavigator {



    void handlePostErrorResponse(ApiCallback apiCallback, Object result, APIError error);


}

