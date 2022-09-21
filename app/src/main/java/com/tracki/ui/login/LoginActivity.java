package com.tracki.ui.login;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.ActionConfig;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Navigation;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityLoginBinding;
import com.tracki.ui.accountlist.AccountListActivity;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.deviceChange.DeviceChangeActivity;
import com.tracki.ui.otp.OtpActivity;
import com.tracki.ui.register.RegisterActivity;
import com.tracki.ui.roleselection.SelectionActivity;
import com.tracki.ui.webview.WebViewActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.Log;
import com.tracki.utils.NextScreen;
import com.tracki.utils.TrackiToast;

import javax.inject.Inject;

import static com.tracki.utils.AppConstants.Extra.EXTRA_LOGOUT;

/**
 * Created by rahul on 3/9/18
 */
public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel>
        implements LoginNavigator{

    @Inject
    LoginViewModel mLoginViewModel;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;

    private String mobile/*, countryCode*/;
    private EditText editText;
    private ActivityLoginBinding mActivityLoginBinding;
    private GoogleApiClient googleApiClient;
    private String TAG="LoginActivity";
    private int PHONE_NUMBER_RC=5000;
    private PhoneNumberCallBack<String> phoneNumberCallback;

    private Snackbar snackBar;

    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar= CommonUtils.showNetWorkConnectionIssue( mActivityLoginBinding.rlMain,getString(R.string.please_check_your_internet_connection));
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public LoginViewModel getViewModel() {
        return mLoginViewModel;
    }

    @Override
    public void login() {

        mobile = editText.getText().toString().trim();

//        countryCode = mActivityLoginBinding.etCountryCode.getText().toString();
//        if (mLoginViewModel.isCountryCodeInvalid(countryCode)) {
//            Toast.makeText(this, getString(R.string.invalid_country_code), Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (CommonUtils.isViewNullOrEmpty(mobile)) {
            TrackiToast.Message.showShort(this,getString(R.string.enter_mobile));
            return;
        }
        if (!mLoginViewModel.isMobileValid(mobile)) {
            TrackiToast.Message.showShort(this,getString(R.string.invalid_mobile));
            return;
        }

//        if (!mActivityLoginBinding.cbConsentForm.isChecked()) {
//            Toast.makeText(this, getString(R.string.age_confirmation_error), Toast.LENGTH_SHORT).show();
//            return;
//        }

        analyticsHelper.addEvent(AppConstants.Analytics.Events.EVENT_LOGIN_CLICKED, AppConstants.Analytics.Pages.PAGE_LOGIN);
        hideKeyboard();
        showLoading();
        Api apiUrl = TrackiApplication.getApiMap().get(ApiType.LOGIN);
//        Api apiUrl = new Api();
//        apiUrl.setName(ApiType.LOGIN);
//        apiUrl.setUrl(BuildConfig.BASE_URL+"sendOtp");
        mLoginViewModel.login(mobile, NextScreen.LOGIN, httpManager, apiUrl);
    }

    @Override
    public void handleResponse(@NonNull ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            SendOtpResponse baseResponse = new Gson().fromJson(String.valueOf(result), SendOtpResponse.class);
            if (baseResponse.getNextScreen() != null) {
                if(baseResponse.getUserAccounts()!=null && !baseResponse.getUserAccounts().isEmpty()){
                    preferencesHelper.saveAccountsList(baseResponse.getUserAccounts());
                }
                if (baseResponse.getNextScreen() == NextScreen.VERIFY_MOBILE) {
                    if(preferencesHelper.getAccessId().isEmpty()&&baseResponse.getUserAccounts()!=null&&!baseResponse.getUserAccounts().isEmpty())
                    {

                        if(baseResponse.getUserAccounts().get(0).getAccessId()!=null) {
                            String accessid = baseResponse.getUserAccounts().get(0).getAccessId();
                            if (accessid != null) {
                             preferencesHelper.setAccessId(accessid);
                            }
                        }
                    }
                    Intent intent = OtpActivity.newIntent(this);
                    intent.putExtra(AppConstants.Extra.NUMBER_EXTRA, mobile);
                    if (getIntent() != null && getIntent().hasExtra(EXTRA_LOGOUT)) {
                        intent.putExtra(AppConstants.Extra.EXTRA_LOGOUT, true);
                    }
                    startActivity(intent);
                }
                else if (baseResponse.getNextScreen() == NextScreen.SELECT_ACCOUNT) {

                    Intent intent=new Intent(LoginActivity.this, AccountListActivity.class);
                    intent.putExtra("mobile",mobile);
                    intent.putExtra("from",NextScreen.LOGIN.name());
                   // intent.putExtra("listaccounts",  (ArrayList<UserAccount>)baseResponse.getUserAccounts() );
                    startActivity(intent);

                } else if (baseResponse.getNextScreen() == NextScreen.SIGNUP) {

                    Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("mobile",mobile);
                    startActivity(intent);

                }else if (baseResponse.getNextScreen() == NextScreen.SELECT_SIGNUP_ACCOUNT) {
                    if(baseResponse.getDraftId()!=null&&!baseResponse.getDraftId().isEmpty()) {
                        Intent intent = new Intent(this, SelectionActivity.class);
                        intent.putExtra(AppConstants.DRAFT_ID, baseResponse.getDraftId());
                        startActivity(intent);
                    }

                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityLoginBinding = getViewDataBinding();
        mLoginViewModel.setNavigator(this);
        analyticsHelper.addEvent(AppConstants.Analytics.Pages.PAGE_LOGIN);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
         setToolbar(mActivityLoginBinding.toolbar,"");

        if (getIntent().hasExtra(EXTRA_LOGOUT)) {
            preferencesHelper.setDeviceId(CommonUtils.getIMEINumber(this));
            CommonUtils.getFcmToken(instanceIdResult -> {
                preferencesHelper.setFcmToken(instanceIdResult.getToken());
                Log.e("LOGIN", "FCMToken : " + instanceIdResult.getToken());
            });
           // CommonUtils.updateSharedContentProvider(this,preferencesHelper);
        }


        TextView tvPrivacyPolicy = mActivityLoginBinding.tvPrivacyPolicy;
        customTextView(tvPrivacyPolicy);
        editText = mActivityLoginBinding.etMobile;
        requestPhoneNumber(result -> {
            result=phoneNumberWithOutCountryCode(result);
            editText.setText(result);
            mobile=result;
        });
//        mActivityLoginBinding.etCountryCode.setText("+91");

//        String code = "+91";
//        editText.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code), null, null, null);
//        editText.setCompoundDrawablePadding((code.length() + code.length()) * 10);
        editText.setOnEditorActionListener(
                (v, actionId, event) -> {
                    // Identifier of the action. This will be either the identifier you supplied,
                    // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        login();
                        return true;
                    }
                    // Return true if you have consumed the action, else false.
                    return false;
                });
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(s.length()>=10){
//                    login();
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    public void requestPhoneNumber(PhoneNumberCallBack<String> callback) {
        phoneNumberCallback = callback;
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), PHONE_NUMBER_RC, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Could not start hint picker Intent");
        }
    }
    private String phoneNumberWithOutCountryCode(String phoneNumber) {
        String number = phoneNumber;
        if (phoneNumber.startsWith("+")) {
            if (phoneNumber.length() == 13) {
                number = phoneNumber.substring(3);
            } else if (phoneNumber.length() == 14) {
                number = phoneNumber.substring(4);
            }
        }

        if (phoneNumber.contains(" ")) {
            number = phoneNumber.replace("\\s", "");
        }
        return number;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_NUMBER_RC) {
            if (resultCode == RESULT_OK) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (phoneNumberCallback != null){
                    phoneNumberCallback.onSuccess(cred.getId());
                }
            }
            phoneNumberCallback = null;
        }
    }
    interface PhoneNumberCallBack<String>{
         void onSuccess(String mobile);
    }

    /**
     * Set the text as custom text
     *
     * @param view view that needs to be set
     */
    private void customTextView(TextView view) {
        Log.e("length","By registering I accept the ".length()+"");
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "Term of Services");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Navigation navigation = new Navigation();
                ActionConfig actionConfig = new ActionConfig();
                actionConfig.setActionUrl(TrackiApplication.getTncUrl());
                navigation.setActionConfig(actionConfig);
                navigation.setTitle("Term of Services");
                startActivity(WebViewActivity.Companion.newIntent(LoginActivity.this)
                        .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation));
            }
        }, spanTxt.length() - "Term of Services".length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.colorPrimary)),
                spanTxt.length() - "Term of Services".length(), spanTxt.length(), 0);
        spanTxt.append(" and");
        spanTxt.setSpan(new ForegroundColorSpan(Color.GRAY), 16, spanTxt.length(), 0);
        spanTxt.append(" Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Navigation navigation = new Navigation();
                ActionConfig actionConfig = new ActionConfig();
                actionConfig.setActionUrl(TrackiApplication.getPrivacyPolicyUrl());
                navigation.setActionConfig(actionConfig);
                navigation.setTitle("Privacy Policy");
                startActivity(WebViewActivity.Companion.newIntent(LoginActivity.this)
                        .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation));
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.colorPrimary)),
                spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferencesHelper.setAccessId(null);
        preferencesHelper.setLoginToken(null);
    }
}
