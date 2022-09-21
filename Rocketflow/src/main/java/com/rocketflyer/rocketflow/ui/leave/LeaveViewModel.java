package com.rocketflyer.rocketflow.ui.leave;

import com.tracki.data.DataManager;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 8/10/18
 */
public class LeaveViewModel extends BaseViewModel<LeaveNavigator> {
    private HttpManager httpManager;

    LeaveViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }


}

//interface OnClickFilterListener{
//    void onClickFilter();
//}