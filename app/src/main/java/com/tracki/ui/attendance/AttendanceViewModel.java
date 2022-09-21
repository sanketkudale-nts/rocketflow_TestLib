package com.tracki.ui.attendance;

import com.tracki.data.DataManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 8/10/18
 */
public class AttendanceViewModel extends BaseViewModel<AttendanceNavigator> {

    AttendanceViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }
}