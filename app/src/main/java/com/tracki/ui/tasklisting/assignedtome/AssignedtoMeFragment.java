package com.tracki.ui.tasklisting.assignedtome;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.local.prefs.StartIdealTrackWork;
import com.tracki.data.model.request.CtaLocation;
import com.tracki.data.model.request.DynamicFormMainData;
import com.tracki.data.model.request.ExecuteUpdateRequest;
import com.tracki.data.model.request.SaveFilterData;
import com.tracki.data.model.request.TaskData;
import com.tracki.data.model.request.TaskRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.CallToActions;
import com.tracki.data.model.response.config.ChannelConfig;
import com.tracki.data.model.response.config.ChannelSetting;
import com.tracki.data.model.response.config.Condition;
import com.tracki.data.model.response.config.DashBoardBoxItem;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.data.model.response.config.TRAGETINFO;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.TaskListing;
import com.tracki.data.model.response.config.Tracking;
import com.tracki.data.model.response.config.WorkFlowCategories;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.FragmentAssignedToMeBinding;
import com.tracki.ui.addplace.Hub;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.common.DoubleButtonDialog;
import com.tracki.ui.common.OnClickListener;
import com.tracki.ui.dynamicform.DynamicFormActivity;
import com.tracki.ui.inventory.InventoryActivity;
import com.tracki.ui.main.filter.TaskFilterActivity;
import com.tracki.ui.newcreatetask.NewCreateTaskActivity;
import com.tracki.ui.receiver.ServiceRestartReceiver;
import com.tracki.ui.taskdetails.NewTaskDetailsActivity;
import com.tracki.ui.tasklisting.PaginationListener;
import com.tracki.ui.tasklisting.PagingData;
import com.tracki.ui.tasklisting.StageListAdapter;
import com.tracki.ui.tasklisting.TaskClickListener;
import com.tracki.ui.tasklisting.TaskItemClickListener;
import com.tracki.ui.tasklisting.TaskListingAdapter;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.BuddyInfo;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.Log;
import com.tracki.utils.NetworkUtils;
import com.tracki.utils.TaskStatus;
import com.tracki.utils.TrackiToast;
import com.trackthat.lib.TrackThat;
import com.trackthat.lib.internal.network.TrackThatCallback;
import com.trackthat.lib.models.ErrorResponse;
import com.trackthat.lib.models.SuccessResponse;
import com.trackthat.lib.models.TrackthatLocation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static com.tracki.ui.tasklisting.PaginationListener.PAGE_START;
import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU;
import static com.tracki.utils.AppConstants.Extra.FROM;

/**
 * Created by rahul on 8/10/18
 */
