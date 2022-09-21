package com.tracki.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.BuildConfig;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.firebase.FeedsJson;
import com.tracki.data.local.db.DatabaseHelper;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.NotificationEventStatus;
import com.tracki.data.model.NotificationModel;
import com.tracki.data.model.request.Addrloc;
import com.tracki.data.model.request.Data;
import com.tracki.data.model.request.EndTaskRequest;
import com.tracki.data.model.request.OnlineOffLineRequest;
import com.tracki.data.model.request.PunchInOut;
import com.tracki.data.model.request.PunchRequest;
import com.tracki.data.model.response.config.ActionConfig;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.model.response.config.BuddyListResponse;
import com.tracki.data.model.response.config.ConfigResponse;
import com.tracki.data.model.response.config.DashBoardBoxItem;
import com.tracki.data.model.response.config.Fleet;
import com.tracki.data.model.response.config.FleetListResponse;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.data.model.response.config.Navigation;
import com.tracki.data.model.response.config.OnLineOfflineResponse;
import com.tracki.data.model.response.config.Place;
import com.tracki.data.model.response.config.ProfileInfo;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.WorkFlowCategories;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.CacheManager;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityMainBinding;
import com.tracki.ui.account.MyAccountActivity;
import com.tracki.ui.addcustomer.AddCustomerActivity;
import com.tracki.ui.addplace.LocationListResponse;
import com.tracki.ui.attendance.AttendanceBaseFragment;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.buddylisting.BuddyListingActivity;
import com.tracki.ui.buddyrequest.BuddyRequestActivity;
import com.tracki.ui.category.AddProductCategoryActivity;
import com.tracki.ui.category.ProductCategoryListActivity;
import com.tracki.ui.common.DoubleButtonDialog;
import com.tracki.ui.common.OnClickListener;
import com.tracki.ui.custom.AnimatedExpandableListView;
import com.tracki.ui.custom.CircleTransform;
import com.tracki.ui.custom.GlideApp;
import com.tracki.ui.custom.RoundImageView;
import com.tracki.ui.earnings.MyEarningsActivity;
import com.tracki.ui.feeddetails.FeedDetailsActivity;
import com.tracki.ui.feeds.FeedsActivity;
import com.tracki.ui.fleetlisting.FleetListingActivity;
import com.tracki.ui.leave.LeaveActivity;
import com.tracki.ui.login.LoginActivity;
import com.tracki.ui.main.taskdashboard.TaskDashBoardFragment;
import com.tracki.ui.messages.MessagesActivity;
import com.tracki.ui.newcreatetask.NewCreateTaskActivity;
import com.tracki.ui.notification.NotificationActivity;
import com.tracki.ui.placelist.MyPlaceListActivity;
import com.tracki.ui.productlist.ProductListActivity;
import com.tracki.ui.products.AddProductActivity;
import com.tracki.ui.service.transition.TransitionService;
import com.tracki.ui.setting.SettingsActivity;
import com.tracki.ui.taskdetails.NewTaskDetailsActivity;
import com.tracki.ui.taskdetails.TaskDetailActivity;
import com.tracki.ui.tasklisting.TaskActivity;
import com.tracki.ui.userlisting.UserListingActivity;
import com.tracki.ui.webview.WebViewActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.Log;
import com.tracki.utils.NetworkUtils;
import com.tracki.utils.NextScreen;
import com.tracki.utils.TaskStatus;
import com.tracki.utils.TrackiToast;
import com.trackthat.lib.TrackThat;
import com.trackthat.lib.internal.network.TrackThatCallback;
import com.trackthat.lib.models.ErrorResponse;
import com.trackthat.lib.models.SuccessResponse;
import com.trackthat.lib.models.TrackthatLocation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU;
import static com.tracki.utils.AppConstants.Extra.EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU;

