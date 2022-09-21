package com.tracki.ui.dynamicform.dynamicfragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Rahul Abrol on 15/7/19.
 */
@Module
abstract class DynamicFragmentProvider {

    @ContributesAndroidInjector(modules = [DynamicFragmentModule::class])
    abstract fun provideDynamicFragmentFactory(): DynamicFragment
}