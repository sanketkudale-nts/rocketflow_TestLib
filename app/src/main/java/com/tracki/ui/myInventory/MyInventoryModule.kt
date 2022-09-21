package com.tracki.ui.myInventory

import com.tracki.data.DataManager
import com.tracki.ui.myDocument.DocumentsAdapter
import com.tracki.ui.myDocument.MyDocumentViewModel
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class MyInventoryModule {

    @Provides
    open fun provideMyInventoryModel(dataManager: DataManager, schedulerProvider: SchedulerProvider):
            MyInventoryViewModel {
        return MyInventoryViewModel(dataManager, schedulerProvider)
    }


    //Add Document type Array list
    @Provides
    fun provideMyInventoryAdapter(): MyInventoryAdapter {
        return MyInventoryAdapter()
    }
}