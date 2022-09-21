package com.tracki.ui.chat

import com.tracki.data.DataManager
import com.tracki.data.model.response.config.ChatResponse
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class ChatActivityModule {

    @Provides
    internal fun provideChatViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): ChatViewModel {
        return ChatViewModel(dataManager, schedulerProvider)
    }

    @Provides
    internal fun provideChatAdapter(): ChatAdapter {
        return ChatAdapter(ArrayList<ChatResponse>())
    }
}