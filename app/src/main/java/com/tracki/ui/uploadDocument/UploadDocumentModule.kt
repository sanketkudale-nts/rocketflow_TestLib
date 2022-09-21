package com.tracki.ui.uploadDocument

import com.tracki.data.DataManager
import com.tracki.ui.myDocument.DocumentsAdapter
import com.tracki.ui.myDocument.MyDocumentViewModel
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class UploadDocumentModule {

    @Provides
    open fun provideUploadDocumentModel(dataManager: DataManager, schedulerProvider: SchedulerProvider):
            UploadDocumentViewModel {
        return UploadDocumentViewModel(dataManager, schedulerProvider)
    }


    //Add Document type Array list
    /*@Provides
    fun provideDocumentListingAdapter(): DocumentsAdapter {
        return DocumentsAdapter()
    }*/
}