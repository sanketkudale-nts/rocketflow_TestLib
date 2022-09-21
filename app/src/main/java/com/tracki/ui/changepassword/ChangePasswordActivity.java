package com.tracki.ui.changepassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.databinding.ActivityChangePasswordBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.utils.CommonUtils;

import javax.inject.Inject;

/**
 * Created by rahul on 4/10/18
 */
public class ChangePasswordActivity extends BaseActivity<ActivityChangePasswordBinding, ChangePasswordViewModel> implements ChangePasswordNavigator {

    @Inject
    ChangePasswordViewModel mChangePasswordViewModel;

    ActivityChangePasswordBinding mActivityChangePasswordBinding;
    private Snackbar snackBar;

    public static Intent newIntent(Context context) {
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    public ChangePasswordViewModel getViewModel() {
        return mChangePasswordViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityChangePasswordBinding = getViewDataBinding();
        mChangePasswordViewModel.setNavigator(this);
        setToolbar(mActivityChangePasswordBinding.toolbar, getString(R.string.change_password));
    }

    @Override
    public void onProceedClick() {

    }
    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar= CommonUtils.showNetWorkConnectionIssue( mActivityChangePasswordBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
}
