package com.rocketflyer.rocketflow.ui.leave.leave_history;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.ui.attendance.AttendanceStatusAdapter;
import com.tracki.ui.leave.leave_history.LeaveHistoryAdapter;
import com.tracki.ui.leave.leave_history.LeaveHistoryViewModel;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

@Module
public class LeaveHistoryFragmentModule {

    @Provides
    LeaveHistoryViewModel leaveHistoryViewModel(DataManager dataManager,
                                                SchedulerProvider schedulerProvider) {
        return new LeaveHistoryViewModel(dataManager, schedulerProvider);
    }

    @Provides
    LeaveHistoryAdapter provideLeaveHistoryAdapter() {
        return new LeaveHistoryAdapter();
    }
    @Provides
    AttendanceStatusAdapter provideAAttendanceStatusAdapter() {
        return new AttendanceStatusAdapter(new ArrayList<>());
    }

    @Provides
    ViewModelProvider.Factory provideLeaveHistoryViewModel(LeaveHistoryViewModel leaveHistoryViewModel) {
        return new ViewModelProviderFactory<>(leaveHistoryViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(LeaveHistoryFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }

}
