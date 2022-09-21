package com.rocketflyer.rocketflow.ui.tasklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.Task;
import com.tracki.databinding.ItemAssignedToMeEmptyViewBinding;
import com.tracki.databinding.ItemRowTaskListBinding;
import com.tracki.ui.base.BaseViewHolder;
import com.tracki.ui.tasklisting.AssignTaskViewModel;
import com.tracki.ui.tasklisting.TaskItemClickListener;
import com.tracki.ui.tasklisting.assignedtome.AssignedtoMeEmptyItemViewModel;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TaskListingAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Task> mResponseList;
    private TaskItemClickListener mListener;
    private Context context;
    private PreferencesHelper preferencesHelper;
    private String categoryId = null;
    private boolean assignedToTask;


    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public void setAssignedToTask(boolean assignedToTask) {
        this.assignedToTask = assignedToTask;
    }


    public TaskListingAdapter(List<Task> responseList, PreferencesHelper preferencesHelper) {
        this.mResponseList = responseList;
        this.preferencesHelper = preferencesHelper;
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
        if (mResponseList != null && !mResponseList.isEmpty()) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                ItemRowTaskListBinding itemAssignedToMeViewBinding = ItemRowTaskListBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new TaskViewHolder(itemAssignedToMeViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemAssignedToMeEmptyViewBinding emptyViewBinding = ItemAssignedToMeEmptyViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }

    public void addItems(List<Task> stringList) {
        //  clearItems();

        mResponseList.addAll(stringList);
        Set<Task> set = new TreeSet<>(new TaskComparator());
        set.addAll(mResponseList);
        mResponseList.clear();
        mResponseList.addAll(set);
        notifyDataSetChanged();
    }

    public Task getTaskByTaskId(String taskId) {
        if (mResponseList != null && !mResponseList.isEmpty()) {
            Task reqTask = new Task();
            reqTask.setTaskId(taskId);
            if (mResponseList.contains(reqTask)) {
                int position = mResponseList.indexOf(reqTask);
                if (position != -1) {
                    return mResponseList.get(position);
                }
            }
        }
        return null;
    }

    public List<Task> getList() {
        return mResponseList;
    }

    public void clearItems() {
        mResponseList.clear();
        notifyDataSetChanged();
    }

    public void setListener(TaskItemClickListener listener) {
        this.mListener = listener;
    }


    public class TaskViewHolder extends BaseViewHolder implements AssignTaskViewModel
            .TaskItemViewModelListener {
        private ItemRowTaskListBinding mBinding;

        TaskViewHolder(ItemRowTaskListBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final Task task = mResponseList.get(position);
            AssignTaskViewModel itemViewModel = new AssignTaskViewModel(task,
                    this, context, preferencesHelper, categoryId, assignedToTask);
            mBinding.setViewModel(itemViewModel);


        }

        @Override
        public void onItemClick(Task bean) {
            mListener.onItemClick(bean, getAdapterPosition());
        }

        @Override
        public void onCallClick(String mobile) {
            mListener.onCallClick(mobile);
        }


        @Override
        public void onDetailsClick(Task task) {
            mListener.onDetailsTaskClick(task);

        }
    }

    public class EmptyViewHolder extends BaseViewHolder implements
            AssignedtoMeEmptyItemViewModel.
                    AssignedtoMeEmptyItemViewModelListener {

        private ItemAssignedToMeEmptyViewBinding mBinding;

        EmptyViewHolder(ItemAssignedToMeEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            AssignedtoMeEmptyItemViewModel emptyItemViewModel = new AssignedtoMeEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }

        @Override
        public void onRetryClick() {
            //     mListener.onRetryClick();
        }
    }

}
