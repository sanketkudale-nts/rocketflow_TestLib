package com.tracki.ui.tasklisting.ihaveassigned;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

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
import com.tracki.data.model.response.config.AssigneeDetail;
import com.tracki.data.model.response.config.CallToActions;
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
import com.tracki.databinding.FragmentIHaveAssignedBinding;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.common.DoubleButtonDialog;
import com.tracki.ui.common.OnClickListener;
import com.tracki.ui.dynamicform.DynamicFormActivity;
import com.tracki.ui.inventory.InventoryActivity;
import com.tracki.ui.main.filter.TaskFilterActivity;
import com.tracki.ui.newcreatetask.NewCreateTaskActivity;
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
public class IhaveAssignedFragment extends BaseFragment<FragmentIHaveAssignedBinding, IhaveAssignedViewModel>
        implements IhaveAssignedNavigator, TaskItemClickListener, SwipeRefreshLayout.OnRefreshListener, StageListAdapter.DashBoardListener, View.OnClickListener {

    private static final String TAG = IhaveAssignedFragment.class.getSimpleName();
    @Inject
    TaskListingAdapter mIhaveAssignedAdapter;
    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    PreferencesHelper preferencesHelper;
    private CardView cvStages;
    private FragmentIHaveAssignedBinding mFragmentIHaveAssignedBinding;
    private Api api;
    private TaskRequest buddyRequest;
    private IhaveAssignedViewModel mIhaveAssignedViewModel;

    private Map<String, String> categoryMap;
    private CallToActions callToActions = null;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private setIHaveChatListener mListener;
    private long fromDate;
    private long toDate;
    private TextView tvFromDate;
    private CardView cardFromDate;
    private ImageButton mButtonSubmit;
    private EditText etSearch;
    private Date today;
    private String stageId = null;
    private String categoryId = null;
    private MediaPlayer mediaPlayer = null;
    LinkedHashMap<String, String> mStageNameMap = null;
    private String regionId;
    private List<String> hubIds;
    private String stateId;
    private String cityId;
    private String hubIdStr;
    private boolean isMerchantTab;
    private boolean userGeoReq = false;

    public static IhaveAssignedFragment newInstance(String value, long fromDate, long toDate, boolean isMerchantTab, boolean geoReq) {
        Bundle args = new Bundle();
        args.putString(AppConstants.Extra.EXTRA_CATEGORIES, value);
        args.putLong(AppConstants.Extra.FROM_DATE, fromDate);
        args.putLong(AppConstants.Extra.FROM_TO, toDate);
        args.putBoolean(AppConstants.Extra.GEO_FILTER,geoReq);
        args.putBoolean(AppConstants.Extra.IS_MERCHANT_TAB, isMerchantTab);
        IhaveAssignedFragment fragment = new IhaveAssignedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView rvIhaveAssigned;
    private RecyclerView rvStages;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_i_have_assigned;
    }

    @Override
    public IhaveAssignedViewModel getViewModel() {
//        mIhaveAssignedViewModel = new ViewModelProvider(this).get(IhaveAssignedViewModel.class);
        mIhaveAssignedViewModel = ViewModelProviders.of(this, mViewModelFactory).get(IhaveAssignedViewModel.class);
        return mIhaveAssignedViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        mIhaveAssignedViewModel.setNavigator(this);
        mIhaveAssignedAdapter.setListener(this);


        List<TaskStatus> statusList = new ArrayList<>();


        if (getArguments() != null) {
            String str = getArguments().getString(AppConstants.Extra.EXTRA_CATEGORIES);
            isMerchantTab = getArguments().getBoolean(AppConstants.Extra.IS_MERCHANT_TAB, false);
            userGeoReq = getArguments().getBoolean(AppConstants.Extra.GEO_FILTER,false);
            Log.e("userGeoReq",""+userGeoReq);
            categoryMap = new Gson().fromJson(str, new TypeToken<HashMap<String, String>>() {
            }.getType());
            if (categoryMap != null && categoryMap.containsKey("categoryId"))
                categoryId = categoryMap.get("categoryId");
            fromDate = getArguments().getLong(AppConstants.Extra.FROM_DATE);
            toDate = getArguments().getLong(AppConstants.Extra.FROM_TO);
            showHideFilter(categoryId);
            // CommonUtils.showLogMessage("e","categoryId",categoryMap.get("categoryId"));
        }
        if (isMerchantTab)
            buddyRequest = new TaskRequest(statusList, BuddyInfo.ASSIGNED_TO_MERCHANT, categoryMap);
        else {
            buddyRequest = new TaskRequest(statusList, BuddyInfo.ASSIGNED_BY_ME, categoryMap);
        }
        buddyRequest.setFrom(fromDate);
        buddyRequest.setTo(toDate);
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

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentIHaveAssignedBinding = getViewDataBinding();
        mIhaveAssignedAdapter.setAssignedToTask(false);
        setUp();
        tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));
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
                    buddyRequest.setUserGeoReq(userGeoReq);
                }

            }
        }
        if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
            api = TrackiApplication.getApiMap().get(ApiType.TASKS);
            if (api != null) {
                api.setAppendWithKey("ASSIGNED_BY_ME");
            }
            if(rvIhaveAssigned!=null)
                rvIhaveAssigned.setVisibility(View.GONE);
            mIhaveAssignedAdapter.clearItems();
            showLoading();
            buddyRequest.setUserGeoReq(userGeoReq);
            mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
        } else {
//            if(getBaseActivity()!=null)
//            TrackiToast.Message.showShort(getBaseActivity(), getString(R.string.please_check_your_internet_connection_you_are_offline_now));

        }

    }


    private void setUp() {

        // SwipeRefreshLayout
        mSwipeRefreshLayout = mFragmentIHaveAssignedBinding.swipeContainer;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        rvStages = mFragmentIHaveAssignedBinding.rvStages;
        cvStages = mFragmentIHaveAssignedBinding.cardstages;
        cardFromDate = mFragmentIHaveAssignedBinding.cardFromDate;
        tvFromDate = mFragmentIHaveAssignedBinding.tvFromDate;
        cardFromDate.setOnClickListener(this);
        tvFromDate.setOnClickListener(this);
        etSearch = mFragmentIHaveAssignedBinding.etSearch;
        cardFromDate = mFragmentIHaveAssignedBinding.cardFromDate;

        if (categoryMap != null && categoryMap.containsKey("categoryId")) {
            String label = CommonUtils.getReffranceLabel(preferencesHelper, categoryMap.get("categoryId"));
            //  mIhaveAssignedAdapter.setReffLabel(label);
            mIhaveAssignedAdapter.setCategoryId(categoryMap.get("categoryId"));
            CommonUtils.showLogMessage("e", "label=>", "=>" + label);
            if (label.length() > 9)
                label = label.substring(0, 8) + "...";
            etSearch.setHint(label);
            etSearch.setHint(label);
        }
        mButtonSubmit = mFragmentIHaveAssignedBinding.btnSubmit;
        cardFromDate.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
        etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i(TAG, "Enter pressed");
                    String refrenceId = etSearch.getText().toString().trim();
                    buddyRequest.setUserGeoReq(userGeoReq);
                    if (!refrenceId.isEmpty()) {
                        buddyRequest.setReferenceId(refrenceId);
//                        mAssignedtoMeAdapter.clearItems();
//                        showLoading();
//                        mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
                    } else {
                        buddyRequest.setReferenceId(null);
//                        mAssignedtoMeAdapter.clearItems();
//                        showLoading();
//                        mAssignedToMeViewModel.getTaskList(httpManager, api, buddyRequest);
                    }
                }
                return false;
            }
        });

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
                    JSONConverter jsonConverter = new JSONConverter();
                    CommonUtils.showLogMessage("e", "whole gson", jsonConverter.objectToJson(list));

                    if (list.size() == 1) {
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

    private void setRecyclerView() {
        if (rvIhaveAssigned == null) {
            rvIhaveAssigned = mFragmentIHaveAssignedBinding.rvIhaveAssigned;
            mLayoutManager.setOrientation(RecyclerView.VERTICAL);
            rvIhaveAssigned.setLayoutManager(mLayoutManager);
            rvIhaveAssigned.setItemAnimator(new DefaultItemAnimator());
            rvIhaveAssigned.setAdapter(mIhaveAssignedAdapter);
        }
        if (rvIhaveAssigned != null)
            rvIhaveAssigned.setVisibility(View.VISIBLE);
        rvIhaveAssigned.addOnScrollListener(new PaginationListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                Log.e("userGeoReq",userGeoReq+" - check");
                buddyRequest.setUserGeoReq(userGeoReq);
                showLoading();
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiApplication.getApiMap().get(ApiType.TASKS);

                if (api != null) {
                    api.setAppendWithKey("ASSIGNED_BY_ME");
                }
                if (rvIhaveAssigned != null)
                    rvIhaveAssigned.setVisibility(View.VISIBLE);
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset((currentPage - 1) * 10);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setPaginationData(pagingData);
                buddyRequest.setUserGeoReq(userGeoReq);
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
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
        mIhaveAssignedViewModel.addItemsToList(mIhaveAssignedViewModel.getTaskListLiveData().getValue());
        mIhaveAssignedAdapter.addItems(mIhaveAssignedViewModel.getBuddyObservableArrayList());
//        if (mIhaveAssignedAdapter != null) {
//            if (!preferencesHelper.getActiveTaskIdId().isEmpty())
//                preferencesHelper.saveCurrentTask(mIhaveAssignedAdapter.getTaskByTaskId(preferencesHelper.getActiveTaskIdId()));
//        }
    }

    private void subscribeToLiveData() {
        mIhaveAssignedViewModel.getTaskListLiveData().observe(getBaseActivity(), list -> {
            if (list != null) {
                mIhaveAssignedViewModel.addItemsToList(list);
            }
        });
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
        hideLoading();
        // Stopping swipe refresh
        isLoading = false;
        mSwipeRefreshLayout.setRefreshing(false);

        if (getBaseActivity() != null){
        if (CommonUtils.handleResponse(callback, error, result, getBaseActivity())) {
            getBaseActivity().runOnUiThread(() -> {
                TaskListing taskListing = new Gson().fromJson(String.valueOf(result), TaskListing.class);
//                stateAdapter.makeAllSelected();
                List<Task> list = taskListing.getTasks();
                mIhaveAssignedViewModel.getTaskListLiveData().setValue(list);
                setRecyclerView();
                CommonUtils.showLogMessage("e", "adapter total_count =>",
                        "" + mIhaveAssignedAdapter.getItemCount());
                CommonUtils.showLogMessage("e", "fetch total_count =>",
                        "" + taskListing.getPaginationData().getDataCount());
                if (taskListing.getPaginationData().getDataCount() == mIhaveAssignedAdapter.getItemCount()) {
                    isLastPage = true;
                }
            });
        } else {
            if (rvIhaveAssigned != null)
                rvIhaveAssigned.setVisibility(View.VISIBLE);
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
        buddyRequest.setUserGeoReq(userGeoReq);
        api = TrackiApplication.getApiMap().get(ApiType.TASKS);
        if (api != null) {
            api.setAppendWithKey("ASSIGNED_BY_ME");
        }
        if (rvIhaveAssigned != null)
            rvIhaveAssigned.setVisibility(View.GONE);
        mIhaveAssignedAdapter.clearItems();
        showLoading();
        mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
    }

//    @Override
//    public void onCallClick(Task task, int position) {
//        if (task.getAssignedToDetails() != null && !task.getAssignedToDetails().isEmpty()) {
//            CommonUtils.showLogMessage("e", "task.getAssignmentType()", task.getAssignmentType());
//            if (task.getAssignmentType() != null && task.getAssignmentType().equals("USER")) {
//                AssigneeDetail assigneeDetail = task.getAssignedToDetails().get(0);
//                if (assigneeDetail != null && assigneeDetail.getMobile() != null) {
//                    String mobile = assigneeDetail.getMobile();
//                    CommonUtils.openDialer(getBaseActivity(), mobile);
//                }
//
//            }
//
//        }
//
//    }

    @Override
    public void onItemClick(Task task, int position) {
        Intent intent = new Intent(getBaseActivity(), NewTaskDetailsActivity.class);
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId());
        intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, task.getAllowSubTask());
        intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, task.getSubCategoryIds());
        intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, task.getReferenceId());
        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_BY_ME);
        intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate);
        intent.putExtra(AppConstants.Extra.FROM_TO, toDate);
        startActivity(intent);
