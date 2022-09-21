package com.tracki.ui.fleetlisting;

import android.view.View;

import androidx.databinding.ObservableField;

import com.tracki.data.model.response.config.Fleet;

/**
 * Created by rahul on 18/9/18
 */
public class FleetListingItemViewModel {
    public final ObservableField<String> title;
    public final ObservableField<String> refId;
    public final ObservableField<Boolean> isActive;
    public final ObservableField<Boolean> isChecked;
    public final ObservableField<String> status;
    public final ObservableField<Boolean> showSelected;
    private Fleet buddyBean;
    private FleetListingItemViewModelListener listener;

    FleetListingItemViewModel(Fleet buddyBean, FleetListingItemViewModelListener listener) {
        this.buddyBean = buddyBean;
        this.listener = listener;
        if(buddyBean.getInvName()!=null)
          title = new ObservableField<>(buddyBean.getInvName());
        else{
            title=new ObservableField<>("");
        }
        if (buddyBean.getStatus().equals("ACTIVE"))
            isActive = new ObservableField<>(true);
        else
            isActive = new ObservableField<>(false);
        refId = new ObservableField<>(buddyBean.getRefId());
        status = new ObservableField<>(buddyBean.getStatus());
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

    public interface FleetListingItemViewModelListener {
        void onItemClick(Fleet item);

        void onItemLongClick(Fleet item);
    }
}
