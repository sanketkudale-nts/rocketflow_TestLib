package com.tracki.ui.attendance.attendance_tab;

import com.tracki.data.model.request.PunchInOut;
import com.tracki.data.model.response.config.LeaveStatus;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AttendanceNavigator extends BaseNavigator {

    public void handleAttendanceResponse(ApiCallback apiCallback, Object result, APIError apiError);
    public void onClickSearch(long from, long to, LeaveStatus status);
    public void handleTodayAttendanceResponse(ApiCallback apiCallback, Object result, APIError apiError);
    public void handleAttendanceMapResponse(ApiCallback apiCallback, Object result, APIError apiError);
    void handlePunchInOutResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                  @Nullable APIError error, PunchInOut event);
}
