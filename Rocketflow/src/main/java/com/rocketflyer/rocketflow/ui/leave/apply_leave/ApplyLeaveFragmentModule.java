package com.rocketflyer.rocketflow.ui.leave.apply_leave;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.ui.leave.apply_leave.ApplyLeaveViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplyLeaveFragmentModule {

    @Provides
    ApplyLeaveViewModel applyLeaveViewModel(DataManager dataManager,
                                            SchedulerProvider schedulerProvider) {
        return new ApplyLeaveViewModel(dataManager, schedulerProvider);
    }

       @Provides
    ViewModelProvider.Factory provideApplyLeaveViewModel(ApplyLeaveViewModel applyLeaveViewModel) {
        return new ViewModelProviderFactory<>(applyLeaveViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(ApplyLeaveFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }

}
