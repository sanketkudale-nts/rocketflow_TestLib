package com.tracki.ui.addcustomer


import com.tracki.ui.newdynamicform.NewDynamicFormFragment
import com.tracki.ui.newdynamicform.NewDynamicFormModule
import dagger.Module
import dagger.android.ContributesAndroidInjector



@Module
abstract class AddCustomerFragmentProvider {
    @ContributesAndroidInjector(modules = [AddUserViewModule::class])
    abstract fun provideCustomerInfoScreenFragment(): CustomerInfoScreenFragment?

    @ContributesAndroidInjector(modules = [NewDynamicFormModule::class])
    abstract fun provideNewDynamicFragmentFactory(): NewDynamicFormFragment
}