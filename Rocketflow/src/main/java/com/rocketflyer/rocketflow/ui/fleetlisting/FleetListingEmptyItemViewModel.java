package com.rocketflyer.rocketflow.ui.fleetlisting;

/**
 * Created by rahul on 18/9/18
 */
public class FleetListingEmptyItemViewModel {
//    private FleetListingEmptyItemViewHolderListener mListener;

    public FleetListingEmptyItemViewModel(FleetListingEmptyItemViewHolderListener listener) {
//        this.mListener = listener;
    }

//    public void onRetryClick() {
//        mListener.onRetryClick();
//    }

    public interface FleetListingEmptyItemViewHolderListener {
        void onRetryClick();
    }
}