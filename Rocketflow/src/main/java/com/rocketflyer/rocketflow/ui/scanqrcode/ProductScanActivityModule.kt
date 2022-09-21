package com.rocketflyer.rocketflow.ui.scanqrcode


import com.tracki.data.DataManager

import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides



@Module
class ProductScanActivityModule {
    @Provides
    fun     getQrCodeViewModel(dataManager: DataManager?,
                               schedulerProvider: SchedulerProvider?): ProductScanViewModel {
        return ProductScanViewModel(dataManager!!, schedulerProvider!!)
    }



}