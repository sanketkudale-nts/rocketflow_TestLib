package com.rocketflyer.rocketflow.ui.register;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.request.RegisterRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.ConfigResponse;
import com.tracki.data.model.response.config.OtpResponse;
import com.tracki.data.model.response.config.ProfileInfo;
import com.tracki.data.model.response.config.SignUpResponse;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityRegisterBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.main.MainActivity;
import com.tracki.ui.otp.MySMSBroadcastReceiver;
import com.tracki.ui.register.RegisterNavigator;
import com.tracki.ui.register.RegisterViewModel;
import com.tracki.ui.roleselection.SelectionActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.Log;
import com.tracki.utils.NextScreen;
import com.tracki.utils.TrackiToast;
import com.trackthat.lib.TrackThat;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static com.tracki.utils.TrackiToast.Message.showShort;

/**
 * Created by rahul on 5/9/18
 */
public class RegisterActivity extends BaseActivity<ActivityRegisterBinding, RegisterViewModel>
        implements RegisterNavigator, MySMSBroadcastReceiver.OTPReceiveListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    @Inject
    RegisterViewModel mRegisterViewModel;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;

    private ActivityRegisterBinding mActivityRegisterBinding;
    private ProfileInfo userDetails;
    private String mMobile = "";
    private Snackbar snackBar;
    private MySMSBroadcastReceiver smsReceiver;
    private CountDownTimer cTimer = null;

    @Override
    public void networkAvailable() {
        if (snackBar != null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityRegisterBinding.llMain, getString(R.string.please_check_your_internet_connection));
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public RegisterViewModel getViewModel() {
        return mRegisterViewModel;
    }

    @Override
    public void validateAndRegister() {
        try {
            String name = mActivityRegisterBinding.etName.getText().toString();
            String email = mActivityRegisterBinding.etEmail.getText().toString();
            String mobile = mActivityRegisterBinding.etMobile.getText().toString();
            String etOtp = mActivityRegisterBinding.etOtp.getText().toString();


            if (mRegisterViewModel.isViewNullOrEmpty(name)) {
                TrackiToast.Message.showShort(this, getString(R.string.name_cannot_be_empty));
                return;
            }


            if (!email.isEmpty() && !mRegisterViewModel.isEmailValid(email)) {
                TrackiToast.Message.showShort(this, getString(R.string.invalid_email));
                return;
            }

            if (!mRegisterViewModel.isMobileValid(mobile)) {
                TrackiToast.Message.showShort(this, getString(R.string.invalid_mobile));
                return;
            }

            if (mRegisterViewModel.isViewNullOrEmpty(etOtp)) {
                TrackiToast.Message.showShort(this, getString(R.string.enter_otp));
                return;
            }
            if (!mRegisterViewModel.isValidOtp(etOtp)) {
                TrackiToast.Message.showShort(this, getString(R.string.invalid_otp));
                return;
            }

            hideKeyboard();
            showLoading();
            userDetails = new ProfileInfo();
            userDetails.setName(name);
            if (!email.isEmpty())
                userDetails.setEmail(email);
            userDetails.setMobile(mobile);
            mMobile = mobile;
            RegisterRequest userRequest = new RegisterRequest(name, email,
                    mobile, etOtp);

            Api api = TrackiApplication.getApiMap().get(ApiType.CREATE_DRAFT);
            mRegisterViewModel.registerUser(userRequest, httpManager, api);

        } catch (Exception e) {
            Log.e(TAG, "methodName: validateAndRegister() error is: " + e);
        }
    }

    @Override
    public void onClickResend() {
        hideKeyboard();
        showLoading();
        cancelTimer();
        Api apiUrl = TrackiApplication.getApiMap().get(ApiType.LOGIN);
        mRegisterViewModel.login(mMobile, NextScreen.LOGIN, httpManager, apiUrl);

    }

    @Override
    public void handleResendOtpResponse(ApiCallback apiCallback, Object result, @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            showShort(this, "Otp send successfully");
            startTimer();
            mRegisterViewModel.getRetry().set(false);
            mRegisterViewModel.getTimerVisibility().set(true);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityRegisterBinding = getViewDataBinding();
        mRegisterViewModel.setNavigator(this);
        setToolbar(mActivityRegisterBinding.toolbar, "Sign Up");
        startTimer();
        startSmsListener();
        mMobile=getIntent().getStringExtra(AppConstants.MOBILE);
        mActivityRegisterBinding.etMobile.setText(getIntent().getStringExtra(AppConstants.MOBILE));
        mActivityRegisterBinding.editMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        mActivityRegisterBinding.etMobile.setEnabled(false);

    }

    private void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    private void startTimer() {
        cTimer = new CountDownTimer(60000
                , 1000) {

            public void onTick(long millisUntilFinished) {
                long milisec = millisUntilFinished / 1000;
                String sec = "" + milisec;
                if (milisec < 10) {
                    sec = "0" + milisec;
                }
                mRegisterViewModel.getTimer().set("00:" + sec);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                cancelTimer();
                mRegisterViewModel.getTimer().set("01:00");
                mRegisterViewModel.getRetry().set(true);
                mActivityRegisterBinding.btnContinue.setVisibility(View.VISIBLE);
                mRegisterViewModel.getTimerVisibility().set(false);
            }

        }.start();
    }

    @Override
    public void handleResponse(@NonNull ApiCallback callback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            SignUpResponse baseResponse = new Gson().fromJson(String.valueOf(result), SignUpResponse.class);


            if (baseResponse != null && baseResponse.getNextScreen() != null) {
                if (baseResponse.getNextScreen() == NextScreen.SELECT_SIGNUP_ACCOUNT) {
                    if(baseResponse.getDraftId()!=null&&!baseResponse.getDraftId().isEmpty()) {
                        Intent intent = new Intent(this, SelectionActivity.class);
                        intent.putExtra(AppConstants.DRAFT_ID, baseResponse.getDraftId());
                        startActivity(intent);
                    }

                } else {

                }
            }
        }
    }

    @Override
    public void handleConfigResponse(ApiCallback callback, Object result, APIError error,
                                     NextScreen nextScreen, String sdkAccessId) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {

            if (sdkAccessId != null) {
                // send accessId to SDK
                TrackThat.setAccessId(sdkAccessId);
            }

            Gson gson = new Gson();
            ConfigResponse configResponse = gson.fromJson(String.valueOf(result), ConfigResponse.class);
            CommonUtils.saveConfigDetails(RegisterActivity.this, configResponse, preferencesHelper, "2");
            CommonUtils.otpgoToNext(RegisterActivity.this, configResponse.getNextScreen(), mMobile);
        }
    }


    @Override
    public void showTimeOutMessage(ApiCallback apiCallback) {
        showShort(this, AppConstants.MSG_REQUEST_TIME_OUT_TYR_AGAIN);
    }

    @Override
    public void onOTPReceived(@NotNull String otp) {
        mActivityRegisterBinding.etOtp.setText(otp);
    }

    @Override
    public void onOTPTimeOut() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(smsReceiver!=null)
            unregisterReceiver(smsReceiver);
    }

    private void startSmsListener() {
        try {
            smsReceiver = new MySMSBroadcastReceiver();
            smsReceiver.initOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            this.registerReceiver(smsReceiver, intentFilter);
            SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);
            Task task = client.startSmsRetriever();
            // Listen for success/failure of the start Task. If in a background thread, this
            // can be made blocking using Tasks.await(task, [timeout]);

            task.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