/**
 * Class @{@link MainActivity} used to handle user's current location
 * filter them with active, idle & offline user.
 * <p>
 * Created by rahul on 6/9/18.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel>
        implements MainNavigator, HasSupportFragmentInjector, View.OnClickListener, TaskDashBoardFragment.NavigationClickFromTaskDashBoard
        , AttendanceBaseFragment.NavigationClickFromAttendance {


    private static final String TAG = "MainActivity";

    @Inject
    MainViewModel mMainViewModel;
    @Inject
    PreferencesHelper preferencesHelper;
    @Inject
    HttpManager httpManager;


    private ActivityMainBinding mActivityMainBinding;
    private DrawerLayout drawer;
    private TextView tvUserName, tvMobile, tvUserRole, tvEdit;
    private RoundImageView ivUserProfile;
    private AnimatedExpandableListView expListView;
    private int mSelectedPosition;
    private ProfileInfo userDetail;
    private LinearLayout llOnlineOffLineStatus;
    // private CacheManager cacheManager;
    private DatabaseHelper databaseHelper;
    private LinearLayout llUserRole;


    private TextView tvStatus;
    private SwitchCompat switchOnLineOffLIne;
    private boolean onlineOfflineStatus = false;
    private Snackbar snackBar;
    private ImageView ivCancel;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public MainViewModel getViewModel() {
        return mMainViewModel;
    }

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = getViewDataBinding();
        mMainViewModel.setNavigator(this);

//        cacheManager = CacheManager.Companion.getInstance(this);
//        String cachedConfig = cacheManager.getFromCache(ApiType.CONFIG.name() + "_", "1.0");
//        if (cachedConfig != null) {
//            Gson gson = new Gson();
//            ConfigResponse configResponse = gson.fromJson(cachedConfig, ConfigResponse.class);
//            configResponse.setNextScreen(NextScreen.HOME);
//            cacheManager.putIntoCache(ApiType.CONFIG.name() + "_", gson.toJson(configResponse), "1.0");
//        }
        //initialize buddy id to pass in dashboard
        // api for default logged-in user
        preferencesHelper.setIsFleetAndBuddyShow(false);
        if (preferencesHelper.getServicePref())
            mMainViewModel.getUserLocations(httpManager);
        setUp();
        if (getIntent() != null && getIntent().hasExtra(AppConstants.NOTIFICATION_DATA)) {
            String message = getIntent().getStringExtra(AppConstants.NOTIFICATION_DATA);
            try {
                JSONObject jsonObjectBody = new JSONObject(message);
                if (!jsonObjectBody.isNull("screen")) {
                    JSONConverter jsonConverter = new JSONConverter();
                    FeedsJson feedsJson = (FeedsJson) jsonConverter.jsonToObject(message, FeedsJson.class);
                    if (feedsJson.getScreen() != null && feedsJson.getScreen().equals("FEED_INFO") && feedsJson.getData() != null && feedsJson.getData().getId() != null
                            && !feedsJson.getData().getId().isEmpty()) {
                        Intent intent1 = FeedDetailsActivity.Companion.getInstance(this, feedsJson.getData().getId());
                        startActivity(intent1);
                    }else{
                        handleTaskWorkFromPush(message);
                    }
                } else {
                    handleTaskWorkFromPush(message);
                }

            } catch (Exception e) {

            }


        }

        // addFragmentInContainer(AttendanceBaseFragment.newInstance(this));
    }

    private void handleTaskWorkFromPush(String message) {
        Gson gson = new Gson();
        NotificationModel notificationModel = gson.fromJson(message, NotificationModel.class);
        if (notificationModel.getEvent() == NotificationEventStatus.TRACKING_STATE_CHANGE) {
            //
            Intent intent = new Intent(this, NewTaskDetailsActivity.class);
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, notificationModel.getTaskId());
            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, notificationModel.getCategoryId());
            intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_TO_ME);
            startActivity(intent);

        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fLayoutNotification) {
            addFragmentInContainer(TaskDashBoardFragment.newInstance(this));
            //    startActivity(NotificationActivity.Companion.newIntent(this));
        } else if (id == R.id.fLayoutRequest) {
            startActivity(BuddyRequestActivity.Companion.newIntent(this));
        } else if (id == R.id.ivNavigationIcon) {
            openOrCloseDrawer();
        } else if (id == R.id.ivDashboardNavigation) {
            if (preferencesHelper.getIsFleetAndBuddySHow()) {
                showLoading();
                mMainViewModel.checkBuddy(httpManager);
            } else {
                Intent intent = NewCreateTaskActivity.Companion.newIntent(this);
                intent.putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);
            }
        } else if (id == R.id.tvEdit || id == R.id.lLayoutProfile) {
            startActivity(MyAccountActivity.newIntent(this));
        } else if (id == R.id.rlayoutLiveTrip) {//                TrackiToast.MessageResponse.showShort(this, "Clicked");
            startActivity(TaskDetailActivity.Companion.newIntent(this)
                    .putExtra(AppConstants.Extra.EXTRA_TASK_ID, TrackThat.getCurrentTrackingId()));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        userDetail = preferencesHelper.getUserDetail();
        if (userDetail != null) {
            if (userDetail.getMobile() != null) {
                tvMobile.setText(userDetail.getMobile());
            }
            tvUserName.setText(userDetail.getName());
            if (userDetail.getProfileImg() != null && !userDetail.getProfileImg().equals("")) {
                GlideApp.with(this)
                        .asBitmap()
                        .load(userDetail.getProfileImg())
                        .apply(new RequestOptions()
                                .transform(new CircleTransform())
                                .placeholder(R.drawable.ic_placeholder_pic))
                        .error(R.drawable.ic_placeholder_pic)
                        .into(ivUserProfile);
            }
        }
//        if (!preferencesHelper.getUserType().isEmpty()) {
//
//            //var role=userAccount!!.name
//            //
//            //                binding.data = "Login as ${role[0].toUpperCase()+role.substring(1)}"
//            String role = preferencesHelper.getUserType().equals(UserType.DRIVER.name()) ? "EXECUTIVE" : preferencesHelper.getUserType();
//            tvUserRole.setText(role.charAt(0) + "".toUpperCase().trim() + role.substring(1).toLowerCase());
//        }
//        if (preferencesHelper.getUserTypeList() != null && preferencesHelper.getUserTypeList().size() > 1) {
//            llUserRole.setVisibility(View.VISIBLE);
//        } else {
//            llUserRole.setVisibility(View.GONE);
//        }
        if (mActivityMainBinding.tvVersion != null) {
            mActivityMainBinding.tvVersion.setText("v-1");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            checkPermissionAndAskPermission();
        }
    }

    private void addFragmentInContainer(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.disallowAddToBackStack();
        ft.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }


    private void setUp() {

        databaseHelper = DatabaseHelper.getInstance(MainActivity.this);


        drawer = mActivityMainBinding.drawerLayout;
        View userView = mActivityMainBinding.navHeader;
        LinearLayout lLayoutProfile = userView.findViewById(R.id.lLayoutProfile);
        tvStatus = userView.findViewById(R.id.tvStatus);
        switchOnLineOffLIne = userView.findViewById(R.id.onLineOffLineSwitch);
        tvUserName = userView.findViewById(R.id.tvUserName);
        tvUserRole = userView.findViewById(R.id.tvUserType);
        llUserRole = userView.findViewById(R.id.llUserRole);
        llOnlineOffLineStatus = userView.findViewById(R.id.llOnlineOffLineStatus);
        mActivityMainBinding.tvVersion.setText("v-1");
        tvMobile = userView.findViewById(R.id.tvMobile);
        tvEdit = userView.findViewById(R.id.tvEdit);
        ivCancel = userView.findViewById(R.id.ivCancel);
        ivUserProfile = userView.findViewById(R.id.ivUserProfile);
//        taskAssignConfirmation = mActivityMainBinding.mainView.taskAssignConfirmation;


        setExpandableListView();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //slideOffset changes from 0 to 1.
                // 1 means it is completely open, 0 - closed
                if (slideOffset == 0) {
                } else {
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        lLayoutProfile.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        if (preferencesHelper.getStatus() != null) {
            llOnlineOffLineStatus.setVisibility(View.GONE);
            if (preferencesHelper.getStatus().equals("OFFLINE")) {
                onlineOfflineStatus = false;
                tvStatus.setText("Offline");
            } else {
                onlineOfflineStatus = true;
                tvStatus.setText("Online");
            }
        } else {
            llOnlineOffLineStatus.setVisibility(View.GONE);
        }
        perFormSwitchTask();
        // checkForGeofence();
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START, true);

            }
        });

    }

    private void perFormSwitchTask() {
//        if (preferencesHelper.getUserType() != null && preferencesHelper.getUserType().equals(UserType.DRIVER.name())) {
//            if (preferencesHelper.getStatus() != null) {
//                llOnlineOffLineStatus.setVisibility(View.VISIBLE);
//                if (preferencesHelper.getStatus().equals("OFFLINE")) {
//                    onlineOfflineStatus = false;
//                    tvStatus.setText("Offline");
//                } else {
//                    onlineOfflineStatus = true;
//                    tvStatus.setText("Online");
//                }
//            } else {
//                llOnlineOffLineStatus.setVisibility(View.GONE);
//            }
//        }
        if (preferencesHelper.getStatus() != null) {
            llOnlineOffLineStatus.setVisibility(View.VISIBLE);
            if (preferencesHelper.getStatus().equals("OFFLINE")) {
                onlineOfflineStatus = false;
                tvStatus.setText("Offline");
            } else {
                onlineOfflineStatus = true;
                tvStatus.setText("Online");
            }
        }
        switchOnLineOffLIne.setOnCheckedChangeListener(null);
        switchOnLineOffLIne.setChecked(onlineOfflineStatus);
        switchOnLineOffLIne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String message = "";
                Boolean cancelChecked = false;

                if (isChecked) {
                    message = "Are you sure want to go online";
                    cancelChecked = false;
                    tvStatus.setText("Online");

                } else {
                    message = "Are you sure want to go offline";
                    cancelChecked = true;
                    tvStatus.setText("Offline");
                }
                Boolean finalCancelChecked = cancelChecked;
                DoubleButtonDialog dialog = new DoubleButtonDialog(MainActivity.this,
                        true,
                        null,
                        message,
                        getString(R.string.yes),
                        getString(R.string.no),
                        new OnClickListener() {
                            @Override
                            public void onClickCancel() {
//                                    OnlineOffLineRequest request=new OnlineOffLineRequest();
//                                    if(!finalCancelChecked){
//                                        request.setStatus("OFFLINE");
//                                        onlineOfflineStatus=false;
//                                    }else{
//                                        onlineOfflineStatus=true;
//                                        request.setStatus("ONLINE");
//                                    }
//                                    showLoading();
//                                    mMainViewModel.markOnlineOffline(httpManager,request);
                                if (finalCancelChecked) {
                                    tvStatus.setText("Online");

                                } else {
                                    tvStatus.setText("Offline");
                                }
                                // onlineOfflineStatus=finalCancelChecked;
                                perFormSwitchTask();

                            }

                            @Override
                            public void onClick() {
                                OnlineOffLineRequest request = new OnlineOffLineRequest();
                                if (!isChecked) {
                                    request.setStatus("OFFLINE");
                                    onlineOfflineStatus = false;
                                } else {
                                    onlineOfflineStatus = true;
                                    request.setStatus("ONLINE");
                                }
                                showLoading();
                                mMainViewModel.markOnlineOffline(httpManager, request);
                            }
                        });
                dialog.show();

            }
        });
    }

    private void setExpandableListView() {
        expListView = mActivityMainBinding.listViewExpandable;
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this,
                TrackiApplication.getNavigationMenuList());

        expListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {

            Navigation groupData = TrackiApplication.getNavigationMenuList().get(groupPosition);
            if (groupData.getNestedMenu() == null || groupData.getNestedMenu().size() == 0) {
                mSelectedPosition = groupPosition;
                showMenuSelected(groupData);
            } else {
                if (expListView.isGroupExpanded(groupPosition)) {
                    expListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    expListView.expandGroupWithAnimation(groupPosition);
                }
            }
            return true;
        });
        expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            final Navigation groupData = TrackiApplication.getNavigationMenuList().get(groupPosition);
            if (groupData.getNestedMenu() == null || groupData.getNestedMenu().size() == 0) {
                showMenuSelected(groupData);
            }
            return false;
        });
        expListView.setAdapter(listAdapter);
        expListView.setItemChecked(mSelectedPosition, true);
        expListView.setChildDivider(getResources().getDrawable(R.color.warm_gray));
        listAdapter.notifyDataSetChanged();
        if (TrackiApplication.getNavigationMenuList() != null) {
            Navigation groupData = TrackiApplication.getNavigationMenuList().get(0);
            if (groupData != null) {
                if (groupData.getActionConfig() != null && groupData.getActionConfig().getActionUrl() != null && groupData.getActionConfig().getActionUrl().equals(AppConstants.DASHBOARD)) {
                    if (groupData.getNestedMenu() == null || groupData.getNestedMenu().size() == 0) {
                        mSelectedPosition = 0;
                        showMenuSelected(groupData);
                    }

                } else if (groupData.getActionConfig() != null && groupData.getActionConfig().getActionUrl() != null && groupData.getActionConfig().getActionUrl().equals(AppConstants.ATTENDANCE)) {
                    if (groupData.getNestedMenu() == null || groupData.getNestedMenu().size() == 0) {
                        mSelectedPosition = 0;
                        showMenuSelected(groupData);
                    }
                } else {
                    Navigation findData1 = new Navigation();
                    ActionConfig actionConfig = new ActionConfig();
                    actionConfig.setActionUrl(AppConstants.DASHBOARD);
                    findData1.setActionConfig(actionConfig);

                    Navigation findData2 = new Navigation();
                    ActionConfig actionConfig2 = new ActionConfig();
                    actionConfig2.setActionUrl(AppConstants.ATTENDANCE);
                    findData2.setActionConfig(actionConfig2);
                    if (TrackiApplication.getNavigationMenuList().contains(findData1)) {
                        int position = TrackiApplication.getNavigationMenuList().indexOf(findData1);
                        if (position != -1) {
                            Navigation findGroupData = TrackiApplication.getNavigationMenuList().get(position);
                            if (findGroupData.getNestedMenu() == null || findGroupData.getNestedMenu().size() == 0) {
                                mSelectedPosition = position;
                                showMenuSelected(findGroupData);
                            }

                        }
                    } else if (TrackiApplication.getNavigationMenuList().contains(findData2)) {
                        int position = TrackiApplication.getNavigationMenuList().indexOf(findData2);
                        if (position != -1) {
                            Navigation findGroupData = TrackiApplication.getNavigationMenuList().get(position);
                            if (findGroupData.getNestedMenu() == null || findGroupData.getNestedMenu().size() == 0) {
                                mSelectedPosition = position;
                                showMenuSelected(findGroupData);
                            }
                        }
                    }

                }

            } else {
                if (expListView.isGroupExpanded(0)) {
                    expListView.collapseGroupWithAnimation(0);
                } else {
                    expListView.expandGroupWithAnimation(0);
                }
            }
        }
    }


    private void openOrCloseDrawer() {
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START, false);
//            drawer.closeDrawer(Gravity.START);
        }
    }

    public void setSelectedPosition(int selectedPosition) {
        this.mSelectedPosition = selectedPosition;
    }

    public void insideSubListView(List<Navigation> subList,
                                  final LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        if (subList != null && subList.size() > 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            for (final Navigation menu : subList) {
                final View view = layoutInflater.inflate(R.layout.item_sub_list, null);
                TextView txtListChild = view.findViewById(R.id.tv);
                txtListChild.setText(menu.getTitle());
                final ImageView ivRightArrow = view.findViewById(R.id.ivRightArrow);
                if (menu.getNestedMenu() != null) {
                    ivRightArrow.setVisibility(View.VISIBLE);
                } else {
                    ivRightArrow.setVisibility(View.GONE);
                }

//                menu.getActionConfig().setActionUrl(AppConstants.ATTENDANCE);
//                menu.getActionConfig().setScreenType(ScreenType.NATIVE);

                final LinearLayout ll_sublist = view.findViewById(R.id.ll_sublist);
                ll_sublist.setVisibility(View.GONE);
                if (menu.getActionConfig() != null) {
                    view.setOnClickListener(view1 -> {
                        if (menu.getNestedMenu() == null) {
                            showMenuSelected(menu);
                        } else {
                            if (ll_sublist.getVisibility() == View.GONE) {
                                insideSubListView(menu.getNestedMenu(), ll_sublist);
                                CommonUtils.expandText(ll_sublist);
                                CommonUtils.rotateArrowAnother(ivRightArrow, false);
//                                CommonUtils.rotate(0f, 90f, ivRightArrow);
                            } else {
                                CommonUtils.collapseText(ll_sublist);
                                CommonUtils.rotateArrowAnother(ivRightArrow, true);
//                                CommonUtils.rotate(90f, 0f, ivRightArrow);
                            }
                        }
                    });
                }
                linearLayout.addView(view);

            }
        }
    }

    public void showMenuSelected(Navigation menu) {
        openOrCloseDrawer();
        if (mActivityMainBinding.viewFaker.getVisibility() != View.VISIBLE) {
//            if (isNetworkConnected()) {
            if (menu.getActionConfig() != null) {
                switch (Objects.requireNonNull(menu.getActionConfig().getScreenType())) {
                    case NATIVE:
                        if (menu.getActionConfig().getActionUrl() != null) {
                            switch (menu.getActionConfig().getActionUrl()) {
                                case AppConstants.DASHBOARD:
                                    addFragmentInContainer(TaskDashBoardFragment.newInstance(this));
                                    break;
                                case AppConstants.PRODUCT_CATEGORY:
                                    Intent categoryIntent = ProductCategoryListActivity.Companion.newIntent(this);
                                    categoryIntent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                                            new Gson().toJson(menu.getActionConfig().getProperties()));
                                    startActivity(categoryIntent);
                                    break;
                                case AppConstants.PRODUCTS:

                                    Intent addproduct = new Intent(this, ProductListActivity.class);
                                    addproduct.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                                            new Gson().toJson(menu.getActionConfig().getProperties()));
                                    startActivity(addproduct);

                                    break;

                                case AppConstants.BROADCAST:
                                    Intent feedsIntent = FeedsActivity.Companion.newIntent(this);
                                    if (menu.getTitle() != null) {
                                        feedsIntent.putExtra(AppConstants.TITLE, menu.getTitle());
                                    }
                                    startActivity(feedsIntent);
                                    break;
                                case AppConstants.MY_LOCATION:
                                    //Remove put extra code
                                    Intent placeIntent = MyPlaceListActivity.Companion.newIntent(this);
                                    placeIntent.putExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_NAVIGATION_MENU, true);
                                    startActivityForResult(placeIntent, AppConstants.REQUEST_CODE_FOR_PLACE);
                                    break;
                                case AppConstants.MY_BUDDY:
                                    startActivity(BuddyListingActivity.newIntent(this)
                                            .putExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_NAVIGATION_MENU, true));
                                    break;
                                case AppConstants.FLEET:
                                    startActivity(FleetListingActivity.newIntent(this)
                                            .putExtra(AppConstants.Extra.EXTRA_FLEET_LIST_CALLING_FROM_NAVIGATION_MENU, true));
                                    break;
                                case AppConstants.CUSTOMERS:
                                    Intent addUserIntent = new Intent(this, UserListingActivity.class);
                                    addUserIntent.putExtra("from", AppConstants.CUSTOMERS);
                                    addUserIntent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                                            new Gson().toJson(menu.getActionConfig().getProperties()));
                                    startActivity(addUserIntent);
                                    break;

                                case AppConstants.EMPLOYEES:
                                    Intent addEmpIntent = new Intent(this, UserListingActivity.class);
                                    addEmpIntent.putExtra("from", AppConstants.EMPLOYEES);
                                    addEmpIntent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                                            new Gson().toJson(menu.getActionConfig().getProperties()));
                                    startActivity(addEmpIntent);
                                    break;
                                case AppConstants.TASK:
                                    final Calendar cal = Calendar.getInstance();
                                    cal.getTime().getTime();
                                    if (preferencesHelper.getDefDateRange() == 0) {
                                        cal.set(Calendar.HOUR_OF_DAY, 0);
                                        cal.set(Calendar.MINUTE, 0);
                                        cal.set(Calendar.SECOND, 0);
                                    } else {
                                        cal.add(Calendar.DAY_OF_MONTH, -1 * preferencesHelper.getDefDateRange());
                                        cal.set(Calendar.HOUR_OF_DAY, 0);
                                        cal.set(Calendar.MINUTE, 0);
                                        cal.set(Calendar.SECOND, 0);
                                    }
                                    long fromDate = cal.getTime().getTime();
                                    long toDate = toDateDateAdd24Hour().getTime();
                                    Intent intent = TaskActivity.newIntent(this);
                                    if (menu.getActionConfig().getProperties() != null && menu.getActionConfig().getProperties().containsKey("categoryId")) {
                                        DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                                        dashBoardBoxItem.setCategoryId(menu.getActionConfig().getProperties().get("categoryId"));
                                        if (preferencesHelper.getWorkFlowCategoriesList() != null && preferencesHelper.getWorkFlowCategoriesList().size() > 0) {
                                            WorkFlowCategories workFlowCategories = new WorkFlowCategories();
                                            workFlowCategories.setCategoryId(dashBoardBoxItem.getCategoryId());
                                            List<WorkFlowCategories> mList = preferencesHelper.getWorkFlowCategoriesList();
                                            if (mList.contains(workFlowCategories)) {
                                                int position = mList.indexOf(workFlowCategories);
                                                if (position != -1) {
                                                    LinkedHashMap<String, String> stageMap = preferencesHelper.getWorkFlowCategoriesList().get(position).getStageNameMap();
                                                    if (stageMap != null && stageMap.size() > 0) {
                                                        Map.Entry<String, String> entry = stageMap.entrySet().iterator().next();
                                                        String key = entry.getKey();
                                                        String value = entry.getValue();
                                                        dashBoardBoxItem.setStageId(key);
                                                        dashBoardBoxItem.setStageName(value);
                                                        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                                                                new Gson().toJson(dashBoardBoxItem));
                                                    } else {
                                                        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                                                                new Gson().toJson(menu.getActionConfig().getProperties()));
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                                                new Gson().toJson(menu.getActionConfig().getProperties()));
                                    }
                                    if (menu.getTitle() != null)
                                        intent.putExtra(AppConstants.Extra.TITLE, menu.getTitle());
                                    intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate);
                                    intent.putExtra(AppConstants.Extra.FROM_TO, toDate);
                                    startActivity(intent);
                                    break;
                                case AppConstants.SETTINGS:
                                    startActivity(SettingsActivity.Companion.newIntent(this));
                                    break;
                                case AppConstants.ATTENDANCE:
                                    addFragmentInContainer(AttendanceBaseFragment.newInstance(this));
                                    //startActivity(AttendanceActivity.newIntent(this));
                                    break;
                                case AppConstants.LEAVE:
                                    startActivity(LeaveActivity.newIntent(this));
                                    break;
                                case AppConstants.LOGOUT:
                                    logoutDialog();
                                    break;
                                case AppConstants.CONVERSATION:
                                    startActivity(MessagesActivity.Companion.newIntent(this));
                                    break;
                                case AppConstants.MY_EARNINGS:
                                    startActivity(MyEarningsActivity.Companion.newIntent(this));
                                    break;
                            }
                        }
                        break;
                    case WEB:
                        startActivity(WebViewActivity.Companion.newIntent(this)
                                .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, menu)
                        );
                        break;
                }
            }
//            } else {
//                TrackiToast.MessageResponse.showLong(this, AppConstants.ALERT_NO_CONNECTION);
//            }
        }
        disableClickForFewSec();

    }

    /**
     * Method used to check if their is any live task is running out if true then
     * stop the service and hit the end task api and then logout api.
     */
    private void logout() {
        // if live trip is running stop it
        CommonUtils.showLogMessage("e", "call logout", "logout");
        if (TrackThat.isTracking()) {
            TrackThat.stopTracking();
        }
        if (preferencesHelper.getIdleTripActive()) {
            preferencesHelper.setIdleTripActive(false);
        }
        preferencesHelper.setIsIdealTrackingEnable(false);
        //com.trackthat.sdk.notification:
        //stop transition service on logout
        if (CommonUtils.isServiceRunningInForeground(MainActivity.this, TransitionService.class.getName())) {
            CommonUtils.manageTransitionService(MainActivity.this, false);
        }
        Task task = preferencesHelper.getCurrentTask();
        if (task != null && (task.getStatus() == TaskStatus.ACCEPTED || task.getStatus() == TaskStatus.ARRIVED)) {

            final Place destination = new Place();
            if (task.getDestination() == null) {

                if (TrackThat.isLastLocationAvailable() && TrackThat.getLastLocation() != null) {
                    TrackthatLocation loc = TrackThat.getLastLocation();
                    GeoCoordinates dest = new GeoCoordinates();
                    dest.setLatitude(loc.getLatitude());
                    dest.setLongitude(loc.getLongitude());

                    destination.setLocation(dest);
                    destination.setAddress(TrackThat.getAddress(loc.getLatitude(), loc.getLongitude()));
                    Log.e(TAG, "End Location is: " + loc.getLatitude() + " "
                            + loc.getLongitude() + " " + destination.getAddress());
                }/* else {
                    TrackThat.getCurrentLocation(new TrackThatCallback() {
                        @Override
                        public void onSuccess(@NonNull SuccessResponse successResponse) {
                            TrackthatLocation loc = (TrackthatLocation) successResponse.getResponseObject();
                            GeoCoordinates dest = new GeoCoordinates();
                            dest.setLatitude(loc.getLatitude());
                            dest.setLongitude(loc.getLongitude());

                            destination.setLocation(dest);
                            destination.setAddress(TrackThat.getAddress(loc.getLatitude(), loc.getLongitude()));
                    Log.e(TAG, "End Location is: " + loc.getLatitude() + " "
                            + loc.getLongitude() + " " + destination.getAddress());
                        }

                        @Override
                        public void onError(@NonNull ErrorResponse errorResponse) {

                        }
                    });
                }*/
            }
            EndTaskRequest request = new EndTaskRequest(task.getTaskId(), null, DateTimeUtil.getCurrentDateInMillis(), destination);
            if (NetworkUtils.isNetworkConnected(this)) {
                showLoading();
                mMainViewModel.endTask(httpManager, request);
            } else {
                TrackiToast.Message.showShort(this, AppConstants.ALERT_NO_CONNECTION);
            }
        } else {
            if (NetworkUtils.isNetworkConnected(this)) {
                showLoading();
                mMainViewModel.logout(httpManager);
            } else {
                TrackiToast.Message.showShort(this, AppConstants.ALERT_NO_CONNECTION);
            }
        }
    }

    private void disableClickForFewSec() {
        try {
            if (mActivityMainBinding.viewFaker != null) {
                mActivityMainBinding.viewFaker.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> {
                    try {
                        mActivityMainBinding.viewFaker.setVisibility(View.GONE);
                    } catch (Exception ignored) {

                    }
                }, 2000);
            }
        } catch (Exception ignore) {

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
        hideLoading();


    }

    private void logoutDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_logout_confirmation);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.8f;
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnLogout = dialog.findViewById(R.id.btnLogout);

        dialog.getWindow().setAttributes(lp);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAuditWork();
                dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();

    }

    private void logoutAuditWork() {
        PunchRequest punchRequest = new PunchRequest();
        punchRequest.setDate(System.currentTimeMillis());
        Data dataObj = new Data();
        dataObj.setRemarks("");
        punchRequest.setEvent(PunchInOut.PUNCH_OUT);
        punchRequest.setUserId(preferencesHelper.getUserDetail().getUserId());
        if (TrackThat.isTracking()) {
            int batteryLevel = TrackThat.getBatteryLevel(MainActivity.this);

            if (preferencesHelper.getIdleTripActive()) {

                TrackThat.getCurrentLocation(new TrackThatCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse successResponse) {
                        TrackthatLocation loc = (TrackthatLocation) successResponse.getResponseObject();
                        TrackThat.addLogoutEvent(loc.getLatitude(), loc.getLongitude(), false, batteryLevel);
                        TrackThat.addForcePunchOutEvent(loc.getLatitude(), loc.getLongitude(), false, batteryLevel);
                        CommonUtils.showLogMessage("e", "logout with locaation", "location");
                        CommonUtils.showLogMessage("e", "batteryLevel", "" + batteryLevel);


                        com.tracki.data.model.request.Location locationobj = new com.tracki.data.model.request.Location();
                        locationobj.setLocationId(CommonUtils.genrateId());
                        locationobj.setLatitude(loc.getLatitude());
                        locationobj.setLongitude(loc.getLongitude());
                        String strReturnedAddress = CommonUtils.getAddress(MainActivity.this, new LatLng(loc.getLatitude(), loc.getLongitude()));
                        Addrloc addrloc = new Addrloc();
                        if (!strReturnedAddress.isEmpty())
                            addrloc.setAddress(strReturnedAddress.toString());
                        addrloc.setLocation(locationobj);
                        punchRequest.setLocation(addrloc);
                        mMainViewModel.punch(httpManager, punchRequest, PunchInOut.PUNCH_OUT);
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        TrackThat.addLogoutEvent(0.0, 0.0, false, batteryLevel);
                        TrackThat.addForcePunchOutEvent(0.0, 0.0, false, batteryLevel);
                        CommonUtils.showLogMessage("e", "batteryLevel", "" + batteryLevel);
                        Log.e(TAG, "onError: " + errorResponse.getErrorMessage());
                        mMainViewModel.punch(httpManager, punchRequest, PunchInOut.PUNCH_OUT);

                    }
                });

                //logout();
            } else if (preferencesHelper.getIsTrackingLiveTrip()) {
                TrackThat.getCurrentLocation(new TrackThatCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse successResponse) {
                        TrackthatLocation loc = (TrackthatLocation) successResponse.getResponseObject();
                        TrackThat.addLogoutEvent(loc.getLatitude(), loc.getLongitude(), false, batteryLevel);
                        TrackThat.addForcePunchOutEvent(loc.getLatitude(), loc.getLongitude(), false, batteryLevel);
                        com.tracki.data.model.request.Location locationobj = new com.tracki.data.model.request.Location();
                        locationobj.setLocationId(CommonUtils.genrateId());
                        locationobj.setLatitude(loc.getLatitude());
                        locationobj.setLongitude(loc.getLongitude());
                        String strReturnedAddress = CommonUtils.getAddress(MainActivity.this, new LatLng(loc.getLatitude(), loc.getLongitude()));
                        Addrloc addrloc = new Addrloc();
                        if (!strReturnedAddress.isEmpty())
                            addrloc.setAddress(strReturnedAddress.toString());
                        addrloc.setLocation(locationobj);
                        punchRequest.setLocation(addrloc);
                        mMainViewModel.punch(httpManager, punchRequest, PunchInOut.PUNCH_OUT);
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        TrackThat.addLogoutEvent(0.0, 0.0, false, batteryLevel);
                        Log.e(TAG, "onError: " + errorResponse.getErrorMessage());
                        TrackThat.addForcePunchOutEvent(0.0, 0.0, false, batteryLevel);
                        Log.e(TAG, "onError: " + errorResponse.getErrorMessage());
                    }
                });

                //logout();
            } else {
                mMainViewModel.punch(httpManager, punchRequest, PunchInOut.PUNCH_OUT);
                //logout();
            }
        } else {
            mMainViewModel.punch(httpManager, punchRequest, PunchInOut.PUNCH_OUT);
            //logout();
        }
    }


    @Override
    public void checkBuddyResponse(ApiCallback callback,
                                   Object result,
                                   APIError error) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            BuddyListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result), BuddyListResponse.class);
            if (buddyListResponse != null) {
                List<Buddy> list = buddyListResponse.getBuddies();
                if (list != null && list.size() > 0) {
                    hideLoading();
                    startActivityForResult(BuddyListingActivity.newIntent(this)
                                    .putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true),
                            AppConstants.REQUEST_CODE_CREATE_TASK);
                } else {
                    mMainViewModel.checkFleet(httpManager);
                }
            }
        }
    }

    @Override
    public void checkFleetResponse(ApiCallback callback,
                                   Object result,
                                   APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            FleetListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result), FleetListResponse.class);
            List<Fleet> list = buddyListResponse.getFleets();
            if (list != null && list.size() > 0) {
                startActivityForResult(FleetListingActivity.newIntent(this)
                                .putExtra(EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU, true),
                        AppConstants.REQUEST_CODE_CREATE_TASK);
            } else {
                startActivityForResult(NewCreateTaskActivity.Companion.newIntent(this)
                                .putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true),
                        AppConstants.REQUEST_CODE_CREATE_TASK);
            }
        }
    }

    @Override
    public void onSuccessfulLogout(ApiCallback apiCallback,
                                   Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            //if logout is successful then clear sync db and recreate for new user.
            databaseHelper.onClearAndRecreateDatabase();
            preferencesHelper.setLoginToken(null);
            preferencesHelper.setAccessId(null);
            preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.NOT_ALL_FOR_EXCEPT_ACCESS_ID);
            //  CommonUtils.updateSharedContentProvider(this, preferencesHelper);
            Intent intent = LoginActivity.newIntent(this);
            intent.putExtra(AppConstants.Extra.EXTRA_LOGOUT, true);
            invalidToken(intent);
        }
    }


    @Override
    public void handleEndTaskResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            // remove this task from the preferences and hit logout api.
