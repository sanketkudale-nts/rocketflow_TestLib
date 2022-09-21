package com.tracki.di.builder;

import com.tracki.ui.receiver.RestartReceiverModule;
import com.tracki.ui.receiver.ServiceRestartReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by rahul on 11/3/19
 */
@Module
abstract public class ReceiverBuilder {

    @ContributesAndroidInjector(modules = RestartReceiverModule.class)
    abstract ServiceRestartReceiver bindRestartReceiver();

}
