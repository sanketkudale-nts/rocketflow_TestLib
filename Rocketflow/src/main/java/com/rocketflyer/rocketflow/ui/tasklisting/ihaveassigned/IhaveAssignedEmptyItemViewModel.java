package com.rocketflyer.rocketflow.ui.tasklisting.ihaveassigned;

/**
 * Created by rahul on 5/10/18
 */
public class IhaveAssignedEmptyItemViewModel {
//    private IhaveAssignedEmptyItemViewModelListener mListener;

    public IhaveAssignedEmptyItemViewModel(IhaveAssignedEmptyItemViewModelListener listener) {
//        this.mListener = listener;
    }

//    public void onRetryClick() {
//        mListener.onRetryClick();
//    }

    public interface IhaveAssignedEmptyItemViewModelListener {

        void onRetryClick();
    }
}
