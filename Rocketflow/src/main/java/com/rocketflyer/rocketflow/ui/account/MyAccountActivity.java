package com.rocketflyer.rocketflow.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.ProfileInfo;
import com.tracki.data.model.response.config.ProfileResponse;
import com.tracki.data.model.response.config.RoleConfigData;
import com.tracki.data.model.response.config.Statistics;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityMyAccountBinding;
import com.tracki.ui.account.MyAccountNavigator;
import com.tracki.ui.account.MyAccountViewModel;
import com.tracki.ui.accountlist.AccountListActivity;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.custom.CircleTransform;
import com.tracki.ui.custom.GlideApp;
import com.tracki.ui.facility.ServicesActivity;
import com.tracki.ui.myDocument.MyDocumentActivity;
import com.tracki.ui.myInventory.MyInventoryActivity;
import com.tracki.ui.profile.MyProfileActivity;
import com.tracki.ui.trackingbuddy.TrackingBuddyActivity;
import com.tracki.ui.wallet.WalletActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.NextScreen;
import com.tracki.utils.TrackiToast;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import static com.tracki.utils.AppConstants.REQUEST_CODE_PROFILE;

/**
 * Created by rahul on 3/10/18
 */
public class MyAccountActivity extends BaseActivity<ActivityMyAccountBinding, MyAccountViewModel>
        implements MyAccountNavigator, View.OnClickListener {

    @Inject
    MyAccountViewModel mMyAccountViewModel;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;

    private Api api;
    private ActivityMyAccountBinding mActivityMyAccountBinding;
    private Snackbar snackBar;

    @Override
    public void networkAvailable() {
        if (snackBar != null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityMyAccountBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection));
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, MyAccountActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_account;
    }

    @Override
    public MyAccountViewModel getViewModel() {
        return mMyAccountViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMyAccountBinding = getViewDataBinding();
        mMyAccountViewModel.setNavigator(this);
        api = TrackiApplication.getApiMap().get(ApiType.MY_PROFILE);
        setUp();
    }

    private void setUp() {
        setToolbar(mActivityMyAccountBinding.toolbar, getString(R.string.my_account));
        RelativeLayout rvMyProfile = mActivityMyAccountBinding.rvMyProfile;
        RelativeLayout rvTrackingBuddy = mActivityMyAccountBinding.rvTrackingBuddy;
        RelativeLayout rvChangePassword = mActivityMyAccountBinding.rvChangePassword;
        RelativeLayout rvSwitchAccount = mActivityMyAccountBinding.rvSwitchAccount;
        RelativeLayout rvSwitchUserRole = mActivityMyAccountBinding.rvSwitchUserRole;
        RelativeLayout rvMyWallet = mActivityMyAccountBinding.rvMyWallet;
        RelativeLayout rvUpdateServices = mActivityMyAccountBinding.rvUpdateServices;
        RelativeLayout rvMyDocuments = mActivityMyAccountBinding.rvMyDocument;
        rvMyProfile.setOnClickListener(this);
        rvTrackingBuddy.setOnClickListener(this);
        rvChangePassword.setOnClickListener(this);
        rvSwitchAccount.setOnClickListener(this);
        rvSwitchUserRole.setOnClickListener(this);
        rvMyWallet.setOnClickListener(this);
        rvUpdateServices.setOnClickListener(this);
        rvMyDocuments.setOnClickListener(this);

        getProfile();
        if (preferencesHelper.getServicePref()) {
            rvUpdateServices.setVisibility(View.VISIBLE);
        } else {
            rvUpdateServices.setVisibility(View.GONE);
        }
        /*if(preferencesHelper.getEnableWalletFlag()){
            rvMyWallet.setVisibility(View.VISIBLE);
        }else{
            rvMyWallet.setVisibility(View.GONE);
        }*/
//        if(preferencesHelper.getUserTypeList()!=null&&preferencesHelper.getUserTypeList().size()>1){
//            rvSwitchUserRole.setVisibility(View.VISIBLE);
//        }else{
//            rvSwitchUserRole.setVisibility(View.GONE);
//        }
        if (preferencesHelper.getAccountList() != null && preferencesHelper.getAccountList().size() > 1) {
            rvSwitchAccount.setVisibility(View.VISIBLE);
        } else {
            rvSwitchAccount.setVisibility(View.GONE);
        }
        if (preferencesHelper.getBuddyListing()) {
            rvTrackingBuddy.setVisibility(View.VISIBLE);
        } else {
            rvTrackingBuddy.setVisibility(View.GONE);
        }
    }

    private void getProfile() {
        showLoading();
        // api hit
        mMyAccountViewModel.getProfile(httpManager, api);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rvMyProfile) {
            startActivityForResult(MyProfileActivity.newIntent(this)
                            .putExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE, true),
                    REQUEST_CODE_PROFILE);
                /*case R.id.tvMyProfile:
                startActivity(MyProfileActivity.newIntent(this));
                break;*/
        } else if (id == R.id.rvTrackingBuddy) {
            startActivity(TrackingBuddyActivity.newIntent(this));
        } else if (id == R.id.rvChangePassword) {
            TrackiToast.Message.showShort(this, "Coming Soon");
//                startActivity(ChangePasswordActivity.newIntent(this));
        } else if (id == R.id.rvSwitchAccount) {
            Intent intent = new Intent(MyAccountActivity.this, AccountListActivity.class);
            intent.putExtra("from", NextScreen.MY_PROFILE.name());
            startActivity(intent);
        } else if (id == R.id.rvSwitchUserRole) {
        } else if (id == R.id.rvMyWallet) {
            Intent intentScan = WalletActivity.Companion.newIntent(this);
            startActivity(intentScan);
        } else if (id == R.id.rvMyDocument) {/*Intent rvUpdateServices = new Intent(this, ServicesActivity.class);
                rvUpdateServices.putExtra(AppConstants.Extra.FROM, "indirect");*/
            Intent rvDocuments = new Intent(this, MyDocumentActivity.class);
            startActivity(rvDocuments);
        } else if (id == R.id.rvUpdateServices) {
            Intent rvUpdateServices = new Intent(this, ServicesActivity.class);
            rvUpdateServices.putExtra(AppConstants.Extra.FROM, "indirect");
//              Intent rvUpdateServices = new Intent(this, MyInventoryActivity.class);
            startActivity(rvUpdateServices);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE) {
            if (resultCode == RESULT_OK) {
                getProfile();
            }
        }
    }

    private void showHideWalletAndDocument(@NonNull String roleId) {
        List<RoleConfigData> listRolls = preferencesHelper.getRoleConfigDataList();
        if (!roleId.isEmpty()&&!listRolls.isEmpty()) {
            RoleConfigData roleConfigData = new RoleConfigData();
            roleConfigData.setRoleId(roleId);
            if (listRolls.contains(roleConfigData)) {
                int index = listRolls.indexOf(roleConfigData);
                if (index != -1) {
                    if (listRolls.get(index).getEnableWallet()) {
                        mActivityMyAccountBinding.rvMyWallet.setVisibility(View.VISIBLE);

                    }else{
                        mActivityMyAccountBinding.rvMyWallet.setVisibility(View.GONE);
                    }
                    if (listRolls.get(index).getEnableDocument()) {
                        mActivityMyAccountBinding.rvMyDocument.setVisibility(View.VISIBLE);
                    }else{
                        mActivityMyAccountBinding.rvMyDocument.setVisibility(View.GONE);
                    }

                }else{
                    mActivityMyAccountBinding.rvMyDocument.setVisibility(View.GONE);
                    mActivityMyAccountBinding.rvMyWallet.setVisibility(View.GONE);
                }
            }else{
                mActivityMyAccountBinding.rvMyDocument.setVisibility(View.GONE);
                mActivityMyAccountBinding.rvMyWallet.setVisibility(View.GONE);
            }

        }else{
            mActivityMyAccountBinding.rvMyDocument.setVisibility(View.GONE);
            mActivityMyAccountBinding.rvMyWallet.setVisibility(View.GONE);
        }

    }

    @Override
    public void handleResponse(@Nullable ApiCallback callback,
                               @Nullable Object result,
                               @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            ProfileResponse profileResponse = new Gson().fromJson(String.valueOf(result), ProfileResponse.class);
            if (profileResponse != null) {
                ProfileInfo profileInfo = profileResponse.getProfileInfo();
                if (profileInfo != null) {
                    //save data into the preferences
                    if (profileInfo.getUserId() == null || profileInfo.getUserId().isEmpty())
                        profileInfo.setUserId(preferencesHelper.getUserDetail().getUserId());
                    if (profileInfo.getRoleId() == null || profileInfo.getRoleId().isEmpty())
                        profileInfo.setRoleId(preferencesHelper.getUserDetail().getRoleId());
                    preferencesHelper.setUserDetail(profileInfo);
                    if(profileInfo.getRoleId()!=null)
                    showHideWalletAndDocument(profileInfo.getRoleId());
                    String name = "";
                    name = profileInfo.getName();
//                    if (!preferencesHelper.getUserType().isEmpty()) {
//
//                        String role = preferencesHelper.getUserType().equals(UserType.DRIVER.name()) ? "EXECUTIVE" : preferencesHelper.getUserType();
//                        name =name+" ("+role.charAt(0) + "".toUpperCase().trim() + role.substring(1).toLowerCase() +")";
//                    }

                    mActivityMyAccountBinding.tvUserName.setText(name);
                    mActivityMyAccountBinding.tvEmail.setText(profileInfo.getEmail());
                    GlideApp.with(this)
                            .asBitmap()
                            .load(profileInfo.getProfileImg())
                            .apply(new RequestOptions()
                                    .transform(new CircleTransform())
                                    .placeholder(R.drawable.ic_placeholder_pic))
                            .error(R.drawable.ic_placeholder_pic)
                            .into(mActivityMyAccountBinding.ivProfileImg);
                    Statistics stats = profileInfo.getStatistics();
                    double distance = 0;
                    int trips = 0;
                    int alertCount = 0;
                    if (stats != null) {
                        distance = stats.getTotalDistanceInKms();
                        trips = stats.getTotalTrips();
                        HashMap<String, Integer> map = stats.getEventMap();
                        if (map != null) {
                            Set<String> keys = map.keySet();
                            for (String key : keys) {
                                alertCount += map.get(key);
                            }
                        }
                    }
                    mActivityMyAccountBinding.tvDistance.setText(String.format("%s", distance));
                    mActivityMyAccountBinding.tvTrips.setText(String.format("%s", trips));
                    mActivityMyAccountBinding.tvAlerts.setText(String.format("%s", alertCount));
                }
            }
        }
    }


}
