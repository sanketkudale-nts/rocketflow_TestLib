package com.tracki.ui.notification

/**
 * Created by rahul on 11/10/18
 */
open class NotificationEmptyItemViewModel(private var listener: NotificationEmptyListener) {

  /*  fun onRetryClick() {
        listener.onRetryClick()
    }*/

    interface NotificationEmptyListener {
        fun onRetryClick()
    }

}