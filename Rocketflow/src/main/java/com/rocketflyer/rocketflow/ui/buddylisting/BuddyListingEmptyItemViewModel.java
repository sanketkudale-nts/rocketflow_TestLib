package com.rocketflyer.rocketflow.ui.buddylisting;

/**
 * Created by rahul on 18/9/18
 */
public class BuddyListingEmptyItemViewModel {
//    private BuddyListingEmptyItemViewHolderListener mListener;

    public BuddyListingEmptyItemViewModel(BuddyListingEmptyItemViewHolderListener listener) {
//        this.mListener = listener;
    }

//    public void onRetryClick() {
//        mListener.onRetryClick();
//    }

    public interface BuddyListingEmptyItemViewHolderListener {
        void onRetryClick();
    }
}