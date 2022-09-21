package com.tracki.data.local.prefs;

import com.tracki.data.DataManager;
import com.tracki.ui.placelist.MyPlacesViewModel;
import com.tracki.ui.tasklisting.assignedtome.AssignedToMeViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vikas Kesharvani on 15/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */


@Module
public class WorkerManagerModule {

    @Provides
    AssignedToMeViewModel provideAssignedToMeViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new AssignedToMeViewModel(dataManager, schedulerProvider);
    }

  /*  @Provides
    PostFileErrorToServerViewModel providePostFileErrorToServerViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new PostFileErrorToServerViewModel(dataManager, schedulerProvider);
    }*/


}