package com.tracki.ui.leave.leave_summary;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tracki.data.model.response.config.Data;
import com.tracki.databinding.ItemLeaveSummaryBinding;
import com.tracki.databinding.ItemLeaveSummaryEmptyViewBinding;
import com.tracki.ui.base.BaseViewHolder;

import java.util.List;

public class LeaveSummaryAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_LEAVE_SUMMARY = 1;
    private List<Data> mResponseList;

    public void setAttendance(List<Data> mResponseList) {
        this.mResponseList = mResponseList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mResponseList != null && mResponseList.size() > 0) {
            return mResponseList.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((mResponseList == null) || (mResponseList.isEmpty())) {
            return VIEW_TYPE_EMPTY;
        } else
            return VIEW_TYPE_LEAVE_SUMMARY;
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            ItemLeaveSummaryEmptyViewBinding itemLeaveSummaryEmptyViewBinding = ItemLeaveSummaryEmptyViewBinding
                    .inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new EmptyViewHolder(itemLeaveSummaryEmptyViewBinding);

        } else {
            ItemLeaveSummaryBinding itemLeaveSummaryBinding = ItemLeaveSummaryBinding
                    .inflate(LayoutInflater.from(parent.getContext()),
                            parent, false);
            return new LeaveSummaryViewHolder(itemLeaveSummaryBinding);
        }
    }

    public class EmptyViewHolder extends BaseViewHolder {

        private ItemLeaveSummaryEmptyViewBinding mBinding;

        EmptyViewHolder(ItemLeaveSummaryEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            LeaveSummaryEmptyItemViewModel emptyItemViewModel = new LeaveSummaryEmptyItemViewModel();
            mBinding.setViewModel(emptyItemViewModel);
        }
    }

    public class LeaveSummaryViewHolder extends BaseViewHolder {

        private ItemLeaveSummaryBinding mBinding;

        LeaveSummaryViewHolder(ItemLeaveSummaryBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            LeaveSummaryItemViewModel leaveSummaryItemViewModel = new LeaveSummaryItemViewModel(mResponseList.get(position ));
            mBinding.setViewModel(leaveSummaryItemViewModel);
            mBinding.executePendingBindings();
        }
    }
}




