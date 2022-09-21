package com.rocketflyer.rocketflow.ui.buddylisting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.model.request.BuddiesRequest;
import com.tracki.data.model.request.DeleteBuddyRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.model.response.config.BuddyListResponse;
import com.tracki.data.model.response.config.BuddySelectionType;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityBuddyListingBinding;
import com.tracki.ui.addbuddy.AddBuddyActivity;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.buddylisting.BuddyListingNavigator;
import com.tracki.ui.buddylisting.BuddyListingViewModel;
import com.tracki.ui.buddyprofile.BuddyProfileActivity;
import com.tracki.ui.common.DoubleButtonDialog;
import com.tracki.ui.common.OnClickListener;
import com.tracki.ui.fleetlisting.FleetListingActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.BuddyInfo;
import com.tracki.utils.BuddyStatus;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.TrackiToast;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU;
import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU;
import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_MESSAGE_LIST;
import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_NAVIGATION_MENU;
import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY_NAME;
import static com.tracki.utils.AppConstants.Extra.EXTRA_BUNDLE;
import static com.tracki.utils.AppConstants.Extra.EXTRA_IS_YOU;
import static com.tracki.utils.AppConstants.Extra.EXTRA_SELECTED_BUDDY;

public class BuddyListingActivity extends BaseActivity<ActivityBuddyListingBinding, BuddyListingViewModel>
        implements BuddyListingNavigator, BuddyListingAdapter.AdapterEventListener {

    @Inject
    BuddyListingViewModel mBuddyListingViewModel;

    @Inject
    BuddyListingAdapter adapter;
    @Inject
    HttpManager httpManager;

    private RecyclerView rvStatistics;
    private boolean showSelectionIcon = false;
    private boolean isSelected = false;
    private BuddiesRequest buddyRequest;
    private Api api;
    private ActivityBuddyListingBinding mActivityBuddyListingBinding;
    private SearchView searchView;
    private Intent mainIntent;
    private Snackbar snackBar;

    public static Intent newIntent(Context context) {
        return new Intent(context, BuddyListingActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_buddy_listing;
    }

    @Override
    public BuddyListingViewModel getViewModel() {
        return mBuddyListingViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBuddyListingBinding = getViewDataBinding();
        mBuddyListingViewModel.setNavigator(this);
        mainIntent = getIntent();
        adapter.setListener(this, mainIntent);
        api = TrackiApplication.getApiMap().get(ApiType.BUDDIES);

        setUp();
        subscribeToLiveData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();

        TextView textView = (TextView) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/campton_book.ttf");
        textView.setTypeface(externalFont);
        textView.setTextColor(Color.BLACK);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
//                TrackiToast.MessageResponse.showShort(BuddyListingActivity.this, "SearchOnQueryTextSubmit: " + query);
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                if (mBuddyListingViewModel.getBuddyObservableArrayList().size() > 0) {
                    adapter.addFilter(query);
                } else {
                    TrackiToast.Message.showShort(BuddyListingActivity.this,
                            getString(R.string.cannot_performe_this_operation));
                }
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mBuddyListingViewModel.getBuddyObservableArrayList().size() > 0) {
                    adapter.addFilter(newText);
                } else {
                    TrackiToast.Message.showShort(BuddyListingActivity.this,
                            getString(R.string.cannot_performe_this_operation));
                }
                return false;
            }
        });
        return true;
    }

    private void setUp() {
        Toolbar toolbar = mActivityBuddyListingBinding.toolbar;
        //setSupportActionBar(toolbar);
        FloatingActionButton fabAddBuddy = mActivityBuddyListingBinding.fabAddBuddy;
        Button btnSubmit = mActivityBuddyListingBinding.btnSubmit;

        if (mainIntent != null) {
            if (mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_NAVIGATION_MENU)) {
                btnSubmit.setVisibility(View.GONE);
                fabAddBuddy.show();
                setToolbar(toolbar, getString(R.string.my_buddy));
                List<BuddyStatus> statusList = new ArrayList<>();
//                statusList.add(BuddyStatus.ALL);
                buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, false);
            } else if (mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU)) {
                List<BuddyStatus> statusList = new ArrayList<>();
                statusList.add(BuddyStatus.ON_TRIP);
                statusList.add(BuddyStatus.OFFLINE);
                statusList.add(BuddyStatus.IDLE);
                buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, true);
                setToolbar(toolbar, getString(R.string.select_buddy_to_create_task));
                showSelectionIcon = true;
                fabAddBuddy.hide();
                btnSubmit.setVisibility(View.VISIBLE);
            } else if (mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU)) {
                List<BuddyStatus> statusList = new ArrayList<>();
                statusList.add(BuddyStatus.ON_TRIP);
                statusList.add(BuddyStatus.OFFLINE);
                statusList.add(BuddyStatus.IDLE);
                if (mainIntent.hasExtra(EXTRA_IS_YOU)) {
                    buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, false);
                } else {
                    buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, true);
                }
                setToolbar(toolbar, getString(R.string.select_buddy));
                showSelectionIcon = true;
                isSelected = true;
                fabAddBuddy.hide();
                btnSubmit.setVisibility(View.VISIBLE);
            }
            else if (mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_MESSAGE_LIST)) {
                List<BuddyStatus> statusList = new ArrayList<>();
                statusList.add(BuddyStatus.ON_TRIP);
                statusList.add(BuddyStatus.OFFLINE);
                statusList.add(BuddyStatus.IDLE);
                if (mainIntent.hasExtra(EXTRA_IS_YOU)) {
                    buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, false);
                } else {
                    buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, true);
                }
                setToolbar(toolbar, getString(R.string.select_buddy));
                showSelectionIcon = true;
                isSelected = true;
                fabAddBuddy.hide();
                adapter.setSelectionType(BuddySelectionType.SINGLE);
                btnSubmit.setVisibility(View.VISIBLE);
            }

        }

        showLoading();
        //fetch hashMap of buddies and pass true to show check button.
        mBuddyListingViewModel.fetchBuddyList(buddyRequest, httpManager, api);
    }

    private void subscribeToLiveData() {
        mBuddyListingViewModel.getBuddyListLiveData().observe(this, buddyObservableArrayList -> {
            if (buddyObservableArrayList != null) {
                System.out.println("--------->" + buddyObservableArrayList.size());
                mBuddyListingViewModel.addItemsToList(buddyObservableArrayList);
                adapter.addItems(buddyObservableArrayList);
            }
        });
    }

    @Override
    public void onItemClick(Buddy buddy) {
        startActivityForResult(BuddyProfileActivity.newIntent(this)
                        .putExtra(AppConstants.Extra.EXTRA_BUDDY, buddy)
                        .putExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE, false),
                AppConstants.REQUEST_CODE_BUDDY_PROFILE);
    }

    @Override
    public void onItemLongClick(Buddy buddy) {
        DoubleButtonDialog dialog = new DoubleButtonDialog(this,
                true,
                null,
                getString(R.string.delete_buddy),
                getString(R.string.yes),
                getString(R.string.no),
                new OnClickListener() {
                    @Override
                    public void onClickCancel() {

                    }

                    @Override
                    public void onClick() {
                        Api api = TrackiApplication.getApiMap().get(ApiType.DELETE_BUDDY);
                        DeleteBuddyRequest delete = new DeleteBuddyRequest(buddy.getBuddyId());
                        mBuddyListingViewModel.deleteBuddy(httpManager, delete, api);
                    }
                });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mainIntent.hasExtra(AppConstants.Extra.IS_FROM_TASK_FILTER_EXTRA)) {
            setResult(RESULT_CANCELED);
        } else if (mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU)||mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_MESSAGE_LIST)) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_BUNDLE, mainIntent.getBundleExtra(EXTRA_BUNDLE));
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    @Override
    public void onSubmitClick() {
        List<Buddy> list = adapter.getItems();
        ArrayList<Buddy> mainList = new ArrayList<>();
        ArrayList<String> buddyIsList = new ArrayList<>();
        String buddyName = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && list.get(i).isSelected()) {
                mainList.add(list.get(i));

                buddyName = list.get(i).getName();
                buddyIsList.add(list.get(i).getBuddyId());
            }
        }
        if (mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU)||mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_MESSAGE_LIST)) {
            if (buddyIsList.size() > 0) {
                Intent intent = new Intent();
//                Bundle args = new Bundle();
//                args.putSerializable(EXTRA_ALL_BUDDY, mainList);
                intent.putExtra(EXTRA_BUDDY_NAME, buddyName);
                intent.putExtra(EXTRA_SELECTED_BUDDY, buddyIsList);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                TrackiToast.Message.showShort(this,
                        "Please select Buddy .");
            }
        } else {
            if (mainList.size() > 0) {
                startActivityForResult(FleetListingActivity.newIntent(this)
                                .putExtra(AppConstants.Extra.SELECTED_BUDDY_LIST_EXTRA, mainList)
                                .putExtra(AppConstants.Extra.SELECT_FLEET_EXTRA, true),
                        AppConstants.REQUEST_CODE_CREATE_TASK);
            } else {
                TrackiToast.Message.showShort(this,
                        "Please select Buddy from above hashMap.");
            }
        }
    }

    @Override
    public void openAddBuddyActivity() {
        startActivityForResult(AddBuddyActivity.newIntent(this),
                AppConstants.REQUEST_CODE_ADD_BUDDY);
    }

    @Override
    public void refreshBuddyList(ApiCallback apiCallback, Object result, APIError error) {
        showLoading();
        mBuddyListingViewModel.fetchBuddyList(buddyRequest, httpManager, api);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_ADD_BUDDY) {
            if (resultCode == RESULT_OK) {
//                showLoading();
                mBuddyListingViewModel.fetchBuddyList(buddyRequest, httpManager, api);
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_BUDDY_PROFILE) {
            if (resultCode == RESULT_OK) {
//                showLoading();
                mBuddyListingViewModel.fetchBuddyList(buddyRequest, httpManager, api);
            }
        }
    }

    @Override
    public void handleResponse(@Nullable ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            BuddyListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result),
                    BuddyListResponse.class);
            List<Buddy> list = buddyListResponse.getBuddies();
            List<Buddy> buddyList = new ArrayList<>();
            if (getIntent().hasExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU)) {
                ArrayList<String> buddyIds = getIntent().getStringArrayListExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Buddy buddy = list.get(i);
                        buddy.setShowSelected(showSelectionIcon);
                        if (buddyIds.size() > 0) {
                            //if we have selected buddies then iterate
                            // and match with buddy id and on match found
                            // set selection true
                            for (int j = 0; j < buddyIds.size(); j++) {
                                if (list.get(i).getBuddyId() != null &&
                                        list.get(i).getBuddyId().equals(buddyIds.get(j)) ||
                                        buddy.isLoggedIn()) {
                                    buddy.setSelected(true);
                                }
                            }
                        } else {
                            if (buddy.isLoggedIn()) {
                                buddy.setSelected(true);
                            }
                        }
                        buddyList.add(buddy);
                    }
                }
            } else {
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Buddy buddy = list.get(i);
                        buddy.setShowSelected(showSelectionIcon);
                        //if is selected is true then mark all buddy selected.
                        buddyList.add(buddy);
                    }
                }
            }
            mBuddyListingViewModel.getBuddyListLiveData().setValue(buddyList);
            setRecyclerView();
        }
    }

    private void setRecyclerView() {
        if (rvStatistics == null) {
            rvStatistics = mActivityBuddyListingBinding.rvBuddyListing;
            rvStatistics.setLayoutManager(new LinearLayoutManager(this));
        }
        if (rvStatistics.getAdapter() == null) {
            rvStatistics.setAdapter(adapter);
        }
        if (mainIntent != null && mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_NAVIGATION_MENU)) {
            rvStatistics.setPadding(0, 0, 0, 0);
        } else if (mainIntent != null && mainIntent.hasExtra(EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU)) {
            rvStatistics.setPadding(0, 0, 0, 100);
        }

        adapter.addItems(mBuddyListingViewModel.getBuddyObservableArrayList());
    }
    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar= CommonUtils.showNetWorkConnectionIssue( mActivityBuddyListingBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
}
