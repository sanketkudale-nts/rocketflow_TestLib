package com.rocketflyer.rocketflow.ui.main;

import com.tracki.data.model.request.PunchInOut;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by rahul on 6/9/18
 */
public interface MainNavigator extends BaseNavigator {
    void checkBuddyResponse(ApiCallback callback, Object result, APIError error);

    void checkFleetResponse(ApiCallback callback, Object result, APIError error);

    void onSuccessfulLogout(ApiCallback apiCallback, Object result, APIError error);



    void handleEndTaskResponse(ApiCallback apiCallback, Object result, APIError error);

    void handlePunchInOutResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                  @Nullable APIError error, PunchInOut event);

    void handleOnlineOfflineResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                     @Nullable APIError error);
    void handleGetTaskData(@NotNull ApiCallback apiCallback, @Nullable Object result,
                           @Nullable APIError error);

    void checkInventoryResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                @Nullable APIError error);


    void handleUnsubscribeResponse(ApiCallback apiCallback, Object result, APIError error);


    void handleMyPlaceResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                               @Nullable APIError error);



//    void setCurrentLocationMarker(Location location);
}
