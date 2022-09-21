package com.tracki.ui.main.taskdashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.request.SaveFilterData;
import com.tracki.data.model.response.config.AttendanceMap;
import com.tracki.data.model.response.config.ChannelConfig;
import com.tracki.data.model.response.config.ChannelSetting;
import com.tracki.data.model.response.config.CreationMode;
import com.tracki.data.model.response.config.DashBoardBoxItem;
import com.tracki.data.model.response.config.DashBoardMapResponse;
import com.tracki.data.model.response.config.InsightResponse;
import com.tracki.data.model.response.config.MappingOn;
import com.tracki.data.model.response.config.ServiceDescr;
import com.tracki.data.model.response.config.WorkFlowCategories;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.LayoutTaskStageDashBoardBinding;
import com.tracki.ui.addbuddy.ContactListActivity;
import com.tracki.ui.addplace.Hub;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.custom.CircleTransform;
import com.tracki.ui.custom.GlideApp;
import com.tracki.ui.main.DashBoardStageCountAdapter;
import com.tracki.ui.main.InsightAdapter;
import com.tracki.ui.main.filter.TaskFilterActivity;
import com.tracki.ui.newcreatetask.NewCreateTaskActivity;
import com.tracki.ui.scanqrcode.ScanQrAndBarCodeActivity;
import com.tracki.ui.scanqrcode.ScanQrCodeActivity;
import com.tracki.ui.selectorder.SelectOrderActivity;
import com.tracki.ui.tasklisting.TaskActivity;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.Log;
import com.tracki.utils.TrackiToast;
import com.tracki.utils.image_utility.ImagePicker;
import com.trackthat.lib.TrackThat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU;

public class TaskDashBoardFragment extends BaseFragment<LayoutTaskStageDashBoardBinding, TaskDashBoardViewModel> implements TaskDashBoardNavigator, View.OnClickListener
        , DashBoardStageCountAdapter.DashBoardListener {
    private static final String TAG = "TaskDashBoardFragment";
    private static final int REQUEST_CAMERA = 3;
    TaskDashBoardViewModel mMainViewModel;
    @Inject
    PreferencesHelper preferencesHelper;
    @Inject
    HttpManager httpManager;
    private static final int REQUEST_READ_STORAGE = 4;

    LayoutTaskStageDashBoardBinding taskStageDashBoardBinding;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    LinkedHashMap<String, String> stageNameMap = null;
    private RecyclerView gridRecyclerView;
    // private RecyclerView gridAttendance;
    private boolean userGeoReq;
    private long fromDate;
    private long toDate;
    private ImageView ivScanQrCode;
    private ImageView ivFilter;
    private ImageView ivNavigationIcon;
    private Date today;
    private AppCompatSpinner taskCategorySpinner;
    private String categoryId;
    private TextView tvFromDate;
    private TabLayout tabLayout;
    private CardView cardViewTab;
    private String categoryName;
    private int cellwidthWillbe = 0;
    private int InsightscellwidthWillbe = 0;
    private FloatingActionButton ivCreateTask;
    private String regionId;
    private List<String> hubIds;
    private String stateId;
    private String invAction;
    private String cityId;
    private String hubIdStr;
    private boolean isTagInventory = false;
    private String LOADBY = "";
    private CardView cardSpinner;
    private String hubId;
    private AppCompatSpinner locationSpinner;
    private CardView cardLocation;
    private boolean showMerchantTab;
    private TextView tvProcessState;
    private ChannelSetting channelSetting;
    private ArrayList<Boolean> catGeo;
    private ArrayList<String> catIds;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_task_stage_dash_board;
    }

    public static TaskDashBoardFragment newInstance(Context context) {
        Bundle args = new Bundle();
        TaskDashBoardFragment fragment = new TaskDashBoardFragment();
        fragment.setListener((NavigationClickFromTaskDashBoard) context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public TaskDashBoardViewModel getViewModel() {
        mMainViewModel = ViewModelProviders.of(this, mViewModelFactory).get(TaskDashBoardViewModel.class);
        return mMainViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel.setNavigator(this);
        taskStageDashBoardBinding = getViewDataBinding();
        setUp(view);

        if (preferencesHelper.getInsights()) {
            getInsights();
        }
        populateCategorySpinner();

    }

    private void getInsights() {
        showLoading();
        mMainViewModel.getInsights(httpManager);
    }

    private void setUp(View mainView) {


        View toolLayOut = mainView.findViewById(R.id.toolLayout);
        taskCategorySpinner = toolLayOut.findViewById(R.id.spinnerTaskCategory);
        cardSpinner = toolLayOut.findViewById(R.id.cardSpinner);
        ivScanQrCode = toolLayOut.findViewById(R.id.ivScanQrCode);
        ivFilter = toolLayOut.findViewById(R.id.ivFilter);

        CardView cardViewFromDate = mainView.findViewById(R.id.cardFromDate);
        ivCreateTask = mainView.findViewById(R.id.ivCreateTask);
        ivCreateTask.setOnClickListener(this);
        tabLayout = mainView.findViewById(R.id.tabLayout);
        cardViewTab = mainView.findViewById(R.id.cardViewTab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tvFromDate = mainView.findViewById(R.id.tvFromDate);
        cardLocation = mainView.findViewById(R.id.cardLocation);
        locationSpinner = mainView.findViewById(R.id.spinnerLocation);
        Button btnSubmit = mainView.findViewById(R.id.btnSubmit);
        ImageView ivRefresh = mainView.findViewById(R.id.ivRefresh);
        gridRecyclerView = mainView.findViewById(R.id.gridRecyclerView);
        tvProcessState = mainView.findViewById(R.id.tvProcessState);
        //  gridAttendance = mainView.findViewById(R.id.gridAttendance);
        ivNavigationIcon = toolLayOut.findViewById(R.id.ivNavigationIcon);
        ivNavigationIcon.setOnClickListener(this);
        ivScanQrCode.setOnClickListener(this);
        ivFilter.setOnClickListener(this);

        RelativeLayout linearLayout = mainView.findViewById(R.id.rlRecycler);
        ViewTreeObserver viewTreeObserver = linearLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = linearLayout.getMeasuredWidth();
                int height = linearLayout.getMeasuredHeight();
                cellwidthWillbe = width / 3;

            }
        });
        LinearLayout llInsights = mainView.findViewById(R.id.llInsights);
        ViewTreeObserver llInsightsTreeObserver = llInsights.getViewTreeObserver();
        llInsightsTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llInsights.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = llInsights.getMeasuredWidth();
                int height = llInsights.getMeasuredHeight();
                InsightscellwidthWillbe = width / 3;

            }
        });
        cardViewFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDatePicker();
            }
        });

        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitDashboardApi();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CommonUtils.showLogMessage("e","fromDate",""+fromDate);
