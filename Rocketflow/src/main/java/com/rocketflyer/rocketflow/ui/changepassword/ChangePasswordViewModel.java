package com.rocketflyer.rocketflow.ui.changepassword;

import com.tracki.data.DataManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 3/10/18
 */
public class ChangePasswordViewModel extends BaseViewModel<ChangePasswordNavigator> {
    public ChangePasswordViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onProceedClick(){
        getNavigator().onProceedClick();
    }
}
