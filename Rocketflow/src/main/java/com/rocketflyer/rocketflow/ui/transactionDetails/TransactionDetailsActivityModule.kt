package com.rocketflyer.rocketflow.ui.transactionDetails

import com.tracki.data.DataManager
import com.tracki.ui.wallet.TransactionViewModel
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.*


/**
 * Created by Vikas Kesharvani on 18/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */

@Module
class TransactionDetailsActivityModule {
    @Provides
    open fun provideWalletActivityModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): TransactionViewModel {
        return TransactionViewModel(dataManager, schedulerProvider)
    }


    /*@Provides
    fun provideWalletListingAdapter(): WalletAdapter {
        return WalletAdapter(ArrayList())
    }*/

}