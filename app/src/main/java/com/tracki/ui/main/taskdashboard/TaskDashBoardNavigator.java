package com.tracki.ui.main.taskdashboard;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface TaskDashBoardNavigator extends BaseNavigator {

    void handleDashboardResponse(ApiCallback apiCallback, Object result, APIError error);

    void handleInsightsResponse(ApiCallback apiCallback, Object result, APIError error);
}
