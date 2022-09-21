package com.tracki.ui.userdetails


import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class UserAddressFragmentProvider {
    @ContributesAndroidInjector(modules = [UserAddressFragmentModule::class])
    abstract fun provideUserAddressListFragment(): UserAddressListFragment?
}