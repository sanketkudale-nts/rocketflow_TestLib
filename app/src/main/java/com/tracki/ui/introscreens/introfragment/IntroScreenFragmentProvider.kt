package com.tracki.ui.introscreens.introfragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by rahul on 22/2/19
 */
@Module
public abstract class IntroScreenFragmentProvider {

    @ContributesAndroidInjector(modules = [IntroScreenFragmentModule::class])
    public abstract fun provideIntroScreenFragmentFactory(): IntroScreenFragment
}