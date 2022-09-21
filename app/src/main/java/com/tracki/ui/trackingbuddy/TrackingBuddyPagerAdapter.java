package com.tracki.ui.trackingbuddy;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tracki.ui.trackingbuddy.iamtracking.IamTrackingFragment;
import com.tracki.ui.trackingbuddy.trackingme.TrackingMeFragment;

/**
 * Created by rahul on 5/10/18
 */
public class TrackingBuddyPagerAdapter extends FragmentPagerAdapter {


    private int mTabCount;

    TrackingBuddyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mTabCount = 0;
    }

    @Override
    public int getCount() {
        return mTabCount;
    }

    void setCount() {
        mTabCount = 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TrackingMeFragment.newInstance();
            case 1:
                return IamTrackingFragment.newInstance();
            default:
                return null;
        }
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Tracking Me";
            case 1:
                return "I am Tracking";
            default:
                return null;
        }
    }
}