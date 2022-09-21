package com.tracki.ui.webview

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

/**
 * Created by rahul on 23/10/18
 */
@Module
class WebViewActivityModule {

    @Provides
    internal fun provideWebViewViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): WebViewModel {
        return WebViewModel(dataManager, schedulerProvider)
    }
}