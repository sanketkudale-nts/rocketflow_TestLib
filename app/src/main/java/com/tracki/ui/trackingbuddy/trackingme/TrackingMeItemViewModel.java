package com.tracki.ui.trackingbuddy.trackingme;

import androidx.databinding.ObservableField;

import com.tracki.data.model.response.config.Buddy;
import com.tracki.utils.CommonUtils;

/**
 * Created by rahul on 5/10/18
 */
public class TrackingMeItemViewModel {
    public final TrackingMeItemViewModelListener mListener;

    public final ObservableField<String> taskName;
    public final ObservableField<String> abbreviation;
    public final ObservableField<String> status;
    private Buddy trackingBean;


    TrackingMeItemViewModel(Buddy trackingBean, TrackingMeItemViewModelListener listener) {
        this.mListener = listener;
        this.trackingBean = trackingBean;
        this.taskName = new ObservableField<>(trackingBean.getName());
        this.abbreviation = new ObservableField<>(trackingBean.getShortCode());
        this.status = new ObservableField<>(CommonUtils.getBuddyStatus(trackingBean.getStatus()));
    }

    public void onItemClick() {
        mListener.onItemClick(trackingBean);
    }

    public interface TrackingMeItemViewModelListener {

        void onItemClick(Buddy trackingBean);
    }
}
