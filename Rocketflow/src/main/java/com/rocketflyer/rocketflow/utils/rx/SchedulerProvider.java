package com.rocketflyer.rocketflow.utils.rx;

import io.reactivex.Scheduler;

/**
 * Created by rahul on 07/07/17.
 */

public interface SchedulerProvider {

    Scheduler computation();

    Scheduler io();

    Scheduler ui();
}
