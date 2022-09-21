package com.tracki.ui.chat

import com.tracki.data.model.response.config.ChatResponse

class LoadMoreItemViewModel(private val response: ChatResponse, var listener: LoadMoreListener) {

    fun onLoadMoreClick() {
        listener.onLoadMoreClick()
    }

    interface LoadMoreListener {
        fun onLoadMoreClick()
    }
}