//                CommonUtils.showLogMessage("e","toDate",""+toDate);
//                String fromDateStr=CommonUtils.getDate(fromDate, "dd/MM/yyyy hh:mm");
//                String toDateStr=CommonUtils.getDate(fromDate, "dd/MM/yyyy hh:mm");
                // CommonUtils.deleteEventUri(getBaseActivity(),"163");
                hitDashboardApi();
            }
        });
        //set adapter here

//        populateCategorySpinner();


    }

    private void changeTab(String categoryId, boolean geoReq) {
        if (tabLayout.getTabCount() > 0) {
            tabLayout.removeAllTabs();
        }
        List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();

        if (categoryId != null) {
            WorkFlowCategories workFlowCategories = new WorkFlowCategories();
            workFlowCategories.setCategoryId(categoryId);
            workFlowCategories.setAllowGeography(geoReq);
            if (listCategory.contains(workFlowCategories)) {
                int position = listCategory.indexOf(workFlowCategories);
                if (position != -1) {
                    WorkFlowCategories myCatData = listCategory.get(position);
                    // userGeoReq=myCatData.getAllowGeography();
                    if (myCatData.getAllowGeography())
                        ivFilter.setVisibility(View.VISIBLE);
                    else {
                        ivFilter.setVisibility(View.GONE);
                    }
                    if (myCatData.getInventoryConfig() != null && myCatData.getInventoryConfig().getMappingOn() == MappingOn.CREATION) {
                        isTagInventory = true;
                    } else {
                        isTagInventory = false;
                    }
                    if (myCatData.getInventoryConfig() != null && myCatData.getInventoryConfig().getInvAction()!=null) {
                        invAction =  myCatData.getInventoryConfig().getInvAction().name();
                    } else {
                        invAction = null;
                    }
                    if (myCatData.getShowMerchantTasks()) {
                        showMerchantTab = true;
                    } else {
                        showMerchantTab = false;
                    }
                }
            }

        }
        Map<String, ChannelConfig> channelConfigMap = preferencesHelper.getWorkFlowCategoriesListChannel();
        if (categoryId != null && channelConfigMap != null && channelConfigMap.containsKey(categoryId)) {
            ChannelConfig channelConfig = channelConfigMap.get(categoryId);
            if (channelConfig != null && channelConfig.getChannelSetting() != null) {
                channelSetting = channelConfig.getChannelSetting();
                if (showMerchantTab) {
                    String merchantTabLabel = "Request BY Merchant";
                    if (channelSetting.getMerchantTaskLabel() != null && !channelSetting.getMerchantTaskLabel().isEmpty())
                        merchantTabLabel = channelSetting.getMerchantTaskLabel();
                    String ASSIGNED_TO_MERCHANT = merchantTabLabel;
                    tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_TO_MERCHANT").setText(ASSIGNED_TO_MERCHANT));
                    LOADBY = "ASSIGNED_TO_MERCHANT";
                    tabLayout.getTabAt(0).select();
                }
               // channelSetting.setLocType("FIXED");
                //channelSetting.setExeLocation(true);
                if (channelSetting.getAllowCreation() != null && channelSetting.getAllowCreation()
                        && channelSetting.getTaskExecution() != null && channelSetting.getTaskExecution()) {
                    //String ASSIGNED_TO_ME = "Assigned to Me";
                    //String ASSIGNED_BY_ME = "I have Assigned";
                    if (channelSetting.getExecutionTitle() != null && !channelSetting.getExecutionTitle().isEmpty()) {
                        String ASSIGNED_TO_ME = channelSetting.getExecutionTitle();
                        if (channelSetting.getExeLocation() != null && channelSetting.getExeLocation()) {
//                            if (channelSetting.getLocType() != null && channelSetting.getLocType().equals("FIXED")) {
//                                performLocation();
//                            } else {
//                                cardLocation.setVisibility(View.GONE);
//                            }
                            tabLayout.addTab(tabLayout.newTab().setTag("LOCATION").setText(ASSIGNED_TO_ME));
                        } else {
                            tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_TO_ME").setText(ASSIGNED_TO_ME));
                        }
                    }
                    if (channelSetting.getCreationTitle() != null && !channelSetting.getCreationTitle().isEmpty()) {
                        String ASSIGNED_BY_ME = channelSetting.getCreationTitle();
                        tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_BY_ME").setText(ASSIGNED_BY_ME));
                    }


                    tabLayout.setVisibility(View.VISIBLE);
                    cardViewTab.setVisibility(View.VISIBLE);
                    if (channelSetting.getCreationMode() == CreationMode.DIRECT) {
                        ivCreateTask.setVisibility(View.VISIBLE);
                    } else {
                        ivCreateTask.setVisibility(View.GONE);
                    }
                    if (channelSetting.getExeLocation() != null && channelSetting.getExeLocation()) {
                        LOADBY = "LOCATION";
                    } else {
                        LOADBY = "ASSIGNED_TO_ME";
                    }

                    tabLayout.getTabAt(0).select();

                } else if (channelSetting.getTaskExecution() != null && channelSetting.getTaskExecution()
                        && (channelSetting.getAllowCreation() == null || !channelSetting.getAllowCreation())) {
                    //String ASSIGNED_TO_ME = "Assigned to Me";
                    if (channelSetting.getExecutionTitle() != null && !channelSetting.getExecutionTitle().isEmpty()) {
                        String ASSIGNED_TO_ME = channelSetting.getExecutionTitle();
                        if (channelSetting.getExeLocation() != null && channelSetting.getExeLocation()) {
                            tabLayout.addTab(tabLayout.newTab().setTag("LOCATION").setText(ASSIGNED_TO_ME));
//                            if (channelSetting.getLocType() != null && channelSetting.getLocType().equals("FIXED")) {
//                                performLocation();
//                            } else {
//                                cardLocation.setVisibility(View.GONE);
//                            }
                        } else {
                            tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_TO_ME").setText(ASSIGNED_TO_ME));
                        }
                        tabLayout.setVisibility(View.GONE);
                        cardViewTab.setVisibility(View.GONE);
                        ivCreateTask.setVisibility(View.GONE);
                        if (channelSetting.getExeLocation() != null && channelSetting.getExeLocation()) {
                            LOADBY = "LOCATION";
                        } else {
                            LOADBY = "ASSIGNED_TO_ME";
                        }
                        tabLayout.getTabAt(0).select();
                    }


                } else if (channelSetting.getAllowCreation() != null && channelSetting.getAllowCreation()
                        && (channelSetting.getTaskExecution() == null || !channelSetting.getTaskExecution())) {
                    //String ASSIGNED_BY_ME = "I have Assigned";
                    if (channelSetting.getCreationTitle() != null && !channelSetting.getCreationTitle().isEmpty()) {
                        String ASSIGNED_BY_ME = channelSetting.getCreationTitle();
                        tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_BY_ME").setText(ASSIGNED_BY_ME));
                        tabLayout.setVisibility(View.GONE);
                        cardViewTab.setVisibility(View.GONE);
                        if (channelSetting.getCreationMode() == CreationMode.DIRECT) {
                            ivCreateTask.setVisibility(View.VISIBLE);
                        } else {
                            ivCreateTask.setVisibility(View.GONE);
                        }
                        LOADBY = "ASSIGNED_BY_ME";
                        tabLayout.getTabAt(0).select();
                    }


                } else {
                    ivCreateTask.setVisibility(View.GONE);
                }
            }
            if (tabLayout.getTabCount() > 1) {
                tabLayout.setVisibility(View.VISIBLE);
                cardViewTab.setVisibility(View.VISIBLE);
            } else {
                performLocationSpinnerTask();
                hitDashboardApi();
                cardViewTab.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LOADBY = tab.getTag().toString();
                performLocationSpinnerTask();
                hitDashboardApi();
                CommonUtils.showLogMessage("e", "LOADBY", "=>" + LOADBY);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                LOADBY = tab.getTag().toString();
                performLocationSpinnerTask();
                hitDashboardApi();
                CommonUtils.showLogMessage("e", "LOADBY", "=>" + LOADBY);
            }
        });
    }

    public void performLocationSpinnerTask() {
        if (LOADBY.equals("LOCATION")) {
            CommonUtils.showLogMessage("e", "performLocationSpinnerTask", "=>" + LOADBY);
            List<Hub> hubsList = preferencesHelper.getUserHubList();
            if(hubsList!=null&&hubsList.size() == 1){
                hubId=hubsList.get(0).getHubId();
                preferencesHelper.setSelectedLocation(hubId);
            }else{
                hubId=preferencesHelper.getSelectedLocation();
            }
            performLocation();
        } else {
            hitDashboardApi();
            hubId=null;
            cardLocation.setVisibility(View.GONE);
        }
    }

    private void performLocation() {
        if (preferencesHelper.getUserHubList() != null && !preferencesHelper.getUserHubList().isEmpty()) {
            List<Hub> hubsList = preferencesHelper.getUserHubList();
            List<String> hubsName = new ArrayList<>();
            List<String> hubIds = new ArrayList<>();
            for (int i = 0; i < hubsList.size(); i++) {
                String address = "";
                if (hubsList.get(i).getHubLocation() != null && hubsList.get(i).getHubLocation().getLocation() != null && hubsList.get(i).getHubLocation().getLocation().getLatitude() != null
                        && hubsList.get(i).getHubLocation().getLocation().getLongitude() != null) {
                    LatLng latLng = new LatLng(hubsList.get(i).getHubLocation().getLocation().getLatitude(), hubsList.get(i).getHubLocation().getLocation().getLongitude());
                    address = CommonUtils.getAddress(getBaseActivity(), latLng);
                }
                if (!address.isEmpty()) {
                    hubsName.add(hubsList.get(i).getName() + " | " + address);
                } else {
                    hubsName.add(hubsList.get(i).getName());
                }
                hubIds.add(hubsList.get(i).getHubId());
            }
            if (hubsList.size() > 0) {
                ArrayAdapter adapter = new ArrayAdapter<String>(getBaseActivity(), android.R.layout.simple_spinner_item, hubsName) {

                    @NotNull
                    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                        ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        return v;
                    }


                    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                        View v = super.getDropDownView(position, convertView, parent);

                        Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                        ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                        return v;
                    }
                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                locationSpinner.setAdapter(adapter);
                if(preferencesHelper.getSelectedLocation()!=null&&!preferencesHelper.getSelectedLocation().isEmpty()){
                    if(hubIds.contains(preferencesHelper.getSelectedLocation())){
                        int index=hubIds.indexOf(preferencesHelper.getSelectedLocation());
                        if(index!=-1){
                            locationSpinner.setSelection(index);
                        }
                    }
                }
                locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        hubId  = hubIds.get(position);
                        preferencesHelper.setSelectedLocation(hubId);
                        hitDashboardApi();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                cardLocation.setVisibility(View.VISIBLE);
            } /*else {
                cardLocation.setVisibility(View.GONE);
                hubId = hubsList.get(0).getHubId();
                preferencesHelper.setSelectedLocation(hubId);
                hitDashboardApi();
            }*/

        } else {
            hubId =null;
            preferencesHelper.setSelectedLocation(hubId);
            hitDashboardApi();
            cardLocation.setVisibility(View.GONE);
        }

    }

    private void hitDashboardApi() {

        if (!tvFromDate.getText().toString().trim().isEmpty()) {
            if (fromDate <= toDate) {
                showLoading();
                if ((preferencesHelper.getFilterMap() != null)) {
                    if (preferencesHelper.getFilterMap().containsKey(categoryId)) {
                        SaveFilterData saveFilterData = preferencesHelper.getFilterMap().get(categoryId);
                        if (saveFilterData != null) {
                            regionId = saveFilterData.getRegionId();
                            stateId = saveFilterData.getStateId();
                            cityId = saveFilterData.getCityId();
                            hubIdStr = saveFilterData.getHubId();

                            if (hubIdStr != null && !hubIdStr.isEmpty()) {
                                hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
                            }
                        }

                    }
                }
                for (int i=0; i< catIds.size(); i++){
                    if (Objects.equals(catIds.get(i), categoryId))
                        userGeoReq = catGeo.get(i);
                }
//                preferencesHelper.setSelectedLocation(hubId);
                if (preferencesHelper != null && preferencesHelper.getUserDetail() != null && preferencesHelper.getUserDetail().getUserId() != null)
                    mMainViewModel.dashboardApi(httpManager, categoryId, fromDate, toDate, preferencesHelper.getUserDetail().getUserId(), LOADBY, hubIds, regionId, stateId, cityId, userGeoReq, hubId);
            } else {
                if (getBaseActivity() != null)
                    TrackiToast.Message.showShort(getBaseActivity(), "From Date can't be greater than To Date");
            }
        } else {
            if (getBaseActivity() != null)
                TrackiToast.Message.showShort(getBaseActivity(), "Please Select To date");

        }
    }

    private void populateCategorySpinner() {
        final Calendar cal = Calendar.getInstance();
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


        fromDate = cal.getTime().getTime();
        toDate = toDateDateAdd24Hour().getTime();
        tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));
        List<String> categories = new ArrayList<>();
        List<String> categoriesId = new ArrayList<>();
        catGeo = new ArrayList<>();
        catIds = new ArrayList<>();
        if (preferencesHelper.getWorkFlowCategoriesList() != null) {
            List<WorkFlowCategories> list = preferencesHelper.getWorkFlowCategoriesList();
            for (WorkFlowCategories data : list) {
                categories.add(data.getName());
                categoriesId.add(data.getCategoryId());
                catIds.add(data.getCategoryId());
                catGeo.add(data.getAllowGeography());

            }
            cardSpinner.setVisibility(View.VISIBLE);
            cardViewTab.setVisibility(View.VISIBLE);
        } else {
            cardSpinner.setVisibility(View.GONE);
            cardViewTab.setVisibility(View.GONE);
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(getBaseActivity(), android.R.layout.simple_spinner_item, categories) {

            @NotNull
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                return v;
            }


            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//                CommonUtils.hideSpinnerDropDown(taskCategorySpinner);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskCategorySpinner.setAdapter(adapter);
        taskCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = categoriesId.get(position);
                categoryName = categories.get(position);
                userGeoReq = catGeo.get(position);
                if(preferencesHelper.getServicePref()) {
                    String tvStatus = categoryName + " Request Within Your Service Location";
                    taskStageDashBoardBinding.tvProcessState.setText(tvStatus);
                }else{
                    //String tvStatus = categoryName + " request status";
                    String tvStatus = categoryName + " Status";
                    taskStageDashBoardBinding.tvProcessState.setText(tvStatus);
                }
                stageNameMap = preferencesHelper.getWorkFlowCategoriesList().get(position).getStageNameMap();
                hubId=preferencesHelper.getSelectedLocation();

                changeTab(categoryId,userGeoReq);
                if (showMerchantTab) {
                    LOADBY = "ASSIGNED_TO_MERCHANT";
                }
//                if (fromDate <= toDate) {
//                    showLoading();
//                    mMainViewModel.dashboardApi(httpManager, categoryId, fromDate, toDate, preferencesHelper.getUserDetail().getUserId());
//                } else {
//                    TrackiToast.Message.showShort(MainActivity.this, "From Date can't be greater than To Date");
//                }


                //view.setOnClickListener(new ItemOnClickListener(parent));
                hitDashboardApi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        showLoading();
//        mMainViewModel.dashboardApi(httpManager, categoryId, fromDate, toDate, preferencesHelper.getUserDetail().getUserId());
        //   hitDashboardApi();

    }

    /*class ItemOnClickListener implements View.OnClickListener {
        private View _parent;

        public ItemOnClickListener(ViewGroup parent) {
            _parent = parent;
        }

        @Override
        public void onClick(View view) {
            //.......
            // close the dropdown
            View root = _parent.getRootView();
            root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        }
    }*/

    private Date toDateDateAdd24Hour() {
        final Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DAY_OF_MONTH, preferencesHelper.getMaxDateRange());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        //c.add(Calendar.DAY_OF_MONTH, preferencesHelper.getMaxDateRange()==0?15:preferencesHelper.getMaxDateRange());
        if (fromDate != 0) {
            c.setTimeInMillis(fromDate);
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        today = c.getTime();
        long minTime = 0L;
        final Calendar calMinTIme = Calendar.getInstance();
        if (preferencesHelper.getMaxPastDaysAllowed() != 0)
            calMinTIme.add(Calendar.DAY_OF_MONTH, -1 * preferencesHelper.getMaxPastDaysAllowed());
        calMinTIme.set(Calendar.HOUR_OF_DAY, 0);
        calMinTIme.set(Calendar.MINUTE, 0);
        calMinTIme.set(Calendar.SECOND, 0);
        minTime = calMinTIme.getTimeInMillis();
        if (getBaseActivity() != null) {
            CommonUtils.openDatePicker(getBaseActivity(), mYear, mMonth,
                    mDay, minTime, 0, (view, year, monthOfYear, dayOfMonth) -> {


                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        // fromDate=0;
                        // tvFromDate.setText(DateTimeUtil.getParsedDate(calendar.getTimeInMillis()));
                        // toDate = 0;
                        CommonUtils.openDatePicker(getBaseActivity(), mYear, mMonth,
                                mDay, calendar.getTimeInMillis(), 0, (view_, yearEnd, monthOfYearEnd, dayOfMonthEnd) -> {

                                    fromDate = calendar.getTimeInMillis();
                                    Calendar calEnd = Calendar.getInstance();
                                    calEnd.set(Calendar.YEAR, yearEnd);
                                    calEnd.set(Calendar.MONTH, monthOfYearEnd);
                                    calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
                                    calEnd.set(Calendar.HOUR_OF_DAY, 23);
                                    calEnd.set(Calendar.MINUTE, 59);
                                    calEnd.set(Calendar.SECOND, 0);
                                    toDate = calEnd.getTimeInMillis();
                                    tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));


                                });

                    });
        }
    }

    @Override
    public void handleDashboardResponse(@NotNull ApiCallback callback,
                                        @Nullable Object result,
                                        @Nullable APIError error) {
        hideLoading();
        if (getBaseActivity() != null) {
            if (gridRecyclerView.getAdapter() != null)
                ((DashBoardStageCountAdapter) gridRecyclerView.getAdapter()).clearList();
            if (CommonUtils.handleResponse(callback, error, result, getBaseActivity())) {
                DashBoardMapResponse dashboardResponse = new Gson().fromJson(String.valueOf(result), DashBoardMapResponse.class);
//                if (dashboardResponse.getAttendanceStatusMap() != null) {
//                    //Present | Absent | No Updated | On Leave  ye 4 boxes bna lo bs
//                    //DAY OFF | HOLIDAYbhi le lo
//                    List<DashBoardBoxItem> lisItemBox = getListofAttandence(dashboardResponse.getAttendanceStatusMap());
//                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseActivity(), 3, GridLayoutManager.VERTICAL, false);
//                    gridAttendance.setLayoutManager(gridLayoutManager);
//                    DashBoardStageCountAdapter adapter = new DashBoardStageCountAdapter((ArrayList<DashBoardBoxItem>) lisItemBox);
//                    adapter.cellWidth(cellwidthWillbe);
//                    gridAttendance.setAdapter(adapter);
//                } else {
//                }
                if (dashboardResponse.getTaskCountMap() != null) {

                    tvProcessState.setVisibility(View.VISIBLE);
                    ArrayList<DashBoardBoxItem> list = new ArrayList<>();
                    Iterator hmIterator = stageNameMap.entrySet().iterator();
                    while (hmIterator.hasNext()) {
                        Map.Entry mapElement = (Map.Entry) hmIterator.next();
                        DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                        dashBoardBoxItem.setCategoryId(categoryId);
                        // dashBoardBoxItem.setCount((int) mapElement.getValue());
                        if (dashboardResponse.getTaskCountMap().get(mapElement.getKey()) != null)
                            dashBoardBoxItem.setCount((int) dashboardResponse.getTaskCountMap().get(mapElement.getKey().toString()));
                        dashBoardBoxItem.setStageId(mapElement.getKey().toString());
                        dashBoardBoxItem.setStageName(mapElement.getValue().toString());
                        CommonUtils.showLogMessage("e", "stageName", mapElement.getValue().toString());
                        list.add(dashBoardBoxItem);
                    }
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseActivity(), 3, GridLayoutManager.VERTICAL, false);
                    gridRecyclerView.setLayoutManager(gridLayoutManager);
                    DashBoardStageCountAdapter adapter = new DashBoardStageCountAdapter(list);
                    adapter.setListener(this);
                    adapter.cellWidth(cellwidthWillbe);
                    gridRecyclerView.setAdapter(adapter);
                } else {
                    tvProcessState.setVisibility(View.GONE);
                    LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getBaseActivity());
                    gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    gridRecyclerView.setLayoutManager(gridLayoutManager);
                    DashBoardStageCountAdapter adapter = new DashBoardStageCountAdapter(new ArrayList<>());
                    adapter.setListener(this);
                    adapter.cellWidth(cellwidthWillbe);
                    gridRecyclerView.setAdapter(adapter);

                }
            } else {
                tvProcessState.setVisibility(View.GONE);
                LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getBaseActivity());
                gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                gridRecyclerView.setLayoutManager(gridLayoutManager);
                DashBoardStageCountAdapter adapter = new DashBoardStageCountAdapter(new ArrayList<>());
                adapter.setListener(this);
                adapter.cellWidth(cellwidthWillbe);
                gridRecyclerView.setAdapter(adapter);

            }
        }

    }

    @Override
    public void handleInsightsResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, getBaseActivity())) {
            JSONConverter jsonConverter = new JSONConverter();
            InsightResponse response = (InsightResponse) jsonConverter.jsonToObject(result.toString(), InsightResponse.class);
            if (response.getData() != null) {
                taskStageDashBoardBinding.cardInsights.setVisibility(View.VISIBLE);
                if (response.getData().getInsights() != null && !response.getData().getInsights().isEmpty()) {
                    InsightAdapter adapter = new InsightAdapter((ArrayList<ServiceDescr>) response.getData().getInsights());
                    adapter.cellWidth(InsightscellwidthWillbe);
                    taskStageDashBoardBinding.rvInsight.setAdapter(adapter);
                }


                if (response.getData().getHeading() != null && !response.getData().getHeading().isEmpty()) {
                    taskStageDashBoardBinding.tvInsightsHeading.setVisibility(View.VISIBLE);
                    taskStageDashBoardBinding.tvInsightsHeading.setText(response.getData().getHeading());
                } else {
                    taskStageDashBoardBinding.tvInsightsHeading.setVisibility(View.GONE);
                }
                if (response.getData().getSubheading() != null && !response.getData().getSubheading().isEmpty()) {
                    taskStageDashBoardBinding.tvSubheading.setVisibility(View.VISIBLE);
                    taskStageDashBoardBinding.tvSubheading.setText(response.getData().getSubheading());
                } else {
                    taskStageDashBoardBinding.tvSubheading.setVisibility(View.GONE);
                }
                if(response.getData().getFooter() == null && response.getData().getButton()==null){
                    taskStageDashBoardBinding.llFooter.setVisibility(View.GONE);
                }
                if (response.getData().getFooter() != null && !response.getData().getFooter().isEmpty()) {
                    taskStageDashBoardBinding.tvFooter.setVisibility(View.VISIBLE);
                    taskStageDashBoardBinding.tvFooter.setText(response.getData().getFooter());
                } else {
                    taskStageDashBoardBinding.tvFooter.setVisibility(View.GONE);
//                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) taskStageDashBoardBinding.llFooter.getLayoutParams();
//                    ll.gravity = Gravity.CENTER;
//                    taskStageDashBoardBinding.llFooter.setLayoutParams(ll);
//                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            0.5f
//                    );
//                    taskStageDashBoardBinding.btnInsights.setLayoutParams(param);
                }
                if (response.getData().getImg() != null && !response.getData().getImg().isEmpty()) {
                    taskStageDashBoardBinding.ivInsights.setVisibility(View.VISIBLE);
                    GlideApp.with(this)
                            .load(response.getData().getImg())
                            .error(R.drawable.ic_undraw_notify)
                            .into(taskStageDashBoardBinding.ivInsights);
                }

                if (response.getData().getButton() != null) {
                    if (response.getData().getButton().getTitle() != null && !response.getData().getButton().getTitle().isEmpty()) {
                        taskStageDashBoardBinding.btnInsights.setText(response.getData().getButton().getTitle());
                    }
                    taskStageDashBoardBinding.btnInsights.setVisibility(View.VISIBLE);
                    if (response.getData().getButton().getUrl() != null && response.getData().getButton().getUrl().equals("CREATE_TASK")) {
                        taskStageDashBoardBinding.btnInsights.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = NewCreateTaskActivity.Companion.newIntent(getBaseActivity());
                                startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);

                            }
                        });
                    } else if (response.getData().getButton().getUrl() != null && response.getData().getButton().getUrl().equals("SHARE")) {
                        taskStageDashBoardBinding.btnInsights.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkStoragePermission();

                            }
                        });
                    }
                } else {
                    taskStageDashBoardBinding.btnInsights.setVisibility(View.GONE);
                }


            }

        } else {
            taskStageDashBoardBinding.cardInsights.setVisibility(View.GONE);
        }

    }

    private List<DashBoardBoxItem> getListofAttandence(AttendanceMap attendanceMap) {
        List<DashBoardBoxItem> attandanceList = new ArrayList<>();
        DashBoardBoxItem da1 = new DashBoardBoxItem();
        da1.setStageName("Present");
        da1.setCount(attendanceMap.getPRESENT());
        attandanceList.add(da1);
        DashBoardBoxItem da2 = new DashBoardBoxItem();
        da2.setStageName("Absent");
        da2.setCount(attendanceMap.getABSENT());
        attandanceList.add(da2);
        DashBoardBoxItem da3 = new DashBoardBoxItem();
        da3.setStageName("No Updated");
        da3.setCount(attendanceMap.getNOT_UPDATED());
        attandanceList.add(da3);
        DashBoardBoxItem da6 = new DashBoardBoxItem();
        da6.setStageName("On Leave");
        da6.setCount(attendanceMap.getON_LEAVE());
        attandanceList.add(da6);
        DashBoardBoxItem da4 = new DashBoardBoxItem();
        da4.setStageName("Day Off");
        da4.setCount(attendanceMap.getDAY_OFF());
        attandanceList.add(da4);
        DashBoardBoxItem da5 = new DashBoardBoxItem();
        da5.setStageName("Holiday");
        da5.setCount(attendanceMap.getHOLIDAY());
        attandanceList.add(da5);
        return attandanceList;
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivScanQrCode) {
            getCameraPermission();
        } else if (id == R.id.ivFilter) {
            Intent intentFilter = TaskFilterActivity.Companion.newIntent(getBaseActivity());
            if (regionId != null)
                intentFilter.putExtra("regionId", regionId);
            if (hubIdStr != null)
                intentFilter.putExtra("hubIdStr", hubIdStr);
            if (stateId != null)
                intentFilter.putExtra("stateId", stateId);
            if (cityId != null)
                intentFilter.putExtra("cityId", cityId);
            if (categoryId != null)
                intentFilter.putExtra("categoryId", categoryId);
            intentFilter.putExtra("from", AppConstants.TASK);
            startActivityForResult(intentFilter, AppConstants.REQUEST_CODE_FILTER_USER);
        } else if (id == R.id.ivNavigationIcon) {
            if (mListener != null) {
                mListener.taskNavigationClick();
            }
        } else if (id == R.id.ivCreateTask || id == R.id.ivDashboardNavigation) {
            CommonUtils.preventTwoClick(v);
            //showLoading();
            //  mMainViewModel.checkInventoryData(httpManager, categoryId);
            if (preferencesHelper.getIsTrackingLiveTrip() && categoryId.equals(preferencesHelper.getActiveTaskCategoryId())) {
                TrackiToast.Message.showShort(getBaseActivity(), AppConstants.MSG_ONGOING_TASK_SAME_CATGEORY);

            } else {
                if (isTagInventory) {
                    Intent intent = SelectOrderActivity.Companion.newIntent(getBaseActivity());
                    DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                    dashBoardBoxItem.setCategoryId(categoryId);
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                            new Gson().toJson(dashBoardBoxItem));
                    if (invAction != null && !invAction.isEmpty())
                        intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET, invAction);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_TAG_INVENTORY);

                } else {

                    Intent intent = NewCreateTaskActivity.Companion.newIntent(getBaseActivity());
                    DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                    dashBoardBoxItem.setCategoryId(categoryId);
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                            new Gson().toJson(dashBoardBoxItem));
                    intent.putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                    // intent.putExtra("from", "taskListing");
                    intent.putExtra("from", "dashBoard");
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);
                }
            }
