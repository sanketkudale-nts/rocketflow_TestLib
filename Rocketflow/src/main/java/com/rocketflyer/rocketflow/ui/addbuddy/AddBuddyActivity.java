package com.rocketflyer.rocketflow.ui.addbuddy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.snackbar.Snackbar;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.database.DatabaseClient;
import com.tracki.data.model.request.AddBuddyRequest;
import com.tracki.data.model.request.Shift;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityAddBuddyBinding;
import com.tracki.ui.addbuddy.AddBuddyNavigator;
import com.tracki.ui.addbuddy.AddBuddyViewModel;
import com.tracki.ui.addbuddy.Contact;
import com.tracki.ui.addbuddy.ContactListActivity;
import com.tracki.ui.base.BaseActivity;
import com.tracki.utils.AddContactInDataBase;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;
import com.tracki.utils.TrackiToast;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by rahul on 18/9/18
 */
public class AddBuddyActivity extends BaseActivity<ActivityAddBuddyBinding, AddBuddyViewModel>
        implements AddBuddyNavigator {

    private static final String TAG = "AddBuddyActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 555;

    @Inject
    AddBuddyViewModel mAddBuddyViewModel;

    @Inject
    HttpManager httpManager;

    private int fromHour = 0, fromMinute = 0, toHour = 0, toMinute = 0;
    private ActivityAddBuddyBinding mActivityAddBuddyBinding;
    private EditText edFrom, edTo;
    private Api api;
    private Snackbar snackBar;


    public static Intent newIntent(Context context) {
        return new Intent(context, AddBuddyActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_buddy;
    }

    @Override
    public AddBuddyViewModel getViewModel() {
        return mAddBuddyViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAddBuddyBinding = getViewDataBinding();
        mAddBuddyViewModel.setNavigator(this);

        api = TrackiApplication.getApiMap().get(ApiType.ADD_BUDDY);

        Toolbar toolbar = mActivityAddBuddyBinding.toolbar;
        setToolbar(toolbar, getString(R.string.add_buddy));

        edFrom = mActivityAddBuddyBinding.edFrom;
        edTo = mActivityAddBuddyBinding.edTo;

        mActivityAddBuddyBinding.rlFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant

                        return;
                    }
                    else {
                        addContacts();
                        Intent intent = new Intent(AddBuddyActivity.this, ContactListActivity.class);
                        startActivityForResult(intent, AppConstants.REQUEST_CODE_PICK_CONTACT);
                    }
                }else {
                    addContacts();
                    Intent intent = new Intent(AddBuddyActivity.this, ContactListActivity.class);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_PICK_CONTACT);
                }
            }

        });


    }
    public void addContacts(){
        DatabaseClient.Companion.getInstance(this).getAppDatabase().contactsDataDao().getAllContact().observe(this, new Observer<List<com.tracki.ui.addbuddy.Contact>>() {
            @Override
            public void onChanged(List<com.tracki.ui.addbuddy.Contact> contacts) {
                if(contacts==null || contacts.isEmpty()){
                    Constraints constraints = new Constraints.Builder()
                            .setRequiresCharging(false)
                            .setRequiresBatteryNotLow(true)
                            .setRequiresStorageNotLow(true)
                            .build();
                    WorkRequest myWorkRequest=new OneTimeWorkRequest.Builder(AddContactInDataBase.class).
                            setConstraints(constraints)
                            .setInitialDelay(20, TimeUnit.MILLISECONDS)
                            .build();



                    WorkManager workManager = WorkManager.getInstance();
                    workManager.enqueue(myWorkRequest);
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addContacts();
                    Intent intent = new Intent(AddBuddyActivity.this, ContactListActivity.class);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_PICK_CONTACT);

                } else {
                    TrackiToast.Message.showLong(AddBuddyActivity.this,"Please Enable Contact Permission");
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {

            case AppConstants.REQUEST_CODE_PICK_CONTACT:
                if(resultCode == Activity.RESULT_OK){
                    Contact contact=data.getParcelableExtra("result");
                    mActivityAddBuddyBinding.edFullName.setText(contact.getName());
                    mActivityAddBuddyBinding.edMobileNumber.setText(phoneNumberWithOutCountryCode(contact.getMobileNumber()));
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }


        }
    }

    private String phoneNumberWithOutCountryCode(String phoneNumber) {

        String number = phoneNumber.replaceAll("\\s", "");


        if (phoneNumber.startsWith("+")) {
            if (number.length() == 13) {
                number = number.substring(3);
            } else if (number.length() == 14) {
                number = number.substring(4);
            }
        }

//
        return number;
    }


    @Override
    public void validate() {
        try {
            String fullName = mActivityAddBuddyBinding.edFullName.getText().toString().trim();
            String mobile = mActivityAddBuddyBinding.edMobileNumber.getText().toString().trim();

            if (mAddBuddyViewModel.isViewNullOrEmpty(fullName)) {
                TrackiToast.Message.showShort(this,getString(R.string.name_cannot_be_empty));
                return;
            }



            if (!mAddBuddyViewModel.isMobileValid(mobile)) {
                TrackiToast.Message.showShort(this,getString(R.string.invalid_mobile));
                return;
            }


            hideKeyboard();
            showLoading();

            Shift shift = new Shift("00:00", "23:59");
            AddBuddyRequest addBuddyRequest = new AddBuddyRequest(fullName, mobile, shift);

            mAddBuddyViewModel.addBuddyInvite(httpManager, addBuddyRequest, api);
        } catch (Exception e) {
            Log.e(TAG, "error inside validate(): " + e);
        }
    }

    @Override
    public void onBackClick() {
        onBackPressed();
    }

    @Override
    public void openTimePicker(View view) {
        //To show current time in the time picker
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = 0, minutes = 0;

        if (view.getId() == R.id.edFrom) {
            if (fromHour == 0 && fromMinute == 0) {
                hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minutes = mcurrentTime.get(Calendar.MINUTE);
            } else {
                hour = fromHour;
                minutes = fromMinute;
            }
        } else if (view.getId() == R.id.edTo) {
            if (toHour == 0 && toMinute == 0) {
                hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minutes = mcurrentTime.get(Calendar.MINUTE);
            } else {
                hour = toHour;
                minutes = toMinute;
            }
        }
        CommonUtils.openTimePicker(this, hour, minutes, (view12, hourOfDay, minute) -> {
            mcurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mcurrentTime.set(Calendar.MINUTE, minute);
            String formatted_time = DateTimeUtil.getFormattedTime(mcurrentTime.getTime());
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
    public void handleResponse(@Nullable ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
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
        snackBar= CommonUtils.showNetWorkConnectionIssue(  mActivityAddBuddyBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
}
