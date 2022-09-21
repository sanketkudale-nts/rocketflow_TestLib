package com.tracki.ui.leave.leave_approval;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

public interface LeaveApprovalNavigator extends BaseNavigator {

//public void onClickSearchApproval();
public void handleApproveResponse(ApiCallback callback, Object result, APIError error);
public void handleRejectResponse(ApiCallback callback, Object result, APIError error);



}
