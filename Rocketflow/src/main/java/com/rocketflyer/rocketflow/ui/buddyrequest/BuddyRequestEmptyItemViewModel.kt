package com.rocketflyer.rocketflow.ui.buddyrequest

/**
 * Created by rahul on 21/12/18
 */
class BuddyRequestEmptyItemViewModel(private var listener: BuddyRequestEmptyListener) {

    interface BuddyRequestEmptyListener {
        fun onRetryClick()
    }

}