public class AssignedtoMeFragment extends BaseFragment<FragmentAssignedToMeBinding, AssignedToMeViewModel>
        implements AssignedtoMeNavigator, TaskItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, StageListAdapter.DashBoardListener, View.OnClickListener {

    private static final String TAG = AssignedtoMeFragment.class.getSimpleName();


    @Inject
    TaskListingAdapter mAssignedtoMeAdapter;

   /* @Inject
    TaskListingAdapter mAdapter;*/

    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;
    private FragmentAssignedToMeBinding mFragmentAssignedToMeBinding;
    private Api api;
    private TaskRequest buddyRequest;
    private AssignedToMeViewModel mAssignedToMeViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView rvAssignedToMe;
    private RecyclerView rvStages;
    private CardView cvStages;
    private Date today;
    private String stageId = null;
    private String regionId;
    private List<String> hubIds;
    private String stateId;
    private String cityId;
    private String hubId;
    private boolean userGeoReq;
    private AppCompatSpinner locationSpinner;
    private CardView cardLocation;

    private BroadcastReceiver refreshTaskListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
                isLastPage = false;
                currentPage = PAGE_START;
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset(0);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setPaginationData(pagingData);
                buddyRequest.setUserGeoReq(userGeoReq);
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiApplication.getApiMap().get(ApiType.TASKS);
                if (api != null) {
                    api.setAppendWithKey("ASSIGNED_TO_ME");
                }
                if(rvAssignedToMe!=null)
                    rvAssignedToMe.setVisibility(View.GONE);
                mAssignedtoMeAdapter.clearItems();
                mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
            }

        }
    };
    private Map<String, String> categoryMap;

    private String dfdId = null;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private long fromDate;
    private long toDate;
    private TextView tvFromDate;
    private CardView cardFromDate;
    private ImageButton mButtonSubmit;
    private EditText etSearch;
    private String categoryId;

    LinkedHashMap<String, String> mStageNameMap = null;
    private String hubIdStr;
    private setAssignToMeChatListener mListener;

    public static AssignedtoMeFragment newInstance(String value, long fromDate, long toDate, @Nullable String taskId, @Nullable String referenceId, @Nullable boolean geoReq) {
        Bundle args = new Bundle();
        args.putString(AppConstants.Extra.EXTRA_CATEGORIES, value);
        args.putLong(AppConstants.Extra.FROM_DATE, fromDate);
        args.putLong(AppConstants.Extra.FROM_TO, toDate);
        args.putBoolean(AppConstants.Extra.GEO_FILTER,geoReq);
        if (taskId != null)
            args.putString(AppConstants.Extra.EXTRA_PAREN_TASK_ID, taskId);
        if (referenceId != null)
            args.putString(AppConstants.Extra.EXTRA_PARENT_REF_ID, referenceId);
        AssignedtoMeFragment fragment = new AssignedtoMeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_assigned_to_me;
    }

    @Override
    public AssignedToMeViewModel getViewModel() {
        mAssignedToMeViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AssignedToMeViewModel.class);
        return mAssignedToMeViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setHasOptionsMenu(true);
        mAssignedToMeViewModel.setNavigator(this);
        mAssignedtoMeAdapter.setListener(this);
        mAssignedtoMeAdapter.setAssignedToTask(true);

    }

    private void perFormStageTask(Map<String, String> categoryMap) {
        List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();
        String categoryId = null;
        String stageId = null;
        if (categoryMap != null && categoryMap.containsKey("categoryId"))
            categoryId = categoryMap.get("categoryId");
        if (categoryMap != null && categoryMap.containsKey("stageId"))
            stageId = categoryMap.get("stageId");
        if (categoryId != null) {
            WorkFlowCategories workFlowCategories = new WorkFlowCategories();
            workFlowCategories.setCategoryId(categoryId);
            if (listCategory.contains(workFlowCategories)) {
                int position = listCategory.indexOf(workFlowCategories);
                if (position != -1) {
                    WorkFlowCategories myCatData = listCategory.get(position);
                    LinkedHashMap<String, String> stageNameMap = myCatData.getStageNameMap();
                    mStageNameMap = myCatData.getStageNameMap();
                    Iterator hmIterator = stageNameMap.entrySet().iterator();
                    ArrayList<DashBoardBoxItem> list = new ArrayList<>();
                    while (hmIterator.hasNext()) {
                        Map.Entry mapElement = (Map.Entry) hmIterator.next();
                        DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                        dashBoardBoxItem.setCategoryId(categoryId);
                        dashBoardBoxItem.setStageId(mapElement.getKey().toString());
                        dashBoardBoxItem.setStageName(mapElement.getValue().toString());
                        if (stageId != null && stageId.equals(mapElement.getKey().toString()))
                            dashBoardBoxItem.setSelected(true);
                        list.add(dashBoardBoxItem);
                    }
//                    JSONConverter jsonConverter = new JSONConverter();
//                    CommonUtils.showLogMessage("e", "whole gson", jsonConverter.objectToJson(list));
                    if (list.size() == 1) {
                        if(cvStages!=null)
                        cvStages.setVisibility(View.GONE);
                        onItemClick(list.get(0));
                    } else {
                        if (stageId == null) {
                            if (!list.isEmpty()) {
                                list.get(0).setSelected(true);
                            }
                        }
                        StageListAdapter adapter = new StageListAdapter(list);
//                    if (recyclerStages == null)
//                        recyclerStages = getView().findViewById(R.id.recyclerStages);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isSelected()) {
                                rvStages.smoothScrollToPosition(i);
                                break;
                            }

                        }
                        rvStages.setAdapter(adapter);

                        adapter.setListener(this);
                    }


                }

            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentAssignedToMeBinding = getViewDataBinding();
        if (getArguments() != null) {
            String str = getArguments().getString(AppConstants.Extra.EXTRA_CATEGORIES);
            userGeoReq = getArguments().getBoolean(AppConstants.Extra.GEO_FILTER,false);
            Log.e("userGeoReqNew",""+userGeoReq);
            categoryMap = new Gson().fromJson(str, new TypeToken<HashMap<String, String>>() {
            }.getType());
            if (categoryMap != null && categoryMap.containsKey("categoryId"))
                categoryId = categoryMap.get("categoryId");
            fromDate = getArguments().getLong(AppConstants.Extra.FROM_DATE);
            toDate = getArguments().getLong(AppConstants.Extra.FROM_TO);
            showHideFilter(categoryId);

        }
        setUp();
        List<TaskStatus> statusList = new ArrayList<>();
        Map<String, ChannelConfig> channelConfigMap = preferencesHelper.getWorkFlowCategoriesListChannel();
        if (categoryId != null && channelConfigMap != null && channelConfigMap.containsKey(categoryId)) {
            ChannelConfig channelConfig = channelConfigMap.get(categoryId);
            if (channelConfig != null && channelConfig.getChannelSetting() != null) {
                ChannelSetting channelSetting = channelConfig.getChannelSetting();
                if(channelSetting.getExeLocation()!=null&&channelSetting.getExeLocation()){
                    buddyRequest = new TaskRequest(statusList, BuddyInfo.LOCATION, categoryMap);
//                    if (channelSetting.getLocType() != null && channelSetting.getLocType().equals("FIXED")) {
//                        List<Hub> hubsList = preferencesHelper.getUserHubList();
//                        if(hubsList!=null&&!hubsList.isEmpty()){
//                            hubId=hubsList.get(0).getHubId();
//                        }
//                        performLocation();
//                    } else {
//                        hubId=null;
//                        cardLocation.setVisibility(View.GONE);
//                    }
                    List<Hub> hubsList = preferencesHelper.getUserHubList();
                    if(hubsList!=null&&!hubsList.isEmpty()){
                        hubId=hubsList.get(0).getHubId();
                    }
                    performLocation();
                }else{
                    hubId=null;
                    if(cardLocation!=null)
                    cardLocation.setVisibility(View.GONE);
                    buddyRequest = new TaskRequest(statusList, BuddyInfo.ASSIGNED_TO_ME, categoryMap);
                }
            }
        }else{
            buddyRequest = new TaskRequest(statusList, BuddyInfo.ASSIGNED_TO_ME, categoryMap);
        }
        buddyRequest.setUserGeoReq(userGeoReq);


        if ((preferencesHelper.getFilterMap() != null)) {
            if (preferencesHelper.getFilterMap().containsKey(categoryId)) {
                SaveFilterData saveFilterData = preferencesHelper.getFilterMap().get(categoryId);
                if (saveFilterData != null) {
                    buddyRequest.setRegionId(saveFilterData.getRegionId());
                    buddyRequest.setStateId(saveFilterData.getStateId());
                    buddyRequest.setCityId(saveFilterData.getCityId());
                    hubIdStr = saveFilterData.getHubId();
                    if (hubIdStr != null && !hubIdStr.isEmpty()) {
                        hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
                    }
                    buddyRequest.setHubIds(hubIds);
                }

            }
        }
        if (getArguments() != null) {
            String parentTaskId = getArguments().getString(AppConstants.Extra.EXTRA_PAREN_TASK_ID);
            String referenceId = getArguments().getString(AppConstants.Extra.EXTRA_PARENT_REF_ID);
            buddyRequest.setParentTaskId(parentTaskId);
            buddyRequest.setReferenceId(referenceId);
        }
        if (categoryMap != null && categoryMap.containsKey("loadBy")) {
            String loadBy = categoryMap.get("loadBy");
            BuddyInfo buddyInfo = BuddyInfo.valueOf(loadBy);
            buddyRequest.setLoadBy(buddyInfo);
            cardFromDate.setVisibility(View.GONE);
        } else {
            buddyRequest.setFrom(fromDate);
            buddyRequest.setTo(toDate);
            cardFromDate.setVisibility(View.VISIBLE);

        }
        if (categoryMap != null && categoryMap.containsKey("categoryId")) {
            mAssignedtoMeAdapter.setCategoryId(categoryMap.get("categoryId"));
        }


        etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i(TAG, "Enter pressed");
                    String refrenceId = etSearch.getText().toString().trim();
                    if (!refrenceId.isEmpty()) {
                        buddyRequest.setReferenceId(refrenceId);

                    } else {
                        buddyRequest.setReferenceId(null);
                    }
                }
                return false;
            }
        });
        tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate)+" - "+DateTimeUtil.getParsedDate(toDate));
        subscribeToLiveData();
        perFormStageTask(categoryMap);

    }


    @Override
    public void setUserVisibleHint(boolean isUserVisible) {
        super.setUserVisibleHint(isUserVisible);
        // when fragment visible to user and view is not null then enter here.
        if (isUserVisible && getRootView() != null) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        isLastPage = false;
        currentPage = PAGE_START;
        PagingData pagingData = new PagingData();
        pagingData.setDatalimit(10);
        pagingData.setPageOffset(0);
        pagingData.setPageIndex(currentPage);
        buddyRequest.setPaginationData(pagingData);
        if ((preferencesHelper.getFilterMap() != null)) {
            if (preferencesHelper.getFilterMap().containsKey(categoryId)) {
                SaveFilterData saveFilterData = preferencesHelper.getFilterMap().get(categoryId);
                if (saveFilterData != null) {
                    buddyRequest.setRegionId(saveFilterData.getRegionId());
                    buddyRequest.setStateId(saveFilterData.getStateId());
                    buddyRequest.setCityId(saveFilterData.getCityId());
                    hubIdStr = saveFilterData.getHubId();
                    if (hubIdStr != null && !hubIdStr.isEmpty()) {
                        hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
                    }
                    buddyRequest.setHubIds(hubIds);
                    buddyRequest.setUserGeoReq(true);
                }

            }
        }
        //buddyRequest.setCategoryId(categoryMap.get("categoryId"));

        if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
            api = TrackiApplication.getApiMap().get(ApiType.TASKS);
            if (api != null) {
                //api.setAppendWithKey("ASSIGNED_TO_ME");
                if(buddyRequest.getLoadBy()!=null) {
                    api.setAppendWithKey(buddyRequest.getLoadBy().name());
                } else{
                    api.setAppendWithKey("ASSIGNED_TO_ME");
                }
            }
            if(rvAssignedToMe!=null)
                rvAssignedToMe.setVisibility(View.GONE);
            mAssignedtoMeAdapter.clearItems();
            showLoading();
            buddyRequest.setUserGeoReq(userGeoReq);
            mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
        }

        // getBaseActivity().requestCurrentLocation(locationCallback);
        //register receiver for listening the refresh list event
        LocalBroadcastManager.getInstance(getBaseActivity())
                .registerReceiver(refreshTaskListReceiver, new IntentFilter(AppConstants.ACTION_REFRESH_TASK_LIST));


        CommonUtils.showLogMessage("e", "Accessid", TrackThat.getAccessId());

    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the event when the user stop this fragment.
        LocalBroadcastManager.getInstance(getBaseActivity()).unregisterReceiver(refreshTaskListReceiver);
    }

    private void setUp() {

        mSwipeRefreshLayout = mFragmentAssignedToMeBinding.swipeContainer;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        rvStages = mFragmentAssignedToMeBinding.rvStages;
        cvStages = mFragmentAssignedToMeBinding.cardstages;
        cardFromDate = mFragmentAssignedToMeBinding.cardDateSearch;
        tvFromDate = mFragmentAssignedToMeBinding.tvFromDate;
        cardFromDate.setOnClickListener(this);
        tvFromDate.setOnClickListener(this);
        etSearch = mFragmentAssignedToMeBinding.etSearch;
        cardLocation =mFragmentAssignedToMeBinding.cardLocation;
        locationSpinner = mFragmentAssignedToMeBinding.spinnerLocation;
        mButtonSubmit = mFragmentAssignedToMeBinding.btnSubmit;

        mButtonSubmit.setOnClickListener(this);

    }

    private void setRecyclerView() {
        if (rvAssignedToMe == null) {
            rvAssignedToMe = mFragmentAssignedToMeBinding.rvAssignedToMe;
            mLayoutManager.setOrientation(RecyclerView.VERTICAL);
            rvAssignedToMe.setLayoutManager(mLayoutManager);
            rvAssignedToMe.setItemAnimator(new DefaultItemAnimator());
            rvAssignedToMe.setAdapter(mAssignedtoMeAdapter);
            //rvAssignedToMe.setAdapter(mAdapter);
            /**
             * add scroll listener while user reach in bottom load more will call
             */


        }
        if(rvAssignedToMe!=null)
            rvAssignedToMe.setVisibility(View.VISIBLE);
        rvAssignedToMe.addOnScrollListener(new PaginationListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                showLoading();
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiApplication.getApiMap().get(ApiType.TASKS);

                if (api != null) {
                    if(buddyRequest.getLoadBy()!=null) {
                        api.setAppendWithKey(buddyRequest.getLoadBy().name());
                    } else{
                        api.setAppendWithKey("ASSIGNED_TO_ME");
                    }
                }
                if (rvAssignedToMe != null)
                    rvAssignedToMe.setVisibility(View.VISIBLE);
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset((currentPage - 1) * 10);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setUserGeoReq(userGeoReq);
                buddyRequest.setPaginationData(pagingData);
                mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        mAssignedToMeViewModel.addItemsToList(mAssignedToMeViewModel.getTaskListLiveData().getValue());
        mAssignedtoMeAdapter.addItems(mAssignedToMeViewModel.getBuddyObservableArrayList());
    }

    private void subscribeToLiveData() {
        mAssignedToMeViewModel.getTaskListLiveData().observe(getBaseActivity(), string -> {
            if (string != null) {
                mAssignedToMeViewModel.addItemsToList(string);
            }
        });
    }

    @Override
    public void onCallClick(String mobile) {
        CommonUtils.openDialer(getBaseActivity(), mobile);
    }

    @Override
    public void onItemClick(Task task, int position) {

        Intent intent = new Intent(getBaseActivity(), NewTaskDetailsActivity.class);
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId());
        //intent.putExtra(AppConstants.Extra.EXTRA_TASK, task);
        intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, task.getAllowSubTask());
        intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, task.getSubCategoryIds());
        intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, task.getReferenceId());
        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate);
        intent.putExtra(AppConstants.Extra.FROM_TO, toDate);
        intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_TO_ME);
        startActivity(intent);

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
                        hubId = hubIds.get(position);
                        preferencesHelper.setSelectedLocation(hubId);
                        buddyRequest.setPlaceId(hubId);
                        onRefresh();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                cardLocation.setVisibility(View.VISIBLE);
            }else{
                cardLocation.setVisibility(View.GONE);
                hubId = hubsList.get(0).getHubId();
                preferencesHelper.setSelectedLocation(hubId);
                buddyRequest.setPlaceId(hubId);
            }

        }else {
            hubId =null;
            preferencesHelper.setSelectedLocation(hubId);
            cardLocation.setVisibility(View.GONE);
        }

    }




    @Override
    public void onDetailsTaskClick(Task task) {
        startActivityForResult(
                DynamicFormActivity.Companion.newIntent(getBaseActivity())
                        /*.putExtra(AppConstants.Extra.EXTRA_FORM_TYPE, callToActions.getName())*/
                        .putExtra(AppConstants.Extra.EXTRA_TCF_ID, task.getTcfId())
                        .putExtra(AppConstants.Extra.EXTRA_FORM_ID, task.getTcfId())
                        .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId())
                        .putExtra(AppConstants.Extra.HIDE_BUTTON, true)
                        .putExtra(AppConstants.Extra.EXTRA_IS_EDITABLE, false),
                AppConstants.REQUEST_CODE_DYNAMIC_FORM);
    }


    private void showHideFilter(String categoryId) {
        List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();

        if (categoryId != null) {
            WorkFlowCategories workFlowCategories = new WorkFlowCategories();
            workFlowCategories.setCategoryId(categoryId);
            if (listCategory.contains(workFlowCategories)) {
                int position = listCategory.indexOf(workFlowCategories);
                if (position != -1) {
                    WorkFlowCategories myCatData = listCategory.get(position);
                    setHasOptionsMenu(myCatData.getAllowGeography());

                }
            }

        }
    }

    @Override
    public void handleResponse(@Nullable ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        // Stopping swipe refresh
        mSwipeRefreshLayout.setRefreshing(false);
        isLoading = false;

        if (CommonUtils.handleResponse(callback, error, result, getBaseActivity())) {
            if (null != getBaseActivity()) {
                getBaseActivity().runOnUiThread(() -> {
                    TaskListing taskListing = new Gson().fromJson(String.valueOf(result), TaskListing.class);
                    if (taskListing.getPaginationData() != null) {

                        List<Task> list = taskListing.getTasks();
                        mAssignedToMeViewModel.getTaskListLiveData().setValue(list);
                        setRecyclerView();
                        CommonUtils.showLogMessage("e", "adapter total_count =>",
                                "" + mAssignedtoMeAdapter.getItemCount());
                        CommonUtils.showLogMessage("e", "fetch total_count =>",
                                "" + taskListing.getPaginationData().getDataCount());
                        if (taskListing.getPaginationData().getDataCount() == mAssignedtoMeAdapter.getItemCount()) {
                            isLastPage = true;
                        }
                    }

//                    setFilterItems(list, item);
                });
            }
        }else{
            if(rvAssignedToMe!=null)
                rvAssignedToMe.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void handleExecuteUpdateResponse(ApiCallback apiCallback, Object result, APIError error) {
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_FILTER_USER) {
            if (resultCode == RESULT_OK) {
                if ((preferencesHelper.getFilterMap() != null)) {
                    if (preferencesHelper.getFilterMap().containsKey(categoryId)) {
                        SaveFilterData saveFilterData = preferencesHelper.getFilterMap().get(categoryId);
                        if (saveFilterData != null) {
                            buddyRequest.setRegionId(saveFilterData.getRegionId());
                            buddyRequest.setStateId(saveFilterData.getStateId());
                            buddyRequest.setCityId(saveFilterData.getCityId());
                            hubIdStr = saveFilterData.getHubId();
                            if (hubIdStr != null && !hubIdStr.isEmpty()) {
                                hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
                            }
                            buddyRequest.setHubIds(hubIds);
                            buddyRequest.setUserGeoReq(true);
                        }

                    }
                }
                isLastPage = false;
                currentPage = PAGE_START;
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset(0);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setPaginationData(pagingData);
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiApplication.getApiMap().get(ApiType.TASKS);
                if (api != null) {
                    if(buddyRequest.getLoadBy()!=null) {
                        api.setAppendWithKey(buddyRequest.getLoadBy().name());
                    } else{
                        api.setAppendWithKey("ASSIGNED_TO_ME");
                    }
                }
                if(rvAssignedToMe!=null)
                    rvAssignedToMe.setVisibility(View.GONE);
                mAssignedtoMeAdapter.clearItems();
                showLoading();
                buddyRequest.setUserGeoReq(userGeoReq);
                mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                regionId = null;
                hubIds = null;
                hubIdStr = null;
                stateId = null;
                cityId = null;
                buddyRequest.setRegionId(regionId);
                buddyRequest.setHubIds(hubIds);
                buddyRequest.setStateId(stateId);
                buddyRequest.setCityId(cityId);
                buddyRequest.setUserGeoReq(false);
                isLastPage = false;
                currentPage = PAGE_START;
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset(0);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setPaginationData(pagingData);
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiApplication.getApiMap().get(ApiType.TASKS);
                if (api != null) {
                    if(buddyRequest.getLoadBy()!=null) {
                        api.setAppendWithKey(buddyRequest.getLoadBy().name());
                    } else{
                        api.setAppendWithKey("ASSIGNED_TO_ME");
                    }
                }
                if(rvAssignedToMe!=null)
                    rvAssignedToMe.setVisibility(View.GONE);
                mAssignedtoMeAdapter.clearItems();
                showLoading();
                buddyRequest.setUserGeoReq(userGeoReq);
                mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
            }
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        isLastPage = false;
        currentPage = PAGE_START;
        PagingData pagingData = new PagingData();
        pagingData.setDatalimit(10);
        pagingData.setPageOffset(0);
        pagingData.setPageIndex(currentPage);
        buddyRequest.setPaginationData(pagingData);
        //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
        api = TrackiApplication.getApiMap().get(ApiType.TASKS);
        if (api != null) {
            if(buddyRequest.getLoadBy()!=null) {
                api.setAppendWithKey(buddyRequest.getLoadBy().name());
            } else{
                api.setAppendWithKey("ASSIGNED_TO_ME");
            }
        }
        if(rvAssignedToMe!=null)
            rvAssignedToMe.setVisibility(View.GONE);
        mAssignedtoMeAdapter.clearItems();
        showLoading();
        buddyRequest.setUserGeoReq(userGeoReq);
        mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
    }

    public void setListenerClick(setAssignToMeChatListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onItemClick(@NotNull DashBoardBoxItem response) {
        if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
            String tvDate = tvFromDate.getText().toString().trim();
            api = TrackiApplication.getApiMap().get(ApiType.TASKS);
            if (!tvDate.isEmpty()) {
                if (mAssignedtoMeAdapter.getList() != null) {
                    if(rvAssignedToMe!=null)
                        rvAssignedToMe.setVisibility(View.GONE);
                    mAssignedtoMeAdapter.clearItems();
                }else{
                    if(rvAssignedToMe!=null)
                        rvAssignedToMe.setVisibility(View.GONE);
                }
                showLoading();
                buddyRequest.setUserGeoReq(userGeoReq);
                buddyRequest.setStageId(response.getStageId());
                if(rvAssignedToMe!=null)
                    rvAssignedToMe.setVisibility(View.GONE);
                mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
            } else {
                TrackiToast.Message.showShort(requireContext(), "Please select to date");
            }
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvFromDate || id == R.id.cardFromDate) {
            openDatePicker();
        } else if (id == R.id.btnSubmit) {
            hitApiAndGetData();
        }
    }

    private void hitApiAndGetData() {
        if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
            String refrenceId = etSearch.getText().toString().trim();
            String tvDate = tvFromDate.getText().toString().trim();
            if (!tvDate.isEmpty()) {
                if (fromDate <= toDate){
                    if (!refrenceId.isEmpty()) {
                        if(rvAssignedToMe!=null)
                            rvAssignedToMe.setVisibility(View.GONE);
                        buddyRequest.setReferenceId(refrenceId);
                        mAssignedtoMeAdapter.clearItems();
                        buddyRequest.setFrom(fromDate);
                        buddyRequest.setTo(toDate);
                        buddyRequest.setUserGeoReq(userGeoReq);
                        showLoading();
                        mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
                    } else {
                        if(rvAssignedToMe!=null)
                            rvAssignedToMe.setVisibility(View.GONE);
                        buddyRequest.setReferenceId(null);
                        mAssignedtoMeAdapter.clearItems();
                        showLoading();
                        buddyRequest.setFrom(fromDate);
                        buddyRequest.setTo(toDate);
                        buddyRequest.setUserGeoReq(userGeoReq);
                        mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
                    }
                }else {
                    if (getBaseActivity() != null)
                        TrackiToast.Message.showShort(getBaseActivity(), "From Date can't be greater than To Date");
                }

            } else {
                TrackiToast.Message.showShort(requireContext(), "Please select to date");
            }
        }

    }

    public interface setAssignToMeChatListener {
        public void chatAssignClick(String buddyId, String buddyname);

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
                                    hitApiAndGetData();

                                });

                    });
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            Intent intent = TaskFilterActivity.Companion.newIntent(getBaseActivity());
//                if (regionId != null)
//                    intent.putExtra("regionId", regionId);
//                if (hubIdStr != null)
//                    intent.putExtra("hubIdStr", hubIdStr);
//                if (stateId != null)
//                    intent.putExtra("stateId", stateId);
//                if (cityId != null)
//                    intent.putExtra("cityId", cityId);
            if (categoryId != null)
                intent.putExtra("categoryId", categoryId);
            intent.putExtra("from", AppConstants.TASK);
            startActivityForResult(intent, AppConstants.REQUEST_CODE_FILTER_USER);
        }
        return super.onOptionsItemSelected(item);
    }


}