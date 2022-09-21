package com.tracki.ui.trackingbuddy.trackingme;

/**
 * Created by rahul on 5/10/18
 */
public class TrackingMeEmptyItemViewModel {
//    private TrackingMeEmptyItemViewModelListener mListener;

    TrackingMeEmptyItemViewModel(TrackingMeEmptyItemViewModelListener listener) {
//        this.mListener = listener;
    }

//    public void onRetryClick() {
//        mListener.onRetryClick();
//    }

    public interface TrackingMeEmptyItemViewModelListener {

        void onRetryClick();
    }
}
