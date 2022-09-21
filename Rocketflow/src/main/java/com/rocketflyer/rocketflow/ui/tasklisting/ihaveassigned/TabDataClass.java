package com.rocketflyer.rocketflow.ui.tasklisting.ihaveassigned;

import com.tracki.ui.base.BaseFragment;

/**
 * Created by Vikas Kesharvani on 08/07/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
public class TabDataClass {
    BaseFragment fragment;
    String title;

    public BaseFragment getFragment() {
        return fragment;
    }

    public String getTitle() {
        return title;
    }

    public TabDataClass(BaseFragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }
}
