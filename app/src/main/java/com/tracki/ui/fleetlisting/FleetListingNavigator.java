package com.tracki.ui.fleetlisting;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

/**
 * Created by rahul on 14/9/18
 */
public interface FleetListingNavigator extends BaseNavigator {
    void onProceedClick();

//    void onRetryClick();

    void openAddFleetActivity();

    void refreshFleetList(ApiCallback apiCallback, Object result, APIError error);

    void changeFleetStatus(ApiCallback apiCallback, Object result, APIError error);
}