//        startActivity(TaskDetailActivity.Companion.newIntent(getBaseActivity())
//                .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId()));
    }

    @Override
    public void onCallClick(String mobile) {
        CommonUtils.openDialer(getBaseActivity(), mobile);


    }


    @Override
    public void onDetailsTaskClick(Task task) {
        startActivityForResult(
                DynamicFormActivity.Companion.newIntent(getBaseActivity())
                        /*.putExtra(AppConstants.Extra.EXTRA_FORM_TYPE, callToActions.getName())*/
                        .putExtra(AppConstants.Extra.EXTRA_FORM_ID, task.getTcfId())
                        .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId())
                        .putExtra(AppConstants.Extra.EXTRA_TCF_ID, task.getTcfId())
                        .putExtra(AppConstants.Extra.HIDE_BUTTON, true)
                        /* .putExtra(AppConstants.Extra.EXTRA_CTA_ID, callToActions.getId())*/
                        .putExtra(AppConstants.Extra.EXTRA_IS_EDITABLE, false),
                AppConstants.REQUEST_CODE_DYNAMIC_FORM);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_FILTER_USER) {
            if (resultCode == RESULT_OK) {
//                regionId = data.getStringExtra("regionId");
//                 hubIdStr = data.getStringExtra("hubId");
//                if(hubIdStr!=null&&!hubIdStr.isEmpty()){
//                    hubIds= Arrays.asList(hubIdStr.split("\\s*,\\s*"));
//                }
//                stateId = data.getStringExtra("stateId");
//                cityId = data.getStringExtra("cityId");
//                buddyRequest.setRegionId(regionId);
//                buddyRequest.setHubIds(hubIds);
//                buddyRequest.setStateId(stateId);
//                buddyRequest.setCityId(cityId);
//                buddyRequest.setUserGeoReq(true);
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
                            buddyRequest.setUserGeoReq(userGeoReq);
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
                buddyRequest.setUserGeoReq(userGeoReq);
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiApplication.getApiMap().get(ApiType.TASKS);
                if (api != null) {
                    api.setAppendWithKey("ASSIGNED_TO_ME");
                }
                if (rvIhaveAssigned != null)
                    rvIhaveAssigned.setVisibility(View.GONE);
                mIhaveAssignedAdapter.clearItems();
                showLoading();
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
            } else {
                regionId = null;
                hubIds = null;
                stateId = null;
                cityId = null;
                buddyRequest.setRegionId(regionId);
                buddyRequest.setHubIds(hubIds);
                buddyRequest.setStateId(stateId);
                buddyRequest.setCityId(cityId);
                buddyRequest.setUserGeoReq(userGeoReq);
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
                    api.setAppendWithKey("ASSIGNED_BY_ME");
                }
                if (rvIhaveAssigned != null)
                    rvIhaveAssigned.setVisibility(View.GONE);
                mIhaveAssignedAdapter.clearItems();
                showLoading();
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
            }
        }

    }


    @Override
    public void handleExecuteUpdateResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();

    }


    public void setListenerClick(setIHaveChatListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onItemClick(@NotNull DashBoardBoxItem response) {
        if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
            String tvDate = tvFromDate.getText().toString().trim();
            api = TrackiApplication.getApiMap().get(ApiType.TASKS);
            if (!tvDate.isEmpty()) {
                if (mIhaveAssignedAdapter.getList() != null) {
                    if (rvIhaveAssigned != null)
                        rvIhaveAssigned.setVisibility(View.GONE);


                    mIhaveAssignedAdapter.clearItems();
                }else{
                    if (rvIhaveAssigned != null)
                        rvIhaveAssigned.setVisibility(View.GONE);
                }
                showLoading();
//                mIhaveAssignedViewModel.getTaskListLiveData().setValue(new ArrayList<>());
                buddyRequest.setStageId(response.getStageId());
                buddyRequest.setUserGeoReq(userGeoReq);
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
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
            hitApiAndGetTask();
        }

    }

    private void hitApiAndGetTask() {
        if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
            String refrenceId = etSearch.getText().toString().trim();
            String tvTodate = tvFromDate.getText().toString().trim();
            if (!tvTodate.isEmpty()) {
                if (!refrenceId.isEmpty()) {
                    buddyRequest.setReferenceId(refrenceId);
                    buddyRequest.setUserGeoReq(userGeoReq);
                    mIhaveAssignedAdapter.clearItems();
                    if(rvIhaveAssigned!=null)
                        rvIhaveAssigned.setVisibility(View.GONE);
                    showLoading();
                    mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
                } else {
                    buddyRequest.setReferenceId(null);
                    buddyRequest.setUserGeoReq(userGeoReq);
                    if(rvIhaveAssigned!=null)
                        rvIhaveAssigned.setVisibility(View.GONE);
                    mIhaveAssignedAdapter.clearItems();
                    showLoading();
                    mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
                }
            } else {
                TrackiToast.Message.showShort(requireContext(), "Please select to date");
            }
        }


    }

    public interface setIHaveChatListener {
        void chatIHaveClick(String buddyId, String buddyname);

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

    private void playSoundStartTracking() {
        if (preferencesHelper.getVoiceAlertsTracking()) {
            if (getBaseActivity() != null) {
                mediaPlayer = MediaPlayer.create(getBaseActivity(), R.raw.tracking_started);
                mediaPlayer.start();
                new Handler().postDelayed(() -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                }, 2000);
            }
        }
    }

    private void playSoundStopTracking() {
        if (preferencesHelper.getVoiceAlertsTracking()) {
            if (getBaseActivity() != null) {
                mediaPlayer = MediaPlayer.create(getBaseActivity(), R.raw.tracking_stopped);
                mediaPlayer.start();
                new Handler().postDelayed(() -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                }, 2000);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mediaPlayer = null;
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
            if (regionId != null)
                intent.putExtra("regionId", regionId);
            if (hubIdStr != null)
                intent.putExtra("hubIdStr", hubIdStr);
            if (stateId != null)
                intent.putExtra("stateId", stateId);
            if (cityId != null)
                intent.putExtra("cityId", cityId);
            if (categoryId != null)
                intent.putExtra("categoryId", categoryId);
            intent.putExtra("from", AppConstants.TASK);
            startActivityForResult(intent, AppConstants.REQUEST_CODE_FILTER_USER);
        }
        return super.onOptionsItemSelected(item);
    }
}


