package com.rocketflyer.rocketflow.ui.buddylisting;

import android.view.View;

import androidx.databinding.ObservableField;

import com.tracki.data.model.response.config.Buddy;
import com.tracki.utils.CommonUtils;

/**
 * Created by rahul on 18/9/18
 */
public class BuddyListingItemViewModel {
    public final ObservableField<String> abbreviation;
    public final ObservableField<String> title;
    //    public final ObservableField<Boolean> isActive;
    public final ObservableField<Boolean> isChecked;
    public final ObservableField<String> status;
    public final ObservableField<Boolean> showSelected;
    private Buddy buddyBean;
    private BuddyListingItemViewModelListener listener;

    BuddyListingItemViewModel(Buddy buddyBean, BuddyListingItemViewModelListener listener) {
        this.buddyBean = buddyBean;
        this.listener = listener;
        abbreviation = new ObservableField<>(buddyBean.getShortCode());
        title = new ObservableField<>(buddyBean.getName());
//        isActive = new ObservableField<>(buddyBean.isActive());
        status = new ObservableField<>(CommonUtils.getBuddyStatus(buddyBean.getStatus()));
        isChecked = new ObservableField<>(buddyBean.isSelected());
        showSelected = new ObservableField<>(buddyBean.getShowSelected());
    }

    public void onItemClick() {
        listener.onItemClick(buddyBean);
    }

    public boolean onItemLongClick(View v) {
        listener.onItemLongClick(buddyBean);
        return false;
    }

    public interface BuddyListingItemViewModelListener {
        void onItemClick(Buddy buddyBean);

        void onItemLongClick(Buddy buddyBean);
    }
}
