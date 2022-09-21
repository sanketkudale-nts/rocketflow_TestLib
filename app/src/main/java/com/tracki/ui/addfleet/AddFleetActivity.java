package com.tracki.ui.addfleet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.model.request.AddFleetRequest;
import com.tracki.data.model.request.UpdateFileRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Fleet;
import com.tracki.data.model.response.config.ProfileResponse;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ActivityAddFleetBinding;
import com.tracki.ui.base.BaseActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.FileType;
import com.tracki.utils.TrackiToast;
import com.tracki.utils.image_utility.Compressor;
import com.tracki.utils.image_utility.ImagePicker;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.tracki.utils.AppConstants.Extra.EXTRA_FLEET;

/**
 * Created by rahul on 6/9/18
 */
public class AddFleetActivity extends BaseActivity<ActivityAddFleetBinding, AddFleetActivityViewModel>
        implements AddFleetNavigator {


    private static final int REQUEST_READ_STORAGE = 3;
    private static final int PICK_IMAGE_FILE_ID = 235;
    @Inject
    AddFleetActivityViewModel mAddFleetActivityViewModel;
    @Inject
    HttpManager httpManager;
    private ActivityAddFleetBinding mActivityAddFleetBinding;
    private File actualImage;
    private File compressedImage;
    private String imageUrl = "";
    private Menu menu;
    private String fleetId;
    private Snackbar snackBar;

    public static Intent newIntent(Context context) {
        return new Intent(context, AddFleetActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAddFleetBinding = getViewDataBinding();
        mAddFleetActivityViewModel.setNavigator(this);

        Toolbar toolbar = mActivityAddFleetBinding.toolbar;
        setToolbar(toolbar, getString(R.string.add_fleet));
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_FLEET)) {
                Fleet fleet = (Fleet) intent.getSerializableExtra(EXTRA_FLEET);
                setToolbar(toolbar, fleet.getFleetName());
                mActivityAddFleetBinding.btnSendInvite.setVisibility(View.GONE);
                mActivityAddFleetBinding.edRegistrationNo.setText(fleet.getRefId());
                mActivityAddFleetBinding.edModel.setText(fleet.getFleetName());
                if (fleet.getFleetImg() != null && !(fleet.getFleetImg().equals(""))) {
                    String[] spl = fleet.getFleetImg().split("/");
                    mActivityAddFleetBinding.tvFleetPicUrl.setText(spl[spl.length - 1]);
                }
                fleetId = fleet.getFleetId();
                imageUrl = fleet.getFleetImg();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (getIntent().hasExtra(EXTRA_FLEET)) {
            getMenuInflater().inflate(R.menu.fleet_profile, menu);
            this.menu = menu;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        if (item.getItemId() == R.id.action_edit) {
            validateViews();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_fleet;
    }

    @Override
    public AddFleetActivityViewModel getViewModel() {
        return mAddFleetActivityViewModel;
    }

    @Override
    public void validateViews() {
        try {
            String edRegistrationNo = mActivityAddFleetBinding.edRegistrationNo.getText().toString().trim();
            String edModel = mActivityAddFleetBinding.edModel.getText().toString().trim();

            if (mAddFleetActivityViewModel.isViewEmpty(edRegistrationNo)) {
                TrackiToast.Message.showShort(this,getString(R.string.invalid_registration_no));
                return;
            }
            if (mAddFleetActivityViewModel.isViewEmpty(edModel)) {
                TrackiToast.Message.showShort(this,getString(R.string.model_cannot_be_empty));
                return;
            }

            hideKeyboard();
            showLoading();
            AddFleetRequest addFleetRequest = new AddFleetRequest(imageUrl, edModel, edRegistrationNo, fleetId);
            Api api;
            if (getIntent().hasExtra(EXTRA_FLEET)) {
                api = TrackiApplication.getApiMap().get(ApiType.UPDATE_FLEET);
                mAddFleetActivityViewModel.addFleet(httpManager, addFleetRequest, api);
            } else {
                api = TrackiApplication.getApiMap().get(ApiType.ADD_FLEET);
                mAddFleetActivityViewModel.addFleet(httpManager, addFleetRequest, api);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void uploadImage() {
        onPickImage();
    }

    @Override
    public void handleFleetPicResponse(ApiCallback callback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            ProfileResponse profileResponse = new Gson().fromJson(String.valueOf(result), ProfileResponse.class);
            if (profileResponse != null) {
                imageUrl = profileResponse.getImageUrl();
                if (imageUrl != null && !(imageUrl.equals(""))) {
                    String[] spl = imageUrl.split("/");
                    mActivityAddFleetBinding.tvFleetPicUrl.setText(spl[spl.length - 1]);
                }
            }
        }
    }

    public void onPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                proceedToImagePicking();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.CAMERA}, REQUEST_READ_STORAGE);
            }
        } else {
            proceedToImagePicking();
        }
    }

    private void proceedToImagePicking() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_FILE_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                proceedToImagePicking();
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_FILE_ID) {
            actualImage = ImagePicker.getImageFileToUpload(this, resultCode, data);
            compressImage();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressLint("CheckResult")
    private void compressImage() {
        if (actualImage == null) {
            TrackiToast.Message.showShort(this, "Please choose an image!");
        } else {
            new Compressor(this)
                    .compressToFileAsFlowable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(file -> {
                        compressedImage = file;
                        showLoading();
                        UpdateFileRequest updateFileRequest = new UpdateFileRequest(compressedImage, FileType.FLEET, "");
                        Api api = TrackiApplication.getApiMap().get(ApiType.UPLOAD_FILE_AGAINEST_ENTITY);
                        mAddFleetActivityViewModel.uploadFleetPic(updateFileRequest, httpManager, api);
                    }, throwable -> {
                        throwable.printStackTrace();
                        TrackiToast.Message.showShort(AddFleetActivity.this, throwable.getMessage());
                    });
        }
    }

    @Override
    public void handleResponse(@Nullable ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
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
        snackBar= CommonUtils.showNetWorkConnectionIssue(  mActivityAddFleetBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }
}
