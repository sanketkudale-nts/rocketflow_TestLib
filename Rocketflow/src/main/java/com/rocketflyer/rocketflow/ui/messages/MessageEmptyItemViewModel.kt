package com.rocketflyer.rocketflow.ui.messages

class MessageEmptyItemViewModel (private var listener: MessageEmptyListener) {

    /*  fun onRetryClick() {
          listener.onRetryClick()
      }*/

    interface MessageEmptyListener {
        fun onRetryClick()
    }

}