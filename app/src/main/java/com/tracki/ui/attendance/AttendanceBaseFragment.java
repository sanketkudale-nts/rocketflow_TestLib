package com.tracki.ui.attendance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.LeaveStatus;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.databinding.LayoutAttendanceBaseFragmentBinding;
import com.tracki.ui.attendance.attendance_tab.AttendanceFragment;
import com.tracki.ui.attendance.punchInOut.PunchInOutFragment;
import com.tracki.ui.attendance.teamattendance.TeamAttendanceFragment;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.common.FilterDialog;
import com.tracki.ui.common.OnClickSearchListener;
import com.tracki.ui.idealtrip.IdealTripDetailsActivity;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
public class AttendanceBaseFragment extends BaseFragment<LayoutAttendanceBaseFragmentBinding, AttendanceViewModel> implements
        AttendanceNavigator, HasSupportFragmentInjector, OnClickSearchListener, PunchInOutFragment.AttendanceIdSendListener, View.OnClickListener {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    AttendanceViewModel mAttendanceViewModel;
    @Inject
    AttendancePagerAdapter mPagerAdapter;
    FilterDialog filterDialog;

    LayoutAttendanceBaseFragmentBinding mActivityAttendanceBinding;
    PunchInOutFragment punchInOutFragment;
    AttendanceFragment attendanceFragment;
    TeamAttendanceFragment teamAttendanceFragment;
    ImageView ivLocationIcon;
    ImageView ivFilterIcon;
    private ImageView ivNavigationIcon;
    private ViewPager viewPager;
    private String punchId;
    @Inject

    PreferencesHelper preferencesHelper;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_attendance_base_fragment;
    }

    @Override
    public AttendanceViewModel getViewModel() {
        mAttendanceViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AttendanceViewModel.class);
        return mAttendanceViewModel;
    }

    @Override
    public void trackingIdVisible(String punchId, boolean isStart) {
        this.punchId = punchId;
        if (ivLocationIcon != null) {
            if (isStart)
                ivLocationIcon.setVisibility(View.VISIBLE);
            else
                ivLocationIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {

    }

    public void onClickFilter() {
        List<String> list = new ArrayList<>();
        list.add(AppConstants.ALL);
        list.add(AppConstants.PRESENT);
        list.add(AppConstants.ABSENT);
        list.add(AppConstants.DAY_OFF);
        list.add(AppConstants.HOLIDAY);
        list.add(AppConstants.ON_LEAVE);


        filterDialog = new FilterDialog(getBaseActivity(), this, list, AppConstants.ATTENDANCE_Title);
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
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivityAttendanceBinding = getViewDataBinding();
        mAttendanceViewModel.setNavigator(this);
        punchInOutFragment = PunchInOutFragment.newInstance();
        punchInOutFragment.setPunchIdFromFragment(this);
        attendanceFragment = AttendanceFragment.newInstance(preferencesHelper.getUserDetail(),true,false);
        teamAttendanceFragment = TeamAttendanceFragment.Companion.newInstance();
        setUp(view);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                CommonUtils.showLogMessage("e", "position", "" + position);
                if (mPagerAdapter.getCount() > 2) {
                    if (position == 0) {
                        ivFilterIcon.setVisibility(View.GONE);
                        ivLocationIcon.setVisibility(View.GONE);

                    } else if (position == 1) {
                        ivFilterIcon.setVisibility(View.GONE);


                    } else {
                        ivLocationIcon.setVisibility(View.GONE);
                        ivFilterIcon.setVisibility(View.GONE);
                    }
                } else {
                    if (position == 0) {
                        ivFilterIcon.setVisibility(View.GONE);


                    } else {
                        ivLocationIcon.setVisibility(View.GONE);
                        ivFilterIcon.setVisibility(View.GONE);
                    }
                }


            }
        });
    }

    private void setUp(View mainView) {


        viewPager = mActivityAttendanceBinding.vpAttendance;
        TabLayout tabLayout = mActivityAttendanceBinding.tabLayoutAttendance;
        ivFilterIcon = mainView.findViewById(R.id.ivFilterIcon);
        ivLocationIcon = mainView.findViewById(R.id.ivLocationIcon);
        //add fragments
        if (teamAttendanceFragment != null && preferencesHelper.isManger()) {
            mPagerAdapter.addFragment(teamAttendanceFragment, AppConstants.TEAM_ATTENDANCE);
        }
        mPagerAdapter.addFragment(punchInOutFragment, AppConstants.PUNCH_IN_OUT);
        mPagerAdapter.addFragment(attendanceFragment, AppConstants.ATTENDANCE_I);
        ivNavigationIcon = mainView.findViewById(R.id.ivNavigationIcon);
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        ivFilterIcon.setOnClickListener(this);
        ivLocationIcon.setOnClickListener(this);
        ivNavigationIcon.setOnClickListener(this);
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public static AttendanceBaseFragment newInstance(Context context) {
        Bundle args = new Bundle();
        AttendanceBaseFragment fragment = new AttendanceBaseFragment();
        fragment.setListener((NavigationClickFromAttendance) context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivFilterIcon) {
            onClickFilter();
        } else if (v.getId() == R.id.ivLocationIcon) {
//            Toast.makeText(getContext(),"Actvity "+punchId,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseActivity(), IdealTripDetailsActivity.class);
            intent.putExtra("taskId", punchId);
            startActivity(intent);
        } else if (v.getId() == R.id.ivNavigationIcon) {
            if (mListener != null)
                mListener.alertAttendanceClick();
        }
    }

    public interface NavigationClickFromAttendance {
        void alertAttendanceClick();

    }

    private NavigationClickFromAttendance mListener;

    public void setListener(NavigationClickFromAttendance mListener) {
        this.mListener = mListener;
    }

}
