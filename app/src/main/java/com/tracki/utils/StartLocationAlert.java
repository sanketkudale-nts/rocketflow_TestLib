package com.tracki.utils;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

/**
 * Created by rahul on 18/2/19
 */
public class StartLocationAlert {
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "StartLocationAlert";
    private Activity context;
    private boolean isConnected = false;
    private LocationSettingCallback callback;

    public StartLocationAlert(Activity context) throws Exception {
        if (context instanceof LocationSettingCallback) {
            this.context = context;
            this.callback = (LocationSettingCallback) context;
            settingRequest();
        } else {
            throw new Exception("Calling activity must implement " +
                    LocationSettingCallback.class.getSimpleName() +
                    "interface.");
        }
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

    public void settingRequest() {
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context)
                .checkLocationSettings(getLocationRequest().build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                if (response != null && response.getLocationSettingsStates().isGpsPresent()) {
                    isConnected = true;
                    if (callback != null) {
                        callback.enabled();
                    }
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
                            Log.e(TAG, "Exception occur inside catch block " + e);
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

    public boolean isConnected() {
        return isConnected;
    }

    public interface LocationSettingCallback {
        void enabled();
    }
}
