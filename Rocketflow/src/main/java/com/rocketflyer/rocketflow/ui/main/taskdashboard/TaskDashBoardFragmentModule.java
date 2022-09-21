package com.rocketflyer.rocketflow.ui.main.taskdashboard;

import androidx.lifecycle.ViewModelProvider;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */


@Module
public class TaskDashBoardFragmentModule {

    @Provides
    TaskDashBoardViewModel provideTaskDashBoardViewModel(DataManager dataManager,
                                                         SchedulerProvider schedulerProvider) {
        return new TaskDashBoardViewModel(dataManager, schedulerProvider);
    }

    @Provides
    ViewModelProvider.Factory provideTaskDashBoardViewModelFactory(TaskDashBoardViewModel taskDashBoardViewModel) {
        return new ViewModelProviderFactory<>(taskDashBoardViewModel);
    }


}