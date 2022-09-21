package com.rocketflyer.rocketflow.data.local.db;

import com.tracki.ui.service.transition.UserActivity;

import java.util.List;

/**
 * Created by rahul on 22/5/19
 */
public interface ITransitionTable {

    void addTransition(UserActivity userActivity);

    List<UserActivity> getAllActivities();

}
