package com.rocketflyer.rocketflow.ui.splash;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.data.firebase.FeedsJson;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.NotificationModel;
import com.tracki.data.model.response.config.ConfigResponse;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivitySplashBinding;
import com.tracki.ui.adjusttime.AdjustTimeActivity;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.common.DoubleButtonDialog;
import com.tracki.ui.common.OnClickListener;
import com.tracki.ui.consent.ConsentActivity;
import com.tracki.ui.deviceChange.DeviceChangeActivity;
import com.tracki.ui.feeddetails.FeedDetailsActivity;
import com.tracki.ui.main.MainActivity;
import com.tracki.ui.splash.SplashNavigator;
import com.tracki.ui.splash.SplashViewModel;
import com.tracki.utils.AppConstants;
import com.tracki.utils.AutoStartHelper;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.Log;
import com.tracki.utils.NextScreen;
import com.tracki.utils.StartLocationAlert;
import com.tracki.utils.TrackiToast;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import static com.tracki.utils.AppConstants.ALERT_TRY_AGAIN;
import static com.tracki.utils.AppConstants.CLOSE;
import static com.tracki.utils.AppConstants.PERMISSIONS_REQUEST_CODE_MULTIPLE;
import static com.tracki.utils.AppConstants.RETRY;


public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel>
        implements SplashNavigator, StartLocationAlert.LocationSettingCallback  {

    private static final String TAG = "SplashActivity";

    @Inject
    SplashViewModel mSplashViewModel;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;
    private ActivitySplashBinding mActivitySplashBinding;
    private String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private StartLocationAlert locationAlert;
    private DoubleButtonDialog dialog = null;
    private String message;

    private SharedPreferences prefs;
    boolean deviceChangeRequestInitiated;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public SplashViewModel getViewModel() {
        return mSplashViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySplashBinding = getViewDataBinding();
        mSplashViewModel.setNavigator(this);
        FirebaseApp.initializeApp(this);



        //device Change request initiated check
        prefs = getSharedPreferences("DeviceChange", MODE_PRIVATE);
        deviceChangeRequestInitiated = prefs.getBoolean("deviceChangeRequestInitiated", false);


            handleNotification(getIntent());
            if (!CommonUtils.checkPlayServices(this)) {
                return;
            }


            addDataContentProvider();
            CommonUtils.getFcmToken(instanceIdResult -> {
                preferencesHelper.setFcmToken(instanceIdResult.getToken());
                CommonUtils.showLogMessage("e", "fcm_id", preferencesHelper.getFcmToken());
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions = new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION/*, Manifest.permission.ACCESS_BACKGROUND_LOCATION*/, Manifest.permission.ACTIVITY_RECOGNITION

                };

            }
            if (!checkMultiplePermission(permissions)) {
                openProminentDisclosure();
            }


    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotification(intent);
    }

    private void handleNotification(Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // String title = extras.getString("title");
              //  String message = extras.getString("body");
                if(extras.containsKey("data")){
                    message = extras.getString("data");
                    CommonUtils.showLogMessage("e","fcm_data",message);
                    if (message != null && message.length() > 0) {
                        // getIntent().removeExtra("body");
                        getIntent().removeExtra("data");
                        try {

                            Gson gson = new Gson();
                            NotificationModel notificationModel = gson.fromJson(message, NotificationModel.class);
                            if (notificationModel != null) {
                                CommonUtils.handleForceActions(notificationModel, SplashActivity.this, preferencesHelper);
                            }


                        } catch (JsonSyntaxException | IllegalStateException je) {
                            Log.e(TAG, "Exception inside handleNotification: " + je);
                        }
                    }

                }else if(extras.containsKey("navigation")){
                    message = extras.getString("navigation");
                    CommonUtils.showLogMessage("e","fcm_data",message);
                }


            }
        }
    }

    @Override
    public void showTimeOutMessage(@NonNull ApiCallback callback) {
        if (!isFinishing()) {
            new android.app.AlertDialog.Builder(this)
                    .setMessage(AppConstants.MSG_REQUEST_TIME_OUT_TYR_AGAIN)
                    .setTitle(ALERT_TRY_AGAIN)
                    .setCancelable(false)
                    .setPositiveButton(RETRY, (dialog, which) -> {
                        dialog.dismiss();
                        mActivitySplashBinding.contentProgressBar.setVisibility(View.VISIBLE);
                        callback.hitApi();
                    })
                    .setNegativeButton(CLOSE, (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    @Override
    public void openLoginActivity() {
        Intent intent = ConsentActivity.newIntent(this);
        setFlags(intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void openMainActivity() {
        Intent intent = MainActivity.newIntent(SplashActivity.this);
        setFlags(intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void handleResponse(@NonNull ApiCallback callback, Object result, @Nullable APIError error) {
        mActivitySplashBinding.contentProgressBar.setVisibility(View.INVISIBLE);
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            if (result == null) {
                return;
            }
            Gson gson = new Gson();
            ConfigResponse configResponse = gson.fromJson(String.valueOf(result), ConfigResponse.class);


                if (configResponse.getTime() != null && Long.parseLong(configResponse.getTime()) != 0L) {
                    if (Math.abs(Long.parseLong(configResponse.getTime()) - System.currentTimeMillis()) >= AppConstants.TIME_DIFF) {// startErrorActivity();
                        startActivityForResult(new Intent(SplashActivity.this, AdjustTimeActivity.class), AppConstants.REQUEST_TIME_ZONE);
                    } else {

                        if (message == null)
                            CommonUtils.saveConfigDetails(SplashActivity.this, configResponse, preferencesHelper, "1");
                        else {
                            CommonUtils.saveConfigDetailsFromSystemTrayClick(SplashActivity.this, configResponse, preferencesHelper, "1", message);
                            message = null;
                        }
                    }
                } else {
                    if (message == null)
                        CommonUtils.saveConfigDetails(SplashActivity.this, configResponse, preferencesHelper, "1");
                    else {
                        CommonUtils.saveConfigDetailsFromSystemTrayClick(SplashActivity.this, configResponse, preferencesHelper, "1", message);
                        message = null;
                    }
                }

        }else{
            CommonUtils.showPermanentSnackBar(findViewById(R.id.rlMain), AppConstants.ALERT_TRY_AGAIN,mSplashViewModel);
        }

    }

    @Override
    public void enabled() {
        if (locationAlert != null && locationAlert.isConnected()) {
            init();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMultiplePermission(permissions)) {
            try {
                if (locationAlert == null) {
                    initLocation();
                } else {
                    if (locationAlert.isConnected()) {
                        init();
                    }else{
                        locationAlert.settingRequest();
                    }
                }
            } catch (Exception e) {
            }
        }



    }
    private void openProminentDisclosure() {
        final Dialog dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_prominent_disclosure);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.8f;
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnProceed = dialog.findViewById(R.id.btnProceed);


        dialog.getWindow().setAttributes(lp);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.preventTwoClick(btnProceed);
                dialog.dismiss();
                finish();
            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CommonUtils.preventTwoClick(btnProceed);
                checkPermissionAndInitLocation();
            }
        });
        if (!dialog.isShowing())
            dialog.show();

    }

    private void checkPermissionAndInitLocation() {
        if (!hasPermission(permissions)) {
           /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,  *//*Build.VERSION.SDK_INT>Build.VERSION_CODES.Q?Manifest.permission.READ_PHONE_NUMBERS: *//*Manifest.permission.READ_PHONE_STATE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)&&
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)&&
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)*//*&&
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)*//*


                ){
                    showDialogOK("PhoneData and Location Services Permission required for this app",
                            (dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        hasPermission(permissions);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        // proceed with logic by disabling the related features or quit the app.
                                        finish();
                                        break;
                                }
                            });
                }else{
                    hasPermission(permissions);
                }
            }else{
                if (*//*ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ||*//*
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)&&
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)

                ){
                    showDialogOK("PhoneData and Location Services Permission required for this app",
                            (dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        hasPermission(permissions);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        // proceed with logic by disabling the related features or quit the app.
                                        finish();
                                        break;
                                }
                            });
                }else{
                    hasPermission(permissions);
                }
            }
