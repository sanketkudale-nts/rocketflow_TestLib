package com.tracki.ui.attendance.punchInOut;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class PunchInOutFragmentModule {

    @Provides
    PunchInOutViewModel punchInOutViewModel(DataManager dataManager,
                                SchedulerProvider schedulerProvider) {
        return new PunchInOutViewModel(dataManager, schedulerProvider);
    }

       @Provides
    ViewModelProvider.Factory punchInOutViewModelFactory(PunchInOutViewModel punchInOutViewModel) {
        return new ViewModelProviderFactory<>(punchInOutViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(PunchInOutFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }

}
