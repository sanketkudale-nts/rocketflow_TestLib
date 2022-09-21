package com.tracki.ui.tasklisting;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.tracki.ui.tasklisting.ihaveassigned.TabDataClass;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by rahul on 8/10/18
 */
public class TaskPagerAdapter extends FragmentStatePagerAdapter {

    private int mTabCount;
    private List<TabDataClass> fragments;
    public TaskPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mTabCount = 0;
    }

    @Override
    public int getCount() {
        return mTabCount;
    }

    public void setFragments(List<TabDataClass> fragments) {
        this.fragments = fragments;
        mTabCount=fragments.size();
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position).getFragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }
}