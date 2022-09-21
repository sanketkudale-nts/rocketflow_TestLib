package com.rocketflyer.rocketflow.ui.myDocument

import com.tracki.data.DataManager
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class MyDocumentModule {

    @Provides
    open fun provideMyDocumentModel(dataManager: DataManager, schedulerProvider: SchedulerProvider):
            MyDocumentViewModel {
        return MyDocumentViewModel(dataManager, schedulerProvider)
    }


    //Add Document type Array list
    @Provides
    fun provideDocumentListingAdapter(): DocumentsAdapter {
        return DocumentsAdapter()
    }
}