package com.tracki.ui.attendance.punchInOut;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class PunchInOutFragmentProvider {

    @ContributesAndroidInjector(modules = PunchInOutFragmentModule.class)
    abstract PunchInOutFragment providePunchInOutFragmentFactory();

}
