package com.tracki.ui.fleetlisting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.tracki.data.model.request.ChangeFleetStatusApiRequest;
import com.tracki.data.model.request.DeleteFleetRequest;
import com.tracki.data.model.request.EntityStatus;
import com.tracki.data.model.request.FleetRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Fleet;
import com.tracki.data.model.response.config.FleetListResponse;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityFleetListingBinding;
import com.tracki.ui.addfleet.AddFleetActivity;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.common.DoubleButtonDialog;
import com.tracki.ui.common.OnClickListener;
import com.tracki.ui.newcreatetask.NewCreateTaskActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.TrackiToast;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.tracki.utils.AppConstants.Extra.EXTRA_FLEET_LIST_CALLING_FROM_NAVIGATION_MENU;
import static com.tracki.utils.AppConstants.Extra.SELECTED_BUDDY_LIST_EXTRA;
import static com.tracki.utils.AppConstants.REQUEST_CODE_FLEET_PROFILE;

/**
 * Created by rahul on 10/10/18
 */
public class FleetListingActivity extends BaseActivity<ActivityFleetListingBinding, FleetListingViewModel>
        implements FleetListingNavigator, FleetListingAdapter.AdapterEventListener {

    @Inject
    FleetListingViewModel mFleetListingViewModel;
    @Inject
    FleetListingAdapter adapter;
    @Inject
    HttpManager httpManager;

    private FleetRequest fleetRequest;
    private RecyclerView rvStatistics;
    private Api api;
    private SearchView searchView;
    private ActivityFleetListingBinding mActivityFleetListingBinding;
    private boolean showSelectionIcon = false;
    private Snackbar snackBar;

    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar= CommonUtils.showNetWorkConnectionIssue( mActivityFleetListingBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, FleetListingActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fleet_listing;
    }

    @Override
    public FleetListingViewModel getViewModel() {
        return mFleetListingViewModel;
    }
    private String categoryMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityFleetListingBinding = getViewDataBinding();
        mFleetListingViewModel.setNavigator(this);
        adapter.setListener(this, getIntent());

        fleetRequest = new FleetRequest("ALL");
        api = TrackiApplication.getApiMap().get(ApiType.FLEETS);

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
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mFleetListingViewModel.getFleetObservableArrayList().size() > 0) {
                    adapter.addFilter(newText);
                } else {
                    TrackiToast.Message.showShort(FleetListingActivity.this,
                            getString(R.string.cannot_performe_this_operation));
                }
                return false;
            }
        });
        return true;
    }


    private void setUp() {
        Toolbar toolbar = mActivityFleetListingBinding.toolbar;
        //setSupportActionBar(toolbar);
        TextView tvSkip = toolbar.findViewById(R.id.tvSkip);


        FloatingActionButton fabAddFleet = mActivityFleetListingBinding.fabAddFleet;
        Button btnProceed = mActivityFleetListingBinding.btnProceed;

        setToolbar(toolbar, getString(R.string.select_fleet));
        tvSkip.setVisibility(View.GONE);
        btnProceed.setVisibility(View.VISIBLE);
        fabAddFleet.hide();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_FLEET_LIST_CALLING_FROM_NAVIGATION_MENU)) {
                btnProceed.setVisibility(View.GONE);
                setToolbar(toolbar, getString(R.string.fleet));
                showSelectionIcon = false;
            }/* else if (intent.hasExtra(EXTRA_IS_CALLING_FROM_BUDDY_PROFILE)) {
                fabAddFleet.hide();
                showSelectionIcon = true;
            } */ else {
                showSelectionIcon = true;
                fabAddFleet.hide();
                tvSkip.setVisibility(View.VISIBLE);
            }
            if (getIntent().hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
                categoryMap = getIntent().getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES);
                //CommonUtils.showLogMessage("e","categoryMap",categoryMap);
            }
        }
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=NewCreateTaskActivity.Companion.newIntent(FleetListingActivity.this);
                if(categoryMap!=null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,categoryMap);
                // hashMap of selected buddies
                intent.putExtra(SELECTED_BUDDY_LIST_EXTRA,
                        getIntent().getSerializableExtra(SELECTED_BUDDY_LIST_EXTRA))
                        .putExtra(AppConstants.Extra.SELECT_FLEET_EXTRA, true);

                startActivityForResult(intent,
                        AppConstants.REQUEST_CODE_CREATE_TASK);
            }
        });
        showLoading();
        //fetch hashMap of buddies and pass true to show check button.
        mFleetListingViewModel.fetchFleets(httpManager, api, fleetRequest);
    }

    private void subscribeToLiveData() {
        mFleetListingViewModel.getFleetListLiveData().observe(this, lists -> {
            if (lists != null) {
                mFleetListingViewModel.addItemsToList(lists);
                adapter.addItems(lists);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if (getIntent().hasExtra(AppConstants.Extra.EXTRA_IS_CALLING_FROM_BUDDY_PROFILE)) {
            setResult(RESULT_CANCELED);
        }*/
        finish();
    }

    @Override
    public void onProceedClick() {
        List<Fleet> list = adapter.getFleetResponseList();
        ArrayList<Fleet> mainList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && list.get(i).isSelected() != null && list.get(i).isSelected()) {
                mainList.add(list.get(i));
            }
        }
        if (mainList.size() > 0) {
          /*  if (getIntent().hasExtra(EXTRA_IS_CALLING_FROM_BUDDY_PROFILE)) {
                Intent intent = new Intent();
                // hashMap of selected fleets
                intent.putExtra(AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA, mainList);
                setResult(RESULT_OK, intent);
                finish();
            } else {*/

            //hashMap items associated with it else error.
           // Intent intent=CreateTaskActivity.Companion.newIntent(this);
            Intent intent= NewCreateTaskActivity.Companion.newIntent(this);
                    // hashMap of selected buddies
            if(categoryMap!=null)
                intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,categoryMap);
            intent.putExtra(SELECTED_BUDDY_LIST_EXTRA,
                            getIntent().getSerializableExtra(SELECTED_BUDDY_LIST_EXTRA))
                    // hashMap of selected fleets
                    .putExtra(AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA, mainList)
                    // calling activity flag
                    .putExtra(AppConstants.Extra.SELECT_FLEET_EXTRA, true);
            startActivityForResult(intent,
                    AppConstants.REQUEST_CODE_CREATE_TASK);
            /*}*/
        } else {
            TrackiToast.Message.showShort(this, "Please select fleet from above hashMap.");
        }
    }

    @Override
    public void openAddFleetActivity() {
        startActivityForResult(AddFleetActivity.newIntent(this),
                AppConstants.REQUEST_CODE_ADD_FLEET);
    }

    @Override
    public void refreshFleetList(ApiCallback apiCallback, Object result, APIError error) {
        showLoading();
        mFleetListingViewModel.fetchFleets(httpManager, api, fleetRequest);
    }

    @Override
    public void changeFleetStatus(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_ADD_FLEET || requestCode == REQUEST_CODE_FLEET_PROFILE) {
            if (resultCode == RESULT_OK) {
                showLoading();
                mFleetListingViewModel.fetchFleets(httpManager, api, fleetRequest);
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void handleResponse(@Nullable ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            FleetListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result), FleetListResponse.class);
            if (buddyListResponse != null&&buddyListResponse.getFleets()!=null) {
                List<Fleet> list = buddyListResponse.getFleets();
                List<Fleet> fleetList = new ArrayList<>();
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Fleet buddy = list.get(i);
                        buddy.setShowSelected(showSelectionIcon);
                        fleetList.add(buddy);
                    }
                } /*else {
                if (getIntent().hasExtra(EXTRA_IS_CALLING_FROM_BUDDY_PROFILE)) {
                    fabAddFleet.show();
                    btnProceed.setVisibility(View.GONE);
                } else {
                    btnProceed.setVisibility(View.VISIBLE);
                    fabAddFleet.hide();
                }
            }*/
                mFleetListingViewModel.getFleetListLiveData().setValue(fleetList);
                setRecyclerView();
            }
        }else{
            setRecyclerView();
        }
    }

    private void setRecyclerView() {
        if (rvStatistics == null) {
            rvStatistics = mActivityFleetListingBinding.rvFleetListing;
            rvStatistics.setLayoutManager(new LinearLayoutManager(this));
        }
        if (rvStatistics.getAdapter() == null) {
            rvStatistics.setAdapter(adapter);
        }
        rvStatistics.setPadding(0, 0, 0, 100);
        if (getIntent() != null && getIntent().hasExtra(EXTRA_FLEET_LIST_CALLING_FROM_NAVIGATION_MENU)) {
            rvStatistics.setPadding(0, 0, 0, 0);
        }
        adapter.addItems(mFleetListingViewModel.getFleetObservableArrayList());
    }

    @Override
    public void onItemClick(Fleet fleetBean) {
        startActivityForResult(AddFleetActivity.newIntent(this)
                        .putExtra(AppConstants.Extra.EXTRA_FLEET, fleetBean),
                REQUEST_CODE_FLEET_PROFILE);
    }

    @Override
    public void onItemLongClick(Fleet fleet) {
        DoubleButtonDialog dialog = new DoubleButtonDialog(this,
                true,
                null,
                getString(R.string.delete_fleet),
                getString(R.string.yes),
                getString(R.string.no),
                new OnClickListener() {
                    @Override
                    public void onClickCancel() {

                    }

                    @Override
                    public void onClick() {
                        Api api = TrackiApplication.getApiMap().get(ApiType.DELETE_FLEET);
                        DeleteFleetRequest delete = new DeleteFleetRequest(fleet.getFleetId());
                        mFleetListingViewModel.deleteFleet(httpManager, delete, api);
                    }
                });
        dialog.show();
    }

    @Override
    public void enableDisableFleet(Fleet fleetBeen, Boolean status) {
        ChangeFleetStatusApiRequest request = new ChangeFleetStatusApiRequest();
        request.setFleetId(fleetBeen.getInvId());
        if(status){
            fleetBeen.setStatus("ACTIVE");
            request.setStatus(EntityStatus.ACTIVE);
        }else{
            fleetBeen.setStatus("INACTIVE");
            request.setStatus(EntityStatus.INACTIVE);
        }
        showLoading();
        Api api = TrackiApplication.getApiMap().get(ApiType.CHANGE_FLEET_STATUS);
        mFleetListingViewModel.changeFleetStatus(httpManager, request, api);

    }
}
