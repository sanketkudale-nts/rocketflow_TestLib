package com.rocketflyer.rocketflow.ui.newdynamicform

import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class NewDynamicFragmentProvider {

    @ContributesAndroidInjector(modules = [NewDynamicFormModule::class])
    abstract fun provideNewDynamicFragmentFactory(): NewDynamicFormFragment
}