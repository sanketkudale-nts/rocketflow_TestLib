package com.tracki.ui.userdetails.basicinfo

import com.tracki.ui.addcustomer.AddUserViewModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class UserBasicInfoFragmentProvider {
    @ContributesAndroidInjector(modules = [AddUserViewModule::class])
    abstract fun provideUserBasicInfoFragment(): UserBasicInfoFragment?
}