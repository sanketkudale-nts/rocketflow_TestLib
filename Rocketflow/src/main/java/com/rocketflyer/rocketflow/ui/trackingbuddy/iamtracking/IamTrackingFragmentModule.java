package com.rocketflyer.rocketflow.ui.trackingbuddy.iamtracking;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul abrol on 05/10/18.
 */
@Module
public class IamTrackingFragmentModule {

    @Provides
    IamTrackingViewModel iamTrackingViewModel(DataManager dataManager,
                                              SchedulerProvider schedulerProvider) {
        return new IamTrackingViewModel(dataManager, schedulerProvider);
    }

    @Provides
    IamTrackingAdapter provideIamTrackingAdapter() {
        return new IamTrackingAdapter(new ArrayList<>());
    }

    @Provides
    ViewModelProvider.Factory provideIamTrackingAdapterViewModel(IamTrackingViewModel iamTrackingViewModel) {
        return new ViewModelProviderFactory<>(iamTrackingViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(IamTrackingFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }
}
