package com.tracki.ui.profile;

import android.view.View;

import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseNavigator;

/**
 * Created by rahul on 3/10/18
 */
public interface MyProfileNavigator extends BaseNavigator {
    void editMobileNumber(View view);

    void uploadProfilePic();

    void handleProfilePicResponse(ApiCallback apiCallback, Object result, APIError error);
}