*/

            return;
        }
        initLocation();
    }

    private void hitApi() {
        if(isNetworkConnected()){
            preferencesHelper.setDeviceId(CommonUtils.getIMEINumber(this));
            mActivitySplashBinding.contentProgressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> mSplashViewModel.startSeeding(httpManager), 3000);
        }else{
            CommonUtils.showSnakbarForNetworkSettings(this, findViewById(R.id.rlMain),getString(R.string.please_check_your_internet_connection_you_are_offline_now));
        }


    }

    private void init() {
        showPopUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_MULTIPLE) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                perms.put(Manifest.permission.ACTIVITY_RECOGNITION, PackageManager.PERMISSION_GRANTED);
               /* perms.put(Manifest.permission.ACCESS_BACKGROUND_LOCATION, PackageManager.PERMISSION_GRANTED);*/
            }
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                Integer actvityRecog = null;
                Integer readPer = perms.get(Manifest.permission.READ_PHONE_STATE);
                Integer locPer = perms.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Integer courseLocPer = perms.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                Integer locBgPer = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    actvityRecog = perms.get(Manifest.permission.ACTIVITY_RECOGNITION);
                  /*  locBgPer=perms.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION);*/
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if ((readPer != null && readPer == PackageManager.PERMISSION_GRANTED)
                            && (locPer != null && locPer == PackageManager.PERMISSION_GRANTED)
                            && (courseLocPer != null && courseLocPer == PackageManager.PERMISSION_GRANTED)
                            && (actvityRecog != null && actvityRecog == PackageManager.PERMISSION_GRANTED)
                           /* && (locBgPer != null && locBgPer == PackageManager.PERMISSION_GRANTED)*/


                    ) {

                        try {
                            if (locationAlert == null) {
                                initLocation();
                            } else {
                                if (locationAlert.isConnected()) {
                                    init();
                                }
                            }
                        } catch (Exception e) {
                        }
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,  Manifest.permission.READ_PHONE_STATE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)/*||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)*/


                        ) {
                            showDialogOK("PhoneData and Location Services Permission required for this app",
                                    (dialog, which) -> {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                hasPermission(permissions);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                finish();
                                                break;
                                        }
                                    });
                        }

                        else {
                            TrackiToast.Message
                                    .showLong(this, "Go to settings and enable permissions");
                        }

                    }
                } else {
                    if ((readPer != null && readPer == PackageManager.PERMISSION_GRANTED)
                            && (locPer != null && locPer == PackageManager.PERMISSION_GRANTED)
                            && (courseLocPer != null && courseLocPer == PackageManager.PERMISSION_GRANTED)
                    ) {

                        try {
                            if (locationAlert == null) {
                                initLocation();
                            } else {
                                if (locationAlert.isConnected()) {
                                    init();
                                } else {
                                }
                            }
                        } catch (Exception e) {
                        }
                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)

                        ) {
                            showDialogOK("PhoneData and Location Services Permission required for this app",
                                    (dialog, which) -> {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                hasPermission(permissions);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                finish();
                                                break;
                                        }
                                    });
                        }

                        else {
                            TrackiToast.Message
                                    .showLong(this, "Go to settings and enable permissions");
                        }

                    }
                }

                // Check for both permissions

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == StartLocationAlert.REQUEST_CHECK_SETTINGS) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        init();
                        break;
                    case Activity.RESULT_CANCELED:
                        locationAlert.settingRequest();
                        break;
                    default:
                        break;
                }
            }
            else  if (requestCode == AppConstants.REQUEST_TIME_ZONE) {
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        init();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void showAutoStartDialog() {
        dialog = new DoubleButtonDialog(this,
                false,
                null,
                "Please enable auto start feature for " + getString(R.string.app_name) + " to work properly.",
                getString(android.R.string.ok),
                null,
                new OnClickListener() {
                    @Override
                    public void onClickCancel() {

                    }

                    @Override
                    public void onClick() {
                        AutoStartHelper.getInstance().getAutoStartPermission(SplashActivity.this, preferencesHelper);
                    }
                });
        dialog.show();
    }

    private void showPopUp() {
        String manufacturer = android.os.Build.MANUFACTURER;
        switch (manufacturer.toLowerCase()) {
            case "xiaomi":
            case "oppo":
            case "vivo":
            case "letv":
            case "honor":
                hitApi();
                break;
            default:
                hitApi();
                break;
        }

    }

    private void initLocation() {
        try {
            if (locationAlert == null) {
                locationAlert = new StartLocationAlert(this);
            }
        } catch (Exception e) {
        }
    }



    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }

        super.onDestroy();
    }
    public void addDataContentProvider(){
//        ContentValues values = new ContentValues();
//        values.put(SharedDataContentProvider.data,"");
//        values.put(SharedDataContentProvider.id,1);
//        Uri uri = getContentResolver().insert(SharedDataContentProvider.CONTENT_URI, values);
    }
}