package com.tracki.ui.buddyprofile;

import android.view.View;

import com.tracki.ui.base.BaseNavigator;

/**
 * Created by rahul on 9/10/18
 */
public interface BuddyProfileNavigator extends BaseNavigator {
    void validate();

    void openTimePicker(View view);

    void openAddFleet();
}
