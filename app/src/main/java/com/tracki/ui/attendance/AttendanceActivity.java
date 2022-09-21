package com.tracki.ui.attendance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.LeaveStatus;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.databinding.ActivityAttendanceBinding;
import com.tracki.ui.attendance.attendance_tab.AttendanceFragment;
import com.tracki.ui.attendance.punchInOut.PunchInOutFragment;
import com.tracki.ui.attendance.teamattendance.TeamAttendanceFragment;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.common.FilterDialog;
import com.tracki.ui.common.OnClickSearchListener;
import com.tracki.ui.idealtrip.IdealTripDetailsActivity;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by rahul on 8/10/18
 */
public class AttendanceActivity extends BaseActivity<ActivityAttendanceBinding, AttendanceViewModel>
        implements AttendanceNavigator, HasSupportFragmentInjector, OnClickSearchListener, PunchInOutFragment.AttendanceIdSendListener {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    @Inject
    AttendanceViewModel mAttendanceViewModel;
    @Inject
    AttendancePagerAdapter mPagerAdapter;
    FilterDialog filterDialog;

    ActivityAttendanceBinding mActivityAttendanceBinding;
    PunchInOutFragment punchInOutFragment;
    AttendanceFragment attendanceFragment;
    TeamAttendanceFragment teamAttendanceFragment;
    MenuItem item;
    MenuItem itemIdleTrip;
    private ViewPager viewPager;
    private String punchId;
    @Inject
    PreferencesHelper preferencesHelper;
    private Snackbar snackBar;

    public static Intent newIntent(Context context) {
        return new Intent(context, AttendanceActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_attendance;
    }

    @Override
    public AttendanceViewModel getViewModel() {
        return mAttendanceViewModel;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    public void onClickFilter() {
        List<String> list = new ArrayList<>();
        list.add(AppConstants.ALL);
        list.add(AppConstants.PRESENT);
        list.add(AppConstants.ABSENT);
        list.add(AppConstants.DAY_OFF);
        list.add(AppConstants.HOLIDAY);
        list.add(AppConstants.ON_LEAVE);


        filterDialog = new FilterDialog(this, this, list, AppConstants.ATTENDANCE_Title);
        filterDialog.show();
        Window window = filterDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onClickSearch(long from, long to, @NotNull LeaveStatus status) {

        if (status == LeaveStatus.ALL)
            attendanceFragment.onClickSearch(from, to, null);
        else
            attendanceFragment.onClickSearch(from, to, status);

        filterDialog.cancel();
    }

    @Override
    public void onClickCancel() {
        filterDialog.cancel();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAttendanceBinding = getViewDataBinding();
        mAttendanceViewModel.setNavigator(this);
        punchInOutFragment = PunchInOutFragment.newInstance();
        punchInOutFragment.setPunchIdFragment(this);
        attendanceFragment = AttendanceFragment.newInstance(preferencesHelper.getUserDetail(),true,false);
        if (preferencesHelper.isManger())
            teamAttendanceFragment = TeamAttendanceFragment.Companion.newInstance();
        setUp();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (mPagerAdapter.getCount() > 2) {

                    if (position == 0) {
                        item.setVisible(false);
                        itemIdleTrip.setVisible(false);
                    } else if (position == 1) {
                        item.setVisible(false);
                    } else
                        item.setVisible(false);
                } else {
                    if (position == 1) {
                        item.setVisible(true);
                    } else
                        item.setVisible(false);
                }

            }
        });
    }

    private void setUp() {
        setToolbar(mActivityAttendanceBinding.toolbar, getString(R.string.attendance));


        viewPager = mActivityAttendanceBinding.vpAttendance;
        TabLayout tabLayout = mActivityAttendanceBinding.tabLayoutAttendance;
        //add fragments
        if (teamAttendanceFragment != null && preferencesHelper.isManger()) {
            mPagerAdapter.addFragment(teamAttendanceFragment, AppConstants.TEAM_ATTENDANCE);
        }
        mPagerAdapter.addFragment(punchInOutFragment, AppConstants.PUNCH_IN_OUT);
        mPagerAdapter.addFragment(attendanceFragment, AppConstants.ATTENDANCE_I);


        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.filter_attendance_leave, menu);
        item = menu.findItem(R.id.action_filter);
        itemIdleTrip = menu.findItem(R.id.action_location);
        if (preferencesHelper.getIsIdealTrackingEnable())
            itemIdleTrip.setVisible(preferencesHelper.getIdleTripActive());
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_filter) {
            onClickFilter();
            return true;
        }
        if (item.getItemId() == R.id.action_location) {
            Intent intent = new Intent(this, IdealTripDetailsActivity.class);
            intent.putExtra("taskId", punchId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void trackingIdVisible(String punchId, boolean isStart) {
        this.punchId = punchId;
        itemIdleTrip.setVisible(isStart);
    }
    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
//        RelativeLayout rlMain=mActivityMainBinding.mainView.findViewById(R.id.rlMain);
        snackBar= CommonUtils.showNetWorkConnectionIssue(  mActivityAttendanceBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
}
