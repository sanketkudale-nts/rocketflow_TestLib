package com.tracki.ui.leave;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.tracki.data.model.response.config.LeaveHistoryData;
import com.tracki.data.model.response.config.LeaveStatus;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.databinding.ActivityLeaveBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.common.FilterDialog;
import com.tracki.ui.common.OnClickSearchListener;
import com.tracki.ui.leave.apply_leave.ApplyLeaveFragment;
import com.tracki.ui.leave.leave_approval.LeaveApprovalFragment;
import com.tracki.ui.leave.leave_history.LeaveHistoryFragment;
import com.tracki.ui.leave.leave_summary.LeaveSummaryFragment;
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
public class LeaveActivity extends BaseActivity<ActivityLeaveBinding, LeaveViewModel>
        implements LeaveNavigator, HasSupportFragmentInjector, OnClickSearchListener {

    public ViewPager viewPager;
    public boolean isEdit = false;
    public LeaveHistoryData leaveHistoryData;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    @Inject
    LeaveViewModel mLeaveViewModel;
    @Inject
    LeavePagerAdapter mPagerAdapter;
    Dialog filterDialog;
    ApplyLeaveFragment applyLeaveFragment;
   // LeaveSummaryFragment leaveSummaryFragment;
    LeaveHistoryFragment leaveHistoryFragment;
    LeaveApprovalFragment leaveApprovalFragment;
    MenuItem item;
    private ActivityLeaveBinding mActivityLeaveBinding;

    private Snackbar snackBar;

    @Inject
    PreferencesHelper preferencesHelper;
    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar= CommonUtils.showNetWorkConnectionIssue( mActivityLeaveBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
    public static Intent newIntent(Context context) {
        return new Intent(context, LeaveActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_leave;
    }

    @Override
    public LeaveViewModel getViewModel() {
        return mLeaveViewModel;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }


    public void onClickFilter() {
        String title = null;

        Fragment fragment = getSupportFragmentManager().
                findFragmentByTag("android:switcher:" + R.id.vpLeave + ":" + viewPager.getCurrentItem());
        List<String> statusList = new ArrayList<>();
        if (fragment instanceof LeaveHistoryFragment) {
            statusList.add(AppConstants.ALL);
            statusList.add(AppConstants.APPLIED);
            statusList.add(AppConstants.APPROVED);
            statusList.add(AppConstants.REJECTED);
            statusList.add(AppConstants.CANCELLED);
            title = AppConstants.HISTORY;
        } else {

            if (fragment instanceof LeaveApprovalFragment) {
                statusList.add(AppConstants.ALL);
                statusList.add(AppConstants.PENDING);
                statusList.add(AppConstants.APPROVED);
                statusList.add(AppConstants.REJECTED);
                title = AppConstants.APPROVAL;
            }
        }

        filterDialog = new FilterDialog(this, this, statusList, title);
        filterDialog.show();
        Window window = filterDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClickSearch(long from, long to, LeaveStatus status) {


        Fragment fragment = getSupportFragmentManager().
                findFragmentByTag("android:switcher:" + R.id.vpLeave + ":" + viewPager.getCurrentItem());
        if (fragment instanceof LeaveHistoryFragment) {
//            if (status == LeaveStatus.ALL)
//                leaveHistoryFragment.onClickSearchHistory(from, to, null);
//            else
//                leaveHistoryFragment.onClickSearchHistory(from, to, status);

        } else {
            if (fragment instanceof LeaveApprovalFragment) {
                if (status == LeaveStatus.ALL)
                    leaveApprovalFragment.onClickSearchApproval(from, to, null);
                else
                    leaveApprovalFragment.onClickSearchApproval(from, to, status);


            }
        }

        LeaveHistoryFragment leaveHistoryFragment = (LeaveHistoryFragment) getSupportFragmentManager().
                findFragmentByTag(LeaveHistoryFragment.TAG);

        if (leaveHistoryFragment != null) {
        } else {
            LeaveApprovalFragment leaveApprovalFragment = (LeaveApprovalFragment) getSupportFragmentManager().
                    findFragmentByTag(LeaveApprovalFragment.TAG);

            if (leaveApprovalFragment != null) {
            }
        }

        filterDialog.cancel();
    }

    @Override
    public void onClickCancel() {
        filterDialog.cancel();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.filter_attendance_leave, menu);
        item = menu.findItem(R.id.action_filter);
        MenuItem itemlocaton = menu.findItem(R.id.action_location);
        itemlocaton.setVisible(false);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_filter) {
            onClickFilter();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityLeaveBinding = getViewDataBinding();
        mLeaveViewModel.setNavigator(this);
        setUp();
    }

    private void setUp() {
        setToolbar(mActivityLeaveBinding.toolbar, getString(R.string.leave));


        applyLeaveFragment = ApplyLeaveFragment.newInstance();
      //  leaveSummaryFragment = LeaveSummaryFragment.newInstance();
        leaveHistoryFragment = LeaveHistoryFragment.newInstance(preferencesHelper.getUserDetail());
        leaveApprovalFragment = LeaveApprovalFragment.newInstance();

        mPagerAdapter.addFragment(applyLeaveFragment, AppConstants.APPLY);
       // mPagerAdapter.addFragment(leaveSummaryFragment, AppConstants.SUMMARY);
        mPagerAdapter.addFragment(leaveHistoryFragment, AppConstants.SUMMARY);
        if (leaveApprovalFragment != null && preferencesHelper.isManger()) {
            mPagerAdapter.addFragment(leaveApprovalFragment, AppConstants.APPROVAL);
        }

        mPagerAdapter.setCount();
        viewPager = mActivityLeaveBinding.vpLeave;
        TabLayout tabLayout = mActivityLeaveBinding.tabLayoutLeave;

        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (mPagerAdapter.getCount() > 2){
                    if (position == 2) {
                        if(item!=null) {
                            item.setVisible(true);
                        }
                    } else {
                        if(item!=null) {
                            item.setVisible(false);
                        }
                    }
                }else{
                    if(item!=null) {
                        item.setVisible(false);
                    }
                }

            }
        });
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {

    }


}
