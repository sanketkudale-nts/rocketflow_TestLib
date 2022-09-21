package com.rocketflyer.rocketflow.ui.tasklisting.ihaveassigned;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

/**
 * Created by rahul on 9/10/18
 */
public interface IhaveAssignedNavigator extends BaseNavigator {
    //    void handleCancelTaskResponse(ApiCallback apiCallback, Object result, APIError error);
    //    void handleEndTaskResponse(ApiCallback apiCallback, Object result, APIError error);
    void handleExecuteUpdateResponse(ApiCallback apiCallback, Object result, APIError error);


}
