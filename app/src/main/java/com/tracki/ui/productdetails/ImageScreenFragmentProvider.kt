package com.tracki.ui.productdetails

import dagger.Module
import dagger.android.ContributesAndroidInjector



@Module
public abstract class ImageScreenFragmentProvider {

    @ContributesAndroidInjector(modules = [ImageScreenFragmentModule::class])
    public abstract fun provideImageFragmentFactory(): ImageFragment
}