package com.rocketflyer.rocketflow.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.tracki.BuildConfig;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.local.db.Action;
import com.tracki.data.local.db.ApiEventModel;
import com.tracki.data.local.db.DatabaseHelper;
import com.tracki.data.local.db.IAlarmTable;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.local.prefs.StartIdealTrackWork;
import com.tracki.data.local.prefs.StopIdleTracking;
import com.tracki.data.model.BaseResponse;
import com.tracki.data.model.NotificationData;
import com.tracki.data.model.NotificationEvent;
import com.tracki.data.model.NotificationModel;
import com.tracki.data.model.TrackingAction;
import com.tracki.data.model.request.CalculateFormData;
import com.tracki.data.model.request.DynamicFormMainData;
import com.tracki.data.model.request.EndTaskRequest;
import com.tracki.data.model.request.TaskData;
import com.tracki.data.model.response.config.AllowedField;
import com.tracki.data.model.response.config.AppConfig;
import com.tracki.data.model.response.config.AttendanceStatus;
import com.tracki.data.model.response.config.CallToActions;
import com.tracki.data.model.response.config.ChannelConfig;
import com.tracki.data.model.response.config.Config;
import com.tracki.data.model.response.config.ConfigResponse;
import com.tracki.data.model.response.config.DataType;
import com.tracki.data.model.response.config.DeprecationAndExpiration;
import com.tracki.data.model.response.config.DynamicFormData;
import com.tracki.data.model.response.config.DynamicFormsNew;
import com.tracki.data.model.response.config.Executor;
import com.tracki.data.model.response.config.Field;
import com.tracki.data.model.response.config.FileUrlsResponse;
import com.tracki.data.model.response.config.Flavour;
import com.tracki.data.model.response.config.FormData;
import com.tracki.data.model.response.config.FoundWidgetItem;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.data.model.response.config.LeaveApprovalStatus;
import com.tracki.data.model.response.config.LeaveStatus;
import com.tracki.data.model.response.config.LeaveType;
import com.tracki.data.model.response.config.OnTripConfig;
import com.tracki.data.model.response.config.Place;
import com.tracki.data.model.response.config.ProfileInfo;
import com.tracki.data.model.response.config.SdkConfig;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.VendorConfig;
import com.tracki.data.model.response.config.WorkFlowCategories;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.ui.addplace.AddLocationActivity;
import com.tracki.ui.base.BaseActivity;
import com.tracki.ui.common.Base64;
import com.tracki.ui.consent.ConsentActivity;
import com.tracki.ui.custom.socket.PacketInfo;
import com.tracki.ui.custom.socket.WebSocketManager;
import com.tracki.ui.deviceChange.DeviceChangeActivity;
import com.tracki.ui.introscreens.IntroActivity;
import com.tracki.ui.login.LoginActivity;
import com.tracki.ui.main.MainActivity;
import com.tracki.ui.receiver.ServiceRestartReceiver;
import com.tracki.ui.register.RegisterActivity;
import com.tracki.ui.selectorder.CatalogProduct;
import com.tracki.ui.service.sync.SyncService;
import com.tracki.ui.service.transition.TransitionService;
import com.tracki.ui.splash.SplashActivity;
import com.tracki.ui.splash.SplashViewModel;
import com.tracki.ui.tasklisting.CallToActionButtonAdapter;
import com.tracki.ui.tasklisting.TaskClickListener;
import com.tracki.ui.update.AppUpdateScreenActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.BuddyStatus;
import com.tracki.utils.ChatUserStatus;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.ErrorScreenHelper;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.NetworkUtils;
import com.tracki.utils.NextScreen;
import com.tracki.utils.ShiftHandlingUtil;
import com.tracki.utils.TaskStatus;
import com.tracki.utils.TrackiToast;
import com.trackthat.lib.TrackThat;
import com.trackthat.lib.internal.common.Trunk;
import com.trackthat.lib.models.TrackthatLocation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import kotlin.jvm.internal.Intrinsics;

import static android.content.Context.ALARM_SERVICE;
import static com.tracki.utils.AppConstants.ALERT_NO_CONNECTION;
import static com.tracki.utils.AppConstants.ALERT_REQUEST_TIME_OUT;
import static com.tracki.utils.AppConstants.ALERT_SERVER_UPGRADE_IN_PROGRESS;
import static com.tracki.utils.AppConstants.ALERT_TRY_AGAIN;
import static com.tracki.utils.AppConstants.CHANGE_SEETINGS;
import static com.tracki.utils.AppConstants.CLOSE;
import static com.tracki.utils.AppConstants.RETRY;
import static com.tracki.utils.AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE;

public final class CommonUtils {
    public static final String TAG = "CommonUtils";
    public static final String TRACK_THAT_DATABASE_NAME_ONLY = "trackthat";
    public static final String TRACK_THAT_DATABASE_NAME = "trackthat.db";
    public static Map<String, ArrayList<String>> stringListHashMap = new ConcurrentHashMap<>();
    public static String errorString =null;
    private static Dialog errorDialog;

    private CommonUtils() {
    }

