package com.tracki.ui.attendance.attendance_tab;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.ui.attendance.AllTypeAttendanceAdapter;
import com.tracki.ui.attendance.AttendanceStatusAdapter;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

@Module
public class AttendanceFragmentModule {

    @Provides
    AttendanceViewModel attendanceViewModel(DataManager dataManager,
                                SchedulerProvider schedulerProvider) {
        return new AttendanceViewModel(dataManager, schedulerProvider);
    }

    @Provides
    AttendanceAdapter provideAttendanceAdapter() {
        return new AttendanceAdapter();
    }
    @Provides
    AttendanceStatusAdapter provideAAttendanceStatusAdapter() {
        return new AttendanceStatusAdapter(new ArrayList<>());
    }
    @Provides
    AllTypeAttendanceAdapter provideAllTypeAttendanceAdapter() {
        return new AllTypeAttendanceAdapter();
    }



    @Provides
    ViewModelProvider.Factory provideAttendanceViewModel(AttendanceViewModel attendanceViewModel) {
        return new ViewModelProviderFactory<>(attendanceViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AttendanceFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }

}
