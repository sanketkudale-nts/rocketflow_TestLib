package com.rocketflyer.rocketflow.ui.tasklisting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.model.response.config.BuddyListResponse;
import com.tracki.data.model.response.config.ChannelConfig;
import com.tracki.data.model.response.config.ChannelSetting;
import com.tracki.data.model.response.config.CreationMode;
import com.tracki.data.model.response.config.DashBoardBoxItem;
import com.tracki.data.model.response.config.Fleet;
import com.tracki.data.model.response.config.FleetListResponse;
import com.tracki.data.model.response.config.InventoryResponse;
import com.tracki.data.model.response.config.MappingOn;
import com.tracki.data.model.response.config.WorkFlowCategories;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityTaskBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.buddylisting.BuddyListingActivity;
import com.tracki.ui.chat.ChatActivity;
import com.tracki.ui.custom.socket.BaseModel;
import com.tracki.ui.custom.socket.ConnectionInfo;
import com.tracki.ui.custom.socket.ConnectionResponse;
import com.tracki.ui.custom.socket.OpenCreateRoomModel;
import com.tracki.ui.custom.socket.WebSocketManager;
import com.tracki.ui.fleetlisting.FleetListingActivity;
import com.tracki.ui.newcreatetask.NewCreateTaskActivity;
import com.tracki.ui.selectorder.SelectOrderActivity;
import com.tracki.ui.tasklisting.TaskNavigator;
import com.tracki.ui.tasklisting.TaskPagerAdapter;
import com.tracki.ui.tasklisting.TaskViewModel;
import com.tracki.ui.tasklisting.assignedtome.AssignedtoMeFragment;
import com.tracki.ui.tasklisting.ihaveassigned.IhaveAssignedFragment;
import com.tracki.ui.tasklisting.ihaveassigned.TabDataClass;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.Log;
import com.tracki.utils.TrackiToast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU;
import static com.tracki.utils.AppConstants.Extra.EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU;

/**
 * Created by rahul on 8/10/18
 */
