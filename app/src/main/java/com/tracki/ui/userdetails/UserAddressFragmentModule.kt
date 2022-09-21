package com.tracki.ui.userdetails

import androidx.lifecycle.ViewModelProvider
import com.tracki.ViewModelProviderFactory
import com.tracki.data.DataManager
import com.tracki.ui.userdetails.UserAddressAdapter
import com.tracki.ui.userdetails.UserAddressViewModel
import com.tracki.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import java.util.ArrayList


@Module
class UserAddressFragmentModule {
    @Provides
    fun provideUserAddressViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider): UserAddressViewModel {
        return UserAddressViewModel(dataManager, schedulerProvider)
    }
    @Provides
    fun provideUserAddressAdapter(): UserAddressAdapter {
        return UserAddressAdapter(ArrayList())
    }

    @Provides
    open fun provideUserAddressViewModelFactory(addUserViewModel: UserAddressViewModel): ViewModelProvider.Factory {
        return ViewModelProviderFactory(addUserViewModel)
    }
}
