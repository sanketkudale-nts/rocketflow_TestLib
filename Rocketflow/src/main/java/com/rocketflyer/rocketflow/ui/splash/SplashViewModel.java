package com.rocketflyer.rocketflow.ui.splash;

import androidx.annotation.Nullable;

import com.tracki.data.DataManager;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.ui.splash.SplashNavigator;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul.
 */
public class SplashViewModel extends BaseViewModel<SplashNavigator> implements ApiCallback {
    //    ObservableBoolean mIsLoading = new ObservableBoolean(false);
    private HttpManager httpManager;

    SplashViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

//    public boolean getIsLoading() {
//        return mIsLoading.get();
//    }
//
//    void setIsLoading(boolean isLoading) {
//        mIsLoading.set(isLoading);
//    }

    void startSeeding(HttpManager httpManager) {
        try {
            this.httpManager = httpManager;
            hitApi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(@Nullable Object result, @Nullable APIError error) {
//        setIsLoading(false);
        if(getNavigator()!=null) {
            getNavigator().handleResponse(this, result, error);
        }
    }

    @Override
    public void hitApi() {
        if(getDataManager()!=null) {
            getDataManager().getConfig(this.httpManager, this);
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void onNetworkErrorClose()
    {
        if(getNavigator()!=null) {
            getNavigator().close();
        }
    }

    @Override
    public void onRequestTimeOut(ApiCallback callBack) {
        if(getNavigator()!=null) {
            getNavigator().showTimeOutMessage(callBack);
        }
    }

    @Override
    public void onLogout() {

    }
}