//                InventoryRequest request=new InventoryRequest();
//                request.setCategoryId(categoryId);
//                mMainViewModel.checkInventoryData(httpManager, request);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.REQUEST_CODE_DYNAMIC_FORM:
                if (resultCode == RESULT_OK) {
                    //if there is no data in the form
                    if (!TrackThat.isTracking()) {
                        preferencesHelper.remove(AppPreferencesHelper.PREF_KEY_TASK);
                        preferencesHelper.remove(AppPreferencesHelper.PREF_KEY_GEOFENCE_IDS);
                    } else {
                        // checkForGeofence();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // checkForGeofence();
                }
                break;
            case AppConstants.REQUEST_CODE_CREATE_TASK:
                if (resultCode == RESULT_OK) {
                    if (tabLayout.getVisibility() == View.VISIBLE) {
                        if (tabLayout.getTabAt(0).getTag().equals("ASSIGNED_TO_MERCHANT")) {
                            tabLayout.getTabAt(0).select();
                        } else if (tabLayout.getTabAt(0).getTag().equals("ASSIGNED_BY_ME")) {
                            tabLayout.getTabAt(0).select();
                        } else if (tabLayout.getTabAt(1).getTag().equals("ASSIGNED_BY_ME")) {
                            tabLayout.getTabAt(1).select();
                        }

                    }
                    if (showMerchantTab) {
                        LOADBY = "ASSIGNED_TO_MERCHANT";
                    } else {
                        LOADBY = "ASSIGNED_BY_ME";
                    }
                    hitDashboardApi();
                }
                break;
            //
            case AppConstants.REQUEST_CODE_TAG_INVENTORY:
                if (resultCode == RESULT_OK) {
                    DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                    dashBoardBoxItem.setCategoryId(categoryId);
                    if (stageNameMap != null && stageNameMap.size() > 0) {
                        Map.Entry<String, String> entry = stageNameMap.entrySet().iterator().next();
                        String key = entry.getKey();
                        String value = entry.getValue();
                        dashBoardBoxItem.setStageId(key);
                        dashBoardBoxItem.setStageName(value);
                    }
                    onItemClick(dashBoardBoxItem);
                }

                break;
            case AppConstants.REQUEST_CODE_TASK_UPDATE: {
                performLocationSpinnerTask();
//                hitDashboardApi();
            }
            case AppConstants.REQUEST_CODE_FILTER_USER: {
                if (resultCode == RESULT_OK) {
                    userGeoReq = true;
//                    regionId = data.getStringExtra("regionId");
//                    hubIdStr = data.getStringExtra("hubId");
//                    if (hubIdStr != null && !hubIdStr.isEmpty()) {
//                        hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
//                    }
//                    stateId = data.getStringExtra("stateId");
//                    cityId = data.getStringExtra("cityId");
                    hitDashboardApi();

                } else if (resultCode == RESULT_CANCELED) {

                    regionId = null;
                    hubIds = null;
                    hubIdStr = null;
                    stateId = null;
                    cityId = null;
                    userGeoReq = false;
                    hitDashboardApi();
                }


            }

            default:
                break;
        }
    }

    @Override
    public void onItemClick(@NotNull DashBoardBoxItem response) {
        Intent intent = TaskActivity.newIntent(getBaseActivity());
        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                new Gson().toJson(response));
        if (categoryName != null)
            intent.putExtra(AppConstants.Extra.TITLE, categoryName);
        intent.putExtra(AppConstants.Extra.EXTRA_STAGEID, response.getStageId());
        intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate);
        intent.putExtra(AppConstants.Extra.FROM_TO, toDate);
        intent.putExtra(AppConstants.Extra.LOAD_BY, LOADBY);
        intent.putExtra(AppConstants.Extra.GEO_FILTER,userGeoReq);
        startActivityForResult(intent, AppConstants.REQUEST_CODE_TASK_UPDATE);
    }

    public void getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getBaseActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openScanActivity();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        } else {
            openScanActivity();
        }
    }

    private void openScanActivity() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Scan",Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Scan","Task").apply();
        startActivity(ScanQrAndBarCodeActivity.Companion.newIntent(getBaseActivity()));
    }

    public interface NavigationClickFromTaskDashBoard {
        void taskNavigationClick();

    }

    private NavigationClickFromTaskDashBoard mListener;

    public void setListener(NavigationClickFromTaskDashBoard mListener) {
        this.mListener = mListener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openScanActivity();
                }
            }
        }
        else if (requestCode == REQUEST_READ_STORAGE) {
            if(grantResults.length>0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
                    proceedToImageSending();
                }
            }
        }else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getBaseActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && getBaseActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                   ) {
                proceedToImageSending();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                       }, REQUEST_READ_STORAGE);
            }
        } else {
            proceedToImageSending();
        }
    }

    private void proceedToImageSending() {
        String appPackageName = getBaseActivity().getPackageName();
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String text="Help us spread the word. Join us to serve the community." +
                "\n" +
                "Download App:"+"\n"+link;
        // String text=getString(R.string.app_name)+" believes in Power of United Community and extending help by connecting those who need help with someone who can help. Download now: "+link;
        CommonUtils.shareImageAndTextOnSocialMedia(getBaseActivity(), text);

    }



}
