package com.tracki.ui.attendance.punchInOut;

import com.tracki.data.model.request.PunchInOut;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PunchInOutNavigator extends BaseNavigator {


    void onClickPunchInOut(String title, PunchInOut punchInOut);
    void  autoPunchOut();



    @Override
    void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error);

    void handlePunchInOutResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                  @Nullable APIError error, PunchInOut event);

    void handleImageResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                  @Nullable APIError error);
    void validatePunchIn(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error,String title, PunchInOut punchInOut);

    void validateGeoPunchIn(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error,String title, PunchInOut punchInOut,int from);
}