    public static File convertBitmapToFile(Context context, Bitmap bitmap, String fileName) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80/*Ignore for PNGs*/, bytes);
        // context.getCacheDir() + File.separator + fileName
        // Environment.getExternalStorageDirectory(), fileName
        File file = new File(context.getCacheDir() + File.separator + fileName);
        try {
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    public static String genrateId() {
        return UUID.randomUUID().toString();
    }

    public static String sig(String stringToSign) {
        String signedInput = null;
        try {
            MessageDigest digestProvider = MessageDigest.getInstance("SHA-256");
            digestProvider.reset();
            byte[] digest = digestProvider.digest(stringToSign.getBytes());
            signedInput = Base64.encodeBytes(digest);
        } catch (Exception ignore) {

        }
        return signedInput;
    }

    public static boolean isEmailValid(String email) {
        return !isViewNullOrEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.NewDialog);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }


    public static void makeScreenDisable(AppCompatActivity context) {
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void makeScreenClickable(AppCompatActivity context) {
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }


    public static void rotate(float from, float to, @NonNull View view) {
        RotateAnimation anim = new RotateAnimation(
                from, to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        //Setup anim with desired properties
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(100); //Put desired duration per anim cycle here, in milliseconds
        anim.setFillAfter(true); // to persist the state of the animation
        anim.setFillEnabled(true);
        view.startAnimation(anim);
    }

    public static void expandText(@NonNull final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static void collapseText(@NonNull final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (initialHeight * 2 / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static int getTotalItemCount(@NotNull String userId, @Nullable PreferencesHelper mPref) {
        int count = 0;
        if (mPref == null)
            return count;
        if (mPref.getProductInCartWRC() != null && !mPref.getProductInCartWRC().isEmpty()) {
              /*  var searchCustomer = CustomerData()
                searchCustomer.userId = userId*/
            if (mPref.getProductInCartWRC().containsKey(userId)) {
                Map<String, CatalogProduct> map = mPref.getProductInCartWRC().get(userId);
                if (map != null && !map.isEmpty()) {
                    for (CatalogProduct data : map.values()) {
                        count += data.getNoOfProduct();
                    }
                }
            }
        }

        return count;

    }

    public static void rotateArrowAnother(ImageView arrow, Boolean isAnimated) {
        RotateAnimation rotate;
        if (isAnimated == null || !isAnimated) {
            rotate = new RotateAnimation(0, 90, 0, arrow.getWidth() / 2, 0,
                    arrow.getHeight() / 2);

        } else {
            rotate = new RotateAnimation(90, 0, 0, arrow.getWidth() / 2, 0,
                    arrow.getHeight() / 2);
        }
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        rotate.setRepeatCount(0);
        arrow.startAnimation(rotate);
    }

    public static boolean isViewNullOrEmpty(String string) {
        return TextUtils.isEmpty(string) || !(string.length() >= 2);
    }

    public static String getTime(int hourOfDay, int minute) {
        String time;
        if (hourOfDay >= 1 && hourOfDay < 12) {
            time = getHour(hourOfDay) + ":" + minute + " AM";
        } else {
            time = getHour(hourOfDay) + ":" + minute + " PM";
        }

        return time;
    }

    @NonNull
    @Contract(pure = true)
    private static String getHour(int hourOfDay) {
        switch (hourOfDay) {
            case 1:
            case 13:
                return "0" + 1;
            case 2:
            case 14:
                return "0" + 2;
            case 3:
            case 15:
                return "0" + 3;
            case 4:
            case 16:
                return "0" + 4;
            case 5:
            case 17:
                return "0" + 5;
            case 6:
            case 18:
                return "0" + 6;
            case 7:
            case 19:
                return "0" + 7;
            case 8:
            case 20:
                return "0" + 8;
            case 9:
            case 21:
                return "0" + 9;
            case 22:
                return 10 + "";
            case 23:
                return 11 + "";
            default:
                return hourOfDay + "";
        }
    }

    public static long milliseconds(String date) {
        try {
            Date parseDate = new SimpleDateFormat("yyyyMMddhhmmss", Locale.US).parse(date);
            return parseDate.getTime();

            //String date_ = date;
//            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
//
//            Date mDate = sdf.parse(date);
//            long timeInMilliseconds = mDate.getTime();
//            System.out.println("Date in milli :: " + timeInMilliseconds);
//            return timeInMilliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean handleResponse(final ApiCallback callBack, @Nullable APIError apiError, Object response,
                                         Context context) {
        return handleResponse(callBack, apiError, response, context,
                false, true);
    }

    public static boolean handleResponse(final ApiCallback callBack, @Nullable APIError apiError,
                                         Object response, Context context, boolean showAlertOnAnyError) {
        return handleResponse(callBack, apiError, response, context,
                showAlertOnAnyError, true);
    }

    public static boolean isMobileValid(String mobile) {
        int count = 0;
        if (isViewNullOrEmpty(mobile)) {
            return false;
        }
        char c = mobile.charAt(0);
        if (!(mobile.length() >= 10 && mobile.length() <= 15)) {
            return false;
        }
        for (int i = 0; i < mobile.length(); i++) {
            if (i != 0 && c == mobile.charAt(i)) {
                count++;
            }
        }
        if (count == mobile.length() - 1) {
            return false;
        }
        return mobile.charAt(0) != 0;
    }

 /*   public static boolean isMyServiceRunning(@NonNull Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Method used to get the IMEI no if available.
     *
     * @param context context of calling class.
     * @return IMEI no as a string.
     */
    @SuppressLint("HardwareIds")
    public static String getIMEINumber(@NonNull final Context context)
            throws SecurityException, NullPointerException {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert tm != null;

            //  imei = tm.getImei();
            //this change is for Android 10 as per security concern it will not provide the imei number.
            // if (imei == null) {
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            // }
        } else {
//            assert tm != null;
//            if (tm.getDeviceId() != null && !tm.getDeviceId().equals("000000000000000")) {
//                imei = tm.getDeviceId();
//            } else {
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            //}
        }

        return imei;
    }

    @Contract("null -> !null")
    public static String getAbbreviation(String string) {
        if (string == null) {
            return "";
        }
        String abbreviation;
        try {
            String[] name = string.split(" ");

            if (name.length > 1) {
                abbreviation = name[0].charAt(0) + "" + name[1].charAt(0);
            } else {
                abbreviation = string.charAt(0) + "" + string.charAt(1);
            }
        } catch (Exception e) {
            abbreviation = "";
            e.printStackTrace();
        }
        return abbreviation;
    }

    @NonNull
    @Contract(pure = true)
    public static String getBuddyStatus(BuddyStatus status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case ONLINE:
                return "Online";
            case ON_TRIP:
                return "On Trip";
            case OFFLINE:
                return "Offline";
            case PENDING:
                return "Invite Sent";
            default:
                return "Offline";
        }
    }

    /**
     * Method used to show the time picker to the user upon select the
     * value we get the time in mulliseconds and show time in user readable format.
     *
     * @param context         calling class
     * @param hour            hour of the time
     * @param minute          minute of the time
     * @param timeSetListener time listener
     */
    public static void openTimePicker(Context context, int hour, int minute,
                                      TimePickerDialog.OnTimeSetListener timeSetListener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, timeSetListener, hour,
                minute, DateFormat.is24HourFormat(context));
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    /**
     * Method used to place a call to given number.
     *
     * @param activity calling activity.
     * @param mobile   mobile number.
     */
    @SuppressLint("MissingPermission")
    public static void call(@NonNull Activity activity, String mobile) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mobile));
        activity.startActivity(callIntent);
    }


    @Contract("null -> !null")
    public static Integer getColor(TaskStatus status) {
        if (status == null) {
            return 0;
        }

        //return this color only
        return Color.parseColor("#4a4a4a");

        /*int color;
        switch (status) {
            case PENDING:
                color = Color.parseColor("#f5a623");
                break;
            case LIVE:
                color = Color.parseColor("#7ed321");
                break;
            case COMPLETED:
                color = Color.parseColor("#4a4a4a");
                break;
            case CANCELLED:
            case REJECTED:
                color = Color.parseColor("#d15d12");
                break;
            case ACCEPTED:
                color = Color.parseColor("#bd10e0");
                break;
            case ARRIVED:
                color = Color.parseColor("#FF33D7");
                break;
            default:
                color = 0;
                break;
        }
        return color;*/
    }


    public static Integer getLeaveColor(LeaveStatus status) {
        if (status == null) {
            return Color.parseColor("#000000");
        }

        int color;
        switch (status) {
            case PENDING:
                color = Color.parseColor("#ecc01f");
                break;
            case APPROVED:
                color = Color.parseColor("#92d051");
                break;
            case REJECTED:
                color = Color.parseColor("#b14f4e");
                break;
            case CANCELLED:
                color = Color.parseColor("#9b9b9b");
                break;
            default:
                color = Color.parseColor("#000000");
                break;

        }
        return color;
    }


    public static Integer getLeaveApprovalColor(LeaveApprovalStatus status) {
        if (status == null) {
            return 0;
        }

        int color;
        switch (status) {
            case PENDING:
                color = Color.parseColor("#ecc01f");
                break;
            case APPROVED:
                color = Color.parseColor("#92d051");
                break;
            case REJECTED:
            case CANCELLED:
                color = Color.parseColor("#b14f4e");
                break;
            default:
                color = 0;
                break;
        }
        return color;
    }


    public static String getStatusString(AttendanceStatus status) {
        if (status == null) {
            return "N/A";
        }
        switch (status) {
            case PRESENT:
                return "Present";
            case ABSENT:
                return "Absent";
            case ON_LEAVE:
                return "On Leave";
            case DAY_OFF:
                return "Day Off";
            case HOLIDAY:
                return "Holiday";
            default:
                return "N/A";
        }
    }

    public static Integer getAttendanceStatusColor(LeaveStatus status) {
        if (status == null) {
            return Color.parseColor("#000000");
        }

        int color;
        switch (status) {
            case ALL:
                color = Color.parseColor("#3581F3");
                break;
            case PRESENT:
                color = Color.parseColor("#23D47D");
                break;
            case ABSENT:
                color = Color.parseColor("#E05353");
                break;
            case ON_LEAVE:
                color = Color.parseColor("#FFB916");
                break;
            case DAY_OFF:
                color = Color.parseColor("#BF7875eb");
                break;
            case HOLIDAY:
                color = Color.parseColor("#1F7B4A");
                break;
            case NOT_UPDATED:
                color = Color.parseColor("#A4A9AD");
                break;
            default:
                color = Color.parseColor("#000000");
                break;
        }

        return color;
    }


    public static String getLeaveStatusString(LeaveStatus status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case PENDING:
                return "Applied";
            case APPROVED:
                return "Approved";
            case REJECTED:
                return "Rejected";
            case CANCELLED:
                return "Cancelled";

            default:
                return "";
        }
    }


    public static String getLeaveApprovalStatusString(LeaveApprovalStatus status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case PENDING:
                return "Pending";
            case APPROVED:
                return "Approved";
            case REJECTED:
                return "Rejected";
            case CANCELLED:
                return "Cancelled";


            default:
                return "";
        }
    }


    public static LeaveStatus getLeaveStatusConstant(String status) {

        switch (status) {
            case "All":
                return LeaveStatus.ALL;
            case "Applied":
            case "Pending":
                return LeaveStatus.PENDING;
            case "Approved":
                return LeaveStatus.APPROVED;
            case "Rejected":
                return LeaveStatus.REJECTED;
            case "Cancelled":
                return LeaveStatus.CANCELLED;
            case "Present":
                return LeaveStatus.PRESENT;
            case "Absent":
                return LeaveStatus.ABSENT;
            case "Holiday":
                return LeaveStatus.HOLIDAY;
            case "Day Off":
                return LeaveStatus.DAY_OFF;
            case "On Leave":
                return LeaveStatus.ON_LEAVE;
            default:
                return null;

        }
    }


    public static LeaveApprovalStatus getLeaveApprovalStatusConstant(String status) {

        switch (status) {
            case "Pending":
                return LeaveApprovalStatus.PENDING;
            case "Approved":
                return LeaveApprovalStatus.APPROVED;
            case "Rejected":
                return LeaveApprovalStatus.REJECTED;
            default:
                return null;
        }
    }


    public static String getLeaveTypeString(LeaveType leaveType) {
        if (leaveType == null) {
            return "";
        }
        switch (leaveType) {
            case PRIVILEGE_LEAVE:
                return "Privilege Leave";
            case SICK_LEAVE:
                return "Sick Leave";
            case CASUAL_LEAVE:
                return "Casual Leave";
            default:
                return "";
        }
    }

    public static LeaveType getLeaveTypeConstant(String leaveType) {

        switch (leaveType) {
            case "Privilege Leave":
                return LeaveType.PRIVILEGE_LEAVE;
            case "Sick Leave":
                return LeaveType.SICK_LEAVE;
            case "Casual Leave":
                return LeaveType.CASUAL_LEAVE;
            default:
                return null;
        }
    }

    public static boolean timeLies(long givenTime) {
        boolean isLies = false;
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);


        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);


        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTimeInMillis(givenTime);

        Date x = calendar3.getTime();
        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
            isLies = true;
        }
        return isLies;

    }

    public static Integer getAttendanceStatusColor(AttendanceStatus status) {
        if (status == null) {
            return 0;
        }
        int color;
        switch (status) {
            case PRESENT:
                //color = Color.parseColor("#8ddfb5");
                color = Color.parseColor("#23D47D");
                break;
            case ABSENT:
                //color = Color.parseColor("#ff928d");
                color = Color.parseColor("#E05353");
                break;
            case ON_LEAVE:
                // color = Color.parseColor("#fffbeb");
                color = Color.parseColor("#FFB916");
                break;
            case DAY_OFF:
                color = Color.parseColor("#d0d0d0");
                break;
            case HOLIDAY:
                color = Color.parseColor("#a1a9b4");
                break;
            default:
                color = 0;
                break;
        }
        return color;
    }

    @NonNull
    public static String setCustomFontTypeSpan(Context context, String
            source, int startIndex, int endIndex, int font) {

        SpannableStringBuilder spannableString = new SpannableStringBuilder(source);
        Typeface typeface = Typeface.create(ResourcesCompat.getFont(context, font), Typeface.BOLD);
        spannableString.setSpan(new StyleSpan(typeface.getStyle()), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString.toString();
    }

    private static boolean handleResponse(final ApiCallback callBack, APIError apiError, Object object,
                                          Context context, boolean showAlertOnAnyError,
                                          boolean showAlertNetwork) {
        boolean success = false;
        try {
            BaseResponse response = new Gson().fromJson(String.valueOf(object), BaseResponse.class);
            if (callBack.isAvailable()) {
                if (apiError != null) {
                   /* Activity baseActivity = (Activity) context;
                    if (baseActivity != null) {*/
                    switch (apiError.getErrorType()) {
                        case INTERNET_CONNECTION:
                            // TrackiToast.Message.showShort(context, ALERT_NO_CONNECTION);
                            break;
                        case SLOW_INTERNET_CONNECTION:
                            TrackiToast.Message.showShort(context, SLOW_INTERNET_CONNECTION_MESSAGE);
                            break;
                        case NETWORK_FAIL:
                            if (showAlertNetwork) {
                                if (!(context instanceof TrackiApplication)) {
                                    new ErrorScreenHelper(context).display(ErrorScreenHelper.NO_INTERNET_ERROR, callBack);
                                }
                            } else {
                                TrackiToast.Message.showShort(context, ALERT_NO_CONNECTION);
                            }
                            break;
                        case TIMEOUT:
                            if (showAlertOnAnyError) {
                                new AlertDialog.Builder(context)
                                        .setMessage(ALERT_REQUEST_TIME_OUT)
                                        .setTitle(ALERT_TRY_AGAIN)
                                        .setCancelable(false)
                                        .setPositiveButton(RETRY, (dialog, which) -> {
                                            dialog.dismiss();
                                            callBack.hitApi();
                                        })
                                        .setNegativeButton(CLOSE, (dialog, which) -> {
                                            dialog.dismiss();
                                            ((BaseActivity) context).finish();
                                        })
                                        .show();
                            } else {
                                callBack.onRequestTimeOut(callBack);
                            }
                            break;
                        case SESSION_EXPIRED:
                            response.getResponseMsg();
                            TrackiToast.Message.showShort(context, response.getResponseMsg());
                            callBack.onLogout();
                            break;
                        default:
                            if (showAlertOnAnyError) {
                                new AlertDialog.Builder(context).setMessage(ALERT_SERVER_UPGRADE_IN_PROGRESS)
                                        .setTitle(ALERT_TRY_AGAIN)
                                        .setCancelable(false)
                                        .setPositiveButton(RETRY, (dialog, which) -> {
                                            dialog.dismiss();
                                            callBack.hitApi();
                                        }).setNegativeButton(CLOSE, (dialog, which) -> {
                                    dialog.dismiss();
                                    callBack.onNetworkErrorClose();
                                }).show();

                            } else {
                                if (!(context instanceof TrackiApplication)) {
                                    new ErrorScreenHelper(context).display(ErrorScreenHelper.SOMETHING_WENT_ERROR, callBack);
                                }
                            }
                    }
                    /*}*/
                } else if (response != null && !response.getSuccessful()) {
                    if (response.getResponseCode() != null) {
                        switch (response.getResponseCode()) {
                            case "204"://invalid token
                            case "211"://invalid login token
                            case "205"://invalid accessId
                                //if user called this method from service then stop tracking service.
                                if (context instanceof TrackiApplication) {
                                    if (CommonUtils.isServiceRunningInForeground(context, TransitionService.class.getName())) {
                                        CommonUtils.manageTransitionService(context, false);
                                    }
                                } else {
                                    //if method is not called from application the
                                    // expire session and open login page.
                                    Intent intent;
                                    if (context instanceof SplashActivity) {
                                        try {
                                            BaseActivity baseActivity = (BaseActivity) context;
                                            baseActivity.invalidLoginTokenOnly();
                                        } catch (Exception e) {

                                        }
//                                        intent = new Intent(context, SplashActivity.class);
//                                        BaseActivity baseActivity = (BaseActivity) context;
//                                        baseActivity.invalidToken(intent);
                                        if (response.getResponseMsg() != null) {
                                            TrackiToast.Message.showShort(context, response.getResponseMsg());
                                        }
                                    } else if (context instanceof LoginActivity) {
                                        try {
                                            BaseActivity baseActivity = (BaseActivity) context;
                                            baseActivity.invalidLoginTokenOnly();
                                        } catch (Exception e) {

                                        }
                                        if (response.getResponseMsg() != null) {
                                            TrackiToast.Message.showShort(context, response.getResponseMsg());
                                        }
                                        // ((LoginActivity) context).finish();
                                        // System.exit(0);
                                    } else {
                                        if (TrackThat.isTracking()) {
                                            TrackThat.stopTracking();
                                        }
                                        intent = LoginActivity.newIntent(context);
                                        intent.putExtra(AppConstants.Extra.EXTRA_LOGOUT, true);
                                        BaseActivity baseActivity = (BaseActivity) context;
                                        baseActivity.invalidToken(intent);
                                    }

                                }
                                break;
                           /* case "300"://invalid otp for dynamic form
                                //needs to be handle inside dynamic activity.
                                TrackiToast.Message.showShort(context, response.getResponseMsg());
                                break;*/
                            default:
                                if (response.getResponseMsg() != null) {
                                    TrackiToast.Message.showShort(context, response.getResponseMsg());
                                } else {
                                    //  TrackiToast.Message.showShort(context, "No Response Message");
                                }
                                break;
                        }
                    } else {
//
//                        switch (apiError.getErrorType()) {
//                            case TIMEOUT:
//                                TrackiToast.Message.showShort(context, "Connection timed out");
//                                break;
//                            case NETWORK_FAIL:
//                                TrackiToast.Message.showShort(context, "Network Fail Error");
//                                break;
//                            case PARSE_FAILED:
//                                TrackiToast.Message.showShort(context, "Parser Error");
//                                break;
//                            case SERVER_DOWN:
//                                TrackiToast.Message.showShort(context, "Server Down");
//                                break;
//                            case UNKNOWN_ERROR:
//                                TrackiToast.Message.showShort(context, "Unknown Error");
//                                break;
//                            case SESSION_EXPIRED:
//                                TrackiToast.Message.showShort(context, "Session Expire");
//                                break;
//                            default:
//                                break;
//
//                        }


                    }
                } else {
                    success = true;
                /*if (response != null) {
                    TrackiToast.MessageResponse.showShort(context, response.getResponseMsg());
                }*/
                }
            }
        } catch (com.google.gson.JsonSyntaxException | NullPointerException | IllegalStateException exception) {
            //TrackiToast.Message.showShort(context, exception.getMessage());
            TrackiToast.Message.showShort(context, "Seems server is not available to process this request,please try after some time");
            exception.printStackTrace();
        }
        return success;
    }

    public static void showSnakbarForNetworkSettings(Context context, View mainlayout, String message) {
        Snackbar snackbar = Snackbar
                .make(mainlayout, message, Snackbar.LENGTH_LONG)
                .setAction(CHANGE_SEETINGS, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(intent);

                    }
                });
        snackbar.show();
    }

    public static void getFcmToken(OnSuccessListener<InstanceIdResult> onSuccessListener) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(onSuccessListener);
    }

    public static void doNotHideSnackbar(@Nullable Snackbar snackbar) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
        java.lang.reflect.Field sHandler = BaseTransientBottomBar.class.getDeclaredField("sHandler");
        sHandler.setAccessible(true);
        Method handleMessage = Handler.class.getMethod("handleMessage", Message.class);
        Object var7 = sHandler.get(snackbar);
        if (var7 == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.os.Handler");
        } else {
            final Handler originalHandler = (Handler) var7;
            Handler decoratedHandler = new Handler(Looper.getMainLooper(), (android.os.Handler.Callback) (new android.os.Handler.Callback() {
                public boolean handleMessage(@NotNull Message message) {
                    Intrinsics.checkNotNullParameter(message, "message");
                    switch (message.what) {
                        case 0:
                            try {
                                handleMessage.invoke(originalHandler, Message.obtain(originalHandler, 0));
                            } catch (IllegalAccessException | InvocationTargetException var3) {
                                var3.printStackTrace();
                            }

                            return true;
                        default:
                            return false;
                    }
                }
            }));
            sHandler.set(snackbar, decoratedHandler);
        }
    }

    public static void showPermanentSnackBar(@NotNull View view, @NotNull String message, @NotNull final SplashViewModel baseViewModel) {
        Snackbar var10000 = Snackbar.make(view, (CharSequence) message, Snackbar.LENGTH_INDEFINITE).setAction((CharSequence) "Retry", (View.OnClickListener) (new View.OnClickListener() {
            public final void onClick(View it) {
                baseViewModel.hitApi();
            }
        }));
        Snackbar snackbar = var10000;
        snackbar.show();

        try {
            doNotHideSnackbar(snackbar);
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException var6) {
            var6.printStackTrace();
        }

    }

    public static Snackbar showNetWorkConnectionIssue(@NotNull View view, @NotNull String message) {
        Snackbar snackbar = Snackbar.make(view, (CharSequence) message, Snackbar.LENGTH_INDEFINITE);
        TextView tv = (TextView) (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/campton_book.ttf");
        tv.setTypeface(font);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbar.show();

        try {
            doNotHideSnackbar(snackbar);
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException var6) {
            var6.printStackTrace();
        } finally {
            return snackbar;
        }

    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context,
                                                              @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        if (background != null) {
            background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            background.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }
        return null;
    }

    public static boolean isPointOutSideCircle(Integer radius, double initialLat, double initialLong,
                                               double finalLat, double finalLong) {
        float[] distance = new float[2];
        Location.distanceBetween(initialLat, initialLong, finalLat, finalLong, distance);
        showLogMessage("e", "distance 2", distance[0] + " Meter");
        return distance[0] <= radius;
    }


    public static String getMessage(@NotNull NotificationEvent event, @NotNull NotificationData data) {
        String inviteeName = data.getTaskAssignedTo();
        String invitorName = data.getTaskAssignedBy();
        String message;
        switch (event) {
            case RECEIVE_INVITATION:
                message = invitorName + " has sent you a Track request";
                break;
            case REJECT_INVITATION:
                message = inviteeName + " has reject your Track request";
                break;
            case ACCEPT_INVITATION:
                message = inviteeName + " has accept your Track request";
                break;
            case END_TASK:
                message = invitorName + " end a task";
                break;
            case START_TASK:
                message = invitorName + " has started a task";
                break;
            case ACCEPT_TASK:
                message = inviteeName + " has accept your task";
                break;
            case ASSIGN_TASK:
                message = invitorName + " has assign you a task";
                break;
            case AUTO_CANCEL_TASK:
            case CANCEL_TASK:
                message = invitorName + " has cancel a task";
                break;
            case REJECT_TASK:
            case CANCEL_TRACKING_REQUEST:
                message = inviteeName + " has reject your task";
                break;
            default:
                message = "";
                break;
        }
        return message;
    }

    public static String getShortName(@NotNull NotificationEvent event,
                                      @Nullable String inviteeName,
                                      @Nullable String invitorName) {
        String shortName;
        switch (event) {
            case CANCEL_TASK:
            case ASSIGN_TASK:
            case START_TASK:
            case RECEIVE_INVITATION:
            case END_TASK:
            case AUTO_CANCEL_TASK:
                shortName = CommonUtils.getAbbreviation(invitorName).toUpperCase();
                break;
            case ACCEPT_TASK:
            case REJECT_TASK:
            case ACCEPT_INVITATION:
            case REJECT_INVITATION:
            case CANCEL_TRACKING_REQUEST:
                shortName = CommonUtils.getAbbreviation(inviteeName).toUpperCase();
                break;
            default:
                shortName = "";
                break;
        }
        return shortName;
    }

    public static void shareImageAndTextOnSocialMedia(@NotNull Context context, String link) {
        if (link == null || link.isEmpty()) {
            return;

        }
        Intent shareIntent;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sharing_image);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Share.png";
        OutputStream out = null;
        File file = new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // path=file.getPath();
        // Uri bmpUri = Uri.parse("file://"+path);
        Uri bmpUri = FileProvider.getUriForFile(
                context,
                "com.rocketflyer.rocketflow" + ".provider", //(use your app signature + ".provider" )
                file);
        ;
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        // Uri imageUri =  Uri.parse(path);
        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, link);
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent, "Share with"));

    }

    public static void shareLinkOnSocialMedia(@NotNull Context context, String link) {
        if (link == null || link.isEmpty()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        i.putExtra(Intent.EXTRA_TEXT, link);
        context.startActivity(Intent.createChooser(i, "Share URL"));
    }

    public static void applyFontToMenu(Menu m, Context mContext) {
        for (int i = 0; i < m.size(); i++) {
            applyFontToMenuItem(m.getItem(i), mContext);
        }
    }

    public static void applyFontToMenuItem(MenuItem mi, Context mContext) {
        if (mi.hasSubMenu())
            for (int i = 0; i < mi.getSubMenu().size(); i++) {
                applyFontToMenuItem(mi.getSubMenu().getItem(i), mContext);
            }

//        Typeface face = Typeface.createFromAsset(mContext.getAssets(),"fonts/OpenSans-Regular.ttf");    //  THIS
        TypefaceSpan face = new TypefaceSpan("fonts/campton_book.ttf"); // OR  THIS
        SpannableStringBuilder title = new SpannableStringBuilder(mi.getTitle());
        title.setSpan(face, 0, title.length(), 0);
        mi.setTitle(title);
    }

    public static void openDatePicker(Context context, int mYear, int mMonth, int mDay, long minTime,
                                      long maxTime, DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, listener, mYear, mMonth, mDay);
        if (minTime != 0) {
            datePickerDialog.getDatePicker().setMinDate(minTime);
        }
        if (maxTime != 0) {
            datePickerDialog.getDatePicker().setMaxDate(maxTime);
        }
        datePickerDialog.show();
    }

    public static void openDatePickerWithTitle(Context context, int mYear, int mMonth, int mDay, long minTime,
                                               long maxTime, DatePickerDialog.OnDateSetListener listener, String title) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, listener, mYear, mMonth, mDay);
        datePickerDialog.setTitle(title);
        if (minTime != 0) {
            datePickerDialog.getDatePicker().setMinDate(minTime);
        }
        if (maxTime != 0) {
            datePickerDialog.getDatePicker().setMaxDate(maxTime);
        }
        datePickerDialog.show();
    }

    /**
     * Method used to check if service is running in foreground mode or not.
     *
     * @param context     context of calling class.
     * @param serviceName service name.
     * @return true if service is running in foreground else false.
     */
    public static boolean isServiceRunningInForeground(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : l) {
//            Trunk.e(TAG,"name: "+runningServiceInfo.service.getClassName());
            if (runningServiceInfo.service.getClassName().equals(serviceName)) {
                if (runningServiceInfo.foreground) {
                    //service run in foreground
                    return true;
                }
            }
        }
        return false;
    }

    public static int metersPerSecToKmPerHr(float speed) {
        //convert  speed in km/hr
        return (int) ((speed * 3600) / 1000);
    }


    public static boolean containsEnum(String value) {

        for (ApiType c : ApiType.values()) {
            if (c.name().equals(value)) {
                return true;
            }
        }

        return false;
    }

    public static String getReffranceLabel(PreferencesHelper preferencesHelper, String categoryId) {
        String lebel = "";
        if (preferencesHelper.getWorkFlowCategoriesList() != null) {
            List<WorkFlowCategories> workFlowCategories = preferencesHelper.getWorkFlowCategoriesList();
            for (int i = 0; i < workFlowCategories.size(); i++) {
                if (categoryId != null && !categoryId.isEmpty()) {
                    if (workFlowCategories.get(i).getCategoryId().equals(categoryId)) {
                        if (workFlowCategories.get(i).getChannelConfig() != null && workFlowCategories.get(i).getChannelConfig().getFields() != null) {
                            List<Field> fieldList = workFlowCategories.get(i).getChannelConfig().getFields();
                            Field field = new Field();
                            field.setField("REFERENCE_ID");
                            if (fieldList.contains(field)) {
                                int position = fieldList.indexOf(field);
                                if (position != -1) {
                                    Field rField = fieldList.get(position);
                                    lebel = rField.getLabel();
                                    break;
                                }
                            }

                        }
                    }
                }

            }
        }
        return lebel;

    }

    public static void createAndShowForegroundNotification(Service context, int iD_SERVICE) {
        try {
            NotificationCompat.Builder builder = getNotificationBuilder(context,
                    "com.tracki.notification:" + context.getClass().getSimpleName(), // Channel id
                    NotificationManagerCompat.IMPORTANCE_MAX);
            builder.setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(false)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(Color.BLACK)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getClass().getSimpleName() + " is Running");

            Notification notification = builder.build();
            context.startForeground(iD_SERVICE, notification);
            Trunk.i(TAG, context.getClass().getSimpleName() + " started in foreground");
        } catch (Exception e) {
            Trunk.e(TAG, "Exception inside createAndShowForegroundNotification() " + e);
        }
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context context,
                                                                     String channelId,
                                                                     int importance) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = "TrackI";
        String description = "notifications_channel_description1";
        final NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        if (nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);
                nm.createNotificationChannel(nChannel);
            }
        }
    }

    /**
     * Method used to start service when auto start is on.
     *
     * @param c           context of calling class
     * @param isAutoStart auto start flag
     */
    public static void manageTransitionService(Context c, boolean isAutoStart) {
        Context context = c.getApplicationContext();
        Log.e(TAG, "Flag is auto start: " + isAutoStart);
        Intent intent = new Intent(context, TransitionService.class);
        if (isAutoStart) {
            if (!CommonUtils.isServiceRunningInForeground(context, TransitionService.class.getName())) {
                intent.putExtra(AppConstants.Extra.EXTRA_STOP_SERVICE, false);
                // start transition api service to monitor activity for
                // user inside or outside vehicle or start or stop walking.
                ContextCompat.startForegroundService(context, intent);
            }
        } else {
            if (CommonUtils.isServiceRunningInForeground(context, TransitionService.class.getName())) {
                intent.putExtra(AppConstants.Extra.EXTRA_STOP_SERVICE, true);
                // start transition api service to monitor activity for
                // user inside or outside vehicle or start or stop walking.
                ContextCompat.startForegroundService(context, intent);
            }
        }
    }

    public static void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeGoogleLogoPosition(@NonNull SupportMapFragment mapFragment, int bottom, GoogleMap mMap) {
        //for google logo pos
        if (mapFragment.getView() != null) {
            View googleLogo = mapFragment.getView().findViewWithTag("GoogleWatermark");
            if (googleLogo != null) {
                RelativeLayout.LayoutParams glLayoutParams = (RelativeLayout.LayoutParams) googleLogo.getLayoutParams();
                glLayoutParams.setMargins(16, 0, 0, bottom);
                googleLogo.setLayoutParams(glLayoutParams);
            }
        }
        /*else {
            mMap.setPadding(16, 0, 0, bottom);
        }*/
    }

    public static Map<String, String> buildDeviceHeader(Context context) {
        Map<String, String> stringBuilder = new HashMap<>();
        try {

            stringBuilder.put("locale",
                    context.getResources().getConfiguration().locale.toString());
            if (TimeZone.getDefault() != null && TimeZone.getDefault().getID() != null) {
                stringBuilder.put("time-zone", TimeZone.getDefault().getID());
            }
            stringBuilder.put("brand",
                    Build.BRAND);
            stringBuilder.put("device",
                    Build.DEVICE);
            stringBuilder.put("model",
                    Build.MODEL);
            stringBuilder.put("manufacturer",
                    Build.MANUFACTURER);
            PackageManager pkm = context.getPackageManager();
            PackageInfo info = pkm.getPackageInfo(context.getPackageName(), 0);
            stringBuilder.put("vname",
                    info.versionName);
            stringBuilder.put("vcode",
                    String.valueOf(info.versionCode));
            stringBuilder.put("os",
                    Build.VERSION.RELEASE);
            DisplayMetrics metrics = new DisplayMetrics();
            ConnectivityManager mConnectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = mConnectivity.getActiveNetworkInfo();
            String network;
            int netType = nInfo.getType();
            int netSubtype = nInfo.getSubtype();
            if (netType == ConnectivityManager.TYPE_WIFI) {
                network = "WIFI";
            } else if (netType == ConnectivityManager.TYPE_MOBILE
                    && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS) {
                network = "3G";
            } else {
                network = "UNKNOWN";
            }
            stringBuilder.put("roaming",
                    String.valueOf(nInfo.isRoaming()));
            stringBuilder.put("network",
                    network);
            if ((context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                stringBuilder.put("platform",
                        "ANDROID_TABLET");
            } else {
                if ((context.getResources().getConfiguration().screenLayout
                        & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    stringBuilder.put("platform",
                            "ANDROID_TABLET");
                } else {
                    stringBuilder.put("platform",
                            "ANDROID_PHONE");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in buildDeviceHeader() : " + e);
        }
        return stringBuilder;
    }

    public static boolean checkPlayServices(Activity context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                if (errorDialog == null) {
                    errorDialog = googleApiAvailability.getErrorDialog(context, resultCode, 2404);
                    errorDialog.setCancelable(false);
                }
                if (!errorDialog.isShowing())
                    errorDialog.show();
            }
        }
        return resultCode == ConnectionResult.SUCCESS;
    }

    public static String getExtension(String photoUrl) {
        String[] splitUpl = photoUrl.split("/");
        return splitUpl[splitUpl.length - 1].toLowerCase();
    }


    /**
     * Method used to get the comma separated string from the hashMap of string
     *
     * @param arrayList hashMap of string
     * @return string of comma separated urls
     */
    public static String getCommaSeparatedList(@Nullable ArrayList<String> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return "";
        }
        StringBuilder urls = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            //if item at ith position is not null then add else ignore
            if (arrayList.get(i) != null) {
                urls.append(arrayList.get(i));

                int val = i;
                val = val + 1;
                //if array hashMap contains next item then add comma into the string
                if (arrayList.size() - 1 >= val) {
                    urls.append(",");
                }
            }
        }
        return urls.toString();
    }

    /**
     * This code is used  to check if there is an internet
     * connection then check if any data is present into the
     * database then start a service and sync with server
     *
     * @param context context of calling class
     */
    public static void checkSyncService(Context context) {
        if (NetworkUtils.isNetworkConnected(context)) {
            Log.i(TAG, "Device connected to a network.");
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
            if (databaseHelper.checkIfRecordExists().size() > 0) {
                Log.i(TAG, "Record exists size is: " + databaseHelper.checkIfRecordExists().size());
                if (!CommonUtils.isServiceRunningInForeground(context, SyncService.class.getName())) {
                    ContextCompat.startForegroundService(context, new Intent(context, SyncService.class)
                            .putExtra(AppConstants.Extra.EXTRA_STOP_SERVICE, false));
                } else {
                    Log.i(TAG, "Service is already running");
                }
            } else {
                Log.i(TAG, "Record Doesn't exists size is: " + databaseHelper.checkIfRecordExists().size());
            }
        } else {
            Log.i(TAG, "Device is NOT connected to a network.");
            if (CommonUtils.isServiceRunningInForeground(context, SyncService.class.getName())) {
                ContextCompat.startForegroundService(context, new Intent(context, SyncService.class)
                        .putExtra(AppConstants.Extra.EXTRA_STOP_SERVICE, true));
            }
        }
    }


    public static void openDialer(Context context, String mobile) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobile));
        context.startActivity(intent);
    }

    /**
     * Method used to handle config response and save data to preferences and handle next screen target.
     *
     * @param context           context of calling class.
     * @param configResponse    response
     * @param preferencesHelper preferences
     * @param from              1 for splash,2 for otp,3 for main
     */
    public static void saveConfigDetails(Activity context, ConfigResponse configResponse,
                                         PreferencesHelper preferencesHelper, String from) {
        saveConfigDetails(context, configResponse, preferencesHelper, from, null);
    }

    public static void saveConfigDetailsFromSystemTrayClick(Activity context, ConfigResponse configResponse,
                                                            PreferencesHelper preferencesHelper, String from, String jsonobject) {
        saveConfigDetails(context, configResponse, preferencesHelper, from, jsonobject);
    }

    /**
     * Method used to handle config response and save data to preferences and handle next screen target.
     *
     * @param context           context of calling class.
     * @param configResponse    response
     * @param preferencesHelper preferences
     * @param from              1 for splash,2 for otp,3 for main
     */
    public static void saveConfigDetails(Activity context, ConfigResponse configResponse,
                                         PreferencesHelper preferencesHelper, String from, @Nullable String jsonobject) {

//        if (configResponse.getDynamicFormConfig() != null) {
//            TrackiApplication.setDynamicFormConfig(configResponse.getDynamicFormConfig());
//        }
        preferencesHelper.saveFilterMap(null);
        if (configResponse.getLookups() != null) {
            TrackiApplication.setLookups(configResponse.getLookups());
        }

        if (configResponse.getDynamicForms() != null) {
            TrackiApplication.setDynamicFormsNews(configResponse.getDynamicForms());
        }


//        if (configResponse.getTaskCancellationReasons() != null &&
//                configResponse.getTaskCancellationReasons().size() > 0) {
//            List<Reasons> reasonsList = new ArrayList<>();
//            for (int i = 0; i < configResponse.getTaskCancellationReasons().size(); i++) {
//                reasonsList.add(new Reasons(i, configResponse.getTaskCancellationReasons().get(i)));
//            }
//            TrackiApplication.setReasonList(reasonsList);
//        }

        SdkConfig sdkConfig = configResponse.getSdkConfig();
        if (sdkConfig != null) {
            //           sdkConfig.getVendorConfig().getOnActiveConfig().setActive(true);
//            if (sdkConfig.getVendorConfig() != null && sdkConfig.getVendorConfig().getOnActiveConfig()!=null) {
//                preferencesHelper.setIsIdealTrackingEnable(sdkConfig.getVendorConfig().getOnActiveConfig().getActive());
//            }
            if (sdkConfig.getVirtualGeofenceRadius() != null) {
                preferencesHelper.setRadius(sdkConfig.getVirtualGeofenceRadius());
            }
            if (sdkConfig.getGeofences() != null) {
                preferencesHelper.saveGeoFence(sdkConfig.getGeofences());
            }
            VendorConfig vendorConfig = sdkConfig.getVendorConfig();
            if (vendorConfig != null) {
                OnTripConfig onTripConfig = vendorConfig.getOnTripConfig();
                if (onTripConfig != null) {
                    if (onTripConfig.getPostFrequency() == 0) {
                        vendorConfig.getOnTripConfig().setPostFrequency(10);
                    }
                    if (onTripConfig.getLocationCaptureFrequency() == 0) {
                        vendorConfig.getOnTripConfig().setLocationCaptureFrequency(5);
                    }
                    if (onTripConfig.getSpeedCaptureFrequency() == 0) {
                        vendorConfig.getOnTripConfig().setSpeedCaptureFrequency(2);
                    }
                    preferencesHelper.savePhoneUsageLimitInMinutesOnTaskTrip(onTripConfig.getPhoneUsageLimit());
                    preferencesHelper.saveOnTripOverStopping(onTripConfig.getOverstoppingConfig());
                }
                OnTripConfig onActiveConfig = vendorConfig.getOnActiveConfig();
                if (onActiveConfig != null) {
                    if (onActiveConfig.getPostFrequency() == 0) {
                        vendorConfig.getOnActiveConfig().setPostFrequency(10);
                    }
                    if (onActiveConfig.getLocationCaptureFrequency() == 0) {
                        vendorConfig.getOnActiveConfig().setLocationCaptureFrequency(5);
                    }
                    if (onActiveConfig.getSpeedCaptureFrequency() == 0) {
                        vendorConfig.getOnActiveConfig().setSpeedCaptureFrequency(2);
                    }
                    preferencesHelper.savePhoneUsageLimitInMinutesOnIdleTrip(onActiveConfig.getPhoneUsageLimit());
                    preferencesHelper.saveOnIdleOverStopping(onActiveConfig.getOverstoppingConfig());
                }
            }
            TrackThat.setConfig(new Gson().toJson(sdkConfig), false);
            if (sdkConfig.getAccessId() != null)
                TrackThat.setAccessId(sdkConfig.getAccessId());

        }

        AppConfig appConfig = configResponse.getAppConfig();
        if (appConfig != null) {
            if (appConfig.getRoles() != null) {
                preferencesHelper.saveRoleConfigDataList(appConfig.getRoles());
            }
            preferencesHelper.setLocationRequired(appConfig.getLocationRequired());
            preferencesHelper.setManger(appConfig.getManager());
            //preferencesHelper.setLocationRequired(true);
            Map<Integer, List<ShiftTime>> newShiftMap = appConfig.getShifts();
            preferencesHelper.setAlwaysNewShiftMap(newShiftMap);
            if (newShiftMap != null && newShiftMap.size() > 0) {
                ShiftHandlingUtil.setShiftTiming(preferencesHelper, newShiftMap);
            } else {
                preferencesHelper.remove(AppConstants.OLD_SHIFT_MAP);
            }
            preferencesHelper.setEnablePunchGeofencing(appConfig.getEnablePunchGeofencing());
            preferencesHelper.setDefDateRange(appConfig.getDefDateRange());
            preferencesHelper.setMaxDateRange(appConfig.getMaxDateRange());
            preferencesHelper.setMaxPastDaysAllowed(appConfig.getMaxPastDaysAllowed());
            //  preferencesHelper.setEnablePunchGeofencing(false);
            preferencesHelper.saveChatUrl(appConfig.getChatServerUrl());
            preferencesHelper.saveAllowArrival(appConfig.getAllowArrival());
            preferencesHelper.saveAllowArrivalOnGeoIn(appConfig.getAllowArrivalOnGeoIn());
            preferencesHelper.saveAutoCancelThresholdInMin(appConfig.getAutoCancelThresholdInMin());
            preferencesHelper.setEnableWalletFlag(appConfig.getEnableWallet());
            try {
                TrackiApplication.setNavigationMenuList(appConfig.getNavigations());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (appConfig.getIdleTrackingInfo() != null) {
                preferencesHelper.setIdleTrackingInfo(appConfig.getIdleTrackingInfo());
                preferencesHelper.setIsIdealTrackingEnable(appConfig.getIdleTrackingInfo().getEnableIdleTracking());
            }
            preferencesHelper.setBuddyListing(appConfig.getBuddyListing());
//            if(preferencesHelper.getIsIdealTrackingEnable()&&!preferencesHelper.getDailyIdleTaskOffEnable()){
//                preferencesHelper.setDailyIdleTaskOffEnable(true);
//                startIdleTrackingOff(context);
//            }
            if (preferencesHelper.getIsIdealTrackingEnable()) {
                startIdleTrackingOff(context);
            }
            if (appConfig.getStatus() != null && !appConfig.getStatus().isEmpty()) {
                if (appConfig.getStatus().equals("OFFLINE")) {
                    preferencesHelper.setStatus("OFFLINE");
                } else {
                    preferencesHelper.setStatus("ONLINE");
                }
            }
            preferencesHelper.setServicePref(appConfig.getServicePref());
            preferencesHelper.setInsights(appConfig.getInsights());

            if (appConfig.getUserGeoFilters() != null) {

                preferencesHelper.setUserGeoFilters(appConfig.getUserGeoFilters());
            }

            if (appConfig.getApis() != null) {
                TrackiApplication.setApiList(appConfig.getApis());
                //store apis in preferences as well for use inside services
                preferencesHelper.saveApiList(appConfig.getApis());
            }

            if (appConfig.getWorkflowCategories() != null) {
                preferencesHelper.saveWorkFlowCategoriesList(appConfig.getWorkflowCategories());
                Map<String, ChannelConfig> channelConfigMap = new HashMap<>();
                //    Map<String, Boolean> allowCreationMap = new HashMap<>();
                for (WorkFlowCategories categories : appConfig.getWorkflowCategories()) {
//                    if (categories.getCategoryId() != null )
//                        allowCreationMap.put(categories.getCategoryId(), categories.getAllowCreation());
                    if (categories.getCategoryId() != null && categories.getChannelConfig() != null) {
                        //categories.getChannelConfig().getChannelSetting().setAllowCreation(categories.getAllowCreation());
                        channelConfigMap.put(categories.getCategoryId(), categories.getChannelConfig());
                    }

                }
                preferencesHelper.saveWorkFlowCategoriesChannel(channelConfigMap);
                //  preferencesHelper.saveWorkFlowAllowCreation(allowCreationMap);
            }
            if (appConfig.getFlavors() != null){
                preferencesHelper.saveFlavorList(appConfig.getFlavors());

                Map<String, Flavour> flavorMap = new HashMap<>();
                for (Flavour categories : appConfig.getFlavors()) {
                    if (categories!=null&&categories.getFlavourId() != null ) {
                        flavorMap.put(categories.getFlavourId(),categories);
                    }
                }
                preferencesHelper.saveFlavourMap(flavorMap);
            }

            //These api are stored into the preferences to access their
            // api model form services as application class reference is not available
            // when app is killed.
            preferencesHelper.saveCreateTaskApi(TrackiApplication.getApiMap().get(ApiType.CREATE_TASK));
            preferencesHelper.saveEndTaskApi(TrackiApplication.getApiMap().get(ApiType.END_TASK));
            ProfileInfo profileInfo = configResponse.getUserInfo();
            if (profileInfo != null) {
                //save user info
//                if(preferencesHelper.getUserRoleId()==null&&!profileInfo.getRoleId().isEmpty())
//                   preferencesHelper.setUserRoleId(profileInfo.getRoleId());
                preferencesHelper.setUserDetail(configResponse.getUserInfo());
                if(profileInfo.getVendorId()!=null){}
                    //TrackThat.setMid(profileInfo.getVendorId());
            }

            if (null != appConfig.getAutoStart()) {
                TrackiApplication.setAutoStart(appConfig.getAutoStart());
            }
            if (from.equalsIgnoreCase("1")) {
                //check for the state of the config,alarm & hub locations.
                if (appConfig.getIdleTrackingInfo() != null && appConfig.getIdleTrackingInfo().getMode() != null && appConfig.getIdleTrackingInfo().getMode().equals("ON_SHIFT") && preferencesHelper.getIsIdealTrackingEnable()) {
                    if (preferencesHelper.getOldShiftMap() != null)
                        ShiftHandlingUtil.checkAlarms(context, preferencesHelper);
                }

                DeprecationAndExpiration screen = configResponse.getAppConfig().getAppVersionInfo();
                if (screen != null) {
                    TrackiApplication.setApiList(appConfig.getApis());
                    //store apis in preferences as well for use inside services
                    preferencesHelper.saveApiList(appConfig.getApis());
                    SharedPreferences prefs = context.getSharedPreferences("DeviceChange", Context.MODE_PRIVATE);
                    boolean deviceChangeRequestInitiated = prefs.getBoolean("deviceChangeRequestInitiated", false);;
                    if (screen.getExpired() || screen.getDeprecated()) {
                        context.startActivity(AppUpdateScreenActivity.Companion.newIntent(context)
                                .putExtra(AppConstants.Extra.EXTRA_NEXT_SCREEN, configResponse.getNextScreen().name())
                                .putExtra(AppConstants.Extra.EXTRA_DEPRICATION_SCREEN, screen.getDeprecated())
                                .putExtra(AppConstants.NOTIFICATION_DATA, jsonobject)
                        );
                        context.finish();
                    } else if(configResponse.getNextScreen() == NextScreen.DEVICE_CHANGE_HARD_POPUP){
                        if (deviceChangeRequestInitiated) {
                            Intent deviceChangeIntent = new Intent(context, DeviceChangeActivity.class);
                            deviceChangeIntent.putExtra("title", "Device Change Under Process");
                            deviceChangeIntent.putExtra("message", "Your Device Change request is under review. We will update you once it's approved");
                            deviceChangeIntent.putExtra("btnMsg", "false");
                            deviceChangeIntent.putExtra("soft", false);
                            context.startActivity(deviceChangeIntent);
                        } else {
                            prefs.edit().clear();
                            Intent intent = new Intent(context, DeviceChangeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("title", "Device Change Detected");
                            intent.putExtra("message", "You are using a new device. Please Initiate a device change request.");
                            intent.putExtra("btnMsg", "Initiate Request");
                            intent.putExtra("soft", false);
                            context.startActivity(intent);
                        }
                    }
                    else {

                        if (profileInfo != null && profileInfo.getRoleId() != null && !profileInfo.getRoleId().isEmpty() && preferencesHelper.getUserRoleId() != null && !preferencesHelper.getUserRoleId().isEmpty()) {

                            // UserType userType = UserType.valueOf(preferencesHelper.getUserType());
                            if (preferencesHelper.getUserRoleId().equals(profileInfo.getRoleId())) {
                                goToNext(preferencesHelper, configResponse.getNextScreen(), context, jsonobject);
                            } else {
                                preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.NOT_ALL);
                                goToNext(preferencesHelper, NextScreen.LOGIN, context, jsonobject);
                            }
                        } else {
                            goToNext(preferencesHelper, configResponse.getNextScreen(), context, jsonobject);
                        }
                    }
                } else {
                    if (profileInfo != null && profileInfo.getRoleId() != null && !profileInfo.getRoleId().isEmpty() && preferencesHelper.getUserRoleId() != null && !preferencesHelper.getUserRoleId().isEmpty()) {

                        // UserType userType = UserType.valueOf(preferencesHelper.getUserType());
                        if (preferencesHelper.getUserRoleId().equals(profileInfo.getRoleId())) {
                            goToNext(preferencesHelper, configResponse.getNextScreen(), context, jsonobject);
                        } else {
                            preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.NOT_ALL);
                            goToNext(preferencesHelper, NextScreen.LOGIN, context, jsonobject);
                        }
                    } else {
                        goToNext(preferencesHelper, configResponse.getNextScreen(), context, jsonobject);
                    }
                    //goToNext(preferencesHelper, configResponse.getNextScreen(), context);
                }
            } else if (from.equalsIgnoreCase("2")) {
                //start or stop autostart service.
                // CommonUtils.manageTransitionService(context, TrackiApplication.getAutoStart());
                //  showLogMessage("e","Start Tracking","start");
//                if(appConfig.getIdleTrackingInfo()!=null){
//
//                    if(appConfig.getIdleTrackingInfo().getMode().equals("ON_PUNCH")) {
//                        if (!preferencesHelper.getIdleTripActive()) {
//                            if(!preferencesHelper.getPunchStatus()) {
//                                if (!TrackThat.isTracking()) {
//                                    preferencesHelper.setIdleTripActive(true);
//                                    TrackThat.startTracking(null, false);
//                                }
//                            }
//                        }
//
//                    }
//                }
//                otpgoToNext(context, configResponse.getNextScreen(), mobile);
                //CommonUtils.updateSharedContentProvider(context, preferencesHelper);
            }
        }
    }

    public static void otpgoToNext(Activity activity, @Nullable NextScreen nextScreen, String mobile) {
        if (nextScreen == null)
            return;
        if (nextScreen == NextScreen.HOME) {
            Intent intent = MainActivity.newIntent(activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        } else if (nextScreen == NextScreen.DEVICE_CHANGE_SOFT_POPUP) {
            Log.e("CHANGE","DEVICE_CHANGE_SOFT_POPUP");
            Intent intent = DeviceChangeActivity.Companion.newIntent(activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("title","Device Change Detected");
            intent.putExtra("message","You are using a new device which is not registered with system");
            intent.putExtra("btnMsg","Proceed");
            intent.putExtra("soft",true);
            activity.startActivity(intent);
            activity.finish();

        } else if (nextScreen == NextScreen.DEVICE_CHANGE_HARD_POPUP) {
            Log.e("CHANGE","DEVICE_CHANGE_HARD_POPUP");
            Intent intent = DeviceChangeActivity.Companion.newIntent(activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("title","Device Change Detected");
            intent.putExtra("message","You are using a new device. Please Initiate a device change request.");
            intent.putExtra("btnMsg","Initiate Request");
            intent.putExtra("soft",false);
            activity.startActivity(intent);
            activity.finish();

        } else if (nextScreen == NextScreen.SIGNUP) {
            //TrackiToast.Message.showLong(activity, "Invalid Mobile Number");
            Intent intent = RegisterActivity.newIntent(activity);
            intent.putExtra(AppConstants.MOBILE, mobile);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        } else if (nextScreen == NextScreen.MY_PROFILE) {
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        } else if (nextScreen == NextScreen.ADD_PLACE) {
            Intent intent = new Intent(activity, AddLocationActivity.class);
            intent.putExtra("from", "direct");
            //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    static void openDeviceDialog(String mobile, Activity activity, String title, String msg, String btnText, Boolean soft) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT
                )
        );
        dialog.setContentView(R.layout.dialog_device_change);
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.8f;
        Window window = dialog.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        window.setGravity(Gravity.CENTER);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView msgtv = dialog.findViewById(R.id.tv_change_message);
        Button btn = dialog.findViewById(R.id.btn_proceed_change);

        tvTitle.setText(title);
        msgtv.setText(msg);
        btn.setText(btnText);

        btn.setOnClickListener(v -> {
            dialog.dismiss();
            if (soft == true){
                otpgoToNext(activity,NextScreen.HOME,mobile);
            }
            else{
                SharedPreferences prefs = activity.getSharedPreferences("DeviceChange",Context.MODE_PRIVATE);
                prefs.edit().putBoolean("deviceChangeRequestInitiated",true).apply();
                activity.finish();
            }
        });

        if (!dialog.isShowing()) dialog.show();
    }

    public static Config getFlavourConfigFromFlavourId(String flavorid, PreferencesHelper preferencesHelper){
        if(preferencesHelper==null||preferencesHelper.getFlavourMap()==null||preferencesHelper.getFlavourMap().size()==0)
            return null;
        if(!preferencesHelper.getFlavourMap().containsKey(flavorid))
            return null;
        Flavour flavour=preferencesHelper.getFlavourMap().get(flavorid);
        if(flavour==null)
            return null;

        return flavour.getConfig();
    }

    public static String getDfIdFromFlavourId(String flavorid, PreferencesHelper preferencesHelper){
        String rquired=null;
        if(preferencesHelper==null||preferencesHelper.getFlavourMap()==null||preferencesHelper.getFlavourMap().size()==0)
            return null;
        if(!preferencesHelper.getFlavourMap().containsKey(flavorid))
            return null;
        Flavour flavour=preferencesHelper.getFlavourMap().get(flavorid);
        if(flavour==null)
            return null;
        if(flavour.getConfig()==null)
            return rquired;
        else{
            if(flavour.getConfig().getDf()&&flavour.getConfig().getDfId()!=null){
                rquired=flavour.getConfig().getDfId();
                return rquired;
            }else{
                return rquired;
            }
        }
    }


    public static AllowedField getFlavourField(String flavorid,String key,PreferencesHelper preferencesHelper){
       AllowedField rquired=null;
        if(preferencesHelper==null||preferencesHelper.getFlavourMap()==null||preferencesHelper.getFlavourMap().size()==0)
            return null;
         if(!preferencesHelper.getFlavourMap().containsKey(flavorid))
            return null;
        Flavour flavour=preferencesHelper.getFlavourMap().get(flavorid);
        if(flavour==null)
            return null;
         if(flavour.getConfig()==null)
            return null;
        if(flavour.getConfig().getAllowedFields()==null||flavour.getConfig().getAllowedFields().size()==0)
            return null;
        List<AllowedField> filedList=flavour.getConfig().getAllowedFields();
        AllowedField search=new AllowedField();
        search.setField(key);
        if(filedList.contains(search)){
            int index=filedList.indexOf(search);
            if(index!=-1){
                rquired=filedList.get(index);
                rquired.setVisible(!filedList.get(index).getSkipVisibilty());
            }
        }
        return rquired;

    }
    public static boolean isBigDate(String todaydate, String givendate) {
        boolean check = false;
        try {
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        Date date1 = sdf.parse("2009-12-31");
//        Date date2 = sdf.parse("2010-01-31");
            Date date1 = sdf.parse(todaydate);
            Date date2 = sdf.parse(givendate);
            Log.e("todaydate", todaydate);
            Log.e("givendate", givendate);

            System.out.println(sdf.format(date1));
            System.out.println(sdf.format(date2));

            if (date1.after(date2)) {
                Log.e("Result", "Date1 is after Date2");
                check = true;
            }

            if (date1.before(date2)) {
                Log.e("Result", "Date1 is before Date2");
                check = false;
            }

            if (date1.equals(date2)) {
                Log.e("Result", "Date1 is equal Date2");
                check = true;
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
            check = false;
        }
        return check;
    }

    public static Field getTaskAllowedField(String flavorid,String key,PreferencesHelper preferencesHelper){
        Field rquired=null;
        if(preferencesHelper==null||preferencesHelper.getWorkFlowCategoriesListChannel()==null||preferencesHelper.getWorkFlowCategoriesListChannel().size()==0)
            return null;
        if(!preferencesHelper.getWorkFlowCategoriesListChannel().containsKey(flavorid))
            return null;
        ChannelConfig channelConfig=preferencesHelper.getWorkFlowCategoriesListChannel().get(flavorid);
        if(channelConfig==null)
            return null;
        if(channelConfig.getFields()==null||channelConfig.getFields().size()==0)
            return null;
        List<Field> filedList=channelConfig.getFields();
        Field search=new Field();
        search.setField(key);
        if(filedList.contains(search)){
            int index=filedList.indexOf(search);
            if(index!=-1){
                rquired=filedList.get(index);
                rquired.setVisible(!filedList.get(index).getSkipVisibilty());
            }
        }
        return rquired;

    }

    private static void startIdleTrackingOff(Context context) {
        Calendar dueDate = Calendar.getInstance();
        // Set Execution around 23:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 23);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);
//        if (dueDate.before(currentDate)) {
//            dueDate.add(Calendar.HOUR_OF_DAY, 24);
//        }
//        Long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();
//        WorkRequest dailyWorkRequest = new OneTimeWorkRequest.Builder(DailyIdealTrackingOffWork.class)
//                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
//                .build();
//        WorkManager.getInstance()
//                .enqueue(dailyWorkRequest);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, StopIdleTracking.class);
        intent.putExtra(AppConstants.Extra.START, true);
        // intent.setAction(TRACK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueDate.getTimeInMillis(),
                        pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, dueDate.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, dueDate.getTimeInMillis(), pendingIntent);
            }
            alarmManager.setRepeating(AlarmManager.RTC, dueDate.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        boolean alarmSet = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
        Log.e(TAG, 0 + " <----requestcode-----Alarm set for start time------alarmset----> " + alarmSet);

        //setting the repeating alarm that will be fired every day

    }

    /**
     * Go to next screen.
     *
     * @param preferencesHelper preferences helper
     * @param nextScreen        response
     */
    public static void goToNext(PreferencesHelper preferencesHelper, NextScreen nextScreen, Activity activity, @Nullable String taskDetailsJson) {
        if (nextScreen == null) {
            return;
        }
        // CommonUtils.updateSharedContentProvider(activity, preferencesHelper);
        if (nextScreen.equals(NextScreen.LOGIN)) {
            if (!preferencesHelper.isIntroFlag()) {
                preferencesHelper.setIntroFlag(true);
                openIntroScreenActivity(activity);
            } else {
                openLoginActivity(activity);
            }
        } else if (nextScreen.equals(NextScreen.HOME)) {
            //start or stop transition service
            CommonUtils.manageTransitionService(activity, TrackiApplication.getAutoStart());
            openMainActivity(activity, taskDetailsJson);
        } else if (nextScreen.equals(NextScreen.ADD_PLACE)) {
            //start or stop transition service
            Intent intent = new Intent(activity, AddLocationActivity.class);
            intent.putExtra("from", "direct");
            //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public static void openMainActivity(Activity activity, @Nullable String taskDetailsJson) {
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();
//        WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(GetUserLocationWorker.class)
//                .setConstraints(constraints)
//                .build();
//        WorkManager workManager = WorkManager.getInstance();
//        workManager.enqueue(myWorkRequest);
        Intent intent = MainActivity.newIntent(activity);
        if (taskDetailsJson != null) {
            intent.putExtra(AppConstants.NOTIFICATION_DATA, taskDetailsJson);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    private static void openLoginActivity(Activity activity) {
        Intent intent = ConsentActivity.newIntent(activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    private static void openIntroScreenActivity(Activity activity) {
        Intent intent = IntroActivity.Companion.newIntent(activity);
        activity.startActivity(intent);
        activity.finish();
    }

    public static String createSocketRequest(PacketInfo packetInfo,
                                             WebSocketManager.SocketDataRequest socketData) {
        String response = null;
        switch (packetInfo) {
            case P1: {
                response = "1:{\"data\" : \"" + socketData.getPacket1() + "\"}";
                Log.e(TAG, "Packet 1 :" + response);
            }
            break;
            case P2:
                response = "2:";
                Log.e(TAG, "Packet 2 :" + response);
                break;
            case P3: {
                /*3:{"data":{"message":{"data":"Hey" , "type":"TEXT"},"roomId":"1001"}}*/
                response = "3:{\"data\" : {\"message\":{\"data\":\"" + socketData.getPacket1() +
                        "\",\"type\": \"" + socketData.getType() + "\"},\"roomId\":\"" + socketData.getRoomId() + "\"} }";
                Log.e(TAG, "Packet 3 :" + response);
            }
            break;
            case P4: {
                StringBuilder connectionIds = new StringBuilder();
                for (int i = 0; i < socketData.getPacket4().size(); i++) {
                    connectionIds.append("\"").append(socketData.getPacket4().get(i)).append("\"");
                    int p = i;
                    p += 1;
                    //if array hashMap contains next item then add comma into the string
                    if (socketData.getPacket4().size() > p) {
                        connectionIds.append(",");
                    }
                }
                /*
                 4:{"data":{"connectionIds": ["10"],"roomId":"xyz","loadMsges":true,"msgCount":20 } }
                 */
                response = "4:{\"data\" : {\"connectionIds\" : [" + connectionIds + "], " +
                        "\"roomId\":\"" + socketData.getRoomId() + "\",\"loadMsges\" :" +
                        socketData.isLoadMsgs() + ",\"msgCount\": " + socketData.getMsgCount() + " } }";

                Log.e(TAG, "Packet 4 is:" + response);
            }
            break;
            case P5:
                break;
            case P6:
                break;
            case P7: {
                /*
                   7: {"data": {"roomId": "1001","pn": "1","mc": "20"}}
                 */
                response = "7:{\"data\" : {\"roomId\":\"" + socketData.getRoomId() +
                        "\",\"pn\":\"" + socketData.getPageNumber() + "\",\"mc\":\""
                        + socketData.getMsgCount() + "\"} }";
                Log.e(TAG, "Packet 7 is:" + response);
            }
            break;
            case P8:
                break;
            case P9:
                break;
            case P10:
                break;
            case P11:
                break;
            default:
                response = null;
                break;
        }
        return response;
    }

    public static ArrayList<File> getFileLists(String value) {
//        Log.e(TAG, "Value is: " + value);
        ArrayList<File> files = new ArrayList<>();
        if (value == null || value.equals("")) {
            return files;
        }
        String[] file = value.split(",");
        for (String s : file) {
            files.add(new File(s));
        }
//        Log.e(TAG, "File hashMap size is: " + files.size());
        return files;
    }

    public static ChatUserStatus getChatUserStatus(String status) {
        if (status == null || status.equals("")) {
            return ChatUserStatus.UNKNOWN;
        }
        switch (status) {
            case "IDLE":
                return ChatUserStatus.IDLE;
            case "BUSY":
                return ChatUserStatus.BUSY;
            case "OFFLINE":
                return ChatUserStatus.OFFLINE;
            case "ONLINE":
                return ChatUserStatus.ONLINE;
            default:
                return ChatUserStatus.UNKNOWN;

        }
    }


    /**
     * Method used to get dynamic form form the hashMap if available.
     *
     * @param formId form id of form
     * @return form
     */
    public static DynamicFormsNew getFormByFormId(String formId) {
        DynamicFormsNew dynamicFormsNew = null;
        List<DynamicFormsNew> form = TrackiApplication.getDynamicFormsNews();
        if (form == null || form.size() == 0) {
            return null;
        }

        for (int i = 0; i < form.size(); i++) {
            if (formId != null && formId.equals(form.get(i).getFormId())) {
                dynamicFormsNew = form.get(i);
                break;
            }
        }
        return dynamicFormsNew;
    }

    public static FoundWidgetItem getFormByFormIdContainsWidget(String formId, DataType type) {
        FoundWidgetItem fwi=new FoundWidgetItem();
        DynamicFormsNew dynamicFormsNew = null;
        List<DynamicFormsNew> form = TrackiApplication.getDynamicFormsNews();
        if (form == null || form.size() == 0) {
            return null;
        }

        for (int i = 0; i < form.size(); i++) {
            if (formId != null && formId.equals(form.get(i).getFormId())) {
                dynamicFormsNew = form.get(i);
                break;
            }
        }
        if(dynamicFormsNew!=null&&dynamicFormsNew.getFields()!=null) {
            for (int j = 0; j < dynamicFormsNew.getFields().size(); j++) {
                FormData formData=dynamicFormsNew.getFields().get(j);
                if(formData.getType()!=null&&formData.getType()==type){
                    fwi.setPresent(true);
                    fwi.setPostion(j);
                    fwi.setName(formData.getName());
                }
            }
        }



        return fwi;
    }

    /**
     * Method used to get the list of the data and return a list of data.
     *
     * @param mainData list of form data
     * @param taskId   taskid
     * @return @{@link DynamicFormMainData}
     */
    public static DynamicFormMainData createFormData2(ArrayList<FormData> mainData, String ctaId, String taskId, String dfId, String dfVersion) {
        ArrayList<DynamicFormData> finalList = new ArrayList<>();
        //JSONConverter jsonConverter=new JSONConverter();
        // String json=jsonConverter.objectToJson(mainData);
        //  Log.e("list",jsonConverter.objectToJson(mainData));
        for (int i = 0; i < mainData.size(); i++) {
            try {
                FormData formData = mainData.get(i);
                DynamicFormData dynamicFormData = new DynamicFormData();
                if (formData.getType() != null) {
                    switch (formData.getType()) {
                        case TIME:
                        case DATE:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getMaxRange() + "");
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            finalList.add(dynamicFormData);
                            break;
                        case DROPDOWN_API:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getFormItemKey());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            finalList.add(dynamicFormData);
                            break;
                        case CONDITIONAL_DROPDOWN_STATIC:
                        /*dynamicFormData.setKey(formData.getName());
                        dynamicFormData.setType(formData.getType());
                        dynamicFormData.setValue(new Gson().toJson());*/
//                        Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
//                        finalList.add(createMap(formData));
                            createMap(formData, finalList);
//                        createMap(formData);
                            break;
                        case DATE_RANGE:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getMaxRange() + "-" + formData.getMinRange());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            finalList.add(dynamicFormData);
                            break;
                        default:

                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getEnteredValue());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            if (formData.getEnteredValue() != null)
                                finalList.add(dynamicFormData);
                            if (formData.getWidgetData() != null) {
                                for (int j = 0; j < formData.getWidgetData().size(); j++) {

                                    List<FormData> llFormData = formData.getWidgetData().get(j).getFormDataList();
                                    if (llFormData != null) {
                                        for (FormData data : llFormData) {
                                            if (data.getEnteredValue() != null) {
                                                DynamicFormData ddfData = new DynamicFormData();
                                                ddfData.setEmbeddedDf(true);
                                                ddfData.setEmbeddedDfId(formData.getWidgetData().get(j).getTarget());
                                                ddfData.setKey(data.getName());
                                                ddfData.setType(data.getType());
                                                ddfData.setValue(data.getEnteredValue());
                                                if (formData.getEnteredValue() != null)
                                                    finalList.add(ddfData);

                                            }
                                        }
                                    }

                                }
                            }
                            break;
                        case LABLE:
                        case BUTTON:
                            break;
                    }
                }
                /*if (formData.getType() != DataType.BUTTON) {
                    DynamicFormData dynamicFormData = new DynamicFormData();
                    dynamicFormData.setKey(formData.getName());
                    dynamicFormData.setType(formData.getType());
                    dynamicFormData.setValue(formData.getEnteredValue());
                    Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                    finalList.add(dynamicFormData);
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TaskData taskData = new TaskData();
        taskData.setCtaId(ctaId);
        taskData.setTaskData(finalList);
        taskData.setUploadedAt(System.currentTimeMillis());
        JSONConverter jsonConverter = new JSONConverter();
        String json = jsonConverter.objectToJson(taskData);
        Log.e("SendData", json);

        return new DynamicFormMainData(taskData, taskId, ctaId, dfId, Integer.valueOf(dfVersion));
    }

    /**
     * Método para prevenir doble click en un elemento
     *
     * @param view
     */
    public static void preventTwoClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            public void run() {
                view.setEnabled(true);
            }
        }, 500);
    }

    public static DynamicFormMainData createFormData(ArrayList<FormData> mainData, @Nullable String ctaId, @Nullable String taskId, @Nullable String dfId, @Nullable String dfVersion) {

//        JSONConverter jsonConverter=new JSONConverter();
//         String json=jsonConverter.objectToJson(mainData);
//          Log.e("list",jsonConverter.objectToJson(mainData));
        for (int i = 0; i < mainData.size(); i++) {
            try {
                FormData formData = mainData.get(i);
                DynamicFormData dynamicFormData = new DynamicFormData();
                dynamicFormData.setEmbeddedDf(formData.getIsembded());
                dynamicFormData.setEmbeddedDfId(formData.getEmbeddedDfId());
                if (formData.getTitle() != null)
                    dynamicFormData.setLabel(formData.getTitle());
                if (formData.getType() != null) {
                    switch (formData.getType()) {
                        case TIME:
                        case DATE:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getMaxRange() + "");
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        case DROPDOWN_API:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getFormItemKey());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        case CONDITIONAL_DROPDOWN_STATIC:
                        /*dynamicFormData.setKey(formData.getName());
                        dynamicFormData.setType(formData.getType());
                        dynamicFormData.setValue(new Gson().toJson());*/
//                        Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
//                        finalList.add(createMap(formData));
                            createMap(formData, mfinalList);
                            Log.e(TAG, "conditional ---> ");
//                        createMap(formData);
                            break;
                        case DATE_RANGE:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getMaxRange() + "-" + formData.getMinRange());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        default:

                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getEnteredValue());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            if (formData.getEnteredValue() != null)
                                mfinalList.add(dynamicFormData);
                            if (formData.getWidgetData() != null) {

                                for (int j = 0; j < formData.getWidgetData().size(); j++) {

                                    //    List<FormData> llFormData = formData.getWidgetData().get(j).getFormDataList();
                                    if (formData.getWidgetData().get(j).getFormDataList() != null)
                                        getData(formData.getWidgetData().get(j).getFormDataList(), false);
//                                    if (llFormData != null) {
//                                        for (FormData data : llFormData) {
//                                            if (data.getEnteredValue() != null) {
//                                                DynamicFormData ddfData = new DynamicFormData();
//                                                ddfData.setEmbeddedDf(true);
//                                                ddfData.setEmbeddedDfId(formData.getWidgetData().get(j).getTarget());
//                                                ddfData.setKey(data.getName());
//                                                ddfData.setType(data.getType());
//                                                ddfData.setValue(data.getEnteredValue());
//                                                if(formData.getEnteredValue()!=null)
//                                                    mfinalList.add(ddfData);
//                                                if(formData.getWidgetData().get(j).getFormDataList()!=null)
//                                                getData(formData.getWidgetData().get(j).getFormDataList());
//
//
//                                            }
//                                        }
//                                    }

                                }
                            }
                            break;
                        case LABLE:
                        case BUTTON:
                            break;
                    }
                }
                /*if (formData.getType() != DataType.BUTTON) {
                    DynamicFormData dynamicFormData = new DynamicFormData();
                    dynamicFormData.setKey(formData.getName());
                    dynamicFormData.setType(formData.getType());
                    dynamicFormData.setValue(formData.getEnteredValue());
                    Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                    finalList.add(dynamicFormData);
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TaskData taskData = new TaskData();
        taskData.setCtaId(ctaId);
        ArrayList<DynamicFormData> finalList = new ArrayList<>();
        finalList.addAll(mfinalList);
        taskData.setTaskData(finalList);
        taskData.setUploadedAt(System.currentTimeMillis());
        JSONConverter jsonConverter = new JSONConverter();
        String json = jsonConverter.objectToJson(taskData);
        Log.e("SendData", json);
        mfinalList.clear();
        return new DynamicFormMainData(taskData, taskId, ctaId, dfId, Integer.valueOf(dfVersion));
    }

    static ArrayList<DynamicFormData> mfinalList = new ArrayList<>();


    public static void getData(List<FormData> mainData, boolean isAllData) {
        for (int i = 0; i < mainData.size(); i++) {
            try {
                FormData formData = mainData.get(i);
                DynamicFormData dynamicFormData = new DynamicFormData();
                dynamicFormData.setEmbeddedDf(formData.getIsembded());
                dynamicFormData.setEmbeddedDfId(formData.getEmbeddedDfId());
                dynamicFormData.setWeight(formData.getWeight());
                if (isAllData) {
                    dynamicFormData.setIncludeForCalculation(formData.getIncludeForCalculation());
                    dynamicFormData.setOperation(formData.getOperation());
                    if (formData.getErrorMessage() != null)
                        dynamicFormData.setErrorMessage(formData.getErrorMessage());
                }
                if (formData.getTitle() != null)
                    dynamicFormData.setLabel(formData.getTitle());
                if (formData.getType() != null) {
                    switch (formData.getType()) {
                        case TIME:
                        case DATE:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getMaxRange() + "");
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        case DROPDOWN_API:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getFormItemKey());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        case CONDITIONAL_DROPDOWN_STATIC:
                        /*dynamicFormData.setKey(formData.getName());
                        dynamicFormData.setType(formData.getType());
                        dynamicFormData.setValue(new Gson().toJson());*/
//                        Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
//                        finalList.add(createMap(formData));
                            createMap(formData, mfinalList);
//                        createMap(formData);
                            break;
                        case DATE_RANGE:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getMaxRange() + "-" + formData.getMinRange());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        default:

                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getEnteredValue());
                            Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            if (isAllData) {
                                mfinalList.add(dynamicFormData);
                            } else {
                                if (formData.getEnteredValue() != null)
                                    mfinalList.add(dynamicFormData);
                            }
                            if (formData.getWidgetData() != null) {

                                for (int j = 0; j < formData.getWidgetData().size(); j++) {
                                    if (formData.getWidgetData().get(j).getTarget() != null && formData.getWidgetData().get(j).getFormDataList() != null)
                                        getData(formData.getWidgetData().get(j).getFormDataList(), isAllData);

                                }
                            }
                            break;
                        case LABLE:
                        case BUTTON:
                            break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static double RoundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.parseDouble(df2.format(val));
    }

    public static CalculateFormData getAllWidgetData(ArrayList<FormData> mainData) {
        for (int i = 0; i < mainData.size(); i++) {
            try {
                FormData formData = mainData.get(i);
                DynamicFormData dynamicFormData = new DynamicFormData();
                dynamicFormData.setEmbeddedDf(formData.getIsembded());
                dynamicFormData.setEmbeddedDfId(formData.getEmbeddedDfId());
                dynamicFormData.setWeight(formData.getWeight());
                dynamicFormData.setIncludeForCalculation(formData.getIncludeForCalculation());
                dynamicFormData.setOperation(formData.getOperation());

                if (formData.getTitle() != null)
                    dynamicFormData.setLabel(formData.getTitle());
                if (formData.getErrorMessage() != null)
                    dynamicFormData.setErrorMessage(formData.getErrorMessage());
                if (formData.getType() != null) {
                    switch (formData.getType()) {
                        case TIME:
                        case DATE:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getMaxRange() + "");
                            // Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        case DROPDOWN_API:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getFormItemKey());
                            // Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        case CONDITIONAL_DROPDOWN_STATIC:

                            createMap(formData, mfinalList);
                            // Log.e(TAG, "conditional ---> ");
//                        createMap(formData);
                            break;
                        case DATE_RANGE:
                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getMaxRange() + "-" + formData.getMinRange());
                            //Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            mfinalList.add(dynamicFormData);
                            break;
                        default:

                            dynamicFormData.setKey(formData.getName());
                            dynamicFormData.setType(formData.getType());
                            dynamicFormData.setValue(formData.getEnteredValue());
                            //Log.e(TAG, "Final Data---> " + dynamicFormData.toString());
                            //if(formData.getEnteredValue()!=null)
                            mfinalList.add(dynamicFormData);
                            if (formData.getWidgetData() != null) {

                                for (int j = 0; j < formData.getWidgetData().size(); j++) {

                                    //    List<FormData> llFormData = formData.getWidgetData().get(j).getFormDataList();
                                    if (formData.getWidgetData().get(j).getFormDataList() != null)
                                        getData(formData.getWidgetData().get(j).getFormDataList(), true);


                                }
                            }
                            break;
                        case LABLE:
                        case BUTTON:
                            break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CalculateFormData taskData = new CalculateFormData();

        ArrayList<DynamicFormData> finalList = new ArrayList<>();
        finalList.addAll(mfinalList);
        taskData.setTaskData(finalList);
        JSONConverter jsonConverter = new JSONConverter();
        String json = jsonConverter.objectToJson(taskData);
        CommonUtils.showLogMessage("e", "SendData", json);
//        Log.e("SendData",json);
        mfinalList.clear();
        return taskData;
    }

    public static String getAddress(Context context, LatLng loc) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        StringBuilder strReturnedAddress = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
//                                                append(System.getProperty("line.separator"));

                }

                //   strReturnedAddress.setLength(strReturnedAddress.length() - 1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (strReturnedAddress != null)
                return strReturnedAddress.toString();
            else
                return "";
        }

    }

    /**
     * Method used to create a map with all the selected values.
     *
     * @param formData  form data.
     * @param finalList list to add
     * @return map of data.
     */
    private static void createMap(FormData formData, ArrayList<DynamicFormData> finalList) {
//        ArrayList<DynamicFormData> finalList = new ArrayList<>();
//        Map<String, Object> objectMap = new HashMap<>();
        DynamicFormData dynamicFormData = new DynamicFormData();
        if (formData != null) {
            if (formData.getType() == DataType.CONDITIONAL_DROPDOWN_STATIC) {
                if (formData.getDynamicSelectLookup() != null &&
                        formData.getDynamicSelectLookup().size() > 0) {
                    if (formData.getName() != null) {
                        dynamicFormData.setKey(formData.getName().trim());
                    }
                    dynamicFormData.setType(formData.getType());
//                    objectMap.put(formData.getFormItemKey(), createMap(formData.getDynamicSelectLookup().get(formData.getFormItemKey()), finalList));
                    dynamicFormData.setValue(formData.getFormItemKey());
                    mfinalList.add(dynamicFormData);
                    //call for next item if any
                    createMap(formData.getDynamicSelectLookup().get(formData.getFormItemKey()), mfinalList);
                }
            } else if (formData.getType() == DataType.DROPDOWN) {
                if (formData.getName() != null) {
                    dynamicFormData.setKey(formData.getName().trim());
                }
                dynamicFormData.setType(formData.getType());
                if (formData.getEnteredValue() != null) {
                    dynamicFormData.setValue(formData.getEnteredValue());
                }
                mfinalList.add(dynamicFormData);
            }
        }
    }

