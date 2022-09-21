package com.rocketflyer.rocketflow.ui.leave.leave_summary;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.ui.leave.leave_summary.LeaveSummaryViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class LeaveSummaryFragmentModule {

    @Provides
    LeaveSummaryViewModel leaveSummaryViewModel(DataManager dataManager,
                                                SchedulerProvider schedulerProvider) {
        return new LeaveSummaryViewModel(dataManager, schedulerProvider);
    }

    @Provides
    LeaveSummaryAdapter provideLeaveSummaryAdapter() {
        return new LeaveSummaryAdapter();
    }

    @Provides
    ViewModelProvider.Factory provideLeaveSummaryViewModel(LeaveSummaryViewModel leaveSummaryViewModel) {
        return new ViewModelProviderFactory<>(leaveSummaryViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(LeaveSummaryFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }

}
