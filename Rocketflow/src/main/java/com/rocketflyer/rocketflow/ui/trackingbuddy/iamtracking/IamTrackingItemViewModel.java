package com.rocketflyer.rocketflow.ui.trackingbuddy.iamtracking;


import androidx.databinding.ObservableField;

import com.tracki.data.model.response.config.Buddy;
import com.tracki.utils.CommonUtils;

/**
 * Created by rahul on 5/10/18
 */
public class IamTrackingItemViewModel {

    public final IamTrackingItemViewModelListener mListener;

    public final ObservableField<String> taskName;
    public final ObservableField<String> abbreviation;
    public final ObservableField<String> status;
    private Buddy trackingBean;

    IamTrackingItemViewModel(Buddy trackingBean, IamTrackingItemViewModelListener listener) {
        this.mListener = listener;
        this.trackingBean = trackingBean;
        this.taskName = new ObservableField<>(trackingBean.getName());
        this.abbreviation = new ObservableField<>(trackingBean.getShortCode());
        this.status = new ObservableField<>(CommonUtils.getBuddyStatus(trackingBean.getStatus()));
    }

    public void onItemClick() {
        mListener.onItemClick(trackingBean);
    }

    public interface IamTrackingItemViewModelListener {

        void onItemClick(Buddy bean);
    }
}