//    /**
//     * Method used to get the list of map explanation example is below
//     * we have a map like hashMapFileRequest is
//     * [
//     * {Gamma=[gamma-0, gamma-1, gamma-2, gamma-3, gamma-4, gamma-5, gamma-6, gamma-7, gamma-8]},
//     * {Charlie=[charlie-1]},
//     * {Alpha=[alpha-0, alpha-1, alpha-2, alpha-3, alpha-4]},
//     * {Bravo=[bravo-0, bravo-1, bravo-2, bravo-3, bravo-4, bravo-5, bravo-6, bravo-7, bravo-8]},
//     * {Beta=[beta-0, beta-1]}
//     * ]
//     * <p>
//     * Chunked Array: [[gamma-0, gamma-1, gamma-2], [gamma-3, gamma-4, gamma-5], [gamma-6, gamma-7, gamma-8]]
//     *
//     * @param hashMapFileRequest map
//     * @return arraylist of map.
//     */
//    public static ArrayList<HashMap<String, ArrayList<File>>> getMapFileList(@NotNull HashMap<String, ArrayList<File>> hashMapFileRequest) {
//        ArrayList<HashMap<String, ArrayList<File>>> mainListMap = new ArrayList<>();
//        for (Map.Entry<String, ArrayList<File>> entry : hashMapFileRequest.entrySet()) {
//            String key = entry.getKey();
//            ArrayList<File> value = entry.getValue();
//            HashMap<String, ArrayList<File>> tempMap = new HashMap<>();
//            tempMap.put(key, value);
//            mainListMap.add(tempMap);
//        }
//        return mainListMap;
//    }

    /**
     * Method used to divide an array list into chunks of given size
     *
     * @param arrayToChunk list that needs to be divided
     * @param chunkSize    size
     * @return list of list of given sizes.
     */
    public static ArrayList<List<File>> chunkArrayList(ArrayList<File> arrayToChunk, int chunkSize) {
        ArrayList<List<File>> chunkList = new ArrayList<>();
        int guide = arrayToChunk.size();
        int index = 0;
        int tale = chunkSize;
        while (tale < arrayToChunk.size()) {
            chunkList.add(arrayToChunk.subList(index, tale));
            guide = guide - chunkSize;
            index = index + chunkSize;
            tale = tale + chunkSize;
        }
        if (guide > 0) {
            chunkList.add(arrayToChunk.subList(index, index + guide));
        }
        System.out.println("Chunked Array: " + chunkList.toString());
        return chunkList;
    }

    /**
     * This method is used to update the data form server
     * if file list is already present then add into the list else create list and add.
     *
     * @param fileResponse contains maps of the file.
     */
    public static synchronized void updateData(@Nullable FileUrlsResponse fileResponse) {
        if (fileResponse != null) {
            HashMap<String, ArrayList<String>> localMap = fileResponse.getFilesUrl();
            if (localMap != null) {
                for (Map.Entry<String, ArrayList<String>> entry : localMap.entrySet()) {
                    String key = entry.getKey();
                    ArrayList<String> value = entry.getValue();

                    if (stringListHashMap.containsKey(key)) {
                        ArrayList<String> list = stringListHashMap.get(key);
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.addAll(value);
                        stringListHashMap.put(key, list);
                    } else {
                        ArrayList<String> list = new ArrayList<>(value);
                        stringListHashMap.put(key, list);
                    }
                }
                Log.e(TAG, stringListHashMap.toString());
            }
        }
    }

    /*public static void showBatteryOptimizationDialog(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }*/

    /**
     * Method is used to redirect user to the google map app with autofill source and destination.
     *
     * @param sourceLat source latitude
     * @param sourceLng source longitude
     * @param destLat   destination latitude
     * @param destLng   destination longitude
     */
    public static void openGoogleMap(Context context, double sourceLat, double sourceLng, double destLat, double destLng) {
        String url = "http://maps.google.com/maps?saddr=" + sourceLat + "," + sourceLng + "&daddr=" + destLat + "," + destLng;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    public static void openGoogleMapWithOneLocation(Context context, double sourceLat, double sourceLng) {
        // Creates an Intent that will load a map of San Francisco sourceLng geo:37.7749,-122.4194
        //  Uri gmmIntentUri = Uri.parse("geo:"+sourceLat+","+sourceLng);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + sourceLat + "," + sourceLng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    /**
     * Method used to handle force notification actions.
     *
     * @param notificationModel model for handle force action for events
     * @param context           Context of calling class.
     * @param preferencesHelper
     */
    public static void handleForceActions(@Nullable NotificationModel notificationModel, Context context, PreferencesHelper preferencesHelper) {
        if (notificationModel != null) {
            //create db helper object to set data
            IAlarmTable alarmDBHelper = DatabaseHelper.getInstance(context);
            switch (notificationModel.getEvent()) {
                case ARRIVED:
                    ApiEventModel acceptedAction = new ApiEventModel();
                    acceptedAction.setAction(Action.ARRIVE_TASK);
                    acceptedAction.setTripId(notificationModel.getTaskId());
                    acceptedAction.setTime(DateTimeUtil.getCurrentDateInMillis());
                    alarmDBHelper.addPendingApiEvent(acceptedAction);
                    break;
                case PUNCH_IN:
                    if (preferencesHelper.getIsIdealTrackingEnable()) {
                        if (!preferencesHelper.getIdleTripActive()) {
                            if (!TrackThat.isTracking()) {
                                preferencesHelper.setIdleTripActive(true);
                                TrackThat.startTracking(null, false);
                            }
                        }
                    }
                    break;
                case PUNCH_OUT:
                    if (preferencesHelper.getIsIdealTrackingEnable()) {
                        if (TrackThat.isTracking()) {
                            preferencesHelper.setIdleTripActive(false);
                            TrackThat.stopTracking();
                        }
                    }
                    break;
                case TRACKING_STATE_CHANGE:
                    if (notificationModel.getTrackingAction() == TrackingAction.END) {
                        if (TrackThat.isTracking()) {
                            preferencesHelper.setIsTrackingLiveTrip(false);
                            TrackThat.stopTracking();
                        }
                        if (preferencesHelper.getPunchStatus()) {
                            if (!preferencesHelper.getIdleTripActive()) {
                                Constraints constraints = new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build();
                                WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(StartIdealTrackWork.class)
                                        .setConstraints(constraints)
                                        .setInitialDelay(60, TimeUnit.SECONDS)
                                        .build();
                                WorkManager workManager = WorkManager.getInstance();
                                workManager.enqueue(myWorkRequest);

                            }
                        }
                    } else if (notificationModel.getTrackingAction() == TrackingAction.START) {
                        if (preferencesHelper.getIdleTripActive()) {
                            TrackThat.stopTracking();
                            preferencesHelper.setIdleTripActive(false);
                        }
                        if (!TrackThat.isTracking()) {
                            preferencesHelper.setIsTrackingLiveTrip(true);
                            if (notificationModel.getTaskId() != null)
                                TrackThat.startTracking(notificationModel.getTaskId(), true);
                        }

                    }
                    break;
                case COMPLETED:
                    if (TrackThat.isTracking()) {
                        //stop tracking
                        TrackThat.stopTracking();
                    }
                    Place destination = new Place();
                    if (TrackThat.isLastLocationAvailable() && TrackThat.getLastLocation() != null) {
                        TrackthatLocation loc = TrackThat.getLastLocation();
                        GeoCoordinates dest = new GeoCoordinates();
                        dest.setLatitude(loc.getLatitude());
                        dest.setLongitude(loc.getLongitude());

                        destination.setLocation(dest);
                        destination.setAddress(TrackThat.getAddress(loc.getLatitude(), loc.getLongitude()));
                        Log.e(TAG, "End Location is: " + loc.getLatitude() + " "
                                + loc.getLongitude() + " " + destination.getAddress());
                    }

                    EndTaskRequest request = new EndTaskRequest(notificationModel.getTaskId(), null,
                            DateTimeUtil.getCurrentDateInMillis(), destination);

                    ApiEventModel endRequest = new ApiEventModel();
                    endRequest.setAction(Action.END_TASK);
                    endRequest.setData(request);
                    endRequest.setTime(DateTimeUtil.getCurrentDateInMillis());
                    endRequest.setAutoEnd(false);
                    endRequest.setAutoStart(false);
                    alarmDBHelper.addPendingApiEvent(endRequest);
                    break;
            }
//            start the service if not running
            if (!CommonUtils.isServiceRunningInForeground(context, SyncService.class.getName())) {
                ContextCompat.startForegroundService(context, new Intent(context, SyncService.class)
                        .putExtra(AppConstants.Extra.EXTRA_STOP_SERVICE, false));
            }
        }
    }

    public static void registerAlarm(long time, Context applicationContext, String action, int requestCode, String taskId) {
        PendingIntent pendingIntent = getAlarmPendingIntent(applicationContext, action, requestCode, taskId);
        AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            Log.e(TAG, "Alarm Set for Fragment: " + time);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            Log.e(TAG, "Alarm Set for Fragment: " + time);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            Log.e(TAG, "Alarm Set for Fragment: " + time);
        }
    }

    private static PendingIntent getAlarmPendingIntent(Context context, String action, int requestCode, String taskId) {
        Intent intents = new Intent(context, ServiceRestartReceiver.class);
        intents.setAction(action);
        intents.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        if (taskId != null) {
            intents.putExtra(AppConstants.TASK_ID, taskId);
        }
        return PendingIntent.getBroadcast(context, requestCode,
                intents, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Cancel the already set alarm.
     * NOTE:- The pending intent to be canceled should be
     * same as the original pending intent that was used to
     * schedule alarm. The pending intent to be cancelled should
     * have set to same action and same data fields,if any have
     * those were used to set the alarm.
     * <p>
     * Below is the command used to check already set alarm in devices:-
     * 'adb shell dumpsys alarm'
     * This command will show all the alarms set by this device for
     * every application.
     *
     * @param context application context
     */
    public static void cancelAlarm(Context context, String action, int requestCode, String taskId) {
        PendingIntent pendingIntent = getAlarmPendingIntent(context, action, requestCode, taskId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);//important
        pendingIntent.cancel(); //important
    }

    /**
     * Method is used to divide the array into the list of list
     *
     * @param arrayToChunk array list
     * @return list of items
     */
    public static ArrayList<List<CallToActions>> divideList(List<CallToActions> arrayToChunk) {
        ArrayList<List<CallToActions>> chunkList = new ArrayList<>();
        int guide = arrayToChunk.size();
        int index = 0;
        int tale = 2;
        while (tale < arrayToChunk.size()) {
            chunkList.add(arrayToChunk.subList(index, tale));
            guide = guide - 2;
            index = index + 2;
            tale = tale + 2;
        }
        if (guide > 0) {
            chunkList.add(arrayToChunk.subList(index, index + guide));
        }
        System.out.println("Chunked Array: " + chunkList.toString());
        return chunkList;
    }

    /**
     * Method used to extract the call to actions which are performed by executor only.
     *
     * @param callToActionList call to action list.
     * @param executor         executor used to extract.
     * @return list of actions which are performed by users only.
     */
    public static List<CallToActions> extractCallToActions(List<CallToActions> callToActionList, Executor executor) {
        List<CallToActions> userCallToActions = new ArrayList<>();
        for (int i = 0; i < callToActionList.size(); i++) {
            if (callToActionList.get(i).getExecutor() == executor) {
                userCallToActions.add(callToActionList.get(i));
            }
        }
        return userCallToActions;
    }

    public static List<CallToActions> extractCallToActionsWithTypeTask(List<CallToActions> callToActionList, Executor executor, String type) {
        List<CallToActions> userCallToActions = new ArrayList<>();
        for (int i = 0; i < callToActionList.size(); i++) {
            if (callToActionList.get(i).getConditions() != null && callToActionList.get(i).getExecutor() == executor) {

                for (int j = 0; j < callToActionList.get(i).getConditions().size(); j++) {
                    if (callToActionList.get(i).getConditions().get(j).getType() != null && callToActionList.get(i).getConditions().get(j).getType().equals(type)) {
                        userCallToActions.add(callToActionList.get(i));
                        break;
                    }

                }

            }
        }
        return userCallToActions;
    }

    /**
     * Method used to handle call to actions for the task.
     *
     * @param context        context of calling class
     * @param task           current task
     * @param llCallToAction view where we have to add
     * @param mListener      callback used while click
     */
    public static void handleCallToActions(Context context, Task task, LinearLayout llCallToAction,
                                           TaskClickListener mListener) {
        if (task.getCurrentStage() != null) {
            List<CallToActions> callToActionList = task.getCurrentStage().getCallToActions();
            //clear all the views
            llCallToAction.removeAllViews();
            if (callToActionList != null && !callToActionList.isEmpty()) {
                // get the actions which are performed by users only.
                List<CallToActions> userCallToActions = CommonUtils.extractCallToActions(callToActionList, Executor.USER);
                //divide list into chunks of three to show three buttons in a list.
                ArrayList<List<CallToActions>> list = CommonUtils.divideList(userCallToActions);
                for (int i = 0; i < list.size(); i++) {

                    FlexboxLayout flexboxLayout = new FlexboxLayout(context);
                    flexboxLayout.setFlexDirection(FlexDirection.ROW);

//                    View view = flexboxLayout.getChildAt(0);
//                    FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) view.getLayoutParams();
////                    lp.order = -1;
////                    lp.flexGrow = 2;
//                    view.setLayoutParams(lp);
                    // Creating a new LinearLayout
                    LinearLayout parent = new LinearLayout(context);
                    // change the code above into
                    LinearLayout.LayoutParams layoutParams = new LinearLayout
                            .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.START;
                    //set margin to the view
                    layoutParams.topMargin = 8;

                    // set weight sum to the view
                    parent.setWeightSum(2);
                    // set orientation of parent view
                    parent.setOrientation(LinearLayout.HORIZONTAL);
                    //set gravity
                    parent.setGravity(Gravity.START);
                    // add height width of parent
                    parent.setLayoutParams(layoutParams);
                    //get item from list at position.
                    List<CallToActions> item = list.get(i);
                    //run through the loop
                    for (int j = 0; j < item.size(); j++) {
                        //children of parent LinearLayout
                        Button button = new Button(context);
                        //identify the button with tag when needed.
                        button.setTag(item.get(j).getId());
                        //set style of the text view
                        button.setTextAppearance(context, R.style.ButtonStyle);
                        //set text
                        button.setText(item.get(j).getName());
                        // change the code above into
                        LinearLayout.LayoutParams params = new LinearLayout
                                .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

//                        params.width=dpToPixel(context,);
//                        params.height=120;
//                        params.topMargin = dpToPixel(context,10);
//                        params.bottomMargin=dpToPixel(context,10);

//                        params.width=320;
//                        params.height=120;
//                        params.topMargin = 10;

                        params.width = dpToPixel(context, 100);
                        params.height = dpToPixel(context, 35);
                        params.topMargin = dpToPixel(context, 2);

//                        params.setMargins(left, top, right, bottom);
                        // yourbutton.setLayoutParams(params);
//
//                        params.weight = 1;

                        //do not add margin to last button
                        if (j != item.size() - 1) {
                            //  params.setMarginEnd(10);
                            params.setMarginEnd(dpToPixel(context, 2));
                        }
                        button.setLayoutParams(params);
                        //change the background and text color of button on the basis of flag.
                        if (item.get(j).getPrimary()) {
                            button.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stroke_grey));
                            button.getBackground().setColorFilter(Color.parseColor(/*green if primary*/"#7ed321"),
                                    PorterDuff.Mode.SRC_ATOP);
                            button.setTextColor(ContextCompat.getColor(context, R.color.white));
                        } else {
                            button.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stroke_red));
                            button.setTextColor(ContextCompat.getColor(context, R.color.red_dark5));
                        }
                        //set font family of a text view
                        Typeface typeface;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/campton_book.ttf");
                        } else {
                            typeface = ResourcesCompat.getFont(context, R.font.campton_book);
                        }
                        button.setTypeface(typeface);
                        //set text size
                        button.setGravity(Gravity.CENTER);
                        button.setTextSize(14);
                        button.setOnClickListener(v -> {
                            // used to perform the action when view is clicked.
                            mListener.onExecuteUpdates((String) v.getTag(), task);
                        });
                        //add first child of this parent
                        flexboxLayout.addView(button);
                    }
                    llCallToAction.addView(flexboxLayout);
                }
            }
        }
    }

    public static boolean handleCallToActionsNew(Context context, Task task, RecyclerView rvCallToAction,
                                                 TaskClickListener mListener) {
        if (task.getCurrentStage() != null) {
            List<CallToActions> callToActionList = task.getCurrentStage().getCallToActions();

            if (callToActionList != null && !callToActionList.isEmpty()) {
                // get the actions which are performed by users only.

                List<CallToActions> userCallToActions = CommonUtils.extractCallToActions(callToActionList, Executor.USER);
                if (userCallToActions != null && !userCallToActions.isEmpty()) {
                    CallToActionButtonAdapter adapter = new CallToActionButtonAdapter(context, mListener, task);
                    rvCallToAction.setAdapter(adapter);
                    adapter.addData(userCallToActions);
                    rvCallToAction.setVisibility(View.VISIBLE);
                    if (!(rvCallToAction.getParent() instanceof ViewGroup)) {
                        throw new IllegalStateException(
                                "mNormalView's ParentView should be a ViewGroup.");
                    }
                    ViewGroup mParent = (ViewGroup) rvCallToAction.getParent();
                    mParent.setVisibility(View.VISIBLE);
                    return true;
                } else {
                    rvCallToAction.setVisibility(View.GONE);
                    if (!(rvCallToAction.getParent() instanceof ViewGroup)) {
                        throw new IllegalStateException(
                                "mNormalView's ParentView should be a ViewGroup.");
                    }
                    ViewGroup mParent = (ViewGroup) rvCallToAction.getParent();
                    mParent.setVisibility(View.GONE);
                    return false;
                }

            } else {
                rvCallToAction.setVisibility(View.GONE);
                if (!(rvCallToAction.getParent() instanceof ViewGroup)) {
                    throw new IllegalStateException(
                            "mNormalView's ParentView should be a ViewGroup.");
                }
                ViewGroup mParent = (ViewGroup) rvCallToAction.getParent();
                mParent.setVisibility(View.GONE);
                return false;
            }
        }
        return false;

    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static int dpToPixel(Context context, int yourdpmeasure) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                yourdpmeasure,
                r.getDisplayMetrics()
        );
        return px;
    }

    public static boolean isCategoryContainsFiled(String fields, String categoryId, PreferencesHelper preferencesHelper) {
        List<Field> allowedFieldsList = new ArrayList();
        List<WorkFlowCategories> workFlowCategoriesList = preferencesHelper.getWorkFlowCategoriesList();
        if (!workFlowCategoriesList.isEmpty()) {
            for (WorkFlowCategories i : workFlowCategoriesList) {
                if (i.getCategoryId() != null && categoryId != null)
                    if (i.getCategoryId().equals(categoryId)) {
                        if (i.getChannelConfig() != null && i.getChannelConfig().getFields() != null && !i.getChannelConfig().getFields().isEmpty())
                            allowedFieldsList = i.getChannelConfig().getFields();

                    }
            }
        }
        Field lfield = new Field();
        lfield.setField(fields);

        return allowedFieldsList.contains(lfield);

    }

    public static List<Field> getAllowedFields(ArrayList<String> fieldsList, String categoryId, PreferencesHelper preferencesHelper){
        List<Field> allowedFieldsList = new ArrayList();
        List<Field> newList = new ArrayList<>();
        List<WorkFlowCategories> workFlowCategoriesList = preferencesHelper.getWorkFlowCategoriesList();
        if (!workFlowCategoriesList.isEmpty()) {
            for (WorkFlowCategories i : workFlowCategoriesList) {
                if (i.getCategoryId() != null && categoryId != null)
                    if (i.getCategoryId().equals(categoryId)) {
                        if (i.getChannelConfig() != null && i.getChannelConfig().getFields() != null && !i.getChannelConfig().getFields().isEmpty()) {
                            allowedFieldsList = i.getChannelConfig().getFields();

                        }

                    }
            }
        }


        for(int i=0;i<fieldsList.size();i++) {
            Field lfield = new Field();
            lfield.setField(fieldsList.get(i));
            if (allowedFieldsList.contains(lfield)) {
                int position = allowedFieldsList.indexOf(lfield);
                if (position != -1 && allowedFieldsList.get(position).getLabel() != null && !allowedFieldsList.get(position).getLabel().isEmpty()) {
                    newList.add(allowedFieldsList.get(position));
                }
            }
        }
        return newList;
    }


    public static String getAllowFieldLabelName(String fields, String categoryId, PreferencesHelper preferencesHelper) {
        List<Field> allowedFieldsList = new ArrayList();
        String lableName = "";
        List<WorkFlowCategories> workFlowCategoriesList = preferencesHelper.getWorkFlowCategoriesList();
        if (!workFlowCategoriesList.isEmpty()) {
            for (WorkFlowCategories i : workFlowCategoriesList) {
                if (i.getCategoryId() != null && categoryId != null)
                    if (i.getCategoryId().equals(categoryId)) {
                        if (i.getChannelConfig() != null && i.getChannelConfig().getFields() != null && !i.getChannelConfig().getFields().isEmpty())
                            allowedFieldsList = i.getChannelConfig().getFields();

                    }
            }
        }
        Field lfield = new Field();
        lfield.setField(fields);
        if (allowedFieldsList.contains(lfield)) {
            int position = allowedFieldsList.indexOf(lfield);
            if (position != -1 && allowedFieldsList.get(position).getLabel() != null && !allowedFieldsList.get(position).getLabel().isEmpty()) {
                lableName = allowedFieldsList.get(position).getLabel();
            }
        }

        return lableName;

    }

    public static String getAssigneeLabel( String categoryId, PreferencesHelper preferencesHelper) {
        String lableName = "";
        List<WorkFlowCategories> workFlowCategoriesList = preferencesHelper.getWorkFlowCategoriesList();
        if (!workFlowCategoriesList.isEmpty()) {
            for (WorkFlowCategories i : workFlowCategoriesList) {
                if (i.getCategoryId() != null && categoryId != null)
                    if (i.getCategoryId().equals(categoryId)) {
                        if (i.getChannelConfig() != null && i.getChannelConfig().getChannelSetting()!= null && i.getChannelConfig().getChannelSetting().getCreationConfig()!=null
                                && i.getChannelConfig().getChannelSetting().getCreationConfig().getAssigneeLabel()!=null)
                            lableName =i.getChannelConfig().getChannelSetting().getCreationConfig().getAssigneeLabel();

                    }
            }
        }


        return lableName;

    }

    public static String getBuddyLabel(String categoryId, PreferencesHelper preferencesHelper) {
        String lableName = "";
        List<WorkFlowCategories> workFlowCategoriesList = preferencesHelper.getWorkFlowCategoriesList();
        if (!workFlowCategoriesList.isEmpty()) {
            for (WorkFlowCategories i : workFlowCategoriesList) {
                if (i.getCategoryId() != null && categoryId != null)
                    if (i.getCategoryId().equals(categoryId)) {
                        if (i.getChannelConfig() != null && i.getChannelConfig().getChannelSetting()!= null && i.getChannelConfig().getChannelSetting().getCreationConfig()!=null
                                && i.getChannelConfig().getChannelSetting().getCreationConfig().getBuddyLabel()!=null)
                            lableName =i.getChannelConfig().getChannelSetting().getCreationConfig().getBuddyLabel();

                    }
            }
        }


        return lableName;

    }



    public static Boolean getHighLightExpiry(String categoryId, PreferencesHelper preferencesHelper) {
        boolean hilightExpiry = false;
        List<WorkFlowCategories> workFlowCategoriesList = preferencesHelper.getWorkFlowCategoriesList();
        if (!workFlowCategoriesList.isEmpty()) {
            for (WorkFlowCategories i : workFlowCategoriesList) {
                if (i.getCategoryId() != null && categoryId != null)
                    if (i.getCategoryId().equals(categoryId)) {
                        hilightExpiry = i.getEnableExpiry();
                        break;

                    }
            }
        }

        return hilightExpiry;

    }

    public static void showLogMessage(String where, String messageTag, String message) {
        if (BuildConfig.DEBUG) {
            if (message != null)
                switch (where) {
                    case "e":
                        Log.e(messageTag, message);
                        break;
                    case "w":
                        Log.w(messageTag, message);
                        break;
                    case "v":
                        Log.v(messageTag, message);
                        break;
                    case "i":
                        Log.i(messageTag, message);
                        break;
                    case "d":
                        Log.d(messageTag, message);
                        break;
                    default:
                        break;
                }
        }
    }

    public static void updateSharedContentProvider(@Nullable Context context, PreferencesHelper preferencesHelper) {
      /*  if (TrackiApplication.getApiMap().get(ApiType.CALL_LOG_SYNC) == null) {
            return;
        }
        if (context == null)
            return;
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        AlarmInfo starAlarm = null;
        AlarmInfo endAlarm = null;
        if (preferencesHelper.getAlwaysNewShiftMap() != null && preferencesHelper.getAlwaysNewShiftMap().containsKey(currentDay)) {
            //I will get here a list of ShiftTime
            List<ShiftTime> shiftTimes = preferencesHelper.getAlwaysNewShiftMap().get(currentDay);
            //get the current shift or upcoming shift.
            ShiftTime shiftTime = ShiftHandlingUtil.checkForShiftTime(shiftTimes, true);

            if (shiftTime != null) {
                String[] startHourMinuteArray = ShiftHandlingUtil.splitString(shiftTime.getFrom());
                String[] endHourMinuteArray = ShiftHandlingUtil.splitString(shiftTime.getTo());
                starAlarm = new AlarmInfo();
                endAlarm = new AlarmInfo();
                int startHour = Integer.parseInt(startHourMinuteArray[0]);
                int startMinute = Integer.parseInt(startHourMinuteArray[1]);

                int endHour = Integer.parseInt(endHourMinuteArray[0]);
                int endMinute = Integer.parseInt(endHourMinuteArray[1]);
                starAlarm.setHour(startHour);
                starAlarm.setMinute(startMinute);
                endAlarm.setHour(endHour);
                endAlarm.setMinute(endMinute);


            } else {

            }
        }


        //String shiftStartTime = new Gson().toJson(preferencesHelper.getStartAlarmInfo());
        String shiftStartTime = new Gson().toJson(starAlarm);
        // String shiftEndTime = new Gson().toJson(preferencesHelper.getStopAlarmInfo());
        String shiftEndTime = new Gson().toJson(endAlarm);
        String shiftList = new Gson().toJson(preferencesHelper.getAlwaysNewShiftMap());
        CommonUtils.showLogMessage("e", "shiftStartTime", shiftStartTime);
        CommonUtils.showLogMessage("e", "shiftEndTime", shiftEndTime);
        CommonUtils.showLogMessage("e", "shiftList", shiftList);
        boolean firstInstall = preferencesHelper.isFirstTimeInstallFlag();
        int s;

        if (firstInstall) {
            s = 0;
        } else {
            s = 1;
        }
        String t = String.valueOf(System.currentTimeMillis());

        BaseAppData baseAppData = new BaseAppData();
        if (TrackiApplication.getApiMap().get(ApiType.CALL_LOG_SYNC) != null)
            baseAppData.setApiInfo(TrackiApplication.getApiMap().get(ApiType.CALL_LOG_SYNC));
        baseAppData.setFirst_install(s);
        baseAppData.setApp_id(preferencesHelper.getFcmToken());
        //baseAppData.setApp_type(preferencesHelper.getUserType());
        baseAppData.setApp_type(AppConstants.Extra.APP_TYPE_ADMIN);
        if (preferencesHelper.getUserDetail() != null && preferencesHelper.getUserDetail().getUserId() != null)
            baseAppData.setUserId(preferencesHelper.getUserDetail().getUserId());
        if (preferencesHelper.getPunchId() == null) {
            baseAppData.setPunchId(null);
        } else {
            baseAppData.setPunchId(preferencesHelper.getPunchId());
        }
        if (preferencesHelper.getPunchStatus()) {
            baseAppData.setPunchIn("1");
        } else {
            baseAppData.setPunchIn("0");
        }
        if (preferencesHelper.getLoginToken() == null) {
            baseAppData.setIslogin("0");
            baseAppData.setLogin_token(null);
        } else {
            baseAppData.setIslogin("1");
            baseAppData.setLogin_token(preferencesHelper.getLoginToken());
        }
        baseAppData.setApp_version(BuildConfig.VERSION_NAME);
        baseAppData.setDevice_id(preferencesHelper.getDeviceId());
        baseAppData.setShift(preferencesHelper.getOldShiftMap());
        baseAppData.setTracki_ai(preferencesHelper.getAccessId());
        baseAppData.setTracki_at(t);
        baseAppData.setTracki_ts(t);
        baseAppData.setVname(BuildConfig.VERSION_NAME);
//        baseAppData.setStartalrm(preferencesHelper.getStartAlarmInfo());
//        baseAppData.setEndalarm(preferencesHelper.getStopAlarmInfo());
        baseAppData.setStartalrm(starAlarm);
        baseAppData.setEndalarm(endAlarm);
        String jsonData = new Gson().toJson(baseAppData);
        ContentValues values = new ContentValues();
        values.put(SharedDataContentProvider.data, jsonData);
        values.put(SharedDataContentProvider.id, 1);
        context.getContentResolver().delete(SharedDataContentProvider.CONTENT_URI, "id = ?", new String[]{"1"});
        Uri uri = context.getContentResolver().insert(SharedDataContentProvider.CONTENT_URI, values);*/
    }

    public static boolean openApp(Context context, String packageName, PreferencesHelper preferencesHelper) {
        if (TrackiApplication.getApiMap().get(ApiType.CALL_LOG_SYNC) == null) {
            return false;
        }
        PackageManager manager = context.getPackageManager();
        Intent callLogerIntent = manager.getLaunchIntentForPackage(packageName);

        if (callLogerIntent == null) {
            return false;
        }
        callLogerIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        callLogerIntent.setAction(packageName + ".action");
        context.startActivity(callLogerIntent);

        return true;
    }

    public static long addAppointmentsToCalender(Context curActivity, String title,
                                                 String desc, String place, int status, long startDate,
                                                 boolean needReminder, boolean needMailService, String taskId, PreferencesHelper preferencesHelper) {
/***************** Event: add event *******************/
        long eventID = -1;
        if (!preferencesHelper.getTimeReminderFlag()) {
            return eventID;
        }
        int timerBefore = Integer.parseInt(preferencesHelper.getTimeBeforeTime());
        try {
            String eventUriString = "content://com.android.calendar/events";
            ContentValues eventValues = new ContentValues();
            eventValues.put("calendar_id", 1); // id, We need to choose from
            // our mobile for primary its 1
            eventValues.put("title", title);
            eventValues.put("description", desc);
            eventValues.put("eventLocation", place);
            eventValues.put("dtstart", startDate);
            //  eventValues.put(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate);

            long endDate = startDate + 1000 * 10 * 10; // For next 10min
            //  eventValues.put(CalendarContract.EXTRA_EVENT_END_TIME, endDate);
            eventValues.put("dtend", endDate);

            // values.put("allDay", 1); //If it is bithday alarm or such
            // kind (which should remind me for whole day) 0 for false, 1
            // for true
            eventValues.put("eventStatus", status); // This information is
            // sufficient for most
            // entries tentative (0),
            // confirmed (1) or canceled
            // (2):
            TimeZone timeZone = TimeZone.getDefault();
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            showLogMessage("e", "timezone", timeZone.getID());
            eventValues.put("eventTimezone", "UTC/GMT +5:30");
            /*
             * Comment below visibility and transparency column to avoid
             * java.lang.IllegalArgumentException column visibility is invalid
             * error
             */
            // eventValues.put("allDay", 1);
            // eventValues.put("visibility", 0); // visibility to default (0),
            // confidential (1), private
            // (2), or public (3):
            // eventValues.put("transparency", 0); // You can control whether
            // an event consumes time
            // opaque (0) or transparent (1).

            eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

            Uri eventUri = curActivity.getApplicationContext()
                    .getContentResolver()
                    .insert(Uri.parse(eventUriString), eventValues);
            eventID = Long.parseLong(eventUri.getLastPathSegment());
            CommonUtils.showLogMessage("e", "eventID", eventID + "");
            if (needReminder) {
                /***************** Event: Reminder(with alert) Adding reminder to event ***********        ********/

                String reminderUriString = "content://com.android.calendar/reminders";
                ContentValues reminderValues = new ContentValues();
                reminderValues.put("event_id", eventID);
                reminderValues.put("minutes", timerBefore); // Default value of the
                // system. Minutes is a integer
                reminderValues.put("method", 1); // Alert Methods: Default(0),
                // Alert(1), Email(2),SMS(3)

                Uri reminderUri = curActivity.getApplicationContext()
                        .getContentResolver()
                        .insert(Uri.parse(reminderUriString), reminderValues);
            }

/***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

            if (needMailService) {
                String attendeuesesUriString = "content://com.android.calendar/attendees";
                /********
                 * To add multiple attendees need to insert ContentValues
                 * multiple times
                 ***********/
                ContentValues attendeesValues = new ContentValues();
                attendeesValues.put("event_id", eventID);
                attendeesValues.put("attendeeName", "xxxxx"); // Attendees name
                attendeesValues.put("attendeeEmail", "yyyy@gmail.com");// Attendee Email
                attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
                // Relationship_None(0),
                // Organizer(2),
                // Performer(3),
                // Speaker(4)
                attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
                // Required(2),
                // Resource(3)
                attendeesValues.put("attendeeStatus", 0); // NOne(0),
                // Accepted(1),
                // Decline(2),
                // Invited(3),
                // Tentative(4)

                Uri eventsUri = Uri.parse("content://calendar/events");
                Uri url = curActivity.getApplicationContext()
                        .getContentResolver()
                        .insert(eventsUri, attendeesValues);

                // Uri attendeuesesUri = curActivity.getApplicationContext()
                // .getContentResolver()
                // .insert(Uri.parse(attendeuesesUriString), attendeesValues);
            }
        } catch (Exception ex) {
            showLogMessage("e", "Error in adding event on calendar", ex.getMessage());
        }
        if (preferencesHelper.getEventIdMap() != null && !preferencesHelper.getEventIdMap().isEmpty()) {
            Map<String, Long> map = preferencesHelper.getEventIdMap();
            map.put(taskId, eventID);
            preferencesHelper.setEventIdInPref(map);

        } else {
            Map<String, Long> map = new HashMap<>();
            map.put(taskId, eventID);
            preferencesHelper.setEventIdInPref(map);
        }

        return eventID;

    }

    public static void deleteEventUri(Context context, Long eventID, String taskId, PreferencesHelper preferencesHelper) {
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = context.getContentResolver().delete(deleteUri, null, null);
        if (preferencesHelper.getEventIdMap() != null && !preferencesHelper.getEventIdMap().isEmpty()) {
            Map<String, Long> map = preferencesHelper.getEventIdMap();
            map.remove(taskId);
            preferencesHelper.setEventIdInPref(map);

        }

    }


    public static void hideViewWithAnimation(@NotNull View view) {
        view.animate()
                .translationY(view.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }


}