public class TaskActivity extends BaseActivity<ActivityTaskBinding, TaskViewModel>
        implements TaskNavigator, HasSupportFragmentInjector, View.OnClickListener, IhaveAssignedFragment.setIHaveChatListener, AssignedtoMeFragment.setAssignToMeChatListener, WebSocketManager.SocketListener {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    @Inject
    TaskViewModel mTaskViewModel;
    @Inject
    TaskPagerAdapter mPagerAdapter;
    @Inject
    HttpManager httpManager;

    @Inject
    PreferencesHelper preferencesHelper;
    private boolean isTagInventory = false;
    private boolean showMerchantTab = false;
    private ActivityTaskBinding mActivityTaskBinding;
    private FloatingActionButton ivCreateTask;
    private String categoryMap;
    private String buddyId;
    private String buddyName;
    private Map<String, String> mMapCategory;
    private int connectionCounter = 0;
    private String categoryId;
    private String LOADBY;
    private long fromDate;
    private long toDate;
    private boolean userGeoReq = true;
    private Snackbar snackBar;

    public static Intent newIntent(Context context) {
        return new Intent(context, TaskActivity.class);
    }

    private List<TabDataClass> fragments = new ArrayList<>();

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_task;
    }

    @Override
    public TaskViewModel getViewModel() {
        return mTaskViewModel;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTaskBinding = getViewDataBinding();
        mTaskViewModel.setNavigator(this);
        setUp();
    }

    private void setUp() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
                categoryMap = getIntent().getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES);
                CommonUtils.showLogMessage("e", "categoryMap", categoryMap);
                mMapCategory = new Gson().fromJson(categoryMap, new TypeToken<HashMap<String, String>>() {
                }.getType());
            }
            if (getIntent().hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
//                viewPager.setCurrentItem(1);
            }
            if (getIntent().hasExtra(AppConstants.Extra.FROM_DATE)) {
                fromDate = getIntent().getLongExtra(AppConstants.Extra.FROM_DATE, 0);
            }
            if (getIntent().hasExtra(AppConstants.Extra.FROM_TO)) {
                toDate = getIntent().getLongExtra(AppConstants.Extra.FROM_TO, 0);
            }
            if (getIntent().hasExtra(AppConstants.Extra.LOAD_BY)) {
                LOADBY = getIntent().getStringExtra(AppConstants.Extra.LOAD_BY);
            }
            if (getIntent().hasExtra(AppConstants.Extra.GEO_FILTER)){
                userGeoReq = getIntent().getBooleanExtra(AppConstants.Extra.GEO_FILTER,false);
            }
        }
        preferencesHelper.setIsFleetAndBuddyShow(false);
        if (getIntent().hasExtra(AppConstants.Extra.TITLE)) {
            setToolbar(mActivityTaskBinding.toolbar, getIntent().getStringExtra(AppConstants.Extra.TITLE));
        } else {
            setToolbar(mActivityTaskBinding.toolbar, getString(R.string.tasks));
        }

        ivCreateTask = mActivityTaskBinding.ivCreateTask;
        ivCreateTask.setOnClickListener(this);
        ViewPager viewPager = mActivityTaskBinding.vpTask;
        TabLayout tabLayout = mActivityTaskBinding.tabLayout;
        if (mMapCategory != null && mMapCategory.containsKey("categoryId")) {
            categoryId = mMapCategory.get("categoryId");
            List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();

            if (categoryId != null) {
                WorkFlowCategories workFlowCategories = new WorkFlowCategories();
                workFlowCategories.setCategoryId(categoryId);
                if (listCategory.contains(workFlowCategories)) {
                    int position = listCategory.indexOf(workFlowCategories);
                    if (position != -1) {
                        WorkFlowCategories myCatData = listCategory.get(position);
                        if (myCatData.getInventoryConfig() != null && myCatData.getInventoryConfig().getMappingOn() == MappingOn.CREATION) {
                            isTagInventory = true;
                        } else {
                            isTagInventory = false;
                        }
                        if(myCatData.getShowMerchantTasks()){
                            showMerchantTab=true;
                            if(LOADBY==null){
                                LOADBY = "ASSIGNED_TO_MERCHANT";
                            }
                        }else{
                            showMerchantTab=false;
                            if(LOADBY==null){
                                LOADBY = "ASSIGNED_TO_ME";
                            }

                        }
                    }
                }

            }
            Map<String, ChannelConfig> channelConfigMap = preferencesHelper.getWorkFlowCategoriesListChannel();
            if (categoryId != null && channelConfigMap != null && channelConfigMap.containsKey(categoryId)) {
                ChannelConfig channelConfig = channelConfigMap.get(categoryId);
                if (channelConfig != null && channelConfig.getChannelSetting() != null) {
                    ChannelSetting channelSetting = channelConfig.getChannelSetting();
                    if (showMerchantTab) {
                        String merchantTabLabel="Request BY Merchant";
                        if (channelSetting.getMerchantTaskLabel() != null && !channelSetting.getMerchantTaskLabel().isEmpty())
                            merchantTabLabel=channelSetting.getMerchantTaskLabel();
                        fragments.add(new TabDataClass(IhaveAssignedFragment.newInstance(categoryMap, fromDate, toDate,true,userGeoReq), merchantTabLabel));

                        mPagerAdapter.setFragments(fragments);
                        // tabLayout.getTabAt(0).select();
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        viewPager.setCurrentItem(0, true);

                    }
                    if (channelSetting.getAllowCreation() != null && channelSetting.getAllowCreation()
                            && channelSetting.getTaskExecution() != null && channelSetting.getTaskExecution()) {
                        if (channelSetting.getExecutionTitle() != null && !channelSetting.getExecutionTitle().isEmpty())
                            fragments.add(new TabDataClass(AssignedtoMeFragment.newInstance(categoryMap, fromDate, toDate, null, null,userGeoReq), channelSetting.getExecutionTitle()));
                        if (channelSetting.getCreationTitle() != null && !channelSetting.getCreationTitle().isEmpty())
                            fragments.add(new TabDataClass(IhaveAssignedFragment.newInstance(categoryMap, fromDate, toDate,false,userGeoReq), channelSetting.getCreationTitle()));
                        if (channelSetting.getCreationMode() == CreationMode.DIRECT) {
                            ivCreateTask.setVisibility(View.VISIBLE);
                        } else {
                            ivCreateTask.setVisibility(View.GONE);
                        }
                        tabLayout.setVisibility(View.VISIBLE);
                        mPagerAdapter.setFragments(fragments);
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        if (LOADBY != null && !LOADBY.isEmpty()) {
                            if(showMerchantTab){
                                if (LOADBY.equals("ASSIGNED_TO_MERCHANT")) {
                                    viewPager.setCurrentItem(0, true);
                                }
                                else if (LOADBY.equals("ASSIGNED_TO_ME")) {
                                    viewPager.setCurrentItem(1, true);
                                } else if (LOADBY.equals("ASSIGNED_BY_ME")) {
                                    viewPager.setCurrentItem(2, true);
                                }

                            }else{
                                if (LOADBY.equals("ASSIGNED_TO_ME")) {
                                    viewPager.setCurrentItem(0, true);
                                } else if (LOADBY.equals("ASSIGNED_BY_ME")) {
                                    viewPager.setCurrentItem(1, true);
                                }
                            }


                        }
                    } else if (channelSetting.getTaskExecution() != null && channelSetting.getTaskExecution() && (channelSetting.getAllowCreation() == null || !channelSetting.getAllowCreation())) {
                        if (channelSetting.getExecutionTitle() != null && !channelSetting.getExecutionTitle().isEmpty())
                            fragments.add(new TabDataClass(AssignedtoMeFragment.newInstance(categoryMap, fromDate, toDate, null, null,userGeoReq), channelSetting.getExecutionTitle()));
                        ivCreateTask.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.GONE);
                        mPagerAdapter.setFragments(fragments);
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        if (LOADBY != null && !LOADBY.isEmpty()){
                            if(showMerchantTab){
                                if (LOADBY.equals("ASSIGNED_TO_MERCHANT")) {
                                    viewPager.setCurrentItem(0, true);
                                }
                                else if (LOADBY.equals("ASSIGNED_TO_ME")) {
                                    viewPager.setCurrentItem(1, true);
                                }
                            }else{
                                viewPager.setCurrentItem(0, true);
                            }
                        }


                    } else if (channelSetting.getAllowCreation() != null && channelSetting.getAllowCreation()
                            && (channelSetting.getTaskExecution() == null || !channelSetting.getTaskExecution())) {
                        if (channelSetting.getCreationTitle() != null && !channelSetting.getCreationTitle().isEmpty())
                            fragments.add(new TabDataClass(IhaveAssignedFragment.newInstance(categoryMap, fromDate, toDate,false,userGeoReq), channelSetting.getCreationTitle()));
                        if (channelSetting.getCreationMode() == CreationMode.DIRECT) {
                            ivCreateTask.setVisibility(View.VISIBLE);
                        } else {
                            ivCreateTask.setVisibility(View.GONE);
                        }
                        tabLayout.setVisibility(View.GONE);
                        mPagerAdapter.setFragments(fragments);
                        // tabLayout.getTabAt(0).select();
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        if (LOADBY != null && !LOADBY.isEmpty()){
                            if(showMerchantTab){
                                if (LOADBY.equals("ASSIGNED_TO_MERCHANT")) {
                                    viewPager.setCurrentItem(0, true);
                                }
                                else if (LOADBY.equals("ASSIGNED_BY_ME")) {
                                    viewPager.setCurrentItem(1, true);
                                }
                            }else{
                                viewPager.setCurrentItem(0, true);
                            }
                        }

                    }

                }


            }
            if (fragments.size() > 1) {
                tabLayout.setVisibility(View.VISIBLE);
            } else {
                tabLayout.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivCreateTask) {
            if(preferencesHelper.getIsTrackingLiveTrip()&&categoryId.equals(preferencesHelper.getActiveTaskCategoryId())){
                TrackiToast.Message.showShort(this, AppConstants.MSG_ONGOING_TASK_SAME_CATGEORY);
            }else {
                if (isTagInventory) {
                    Intent intent = SelectOrderActivity.Companion.newIntent(this);
                    DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                    dashBoardBoxItem.setCategoryId(categoryId);
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                            new Gson().toJson(dashBoardBoxItem));
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);

                } else {
                    if (preferencesHelper.getIsFleetAndBuddySHow()) {
                        showLoading();
                        mTaskViewModel.checkBuddy(httpManager);
                    } else {
                        Intent intent = NewCreateTaskActivity.Companion.newIntent(this);
                        intent.putExtra(AppConstants.Extra.FROM, "taskListing");
                        intent.putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                        if (categoryMap != null)
                            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                        startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);
                    }
                }
            }

        }
    }

    @Override
    public void checkBuddyResponse(ApiCallback callback,
                                   Object result,
                                   APIError error) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            BuddyListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result), BuddyListResponse.class);
            List<Buddy> list = buddyListResponse.getBuddies();
            if (list != null && list.size() > 0) {
                //categoryId
                hideLoading();
                Intent intent = BuddyListingActivity.newIntent(this);
                intent.putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                if (categoryMap != null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                startActivityForResult(intent,
                        AppConstants.REQUEST_CODE_CREATE_TASK);
            } else {
                mTaskViewModel.checkFleet(httpManager);
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
                //categoryId
                Intent intent = FleetListingActivity.newIntent(this);
                intent.putExtra(EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                if (categoryMap != null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                startActivityForResult(intent,
                        AppConstants.REQUEST_CODE_CREATE_TASK);
            } else {
                Intent intent = NewCreateTaskActivity.Companion.newIntent(this);
                intent.putExtra(AppConstants.Extra.FROM, "taskListing");
                intent.putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                if (categoryMap != null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);

            }
        }
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {

    }

    @Override
    public void chatAssignClick(String buddyId, String buddyname) {
        initSocket();
        connectSocket(this);
        try {
            ++connectionCounter;
            if (webSocketManager != null && webSocketManager.isConnected()) {
                showLoading();
                webSocketManager.connectPacket(buddyId);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }/*finally {
            if(connectionCounter>=3){
                Toast.makeText(this, "Connection not established please try after some time", Toast.LENGTH_SHORT).show();
            }
        }*/

        this.buddyId = buddyId;
        this.buddyName = buddyname;
//        ArrayList list = new ArrayList<String>();
//         list.add(buddyId);
//        webSocketManager.openCreateRoom(list, null, true, 10);
    }

    @Override
    public void chatIHaveClick(String buddyId, String buddyname) {

        initSocket();
        connectSocket(this);
        try {
            ++connectionCounter;
            if (webSocketManager != null && webSocketManager.isConnected()) {
                showLoading();
                webSocketManager.connectPacket(buddyId);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }/*finally {
            Log.e("tag",connectionCounter+"");
            if(connectionCounter>=3){
                Toast.makeText(this, "Connection not established please try after some time", Toast.LENGTH_SHORT).show();
            }
        }*/


        this.buddyId = buddyId;
        this.buddyName = buddyname;
        /*ArrayList list = new ArrayList<String>();
        list.add(buddyId);
        webSocketManager.openCreateRoom(list, null, true, 10);*/
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof AssignedtoMeFragment) {
            ((AssignedtoMeFragment) fragment).setListenerClick(this);
        } else if (fragment instanceof IhaveAssignedFragment) {
            ((IhaveAssignedFragment) fragment).setListenerClick(this);
        }
    }

    @Override
    public void onSocketResponse(int eventName, BaseModel baseModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//               Log.e("TaskActivity", "onSocketResponse() called");
//               Log.e("TaskActivity", "eventName =>" + eventName);
//               Log.e("TaskActivity", "baseModel =>" + baseModel);
                hideLoading();
                if (eventName == 4) {
                    OpenCreateRoomModel openCreateRoomModel = (OpenCreateRoomModel) baseModel;
                    String roomId = openCreateRoomModel.getRoomId();
                    ArrayList list = new ArrayList<String>();
                    list.add(buddyId);
                    startActivity(ChatActivity.Companion.newIntent(TaskActivity.this)
                            .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, list)
                            .putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
                            .putExtra(AppConstants.Extra.EXTRA_IS_CREATE_ROOM, false)
                            .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId));
                } else if (eventName == 1) {
                    connectionCounter = 0;
                    //connection hashMap
                    ArrayList connectionInfoList = new ArrayList<ConnectionInfo>();
                    //buddy message hashMap
                    ArrayList buddyList = new ArrayList<Buddy>();
                    Map map = new HashMap<String, String>();
                    ConnectionResponse connectionResponse = (ConnectionResponse) baseModel;

                    ArrayList list = new ArrayList<String>();
                    list.add(buddyId);
                    webSocketManager.openCreateRoom(list, null, true, 10);

                }
            }
        });
    }

    @Override
    public void onOpen() {
//        ArrayList list = new ArrayList<String>();
//        list.add(buddyId);
//        webSocketManager.openCreateRoom(list, null, true, 10);
    }

    @Override
    public void closed() {
        webSocketManager = null;
        Log.e("close call", "close call");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (webSocketManager != null) {
//            webSocketManager.disconnect();
//            webSocketManager = null;
//        }

    }

    @Override
    protected void onResume() {
        initSocket();
        connectSocket(this);
        super.onResume();

    }

    @Override
    public void checkInventory(@Nullable ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        JSONConverter jsonConverter = new JSONConverter();
        InventoryResponse response = (InventoryResponse) jsonConverter.jsonToObject(result.toString(), InventoryResponse.class);
        if (response.getSuccessful()) {
            Intent intent = SelectOrderActivity.Companion.newIntent(this);
            DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
            dashBoardBoxItem.setCategoryId(categoryId);
            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                    new Gson().toJson(dashBoardBoxItem));
            startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);

        } else {
            if (preferencesHelper.getIsFleetAndBuddySHow()) {
                showLoading();
                mTaskViewModel.checkBuddy(httpManager);
            } else {
                // Intent intent=CreateTaskActivity.Companion.newIntent(this);
                Intent intent = NewCreateTaskActivity.Companion.newIntent(this);
                intent.putExtra(AppConstants.Extra.FROM, "taskListing");
                intent.putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                if (categoryMap != null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);
            }
        }
    }
    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
//        RelativeLayout rlMain=mActivityMainBinding.mainView.findViewById(R.id.rlMain);
        snackBar= CommonUtils.showNetWorkConnectionIssue(  mActivityTaskBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
}
