package com.rocketflyer.rocketflow.ui.taskdetails.timeline;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.ui.dynamicform.dynamicfragment.DynamicAdapter;
import com.tracki.ui.dynamicform.dynamicfragment.DynamicFragment;
import com.tracki.ui.dynamicform.dynamicfragment.DynamicViewModel;
import com.tracki.ui.taskdetails.NewTaskDetailsViewModel;
import com.tracki.ui.taskdetails.timeline.TaskDetailsFragment;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;


@Module
public class TaskDetailsFragmentModule {

  /*  @Provides
    DynamicViewModel newDynamicViewModel(DataManager dataManager, SchedulerProvider schedulerProvider){
        return new DynamicViewModel(dataManager,schedulerProvider);
    }

    @Provides
    ViewModelProvider.Factory provideDynamicViewModel(DynamicViewModel dynamicViewModel) {
        return new ViewModelProviderFactory<>(dynamicViewModel);
    }
*/

    @Provides
    NewTaskDetailsViewModel newTaskDetailsViewModel(DataManager dataManager,
                                                    SchedulerProvider schedulerProvider) {
        return new NewTaskDetailsViewModel(dataManager, schedulerProvider);
    }



    @Provides
    ViewModelProvider.Factory provideTaskDetailsViewModel(NewTaskDetailsViewModel newTaskDetailsViewModel) {
        return new ViewModelProviderFactory<>(newTaskDetailsViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(TaskDetailsFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }

}
