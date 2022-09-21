package com.tracki.ui.tasklisting.assignedtome;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

/**
 * Created by rahul abrol on 05/10/18.
 */

public interface AssignedtoMeNavigator extends BaseNavigator {

//    void acceptTaskResponse(ApiCallback apiCallback, Object result, APIError error);
//
//    void handleStartTaskResponse(ApiCallback apiCallback, Object result, APIError error);
//
//    void handleCancelTaskResponse(ApiCallback apiCallback, Object result, APIError error);

//    void handleEndTaskResponse(ApiCallback apiCallback, Object result, APIError error);

//    void handleArriveReachTaskResponse(ApiCallback apiCallback, Object result, APIError error);

    void handleExecuteUpdateResponse(ApiCallback apiCallback, Object result, APIError error);


}
