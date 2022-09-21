package com.rocketflyer.rocketflow.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.tracki.R;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.splash.SplashActivity;
import com.tracki.utils.TrackiToast;
import com.trackthat.lib.TrackThat;

import static com.tracki.utils.StartLocationAlert.REQUEST_CHECK_SETTINGS;

/**
 * Created by rahul on 13/11/18
 */
public class ErrorScreenHelper {

    final static int SOMETHING_WENT_ERROR = 1;
    final static int NO_INTERNET_ERROR = 2;
    public final static int LOCATION_SERVICE_ERROR = 3;
    public Dialog dialog;
    private BaseActivity baseActivity;

    public ErrorScreenHelper(Context context) {
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (!baseActivity.isFinishing()) {
                dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
        }
    }

    void display(int errorType, final ApiCallback apiCallBack) {
        if (baseActivity != null && !baseActivity.isFinishing()) {
            switch (errorType) {
                case SOMETHING_WENT_ERROR: {
                    dialog.setContentView(R.layout.something_went_wrong);
                    Button btnReloadNow = dialog.findViewById(R.id.btnReloadNow);
                    btnReloadNow.setOnClickListener(v -> {
                        apiCallBack.hitApi();
                        dialog.dismiss();
                    });
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    }
                    if(!baseActivity.isFinishing())
                    dialog.show();
                }
                break;
                case NO_INTERNET_ERROR: {
                    dialog.setContentView(R.layout.no_internet);
                    Button btnRetry = dialog.findViewById(R.id.btnRetry);
                    btnRetry.setOnClickListener(v -> {
                        apiCallBack.hitApi();
                        dialog.dismiss();
                    });
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    }
                    if(!baseActivity.isFinishing())
                    dialog.show();
                }
                break;
                case LOCATION_SERVICE_ERROR:{
                    dialog.setContentView(R.layout.layout_enable_location);
                    Button btnRetry = dialog.findViewById(R.id.btnEnableNow);
                    btnRetry.setOnClickListener(v -> {

                        dialog.dismiss();
                    });
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    }
                    if(!baseActivity.isFinishing())
                    dialog.show();
                }
                break;
            }
        }
    }
    public void displayLocation(int errorType) {
        if (baseActivity != null && !baseActivity.isFinishing()) {
            switch (errorType) {
                case LOCATION_SERVICE_ERROR: {
                    dialog.setContentView(R.layout.layout_enable_location);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    Button btnRetry = dialog.findViewById(R.id.btnEnableNow);
                    btnRetry.setOnClickListener(v -> {
                        try {
                           settingRequest(baseActivity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    }
                    if(!baseActivity.isFinishing())
                       dialog.show();
                }
                break;
            }
        }
    }
    public void changeAccessId(PreferencesHelper preferencesHelper) {
        if (baseActivity != null && !baseActivity.isFinishing()) {
           // CommonUtils.showLogMessage("e","Dilog","yes");
            dialog.setContentView(R.layout.layout_change_accessid);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button btnRetry = dialog.findViewById(R.id.btnSubmit);
            ImageView ivCancel = dialog.findViewById(R.id.ivCancel);
            EditText etAccessId = dialog.findViewById(R.id.etAccessId);
            etAccessId.setText(preferencesHelper.getAccessId());
            CommonUtils.showLogMessage("e","accessid",preferencesHelper.getAccessId());
            ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btnRetry.setOnClickListener(v -> {
                try {
                    String accesid=etAccessId.getText().toString().trim();
                    if(accesid.isEmpty())
                    {
                        TrackiToast.Message.showShort(baseActivity,"Please Enter Your Access Id");
                    }else{

                        Intent intent = new Intent(baseActivity, SplashActivity.class);
                        intent.putExtra(AppConstants.Extra.EXTRA_LOGOUT, true);
                     //   String login_token=null;
//                        if(preferencesHelper.getLoginToken()!=null)
//                           login_token=preferencesHelper.getLoginToken();
                        preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.NOT_ALL);
                        preferencesHelper.setAccessId(accesid);
//                        if(login_token!=null)
//                          preferencesHelper.setLoginToken(login_token);
                        if (TrackThat.isTracking()) {
                            TrackThat.stopTracking();
                        }
                        setFlags(intent);
                        baseActivity.startActivity(intent);
                        baseActivity.finish();
                        dialog.dismiss();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            dialog.show();
        }
    }
    protected void setFlags(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
    private LocationSettingsRequest.Builder getLocationRequest() {

        LocationRequest mLocationRequestBalancedPowerAccuracy = LocationRequest.create();
        mLocationRequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestBalancedPowerAccuracy.setInterval(0);
        mLocationRequestBalancedPowerAccuracy.setFastestInterval(0);
//        mLocationRequestBalancedPowerAccuracy.setInterval(5000);
//        mLocationRequestBalancedPowerAccuracy.setFastestInterval(2000);
//        mLocationRequestBalancedPowerAccuracy.setSmallestDisplacement(20f);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestBalancedPowerAccuracy);
        builder.setAlwaysShow(true); //this is the key ingredient and it means-
        // we will allow only two options yes and no
        // ,never option will not be shown.
        CommonUtils.showLogMessage("e","Loacation Updates","Location");
        return builder;
    }

    public void settingRequest(BaseActivity context) {
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context)
                .checkLocationSettings(getLocationRequest().build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                if (response != null && response.getLocationSettingsStates().isGpsPresent()) {
                    if(dialog!=null)
                     dialog.dismiss();
                }
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    context,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException | ClassCastException e) {
                            // Ignore the error.
                            Log.e("Baseactivity", "Exception occur inside catch block " + e);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }
}