//            preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.NOT_ALL_FOR_EXCEPT_ACCESS_ID);
            mMainViewModel.logout(httpManager);
        }
    }

    @Override
    public void handlePunchInOutResponse(@NotNull ApiCallback apiCallback, @org.jetbrains.annotations.Nullable Object result, @org.jetbrains.annotations.Nullable APIError error, PunchInOut event) {

        preferencesHelper.setPunchOutTime(System.currentTimeMillis());
        preferencesHelper.setPunchStatus(false);
        //remove punch in and out time after punch out
        preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.PUNCH);
        preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.SELFIE_URL);
        logout();

    }

    @Override
    public void handleOnlineOfflineResponse(@NotNull ApiCallback apiCallback, @org.jetbrains.annotations.Nullable Object result, @org.jetbrains.annotations.Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            JSONConverter jsonConverter = new JSONConverter();
            OnLineOfflineResponse response = (OnLineOfflineResponse) jsonConverter.jsonToObject(result.toString(), OnLineOfflineResponse.class);

            if (response.getStatus().equals("ONLINE")) {
                preferencesHelper.setStatus("ONLINE");
            } else {
                preferencesHelper.setStatus("OFFLINE");
            }
            if (preferencesHelper.getStatus() != null) {
                if (preferencesHelper.getStatus().equals("OFFLINE")) {
                    tvStatus.setText("Offline");
                } else {
                    tvStatus.setText("Online");
                }
            }
            perFormSwitchTask();
        }
    }

    @Override
    public void handleGetTaskData(@NotNull ApiCallback apiCallback, @org.jetbrains.annotations.Nullable Object result, @org.jetbrains.annotations.Nullable APIError error) {

    }

    @Override
    public void checkInventoryResponse(@NotNull ApiCallback apiCallback, @org.jetbrains.annotations.Nullable Object result, @org.jetbrains.annotations.Nullable APIError error) {

    }


    @Override
    public void handleUnsubscribeResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        mMainViewModel.logout(httpManager);
    }

    @Override
    public void handleMyPlaceResponse(@NotNull ApiCallback apiCallback, @org.jetbrains.annotations.Nullable Object result, @org.jetbrains.annotations.Nullable APIError error) {
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            JSONConverter jsonConverter = new JSONConverter();
            LocationListResponse response = (LocationListResponse) jsonConverter.jsonToObject(result.toString(), LocationListResponse.class);
            if (response.getSuccessful()) {
                if (response.getHubs() != null && !response.getHubs().isEmpty()) {
                    preferencesHelper.saveUserHubList(response.getHubs());
                } else {
                    preferencesHelper.saveUserHubList(new ArrayList());
                }
            } else {
                preferencesHelper.saveUserHubList(new ArrayList());
            }
        } else {
            preferencesHelper.saveUserHubList(new ArrayList());
        }
    }


    private Date toDateDateAdd24Hour() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }


    @Override
    public void taskNavigationClick() {
        openOrCloseDrawer();
    }

    @Override
    public void alertAttendanceClick() {
        openOrCloseDrawer();
    }

    @Override
    public void networkAvailable() {
        if (snackBar != null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityMainBinding.drawerLayout, getString(R.string.please_check_your_internet_connection));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_FOR_PLACE) {
            TaskDashBoardFragment fragmentByTag = (TaskDashBoardFragment) getSupportFragmentManager().findFragmentByTag(TaskDashBoardFragment.class.getSimpleName());
            if (fragmentByTag != null) {
                fragmentByTag.performLocationSpinnerTask();
            }

        }
    }

    public void showDialogOK(String title, String message, DialogInterface.OnClickListener onClickListener) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        // Prevent dialog close on back press button
                        return keyCode == KeyEvent.KEYCODE_BACK;
                    }
                })
                .setPositiveButton("OK", onClickListener)
