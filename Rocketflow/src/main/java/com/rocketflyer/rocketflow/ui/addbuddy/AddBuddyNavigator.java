package com.rocketflyer.rocketflow.ui.addbuddy;

import android.view.View;

import com.tracki.ui.base.BaseNavigator;

/**
 * Created by rahul on 18/9/18
 */
public interface AddBuddyNavigator extends BaseNavigator {
    void validate();

    void onBackClick();

    void openTimePicker(View view);
}
