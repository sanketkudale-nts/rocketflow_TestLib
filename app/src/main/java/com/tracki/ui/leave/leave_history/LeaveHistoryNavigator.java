package com.tracki.ui.leave.leave_history;

import androidx.annotation.Nullable;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

public interface LeaveHistoryNavigator extends BaseNavigator {

    //public void onClickSearchHistory();
    void fetchLeaves();

    public void handleCancelResponse(ApiCallback callback, Object result, APIError error);

    public void handleLeaveSummaryResponse(@Nullable ApiCallback callback, @Nullable Object result, @Nullable APIError error);


}