/*
                .setNegativeButton("Cancel", onClickListener)
*/
                .create()
                .show();
    }

    public static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 5389;
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 5293;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void checkPermissionAndAskPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
            ) {
                askBackgroundLocationPermission();
            }
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                        },
                        PERMISSION_REQUEST_FINE_LOCATION
                );
            } else {
                showDialogOK(getString(R.string.function_limited),
                        getString(R.string.background_location_message),
                        (dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    // This will take the user to a page where they have to click twice to drill down to grant the permission
                                    startActivity(intent);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        });
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void askBackgroundLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            showDialogOK(getString(R.string.app_need_background_location_permission),
                    getString(R.string.please_grant_location_permission_in_background),
                    (dialog, which) -> {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                requestPermissions(new String[]{
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                }, PERMISSION_REQUEST_BACKGROUND_LOCATION);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    });


        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                showDialogOK(getString(R.string.function_limited),
                        getString(R.string.background_location_message),
                        (dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    // This will take the user to a page where they have to click twice to drill down to grant the permission
                                    startActivity(intent);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        });

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_BACKGROUND_LOCATION) {
            if (grantResults.length > 0) {
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted) {

                } else {
                    TrackiToast.Message
                            .showLong(this, "Go to settings and enable permissions");
                }
            }
        } else if (requestCode == PERMISSION_REQUEST_FINE_LOCATION) {
            boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (locationAccepted) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
                    askBackgroundLocationPermission();
            } else {
                TrackiToast.Message
                        .showLong(this, "Go to settings and enable permissions");
            }
        }
    }

}


