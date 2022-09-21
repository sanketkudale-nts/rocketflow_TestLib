package com.rocketflyer.rocketflow.ui.addfleet;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

/**
 * Created by rahul on 6/9/18
 */
public interface AddFleetNavigator extends BaseNavigator {
    void validateViews();

    void uploadImage();

    void handleFleetPicResponse(ApiCallback apiCallback, Object result, APIError error);
}
