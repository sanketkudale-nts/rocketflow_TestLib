package com.rocketflyer.rocketflow.ui.otp;

import androidx.annotation.Nullable;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;
import com.tracki.utils.NextScreen;

/**
 * Created by rahul on 5/9/18
 */
interface OtpNavigator extends BaseNavigator {

    void onBackClick();
    void onClickResend();

    void verifyOtp();

    void deviceChange();

    void handleConfigResponse(ApiCallback config, Object result, @Nullable APIError error, NextScreen nextScreen, @Nullable String sdkAccessId);

    void handleResendOtpResponse(ApiCallback apiCallback, Object result, @Nullable APIError error);

    void handlePunchInPunchOutResponse(ApiCallback apiCallback, Object result, @Nullable APIError error, NextScreen nextScreen);
}
