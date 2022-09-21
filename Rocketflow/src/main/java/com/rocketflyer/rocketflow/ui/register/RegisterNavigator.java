package com.rocketflyer.rocketflow.ui.register;

import androidx.annotation.Nullable;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;
import com.tracki.utils.NextScreen;

/**
 * Created by rahul on 5/9/18
 */
interface RegisterNavigator extends BaseNavigator {

    void validateAndRegister();
    void onClickResend();
    void handleResendOtpResponse(ApiCallback apiCallback, Object result, @Nullable APIError error);
    void handleConfigResponse(ApiCallback apiCallback, Object result, APIError error, NextScreen nextScreen, String sdkAccessId);
}
