package com.rocketflyer.rocketflow.ui.messages

import com.tracki.data.DataManager
import com.tracki.data.model.response.config.Buddy
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class MessageActivityModule {
    @Provides
    internal fun provideMessageViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): MessageViewModel {
        return MessageViewModel(dataManager, schedulerProvider)
    }

    @Provides
    internal fun provideMessageAdapter(): MessageAdapter {
        return MessageAdapter(ArrayList<Buddy>())
    }
}