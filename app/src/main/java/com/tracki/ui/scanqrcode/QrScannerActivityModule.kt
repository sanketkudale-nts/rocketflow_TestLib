package com.tracki.ui.scanqrcode


import com.tracki.data.DataManager

import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides



@Module
class QrScannerActivityModule {
    @Provides
    fun     getQrCodeViewModel(dataManager: DataManager?,
                               schedulerProvider: SchedulerProvider?): QrCodeValueViewModel {
        return QrCodeValueViewModel(dataManager!!, schedulerProvider!!)
    }



}