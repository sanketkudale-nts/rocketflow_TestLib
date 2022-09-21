package com.tracki.ui.leave;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 8/10/18
 */
public class LeavePagerAdapter extends FragmentPagerAdapter {

    private int mTabCount;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    LeavePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mTabCount = 0;
    }

    public void addFragment(Fragment fragment, String tag) {
        fragmentList.add(fragment);
        titleList.add(tag);
    }

    @Override
    public int getCount() {
        return mTabCount;
    }

    void setCount() {
        mTabCount = fragmentList.size();
    }


    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
        /*switch (position) {
            default:
                return ApplyLeaveFragment.newInstance();
            case 1:
                return LeaveSummaryFragment.newInstance();
            case 2:
                return LeaveHistoryFragment.newInstance();
            case 3:
                return LeaveApprovalFragment.newInstance();
        }*/
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(titleList.get(position)!=null)
        return titleList.get(position);
        else
            return "";
        /*switch (position) {
            default:
                return ;
            case 1:
                return;
            case 2:
                return ;
            case 3:
                return ;


        }*/
    }
}