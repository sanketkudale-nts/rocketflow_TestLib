package com.rocketflyer.rocketflow.ui.changemobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.model.BaseResponse;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityChangeMobileBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.changemobile.ChangeMobileNavigator;
import com.tracki.ui.changemobile.ChangeMobileViewModel;
import com.tracki.ui.otp.OtpActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.TrackiToast;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static com.tracki.utils.AppConstants.Extra.NUMBER_EXTRA;

/**
 * Created by rahul on 4/10/18
 */
public class ChangeMobileActivity extends BaseActivity<ActivityChangeMobileBinding, ChangeMobileViewModel>
        implements ChangeMobileNavigator {


    @Inject
    ChangeMobileViewModel mChangeMobileViewModel;
    @Inject
    HttpManager httpManager;

    private String mobile, enteredMobile;
    private ActivityChangeMobileBinding mActivityChangeMobileBinding;
    private Snackbar snackBar;

    public static Intent newIntent(Context context) {
        return new Intent(context, ChangeMobileActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_mobile;
    }

    @Override
    public ChangeMobileViewModel getViewModel() {
        return mChangeMobileViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityChangeMobileBinding = getViewDataBinding();
        mChangeMobileViewModel.setNavigator(this);
        if (getIntent() != null && getIntent().hasExtra(NUMBER_EXTRA)) {
            mobile = getIntent().getStringExtra(NUMBER_EXTRA);
            mActivityChangeMobileBinding.edMobile.setText(mobile);
            mActivityChangeMobileBinding.edMobile.setSelection(mobile.length());
        }

        setToolbar(mActivityChangeMobileBinding.toolbar, getString(R.string.change_mobile));
    }

    @Override
    public void onProceedClick() {
        String enteredMobile = mActivityChangeMobileBinding.edMobile.getText().toString();
        if (!mobile.equals(enteredMobile)) {
            validateMobile(enteredMobile);
        } else {
            onBackPressed();
        }
        //TODO open otp screen after validation

    }

    private void validateMobile(String enteredMobile) {
        if (CommonUtils.isViewNullOrEmpty(enteredMobile)) {
            TrackiToast.Message.showShort(this,getString(R.string.field_cannot_be_empty));
            return;
        }
        if (!mChangeMobileViewModel.isMobileValid(enteredMobile)) {
            TrackiToast.Message.showShort(this,getString(R.string.invalid_mobile));
            return;
        }

        hideKeyboard();
        showLoading();
        this.enteredMobile = enteredMobile;
        Api apiUrl = TrackiApplication.getApiMap().get(ApiType.LOGIN);
        mChangeMobileViewModel.changeMobile(enteredMobile, httpManager, apiUrl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void handleResponse(@NotNull ApiCallback apiCallback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            BaseResponse baseResponse = new Gson().fromJson(String.valueOf(result), BaseResponse.class);
            if (baseResponse.getNextScreen() != null) {
//                if (baseResponse.getNextScreen() == NextScreen.VERIFY_MOBILE) {
                Intent intent = OtpActivity.newIntent(this);
                intent.putExtra(AppConstants.Extra.NUMBER_EXTRA, this.enteredMobile);
                intent.putExtra(AppConstants.Extra.EXTRA_CHANGE_MOBILE, true);
                startActivityForResult(intent, AppConstants.REQUEST_CODE_CHANGE_MOBILE);
//                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.REQUEST_CODE_CHANGE_MOBILE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, new Intent().putExtra(AppConstants.Extra.NUMBER_EXTRA, this.enteredMobile));
                    finish();
                }
                break;
        }
    }
    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar= CommonUtils.showNetWorkConnectionIssue( mActivityChangeMobileBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
}
