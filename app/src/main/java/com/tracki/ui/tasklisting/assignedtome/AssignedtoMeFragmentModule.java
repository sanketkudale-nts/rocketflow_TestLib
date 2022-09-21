package com.tracki.ui.tasklisting.assignedtome;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.ui.tasklisting.TaskListingAdapter;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul on 8/10/18
 */
@Module
public class AssignedtoMeFragmentModule {

    @Provides
    AssignedToMeViewModel assignedtoMeViewModel(DataManager dataManager,
                                                SchedulerProvider schedulerProvider) {
        return new AssignedToMeViewModel(dataManager, schedulerProvider);
    }

    @Provides
    AssignedtoMeAdapter provideAssignedtoMeAdapter(PreferencesHelper preferencesHelper) {
        return new AssignedtoMeAdapter(new ArrayList<>(), preferencesHelper);
    }

    @Provides
    TaskListingAdapter provideTaskListingAdapter(PreferencesHelper preferencesHelper) {
        return new TaskListingAdapter(new ArrayList<>(), preferencesHelper);
    }

    @Provides
    ViewModelProvider.Factory provideAssignedtoMeViewModel(AssignedToMeViewModel assignedToMeViewModel) {
        return new ViewModelProviderFactory<>(assignedToMeViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AssignedtoMeFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }

}
