package com.rocketflyer.rocketflow.ui.attendance.attendance_tab;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tracki.data.model.response.config.Data;
import com.tracki.databinding.ItemAttendanceBinding;
import com.tracki.databinding.ItemAttendanceEmptyViewBinding;
import com.tracki.databinding.ItemAttendanceHeaderBinding;
import com.tracki.ui.attendance.attendance_tab.AttendanceEmptyItemViewModel;
import com.tracki.ui.attendance.attendance_tab.AttendanceHeaderItemViewModel;
import com.tracki.ui.attendance.attendance_tab.AttendanceItemViewModel;
import com.tracki.ui.base.BaseViewHolder;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_ATTENDANCE = 2;
    private static final int VIEW_TYPE_HEADER = 1;
    private List<Data> mResponseList;

    public void setAttendance(List<Data> mResponseList) {
        this.mResponseList = mResponseList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mResponseList != null && mResponseList.size() > 0) {
            return mResponseList.size() + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((mResponseList == null) || (mResponseList.isEmpty())) {
            return VIEW_TYPE_EMPTY;
        } else {
            if (position == 0) {
                return VIEW_TYPE_HEADER;
            } else
                return VIEW_TYPE_ATTENDANCE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ATTENDANCE) {
            ItemAttendanceBinding itemAttendanceBinding = ItemAttendanceBinding
                    .inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new AttendanceViewHolder(itemAttendanceBinding);
        } else if (viewType == VIEW_TYPE_HEADER) {

            ItemAttendanceHeaderBinding itemAttendanceHeaderBinding = ItemAttendanceHeaderBinding.
                    inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new AttendanceHeaderViewHolder(itemAttendanceHeaderBinding);
        } else {
            ItemAttendanceEmptyViewBinding itemAttendanceEmptyViewBinding = ItemAttendanceEmptyViewBinding
                    .inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new EmptyViewHolder(itemAttendanceEmptyViewBinding);
        }
    }

    public class EmptyViewHolder extends BaseViewHolder {

        private ItemAttendanceEmptyViewBinding mBinding;


        EmptyViewHolder(ItemAttendanceEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            AttendanceEmptyItemViewModel emptyItemViewModel = new AttendanceEmptyItemViewModel();
            mBinding.setViewModel(emptyItemViewModel);
        }
    }

    public class AttendanceViewHolder extends BaseViewHolder {

        private ItemAttendanceBinding mBinding;

        AttendanceViewHolder(ItemAttendanceBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            AttendanceItemViewModel attendanceItemViewModel = new AttendanceItemViewModel(mResponseList.get(position-1));
            mBinding.setViewModel(attendanceItemViewModel);
            mBinding.executePendingBindings();
        }
    }

    private class AttendanceHeaderViewHolder extends BaseViewHolder {

        private ItemAttendanceHeaderBinding mBinding;

        AttendanceHeaderViewHolder(ItemAttendanceHeaderBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }


        @Override
        public void onBind(int position) {
            AttendanceHeaderItemViewModel attendanceHeaderItemViewModel = new AttendanceHeaderItemViewModel();
            mBinding.setViewModel(attendanceHeaderItemViewModel);
            mBinding.executePendingBindings();
        }
    }

}
