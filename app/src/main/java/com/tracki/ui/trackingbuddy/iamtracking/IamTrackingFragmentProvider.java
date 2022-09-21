package com.tracki.ui.trackingbuddy.iamtracking;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class IamTrackingFragmentProvider {

    @ContributesAndroidInjector(modules = IamTrackingFragmentModule.class)
    abstract IamTrackingFragment provideIamTrackingFragmentFactory();
}
