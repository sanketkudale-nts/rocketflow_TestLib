package com.rocketflyer.rocketflow.ui.trackingbuddy.buddydetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.request.RequestOptions;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Buddy;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityTrackingBuddyDetailBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.common.DoubleButtonDialog;
import com.tracki.ui.common.OnClickListener;
import com.tracki.ui.custom.CircleTransform;
import com.tracki.ui.custom.GlideApp;
import com.tracki.ui.trackingbuddy.buddydetail.TrackingBuddyDetailNavigator;
import com.tracki.ui.trackingbuddy.buddydetail.TrackingBuddyDetailViewModel;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static com.tracki.utils.AppConstants.Extra.EXTRA_BUDDY;

/**
 * Created by rahul on 9/10/18
 */
public class TrackingBuddyDetailActivity extends BaseActivity<ActivityTrackingBuddyDetailBinding, TrackingBuddyDetailViewModel>
        implements TrackingBuddyDetailNavigator {

    @Inject
    HttpManager httpManager;
    @Inject
    TrackingBuddyDetailViewModel mTrackingBuddyDetailViewModel;
    ActivityTrackingBuddyDetailBinding mActivityTrackingBuddyDetailBinding;
    private Buddy buddy;
    private Api api;

    public static Intent newIntent(Context context) {
        return new Intent(context, TrackingBuddyDetailActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tracking_buddy_detail;
    }

    @Override
    public TrackingBuddyDetailViewModel getViewModel() {
        return mTrackingBuddyDetailViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTrackingBuddyDetailBinding = getViewDataBinding();
        mTrackingBuddyDetailViewModel.setNavigator(this);
        setUp();
    }

    private void setUp() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_BUDDY)) {
                buddy = (Buddy) intent.getSerializableExtra(EXTRA_BUDDY);
            }
            if (intent.hasExtra(AppConstants.Extra.EXTRA_SHOW_CANCEL_BUTTON)) {
                mActivityTrackingBuddyDetailBinding.tvCancelRequest.setVisibility(View.VISIBLE);
            }
            setToolbar(mActivityTrackingBuddyDetailBinding.toolbar, buddy.getName());
            GlideApp.with(this)
                    .asBitmap()
                    .load(buddy.getProfileImage())
                    .apply(new RequestOptions()
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.ic_placeholder_pic))
                    .error(R.drawable.ic_placeholder_pic)
                    .into(mActivityTrackingBuddyDetailBinding.ivBuddyProfilePic);

            mActivityTrackingBuddyDetailBinding.edFullName.setText(buddy.getName());
            if (buddy.getEmail() != null) {
                mActivityTrackingBuddyDetailBinding.edEmailId.setText(buddy.getEmail());
            } else {
                mActivityTrackingBuddyDetailBinding.edEmailId.setVisibility(View.GONE);
            }
            mActivityTrackingBuddyDetailBinding.edMobileNumber.setText(buddy.getMobile());

            mActivityTrackingBuddyDetailBinding.edFullName.setEnabled(false);
            mActivityTrackingBuddyDetailBinding.edEmailId.setEnabled(false);
            mActivityTrackingBuddyDetailBinding.edMobileNumber.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        if (item.getItemId() == R.id.action_call) {
            CommonUtils.openDialer(this, buddy.getMobile());
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayPopUp() {
        DoubleButtonDialog dialog = new DoubleButtonDialog(this,
                true,
                null,
                getString(R.string.cancel_buddy_tracking_request),
                getString(R.string.yes),
                getString(R.string.no),
                new OnClickListener() {
                    @Override
                    public void onClickCancel() {

                    }

                    @Override
                    public void onClick() {
                        api = TrackiApplication.getApiMap().get(ApiType.CANCEL_REQUEST);
                        if (api != null) {
                            showLoading();
                            mTrackingBuddyDetailViewModel.cancelApi(httpManager, api, buddy.getBuddyId());
                        }
                    }
                });
        dialog.show();
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == AppConstants.REQUEST_CODE_CALL_BUDDY) {
            if (grantResults.length > 0) {
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted) {
                    CommonUtils.call(TrackingBuddyDetailActivity.this, buddy.getMobile());
                    *//*displayPopUp();*//*
                } else {
                    TrackiToast.MessageResponse.showLong(this, "Permission denied !!!");
                }
            }
        }
    }*/

    @Override
    public void cancelTrackRequest() {
        displayPopUp();
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result,
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
}
