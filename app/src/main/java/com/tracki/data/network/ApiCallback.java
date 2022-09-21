package com.tracki.data.network;

import androidx.annotation.Nullable;

/**
 * Created by Rahul Abrol on 4/6/18.
 */
public interface ApiCallback {

    void onResponse(Object result, @Nullable APIError error);

    void hitApi();

    boolean isAvailable();

    void onNetworkErrorClose();

    void onRequestTimeOut(ApiCallback callBack);

    void onLogout();
}
