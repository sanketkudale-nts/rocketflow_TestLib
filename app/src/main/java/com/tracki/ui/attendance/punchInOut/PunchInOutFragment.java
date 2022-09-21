package com.tracki.ui.attendance.punchInOut;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraCharacteristics;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;
import com.tracki.BR;
import com.tracki.BuildConfig;
import com.tracki.R;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.BaseResponse;
import com.tracki.data.model.request.Addrloc;
import com.tracki.data.model.request.Data;
import com.tracki.data.model.request.Location;
import com.tracki.data.model.request.PunchInOut;
import com.tracki.data.model.request.PunchRequest;
import com.tracki.data.model.response.config.FileUrlsResponse;
import com.tracki.data.model.response.config.GeoCoordinate;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.data.model.response.config.GeoFenceData;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.FragmentPunchInOutBinding;
import com.tracki.ui.adjusttime.AdjustTimeActivity;
import com.tracki.ui.attendance.AttendanceBaseFragment;
import com.tracki.ui.base.BaseFragment;
import com.tracki.ui.common.OnClickViews;
import com.tracki.ui.common.PunchInOutDialog;
import com.tracki.ui.custom.GlideApp;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.Log;
import com.tracki.utils.TrackiToast;
import com.tracki.utils.image_utility.Compressor;
import com.tracki.utils.image_utility.ImagePicker;
import com.trackthat.lib.TrackThat;
import com.trackthat.lib.internal.network.TrackThatCallback;
import com.trackthat.lib.models.ErrorResponse;
import com.trackthat.lib.models.SuccessResponse;
import com.trackthat.lib.models.TrackthatLocation;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PunchInOutFragment extends BaseFragment<FragmentPunchInOutBinding, PunchInOutViewModel>
        implements PunchInOutNavigator, OnClickViews {

    private static final String TAG = PunchInOutFragment.class.getSimpleName();
    private static final int CAMERA_PIC_REQUEST = 1277;
    public String imageUrl = null;
    @Inject
    LinearLayoutManager mLayoutManager;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    HttpManager httpManager;
    @Inject
    PreferencesHelper preferencesHelper;
    private MediaPlayer mediaPlayer = null;
    private AttendanceIdSendListener mListener;
    private File image = null;
    private PunchInOutViewModel mPunchInOutViewModel;
    private String[] permissionArray = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Uri uri;
    private File actualImage = null;
    private File compressedImage = null;
    private List<String> imageUrlList = new ArrayList<>();
    private PunchInOutDialog punchInOutDialog;
    private Button btnPunchInOut;
    private boolean noTrack = false;


    private GeoCoordinates currentLocation;
    private FragmentPunchInOutBinding mFragmentPunchInOutBinding;

    private ProgressDialog mProgressDialog;

    public static PunchInOutFragment newInstance() {
        Bundle args = new Bundle();
        PunchInOutFragment fragment = new PunchInOutFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_punch_in_out;
    }

    @Override
    public PunchInOutViewModel getViewModel() {
//        mPunchInOutViewModel = new ViewModelProvider(this).get(PunchInOutViewModel.class);
        mPunchInOutViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PunchInOutViewModel.class);
        return mPunchInOutViewModel;
    }

    @Override
    public void setUserVisibleHint(boolean isUserVisible) {
        super.setUserVisibleHint(isUserVisible);
        // when fragment visible to user and view is not null then enter here.
        CommonUtils.showLogMessage("e", "User Visible", "" + isUserVisible);
        if (isUserVisible && getRootView() != null) {
            CommonUtils.showLogMessage("e", "Pass ALL event", "" + isUserVisible);

            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        getCurrentLocation();
        getBaseActivity().requestCurrentLocation(locationCallback);
        if (getBaseActivity() != null) {
            getBaseActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (httpManager != null && mPunchInOutViewModel != null) {
                        showLoading();
                        mPunchInOutViewModel.getPunchInPunchOutData(httpManager);
                    }
                }
            });
        }


    }

    public void perFormSubmit(@NotNull PunchInOut event, String data) {
        showLoading();
        TrackThat.getCurrentLocation(
                new TrackThatCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse successResponse) {
                        TrackthatLocation loc = (TrackthatLocation) successResponse.getResponseObject();
                        if (getContext() != null && loc != null && loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {

                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            StringBuilder strReturnedAddress = null;
                            try {
                                List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                                if (addresses != null) {
                                    Address returnedAddress = addresses.get(0);
                                    strReturnedAddress = new StringBuilder("");

                                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            PunchRequest punchRequest = new PunchRequest();
                            punchRequest.setDate(System.currentTimeMillis());
                            Data dataObj = new Data();
                            dataObj.setRemarks(data);

                            if (imageUrl != null)
                                dataObj.setSelfie(imageUrl);
                            if (preferencesHelper.getIsTrackingLiveTrip()) {
                                noTrack = true;
                            }
                            punchRequest.setData(dataObj);
                            if (event == PunchInOut.PUNCH_IN) {
                                //  punchRequest.setDate(preferencesHelper.getPunchInTime());
//                               if(TrackThat.isTracking())
//                                   TrackThat.stopTracking();

//                                if (!noTrack && !preferencesHelper.getIdleTripActive()) {
//                                    if (preferencesHelper.getIsIdealTrackingEnable()) {
//                                        preferencesHelper.setIdleTripActive(true);
//                                        TrackThat.startTracking(null, false);
//                                    }
//                                }
                            } else {

//                                if (!noTrack && preferencesHelper.getIdleTripActive())
//                                    TrackThat.stopTracking();
                                //      preferencesHelper.setIdleTripActive(false);
                                //   punchRequest.setDate(preferencesHelper.getPunchOutTime());
                            }
                            punchRequest.setEvent(event);
                            punchRequest.setUserId(preferencesHelper.getUserDetail().getUserId());

                            Location locationobj = new Location();
                            locationobj.setLocationId(CommonUtils.genrateId());
                            locationobj.setLatitude(loc.getLatitude());
                            locationobj.setLongitude(loc.getLongitude());

                            Addrloc addrloc = new Addrloc();
                            if (strReturnedAddress != null)
                                addrloc.setAddress(strReturnedAddress.toString());
                            addrloc.setLocation(locationobj);
                            punchRequest.setLocation(addrloc);

                            //api hit
                            mPunchInOutViewModel.punch(httpManager, punchRequest, event);

                            final Runnable r = new Runnable() {
                                public void run() {
                                    btnPunchInOut.setEnabled(true);

                                }
                            };

                            new Handler().postDelayed(r, 2500);


                        }
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        TrackiToast.Message.showShort(getBaseActivity(), "Unable to get location. Try Again");
                    }
                });

    }

    @Override
    public void onClickSubmit(@NotNull PunchInOut event, String data) {
        if (getBaseActivity() != null) {
            getBaseActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    perFormSubmit(event, data);
                }
            });
        }

    }


    @Override
    public void autoPunchOut() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    /**
     * Method used to get the current location from the sdk.
     */
    private void getCurrentLocation() {
        TrackThat.getCurrentLocation(new TrackThatCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                TrackthatLocation loc = (TrackthatLocation) successResponse.getResponseObject();
                currentLocation = new GeoCoordinates();
                currentLocation.setLatitude(loc.getLatitude());
                currentLocation.setLongitude(loc.getLongitude());
                Log.e(TAG, "getCurrentLocation(): onSuccess: " + currentLocation);
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                currentLocation = null;
                Log.e(TAG, "onError: " + errorResponse.getErrorMessage());
            }
        });
    }

    public void perFormPunchInOut(String title, PunchInOut punchInOut) {
        getCurrentLocation();
        if (preferencesHelper.getEnablePunchGeofencing()) {
//            boolean isNotInGeo = false;

            if (currentLocation != null) {
//                if (preferencesHelper.getGeoFenceList() != null) {
//
//                    if (preferencesHelper.getGeoFenceList() != null) {
//                        List<GeoFenceData> geoFenceData = preferencesHelper.getGeoFenceList();
//                        for (GeoFenceData data : geoFenceData) {
//                            if (data != null) {
//                                if (Objects.requireNonNull(data.getCircleData()).getGeoCoordinates() != null) {
//                                    for (GeoCoordinate geocordinate : data.getCircleData().getGeoCoordinates()) {
//                                        if (CommonUtils.isPointOutSideCircle((int) data.getCircleData().getRadius(),
//                                                geocordinate.getLatitude(), geocordinate.getLongitude(),
//                                                currentLocation.getLatitude(), currentLocation.getLongitude())) {
//                                            isNotInGeo = true;
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//
//
//                        }
//                    }
//
//                }
//                if (isNotInGeo) {
//                    validatePunch(title, punchInOut);
//                } else {
//                    getBaseActivity().openDialougShowLocationError("You are not inside the assigned GeoFence to mark Attendance");
//                }
                //  currentLocation.getLatitude(), currentLocation.getLongitude();
                Location location = new Location();
                location.setLatitude(currentLocation.getLatitude());
                location.setLongitude(currentLocation.getLongitude());

                mPunchInOutViewModel.validateGeoPunchIn(httpManager, title, punchInOut, location, AppConstants.FROM_NORMAL);
            } else {

                TrackiToast.Message.showShort(getBaseActivity(), "Please wait .. getting current location ");
                if (TrackThat.getLastLocation() != null) {
                    currentLocation = new GeoCoordinates();
                    currentLocation.setLatitude(TrackThat.getLastLocation().getLatitude());
                    currentLocation.setLongitude(TrackThat.getLastLocation().getLongitude());
                }

            }

        } else {
            validatePunch(title, punchInOut);
        }
    }

    @Override
    public void onClickPunchInOut(String title, PunchInOut punchInOut) {
        // btnPunchInOut.setEnabled(false);

        if (getBaseActivity() != null) {
            getBaseActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    perFormPunchInOut(title, punchInOut);
                }
            });
        }



    }

    public void perFormPunch(String title, PunchInOut punchInOut) {
        if (getBaseActivity() != null) {
            if (punchInOut.equals(PunchInOut.PUNCH_IN))
                preferencesHelper.setPunchInTime(System.currentTimeMillis());
            else {
                preferencesHelper.setPunchOutTime(System.currentTimeMillis());
                preferencesHelper.setLastPunchOutTime(System.currentTimeMillis());
            }
            punchInOutDialog = new PunchInOutDialog(getBaseActivity(), preferencesHelper, title, "",
                    AppConstants.SUBMIT, punchInOut, this);
            punchInOutDialog.show();
            Window window = punchInOutDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void validatePunch(String title, PunchInOut punchInOut) {
        if (httpManager != null && mPunchInOutViewModel != null) {
            showLoading();
            mPunchInOutViewModel.validatePunch(httpManager, title, punchInOut);
        }
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, getContext())) {
            JSONConverter jsonConverter = new JSONConverter();
            PunchInPunchOutData punchInPunchOutData = (PunchInPunchOutData) jsonConverter.jsonToObject(result.toString(), PunchInPunchOutData.class);


            if (punchInPunchOutData != null) {
                long now = punchInPunchOutData.getTime();
                if (Math.abs(now - System.currentTimeMillis()) >= AppConstants.TIME_DIFF) {
                    // the device's time is wrong
                    // startErrorActivity();
                    startActivityForResult(new Intent(getBaseActivity(), AdjustTimeActivity.class), AppConstants.REQUEST_TIME_ZONE);
                }else{
                    if (punchInPunchOutData.getData() == null) {
                        perFormPunchIn();
                        if (mListener != null)
                            mListener.trackingIdVisible(null, false);
                    } else {
//                    PRESENT- Check if punch out time exists if yes show punch in otherwise show punch out screen
//                    ABSENT - Show blocker popup with message- you have already marked as absent in system , please contact system admin.
//                            ON_LEAVE - Show blocker popup- You are on leave today- can't punch in, please contact system admin.
//                    HOLIDAY- Show blocker popup- Can't punch in on holiday, please contact system admin.
//                    DAY_OFF- Allow punch in.
                        //PRESENT("Present"), ABSENT("Absent"), ON_LEAVE("On Leave"), HOLIDAY("Holiday"), DAY_OFF("Day Off")
                        if (punchInPunchOutData.getData().getStatus() != null && !punchInPunchOutData.getData().getStatus().isEmpty()) {
                            if (punchInPunchOutData.getData().getStatus().equals("ABSENT")) {
                                String message = "you have already marked as absent in system , please contact system admin.";
                                showLayOutForProhibition(message);
                                if (mListener != null)
                                    mListener.trackingIdVisible(null, false);
                            } else if (punchInPunchOutData.getData().getStatus().equals("ON_LEAVE")) {
                                String message = "You are on leave today- can't punch in, please contact system admin.";
                                showLayOutForProhibition(message);
                                if (mListener != null)
                                    mListener.trackingIdVisible(null, false);
                            } else if (punchInPunchOutData.getData().getStatus().equals("HOLIDAY")) {
                                String message = "Can't punch in on holiday, please contact system admin.";
                                showLayOutForProhibition(message);
                                if (mListener != null)
                                    mListener.trackingIdVisible(null, false);
                            } else if (punchInPunchOutData.getData().getStatus().equals("DAY_OFF")) {
                                String message = "Today is day off, please contact system admin.";
                                showLayOutForProhibition(message);
                                // perFormPunchIn();
                                if (mListener != null)
                                    mListener.trackingIdVisible(null, false);
                            } else if (punchInPunchOutData.getData().getStatus().equals("PRESENT")) {

                                if (punchInPunchOutData.getData().getPunchOutAt() != 0) {

                                    preferencesHelper.setPunchOutTime(punchInPunchOutData.getData().getPunchOutAt());
                                    preferencesHelper.setLastPunchOutTime(punchInPunchOutData.getData().getPunchOutAt());
                                    perFormPunchIn();
                                    if (preferencesHelper.getIdleTrackingInfo()!=null&&preferencesHelper.getIdleTrackingInfo().getMode()!=null&&preferencesHelper.getIdleTrackingInfo().getMode().equals("ON_PUNCH")&&preferencesHelper.getIsIdealTrackingEnable()) {
                                        if (TrackThat.isTracking()) {
                                            preferencesHelper.setIdleTripActive(false);
                                            TrackThat.stopTracking();
                                            //playSoundStopTracking();
                                            preferencesHelper.setPunchId(null);
                                            if (mListener != null)
                                                mListener.trackingIdVisible(null, false);
                                        }
                                    }

                                } else {
                                    mFragmentPunchInOutBinding.setIsProhibitionShow(false);
                                    mFragmentPunchInOutBinding.rlMain.setVisibility(View.VISIBLE);
                                    preferencesHelper.setPunchStatus(true);
                                    preferencesHelper.setPunchInTime(punchInPunchOutData.getData().getPunchInAt());
                                    if (punchInPunchOutData.getData().getPunchData() != null && punchInPunchOutData.getData().getPunchData().getPunchInData() != null) {
                                        if (punchInPunchOutData.getData().getPunchData().getPunchInData().getSelfie() != null) {
                                            imageUrl = punchInPunchOutData.getData().getPunchData().getPunchInData().getSelfie();
                                            preferencesHelper.setSelfieUrl(imageUrl);
                                            GlideApp.with(getBaseActivity()).load(imageUrl).circleCrop()
                                                    .into(mFragmentPunchInOutBinding.ivSelfie);

                                        }

                                    }
                                    if (preferencesHelper.getIdleTrackingInfo()!=null&&preferencesHelper.getIdleTrackingInfo().getMode()!=null&&preferencesHelper.getIdleTrackingInfo().getMode().equals("ON_PUNCH")&&preferencesHelper.getIsIdealTrackingEnable()) {
                                        if (!preferencesHelper.getIdleTripActive()) {
                                            preferencesHelper.setIdleTripActive(true);
                                            preferencesHelper.setPunchId(punchInPunchOutData.getData().getPunchId());
                                            if (!TrackThat.isTracking()) {
                                                TrackThat.startTracking(punchInPunchOutData.getData().getPunchId(), false);
                                                playSoundStartTracking();

                                            }

                                        }
                                        if (mListener != null) {
                                            if (punchInPunchOutData.getData().getPunchId() != null)
                                                mListener.trackingIdVisible(punchInPunchOutData.getData().getPunchId(), true);
                                        }
                                    }
                                    mPunchInOutViewModel.setUserDetails(preferencesHelper);
                                    //  CommonUtils.updateSharedContentProvider(getBaseActivity(), preferencesHelper);

                                }

                            }
                        }
                    }
                }


            }


        }


    }

    private void playSoundStartTracking() {
        if (preferencesHelper.getVoiceAlertsTracking()) {
            if (getBaseActivity() != null) {
                mediaPlayer = MediaPlayer.create(getBaseActivity(), R.raw.punch_in_for_day);
                mediaPlayer.start();
                new Handler().postDelayed(() -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                }, 2000);
            }
        }

    }

    private void playSoundStopTracking() {
        if (preferencesHelper.getVoiceAlertsTracking()) {
            if (getBaseActivity() != null) {
                mediaPlayer = MediaPlayer.create(getBaseActivity(), R.raw.punchout_for_day);
                mediaPlayer.start();
                new Handler().postDelayed(() -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                }, 2000);
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mediaPlayer = null;
    }

    private void showLayOutForProhibition(String message) {
        mFragmentPunchInOutBinding.setIsProhibitionShow(true);
        mFragmentPunchInOutBinding.rlMain.setVisibility(View.VISIBLE);
        mFragmentPunchInOutBinding.setMessage(message);

    }

    public void perFormPunchIn() {
        mFragmentPunchInOutBinding.setIsProhibitionShow(false);
        mFragmentPunchInOutBinding.rlMain.setVisibility(View.VISIBLE);
        preferencesHelper.setPunchStatus(false);
        //remove punch in and out time after punch out
        preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.PUNCH);
        preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.SELFIE_URL);
        if(getBaseActivity()!=null)
        GlideApp.with(getBaseActivity()).load(R.drawable.ic_placeholder_pic_grey).circleCrop()//*.transform(RoundedCorners(radius))*//*
                .into(mFragmentPunchInOutBinding.ivSelfie);
        mPunchInOutViewModel.setUserDetails(preferencesHelper);
        if (mListener != null)
            mListener.trackingIdVisible(null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPunchInOutViewModel.setNavigator(this);
        mFragmentPunchInOutBinding = getViewDataBinding();
        btnPunchInOut = mFragmentPunchInOutBinding.btnPunchInOut;


        //change
    }

    @Override
    public void handlePunchInOutResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                         @Nullable APIError error, PunchInOut event) {

        if (CommonUtils.handleResponse(apiCallback, error, result, getContext())) {
            BaseResponse baseResponse = new Gson().fromJson(String.valueOf(result), BaseResponse.class);
            if (preferencesHelper.getIsTrackingLiveTrip()) {
                noTrack = true;
            }

            if (event == PunchInOut.PUNCH_OUT) {
                preferencesHelper.setPunchOutTime(System.currentTimeMillis());
                preferencesHelper.setLastPunchOutTime(System.currentTimeMillis());
                preferencesHelper.setPunchStatus(false);
                //remove punch in and out time after punch out
                preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.PUNCH);
                preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.SELFIE_URL);
                Log.e("idletripactive", preferencesHelper.getIdleTripActive() + "");
                if (!noTrack && preferencesHelper.getIdleTripActive()) {
                    TrackThat.stopTracking();
                    playSoundStopTracking();
                    preferencesHelper.setPunchId(null);
                }

                preferencesHelper.setIdleTripActive(false);
                GlideApp.with(getBaseActivity()).load(R.drawable.ic_placeholder_pic_grey).circleCrop()/*.transform(RoundedCorners(radius))*/
                        .into(mFragmentPunchInOutBinding.ivSelfie);
                if (mListener != null) {
                    mListener.trackingIdVisible(baseResponse.getPunchId(), false);
                }
                //  CommonUtils.updateSharedContentProvider(getBaseActivity(), preferencesHelper);

            } else {
                if (!noTrack && !preferencesHelper.getIdleTripActive()) {
                    if (preferencesHelper.getIdleTrackingInfo()!=null&&preferencesHelper.getIdleTrackingInfo().getMode()!=null&&preferencesHelper.getIdleTrackingInfo().getMode().equals("ON_PUNCH")&&preferencesHelper.getIsIdealTrackingEnable()) {
                        preferencesHelper.setIdleTripActive(true);
                        TrackThat.startTracking(baseResponse.getPunchId(), false);
                        preferencesHelper.setPunchId(baseResponse.getPunchId());
                        playSoundStartTracking();
                        if (mListener != null) {
                            mListener.trackingIdVisible(baseResponse.getPunchId(), true);
                        }
                    }
                }
                preferencesHelper.setPunchStatus(true);
                preferencesHelper.setPunchInTime(System.currentTimeMillis());
                //  CommonUtils.updateSharedContentProvider(getBaseActivity(), preferencesHelper);
                //  CommonUtils.openApp(getBaseActivity(), "com.rocketflyer.calllogmanager", preferencesHelper);
            }

            mPunchInOutViewModel.setUserDetails(preferencesHelper);

        }
        hideLoading();

    }


    @Override
    public void handleImageResponse(ApiCallback apiCallback, Object result, APIError error) {
        if (CommonUtils.handleResponse(apiCallback, error, result, getContext())) {
            assert result != null;
            Log.i("STATUS", result.toString());
            FileUrlsResponse fileUrlsResponse = new Gson().fromJson(String.valueOf(result), FileUrlsResponse.class);
            HashMap<String, ArrayList<String>> imageResponseMap = fileUrlsResponse.getFilesUrl();

            imageUrlList = imageResponseMap.get(AppConstants.CAPTURED_KEY);
            imageUrl = imageUrlList.get(0);
            preferencesHelper.setSelfieUrl(imageUrl);
            if (punchInOutDialog != null && punchInOutDialog.isShowing()) {
                punchInOutDialog.loadImage();
            }

            GlideApp.with(getBaseActivity()).load(imageUrl).circleCrop()
                    .placeholder(R.drawable.ic_loading)
                    .into(mFragmentPunchInOutBinding.ivSelfie);
        }
    }

    @Override
    public void validatePunchIn(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error, String title, PunchInOut punchInOut) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, getContext())) {
            assert result != null;
            JSONConverter jsonConverter = new JSONConverter();
            PunchInPunchOutData punchInPunchOutData = (PunchInPunchOutData) jsonConverter.jsonToObject(result.toString(), PunchInPunchOutData.class);


            if (punchInPunchOutData != null) {
                long now = punchInPunchOutData.getTime();
                if (Math.abs(now - System.currentTimeMillis()) >= AppConstants.TIME_DIFF) {
                    // the device's time is wrong
                    // startErrorActivity();
                    startActivityForResult(new Intent(getBaseActivity(), AdjustTimeActivity.class), AppConstants.REQUEST_TIME_ZONE);
                }else{
                    if (punchInPunchOutData.getData() == null) {
                        perFormPunch(title, punchInOut);
                    } else {
//                    PRESENT- Check if punch out time exists if yes show punch in otherwise show punch out screen
//                    ABSENT - Show blocker popup with message- you have already marked as absent in system , please contact system admin.
//                            ON_LEAVE - Show blocker popup- You are on leave today- can't punch in, please contact system admin.
//                    HOLIDAY- Show blocker popup- Can't punch in on holiday, please contact system admin.
//                    DAY_OFF- Allow punch in.
                        //PRESENT("Present"), ABSENT("Absent"), ON_LEAVE("On Leave"), HOLIDAY("Holiday"), DAY_OFF("Day Off")
                        if (punchInPunchOutData.getData().getStatus() != null && !punchInPunchOutData.getData().getStatus().isEmpty()) {
                            if (punchInPunchOutData.getData().getStatus().equals("PRESENT")) {

                                if (punchInPunchOutData.getData().getPunchOutAt() != 0) {

                                    preferencesHelper.setPunchOutTime(punchInPunchOutData.getData().getPunchOutAt());
                                    preferencesHelper.setLastPunchOutTime(punchInPunchOutData.getData().getPunchOutAt());

                                    perFormPunchIn();

                                } else {
                                    mFragmentPunchInOutBinding.setIsProhibitionShow(false);
                                    mFragmentPunchInOutBinding.rlMain.setVisibility(View.VISIBLE);
                                    preferencesHelper.setPunchStatus(true);
                                    preferencesHelper.setPunchInTime(punchInPunchOutData.getData().getPunchInAt());
                                    if (punchInPunchOutData.getData().getPunchData() != null && punchInPunchOutData.getData().getPunchData().getPunchInData() != null) {
                                        if (punchInPunchOutData.getData().getPunchData().getPunchInData().getSelfie() != null) {
                                            imageUrl = punchInPunchOutData.getData().getPunchData().getPunchInData().getSelfie();
                                            preferencesHelper.setSelfieUrl(imageUrl);
                                            GlideApp.with(getBaseActivity()).load(imageUrl).circleCrop()
                                                    .into(mFragmentPunchInOutBinding.ivSelfie);
                                        }

                                    }
                                    mPunchInOutViewModel.setUserDetails(preferencesHelper);


                                }
                                if (preferencesHelper.getPunchStatus()) {
                                    if (punchInOut.equals(PunchInOut.PUNCH_IN)) {
                                        TrackiToast.Message.showShort(getContext(), "You already punch in ");
                                    } else {
                                        perFormPunch(title, punchInOut);
                                    }
                                } else {
                                    if (punchInOut.equals(PunchInOut.PUNCH_OUT)) {
                                        TrackiToast.Message.showShort(getContext(), "You already punch out ");
                                    } else {
                                        perFormPunch(title, punchInOut);
                                    }
                                }

                            }
                        }
                    }
                }


            }


        }
    }

    @Override
    public void validateGeoPunchIn(@NotNull ApiCallback callback, @org.jetbrains.annotations.Nullable Object result, @org.jetbrains.annotations.Nullable APIError error, String title, PunchInOut punchInOut, int from) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, getContext())) {
            if (from == AppConstants.FROM_NORMAL) {
                validatePunch(title, punchInOut);
            } else {
                if (hasPermission(permissionArray))
                    openCamera();
            }

        } else {
            if (getBaseActivity() != null)
                getBaseActivity().openDialougShowLocationError("You are not inside the assigned GeoFence to mark Attendance");
        }
    }


    @Override
    public void onCameraOpen(@NotNull PunchInOut event) {
        getCurrentLocation();
        if (preferencesHelper.getEnablePunchGeofencing()) {
//            boolean isNotInGeo = false;
//            if (preferencesHelper.getGeoFenceList() != null) {
//                List<GeoFenceData> geoFenceData = preferencesHelper.getGeoFenceList();
//                for (GeoFenceData data : geoFenceData) {
//                    for (GeoCoordinate geocordinate : data.getCircleData().getGeoCoordinates()) {
//                        if (CommonUtils.isPointOutSideCircle((int) data.getCircleData().getRadius(),
//                                geocordinate.getLatitude(), geocordinate.getLongitude(),
//                                currentLocation.getLatitude(), currentLocation.getLongitude())) {
//                            isNotInGeo = true;
//                            break;
//                        }
//                    }
//
//                }
//            }
            if (currentLocation != null) {
                Location location = new Location();
                location.setLatitude(currentLocation.getLatitude());
                location.setLongitude(currentLocation.getLongitude());

                mPunchInOutViewModel.validateGeoPunchIn(httpManager, "", event, location, AppConstants.FROM_CAMERA);
            } else {

                TrackiToast.Message.showShort(getBaseActivity(), "Please wait .. getting current location ");
                if (TrackThat.getLastLocation() != null) {
                    currentLocation = new GeoCoordinates();
                    currentLocation.setLatitude(TrackThat.getLastLocation().getLatitude());
                    currentLocation.setLongitude(TrackThat.getLastLocation().getLongitude());
                }
            }
//            if (isNotInGeo) {
//                if (hasPermission(permissionArray))
//                    openCamera();
//            } else {
//                getBaseActivity().openDialougShowLocationError("You are not inside the assigned GeoFence to mark Attendance");
//            }

        } else {
            if (hasPermission(permissionArray))
                openCamera();
        }

    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (android.location.Location location : locationResult.getLocations()) {
                if (location != null) {

                    currentLocation = new GeoCoordinates();
                    currentLocation.setLatitude(location.getLatitude());
                    currentLocation.setLongitude(location.getLongitude());
                    Log.e(TAG, "getCurrentLocation(): onSuccess: " + currentLocation);
                    if (getBaseActivity() != null)
                        getBaseActivity().removeLocationUpdates(locationCallback);
                }
            }
        }
    };

    private void openCamera() {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoIntent.resolveActivity(getBaseActivity().getPackageManager()) != null) {
            File file;
            try {
                file = createImageFile();
                if (file != null && file.exists()) {
                    uri = FileProvider.getUriForFile(getBaseActivity().getApplicationContext(),
                            "com.rocketflyer.rocketflow" + ".provider", file);
//                    uri = Uri.parse(new File(imageFilePath).toString());
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        photoIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK); // Tested on API 24 Android version 7.0(Samsung S6)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        photoIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK);// Tested on API 27 Android version 8.0(Nexus 6P)
                        photoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                    } else {
                        photoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1); // Tested API 21 Android version 5.0.1(Samsung S4)
                    }
                    photoIntent.putExtra("TEST", "String Extra");
                    startActivityForResult(photoIntent, CAMERA_PIC_REQUEST);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.PERMISSIONS_REQUEST_CODE_MULTIPLE) {

            HashMap<String, Integer> perms = new HashMap<String, Integer>();
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            if (grantResults != null) {
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);

                }

                Integer camPer = perms.get(Manifest.permission.CAMERA);
                Integer extStorPer = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                // Check for both permissions
                if ((camPer != null && camPer == PackageManager.PERMISSION_GRANTED) &&
                        (extStorPer != null && extStorPer == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "Camera and Storage permission granted");
                    openCamera();

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getBaseActivity(), Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(getBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showDialogOK("", (dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    getBaseActivity().hasPermission(permissions);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    // proceed with logic by disabling the related features or quit the app.
                                    getBaseActivity().finish();
                                    break;
                            }
                        });
                    }
                }
            }
        } else {
            TrackiToast.Message
                    .showLong(getBaseActivity(), "Please go to settings and enable permissions");
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //final File storageDir = Environment.getExternalStorageDirectory();
        final File storageDir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {

                File file = new File(image.getPath());
                if (file.exists()) {

                    Bitmap bm = ImagePicker.getImageResized(getBaseActivity(), uri);
                    // Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                    // Code to manage the bitmap
                    actualImage = CommonUtils.convertBitmapToFile(getBaseActivity(), bm,
                            "upload_" + Calendar.getInstance().getTimeInMillis() + ".jpg");
                    if (actualImage != null) {

                        new Compressor(getBaseActivity())
                                .compressToFileAsFlowable(actualImage)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(file1 -> {
                                    compressedImage = file1;

                                    ArrayList<File> images = new ArrayList<>();
                                    images.add(compressedImage);

                                    HashMap<String, ArrayList<File>> map = new HashMap<>();
                                    map.put(AppConstants.CAPTURED_KEY, images);

                                    mPunchInOutViewModel.uploadSelfie(httpManager, map);
                                    if (punchInOutDialog != null && punchInOutDialog.isShowing()) {
                                        GlideApp.with(getBaseActivity()).load(R.drawable.ic_placeholder_pic_grey)/*.transform(RoundedCorners(radius))*/
                                                .into(punchInOutDialog.ivAddSelfie);
                                        punchInOutDialog.ivAddSelfie.setVisibility(View.VISIBLE);
                                        punchInOutDialog.progressbar.setVisibility(View.VISIBLE);
                                        punchInOutDialog.btnAddSelfie.setVisibility(View.GONE);

                                    }

                                }, error -> {
                                    TrackiToast.Message.showShort(getBaseActivity(), error.getMessage());

                                });
                    }
                } else {
                    Log.e(TAG, "Image is not captured");
                }

            } catch (Exception c) {
                c.printStackTrace();
            }


        } else if (resultCode == Activity.RESULT_CANCELED) {
            // User Cancelled the action
            TrackiToast.Message.showLong(getBaseActivity(), "User Cancelled the action");
        }


    }

    public void setPunchIdFragment(Context context) {
        mListener = (AttendanceIdSendListener) context;
    }

    public void setPunchIdFromFragment(AttendanceBaseFragment context) {
        mListener = (AttendanceIdSendListener) context;
    }

    public interface AttendanceIdSendListener {
        void trackingIdVisible(String punchId, boolean isStart);

    }


}