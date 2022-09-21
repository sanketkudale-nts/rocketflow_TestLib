package com.tracki.ui.trackingbuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.databinding.ActivityTrackingBuddyBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.utils.CommonUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by rahul on 5/10/18
 */
public class TrackingBuddyActivity extends BaseActivity<ActivityTrackingBuddyBinding, TrackingBuddyViewModel>
        implements TrackingBuddyNavigator, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    @Inject
    TrackingBuddyViewModel mTrackingBuddyViewModel;
    @Inject
    TrackingBuddyPagerAdapter mPagerAdapter;

    private ActivityTrackingBuddyBinding mActivityTrackingBuddyBinding;

    private Snackbar snackBar;

    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar= CommonUtils.showNetWorkConnectionIssue( mActivityTrackingBuddyBinding.llMain,getString(R.string.please_check_your_internet_connection));
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, TrackingBuddyActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tracking_buddy;
    }

    @Override
    public TrackingBuddyViewModel getViewModel() {
        return mTrackingBuddyViewModel;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTrackingBuddyBinding = getViewDataBinding();
        setUp();
    }

    private void setUp() {
        setToolbar(mActivityTrackingBuddyBinding.toolbar, getString(R.string.tracking_buddy));
        mPagerAdapter.setCount();

        ViewPager viewPager = mActivityTrackingBuddyBinding.vpTrackingBuddy;
        TabLayout tabLayout = mActivityTrackingBuddyBinding.tabLayout;

        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tracking_me)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.i_am_tracking)));

        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        /*tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
        });*/
    }
}
