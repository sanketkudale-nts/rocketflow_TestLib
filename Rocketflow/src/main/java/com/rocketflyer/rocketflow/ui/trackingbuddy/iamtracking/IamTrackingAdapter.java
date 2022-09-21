package com.rocketflyer.rocketflow.ui.trackingbuddy.iamtracking;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tracki.data.model.response.config.Buddy;
import com.tracki.databinding.ItemIAmTrackingEmptyViewBinding;
import com.tracki.databinding.ItemIAmTrackingViewBinding;
import com.tracki.ui.base.BaseViewHolder;
import com.tracki.ui.trackingbuddy.iamtracking.IamTrackingEmptyItemViewModel;
import com.tracki.ui.trackingbuddy.iamtracking.IamTrackingItemViewModel;

import java.util.List;

/**
 * Created by rahul on 5/10/18
 */
public class IamTrackingAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;

    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Buddy> mResponseList;

    private IamTrackingAdapterListener mListener;

    public IamTrackingAdapter(List<Buddy> responseList) {
        this.mResponseList = responseList;
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
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                ItemIAmTrackingViewBinding iAmTrackingViewBinding = ItemIAmTrackingViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new IamTrackingViewHolder(iAmTrackingViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemIAmTrackingEmptyViewBinding emptyViewBinding = ItemIAmTrackingEmptyViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }

    public void addItems(List<Buddy> stringList) {
        clearItems();
        mResponseList.addAll(stringList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mResponseList.clear();
    }

    public void setListener(IamTrackingAdapterListener listener) {
        this.mListener = listener;
    }

    public interface IamTrackingAdapterListener {

        void onAdapterItemClick(Buddy trackingBean);
    }


    public class IamTrackingViewHolder extends BaseViewHolder implements IamTrackingItemViewModel.IamTrackingItemViewModelListener {
        IamTrackingItemViewModel itemViewModel;
        private ItemIAmTrackingViewBinding mBinding;

        IamTrackingViewHolder(ItemIAmTrackingViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final Buddy buddyList = mResponseList.get(position);
            if (buddyList.getStatus() != null) {
                switch (buddyList.getStatus()) {
                    case OFFLINE:
                        mBinding.ivDriverStatus.setEnabled(false);
                        mBinding.ivDriverStatus.setSelected(false);
                        break;
                    case ONLINE:
                        mBinding.ivDriverStatus.setSelected(true);
                        break;
                    case PENDING:
                        mBinding.ivDriverStatus.setEnabled(true);
                        break;
                    default:
                        mBinding.ivDriverStatus.setEnabled(false);
                        mBinding.ivDriverStatus.setSelected(false);
                        break;
                }
            }
            itemViewModel = new IamTrackingItemViewModel(buddyList, this);
            mBinding.setViewModel(itemViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
        }

        @Override
        public void onItemClick(Buddy trackingBean) {
            mListener.onAdapterItemClick(trackingBean);
        }
    }

    public class EmptyViewHolder extends BaseViewHolder implements IamTrackingEmptyItemViewModel.IamTrackingEmptyItemViewModelListener {

        private ItemIAmTrackingEmptyViewBinding mBinding;

        EmptyViewHolder(ItemIAmTrackingEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            IamTrackingEmptyItemViewModel emptyItemViewModel = new IamTrackingEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }


        @Override
        public void onRetryClick() {
//            mListener.onRetryClick();
        }
    }

}