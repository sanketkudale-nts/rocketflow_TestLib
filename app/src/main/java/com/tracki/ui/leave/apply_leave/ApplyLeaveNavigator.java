package com.tracki.ui.leave.apply_leave;

import androidx.annotation.Nullable;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

import org.jetbrains.annotations.NotNull;

public interface ApplyLeaveNavigator extends BaseNavigator {


    void onClickSubmit();

    void handleLeaveSummaryResponse(ApiCallback callback, @androidx.annotation.Nullable Object result, @Nullable APIError error);

    void handleEditResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error);


    void handleServerTimeResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error);

    void handleServerTimeOnSubmitResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error);


}


