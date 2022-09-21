package com.rocketflyer.rocketflow.ui.buddyprofile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.model.request.Shift;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.model.response.config.Fleet;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityBuddyProfileBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.buddyprofile.BuddyProfileNavigator;
import com.tracki.ui.buddyprofile.BuddyProfileViewModel;
import com.tracki.ui.fleetlisting.FleetListingActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;
import com.tracki.utils.TrackiToast;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY;
import static com.tracki.utils.AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA;
import static com.tracki.utils.AppConstants.REQUEST_CODE_BUDDY_PROFILE;

/**
 * Created by rahul on 9/10/18
 */
public class BuddyProfileActivity extends BaseActivity<ActivityBuddyProfileBinding, BuddyProfileViewModel>
        implements BuddyProfileNavigator {

    private static final String TAG = BuddyProfileActivity.class.getSimpleName();
    @Inject
    BuddyProfileViewModel mBuddyProfileViewModel;
    @Inject
    HttpManager httpManager;

    ActivityBuddyProfileBinding mActivityBuddyProfileBinding;

    private int fromHour = 0, fromMinute = 0, toHour = 0, toMinute = 0;
    private EditText edFleetDetails;
    private EditText edFrom, edTo;
    private Buddy buddy;
    private Api api;
    private boolean isEditMode;
    private Snackbar snackBar;

    public static Intent newIntent(Context context) {
        return new Intent(context, BuddyProfileActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_buddy_profile;
    }

    @Override
    public BuddyProfileViewModel getViewModel() {
        return mBuddyProfileViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityBuddyProfileBinding = getViewDataBinding();
        mBuddyProfileViewModel.setNavigator(this);
        api = TrackiApplication.getApiMap().get(ApiType.UPDATE_BUDDY);
        setUp();
    }

    private void setUp() {
        edFrom = mActivityBuddyProfileBinding.edFrom;
        edTo = mActivityBuddyProfileBinding.edTo;
        EditText edFullName = mActivityBuddyProfileBinding.edFullName;
        EditText edMobileNumber = mActivityBuddyProfileBinding.edMobileNumber;
        edFleetDetails = mActivityBuddyProfileBinding.edFleetDetails;
//        tvAddFleet = mActivityBuddyProfileBinding.tvAddFleet;

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_BUDDY)) {
                buddy = (Buddy) intent.getSerializableExtra(EXTRA_BUDDY);
                setToolbar(mActivityBuddyProfileBinding.toolbar, buddy.getName());
            }

            if (intent.hasExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE)) {
                isEditMode = intent.getBooleanExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE, false);
            }
        }

        if (!isEditMode) {
            edFullName.setEnabled(false);
            edMobileNumber.setEnabled(false);
        }

        // set Data
        String name = buddy.getName();
        String mobile = buddy.getMobile();
        if (buddy.getShift() != null ) {
            String from = buddy.getShift().getFrom();
            edFrom.setText(from);
            edFrom.setSelection(from.trim().length());

            String[] fr = from.split(":");

            fromHour = Integer.parseInt(fr[0]);
            fromMinute = Integer.parseInt(fr[1]);

            String to = buddy.getShift().getTo();
            edTo.setText(to);
            edTo.setSelection(to.trim().length());

            String[] t = to.split(":");

            toHour = Integer.parseInt(t[0]);
            toMinute = Integer.parseInt(t[1]);
        }

        if (name != null) {
            edFullName.setText(name.trim());
            edFullName.setSelection(name.trim().length());
        }
        if (mobile != null) {
            edMobileNumber.setText(mobile);
            edMobileNumber.setSelection(mobile.trim().length());
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.buddy_profile, menu);

        MenuItem item = menu.findItem(R.id.action_edit);
        if (item != null && !isEditMode) {
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        if (item.getItemId() == R.id.action_edit) {
            validate();
               /* if (edFullName.isEnabled()) {
                    validate();
                    *//*TODO change the below icon and fields in on success*//*
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_edit_black_24dp));
                    setEnables(false);

                } else {
                    setEnables(true);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_tick_black_24dp));
                }*/

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void validate() {
        try {
            String edFleetDetail = edFleetDetails.getText().toString().trim();
            String fullName = mActivityBuddyProfileBinding.edFullName.getText().toString().trim();
            String mobile = mActivityBuddyProfileBinding.edMobileNumber.getText().toString().trim();
//            String from = mActivityBuddyProfileBinding.edFrom.getText().toString().trim();
//            String to = mActivityBuddyProfileBinding.edTo.getText().toString().trim();

            if (mBuddyProfileViewModel.isViewNullOrEmpty(fullName)) {
                TrackiToast.Message.showShort(this,getString(R.string.name_cannot_be_empty));
                return;
            }

            if ((edFleetDetails.getVisibility() == View.VISIBLE) && !mBuddyProfileViewModel.isViewNullOrEmpty(edFleetDetail)) {
                TrackiToast.Message.showShort(this,getString(R.string.field_cannot_be_empty));
                return;
            }

            if (CommonUtils.isViewNullOrEmpty(mobile)) {
                TrackiToast.Message.showShort(this,getString(R.string.field_cannot_be_empty));
                return;
            }

            if (!mBuddyProfileViewModel.isMobileValid(mobile)) {
                TrackiToast.Message.showShort(this,getString(R.string.invalid_mobile));
                return;
            }

//            if (mBuddyProfileViewModel.isViewNullOrEmpty(from)) {
//                Toast.makeText(this, R.string.select_shift_start_time, Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (mBuddyProfileViewModel.isViewNullOrEmpty(to)) {
//                Toast.makeText(this, R.string.select_shift_end_time, Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if ((fromHour > toHour) || ((fromHour == toHour) && fromMinute >= toMinute)) {
//                Toast.makeText(this, R.string.start_time_cannot_be_greater, Toast.LENGTH_SHORT).show();
//                return;
//            }

            hideKeyboard();
            showLoading();

//            Shift shift = new Shift(fromHour + ":" + fromMinute, toHour + ":" + toMinute);
            Shift shift = new Shift("00:00", "23:59");

            Buddy buddy1 = new Buddy();
            buddy1.setBuddyId(buddy.getBuddyId());
            buddy1.setName(fullName);
            buddy1.setMobile(mobile);
            buddy1.setShift(shift);

            mBuddyProfileViewModel.updateBuddyProfile(buddy1, httpManager, api);

        } catch (Exception e) {
            Log.e(TAG, "error inside validate(): " + e);
        }
    }

    @Override
    public void openTimePicker(View view) {
        //To show current date in the date picker
        Calendar currentTime = Calendar.getInstance();
        int hour, minutes;
        if (view.getId() == R.id.edFrom) {
            if (fromHour == 0 && fromMinute == 0) {
                hour = currentTime.get(Calendar.HOUR_OF_DAY);
                minutes = currentTime.get(Calendar.MINUTE);
            } else {
                hour = fromHour;
                minutes = fromMinute;
            }
        } else {
            if (toHour == 0 && toMinute == 0) {
                hour = currentTime.get(Calendar.HOUR_OF_DAY);
                minutes = currentTime.get(Calendar.MINUTE);
            } else {
                hour = toHour;
                minutes = toMinute;
            }
        }

        CommonUtils.openTimePicker(this, hour, minutes, (view12, hourOfDay, minute) -> {
            currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            currentTime.set(Calendar.MINUTE, minute);
            String formatted_time = DateTimeUtil.getFormattedTime(currentTime.getTime());
            int id = view.getId();
            if (id == R.id.edFrom) {
                fromHour = hourOfDay;
                fromMinute = minute;
                edFrom.setText(formatted_time);
            } else if (id == R.id.edTo) {
                toHour = hourOfDay;
                toMinute = minute;
                edTo.setText(formatted_time);
            }
        });
    }

    @Override
    public void openAddFleet() {
        startActivityForResult(FleetListingActivity.newIntent(this)
                        .putExtra(AppConstants.Extra.EXTRA_IS_CALLING_FROM_BUDDY_PROFILE, true)
                , REQUEST_CODE_BUDDY_PROFILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_BUDDY_PROFILE:
                if (resultCode == Activity.RESULT_OK) {
//                    tvAddFleet.setVisibility(View.GONE);
                    edFleetDetails.setVisibility(View.VISIBLE);
                    if (data != null && data.hasExtra(SELECTED_FLEET_LIST_EXTRA)) {
                        @SuppressWarnings("unchecked")
                        ArrayList<Fleet> list = (ArrayList<Fleet>) data.getSerializableExtra(SELECTED_FLEET_LIST_EXTRA);
                        String selectedFleets = null;
                        for (int i = 0; i < list.size(); i++) {
                            if (selectedFleets == null || i == list.size() - 1) {
                                selectedFleets = list.get(i).getFleetName();
                            } else {
                                selectedFleets = "," + list.get(i).getFleetName();
                            }
                            edFleetDetails.setText(selectedFleets);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void handleResponse(@Nullable ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            TrackiToast.Message.showShort(this,"Buddy detail updated successFully");
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar= CommonUtils.showNetWorkConnectionIssue( mActivityBuddyProfileBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
}