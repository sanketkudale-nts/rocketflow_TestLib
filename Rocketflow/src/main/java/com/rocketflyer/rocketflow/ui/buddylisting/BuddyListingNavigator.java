package com.rocketflyer.rocketflow.ui.buddylisting;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

/**
 * Created by rahul on 14/9/18
 */
public interface BuddyListingNavigator extends BaseNavigator {
    void onSubmitClick();

//    void onRetryClick();

    void openAddBuddyActivity();

    void refreshBuddyList(ApiCallback apiCallback, Object result, APIError error);
}
