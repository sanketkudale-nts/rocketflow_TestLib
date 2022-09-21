package com.tracki.ui.leave.leave_approval;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class LeaveApprovalFragmentModule {

    @Provides
    LeaveApprovalViewModel leaveApprovalViewModel(DataManager dataManager,
                                SchedulerProvider schedulerProvider) {
        return new LeaveApprovalViewModel(dataManager, schedulerProvider);
    }

    @Provides
    LeaveApprovalAdapter provideLeaveApprovalAdapter() {
        return new LeaveApprovalAdapter();
    }

    @Provides
    ViewModelProvider.Factory provideLeaveApprovalViewModel(LeaveApprovalViewModel leaveApprovalViewModel) {
        return new ViewModelProviderFactory<>(leaveApprovalViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(LeaveApprovalFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }

}
