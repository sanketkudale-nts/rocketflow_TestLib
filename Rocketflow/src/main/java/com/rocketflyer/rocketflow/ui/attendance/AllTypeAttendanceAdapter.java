package com.rocketflyer.rocketflow.ui.attendance;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tracki.data.model.response.config.AttendanceStatus;
import com.tracki.data.model.response.config.Data;
import com.tracki.databinding.ItemAttendanceBinding;
import com.tracki.databinding.ItemAttendanceEmptyViewBinding;
import com.tracki.databinding.ItemAttendanceHeaderBinding;
import com.tracki.databinding.ItemAttendancePresentBinding;
import com.tracki.databinding.ItemLayoutAbsentBinding;
import com.tracki.databinding.ItemLayoutDayOffBinding;
import com.tracki.databinding.ItemLayoutOnleaveBinding;
import com.tracki.ui.attendance.AbsentViewModel;
import com.tracki.ui.attendance.DayOffViewModel;
import com.tracki.ui.attendance.OnLeaveViewModel;
import com.tracki.ui.attendance.PresentItemViewModel;
import com.tracki.ui.attendance.attendance_tab.AttendanceAdapter;
import com.tracki.ui.attendance.attendance_tab.AttendanceEmptyItemViewModel;
import com.tracki.ui.attendance.attendance_tab.AttendanceHeaderItemViewModel;
import com.tracki.ui.attendance.attendance_tab.AttendanceItemViewModel;
import com.tracki.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AllTypeAttendanceAdapter  extends RecyclerView.Adapter<BaseViewHolder> {

    //ALL, PRESENT, ABSENT, ON_LEAVE, HOLIDAY, DAY_OFF, NOT_UPDATED
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_PRESENT = 1;
    private static final int VIEW_TYPE_ABSENT = 2;
    private static final int VIEW_TYPE_ON_LEAVE= 3;
    private static final int VIEW_TYPE_DAY_OFF = 4;
    private List<Data> mResponseList=new ArrayList<>();

    public void setAttendance(List<Data> mResponseList) {
        this.mResponseList.addAll(mResponseList) ;
        notifyDataSetChanged();
    }
    public  void clearData(){
        if(mResponseList!=null&&!mResponseList.isEmpty()){
            mResponseList.clear();
            notifyDataSetChanged();
        }
    }
    @Override
    public int getItemCount() {
        if (mResponseList != null && mResponseList.size() > 0) {
            return mResponseList.size() ;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((mResponseList == null) || (mResponseList.isEmpty())) {
            return VIEW_TYPE_EMPTY;
        } else {
            if(mResponseList.get(position).getStatus()!=null){
                if(mResponseList.get(position).getStatus()== AttendanceStatus.PRESENT){
                    return VIEW_TYPE_PRESENT;
                }else if(mResponseList.get(position).getStatus()== AttendanceStatus.ABSENT){
                    return VIEW_TYPE_ABSENT;
                }else if(mResponseList.get(position).getStatus()== AttendanceStatus.ON_LEAVE){
                    return VIEW_TYPE_ON_LEAVE;
                }else if(mResponseList.get(position).getStatus()== AttendanceStatus.DAY_OFF){
                    return VIEW_TYPE_DAY_OFF;
                }else{
                    return VIEW_TYPE_EMPTY;
                }

            }else{
                return VIEW_TYPE_EMPTY;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PRESENT) {
            ItemAttendancePresentBinding itemAttendanceBinding = ItemAttendancePresentBinding
                    .inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new PresentViewHolder(itemAttendanceBinding);
        } else if (viewType == VIEW_TYPE_ABSENT) {

            ItemLayoutAbsentBinding itemAttendanceHeaderBinding = ItemLayoutAbsentBinding.
                    inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new AbsentViewViewHolder(itemAttendanceHeaderBinding);
        } else if (viewType == VIEW_TYPE_ON_LEAVE) {

            ItemLayoutOnleaveBinding itemAttendanceHeaderBinding = ItemLayoutOnleaveBinding.
                    inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new OnLeaveViewHolderViewViewHolder(itemAttendanceHeaderBinding);
        } else if (viewType == VIEW_TYPE_DAY_OFF) {

            ItemLayoutDayOffBinding itemAttendanceHeaderBinding = ItemLayoutDayOffBinding.
                    inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new DayOffViewHolderViewViewHolder(itemAttendanceHeaderBinding);
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

    public class PresentViewHolder extends BaseViewHolder {

        private ItemAttendancePresentBinding mBinding;

        PresentViewHolder(ItemAttendancePresentBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            PresentItemViewModel attendanceItemViewModel = new PresentItemViewModel(mResponseList.get(position));
            mBinding.setViewModel(attendanceItemViewModel);
            mBinding.executePendingBindings();
        }
    }
    public class AbsentViewViewHolder extends BaseViewHolder {

        private ItemLayoutAbsentBinding mBinding;

        AbsentViewViewHolder(ItemLayoutAbsentBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            AbsentViewModel attendanceItemViewModel = new AbsentViewModel(mResponseList.get(position));
            mBinding.setViewModel(attendanceItemViewModel);
            mBinding.executePendingBindings();
        }
    }

    public class OnLeaveViewHolderViewViewHolder extends BaseViewHolder {

        private ItemLayoutOnleaveBinding mBinding;

        OnLeaveViewHolderViewViewHolder(ItemLayoutOnleaveBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            OnLeaveViewModel attendanceItemViewModel = new OnLeaveViewModel(mResponseList.get(position));
            mBinding.setViewModel(attendanceItemViewModel);
            mBinding.executePendingBindings();
        }
    }

    public class DayOffViewHolderViewViewHolder extends BaseViewHolder {

        private ItemLayoutDayOffBinding mBinding;

        DayOffViewHolderViewViewHolder(ItemLayoutDayOffBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            DayOffViewModel attendanceItemViewModel = new DayOffViewModel(mResponseList.get(position));
            mBinding.setViewModel(attendanceItemViewModel);
            mBinding.executePendingBindings();
        }
    }



}
