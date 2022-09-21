//package com.tracki.ui.task_listing.filter;
//
//import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.CardView;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//
//import com.tracki.BR;
//import com.tracki.R;
//import com.tracki.databinding.ActivityTaskFilterBinding;
//import com.tracki.ui.base.BaseActivity;
//import com.tracki.ui.buddy_listing.BuddyListingActivity;
//import com.tracki.utils.AppConstants;
//import com.tracki.utils.CommonUtils;
//import com.tracki.utils.Log;
//
//import java.util.Calendar;
//
//import javax.inject.Inject;
//
///**
// * Created by rahul on 9/10/18
// */
//public class TaskFilterActivity extends BaseActivity<ActivityTaskFilterBinding, TaskFilterViewModel> implements TaskFilterNavigator, View.OnClickListener {
//
//    static final String TAG = "TaskFilterActivity";
//
//    @Inject
//    TaskFilterViewModel mTaskFilterViewModel;
//
//    private ActivityTaskFilterBinding mActivityTaskFilterBinding;
//    private int mYear, mMonth, mDay, mHour, mMin;
//    private EditText edFilterFrom, edFilterTo;
//    private TextView tvBuddyName;
//    private TableLayout tab_lay;
//
//    public static Intent newIntent(Context context) {
//        return new Intent(context, TaskFilterActivity.class);
//    }
//
//    @Override
//    public int getBindingVariable() {
//        return BR.viewModel;
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_task_filter;
//    }
//
//    @Override
//    public TaskFilterViewModel getViewModel() {
//        return mTaskFilterViewModel;
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mActivityTaskFilterBinding = getViewDataBinding();
//        mTaskFilterViewModel.setNavigator(this);
//        setUp();
//        setTableLayout();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ivNavigationIcon:
//                onBackPressed();
//                break;
//            case R.id.tvMenuText:
//                //TODO reset Button Click here
//                edFilterTo.setText("");
//                edFilterFrom.setText("");
//                tvBuddyName.setText("");
//                tvBuddyName.setVisibility(View.GONE);
//                setTableLayout();
//                break;
//            case R.id.cvByBuddy:
//                startActivityForResult(BuddyListingActivity.newIntent(this)
//                                .putExtra(AppConstants.Extra.IS_FROM_TASK_FILTER_EXTRA, true),
//                        AppConstants.REQUEST_CODE_BUDDY_LIST);
//                break;
//            case R.id.cvTaskStatus:
//                break;
//            case R.id.edFilterFrom:
//                openDateTimePicker(true);
//                break;
//            case R.id.edFilterTo:
//                openDateTimePicker(false);
//                break;
//            case R.id.btnApplyFilter:
//                Intent intent = new Intent();
//                setResult(RESULT_OK, intent);
//                finish();
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        setResult(RESULT_CANCELED);
//        finish();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }
//
//    private void openDateTimePicker(boolean isFrom) {
//        // Get Current Date
//        final Calendar c = Calendar.getInstance();
//        mYear = c.get(Calendar.YEAR);
//        mMonth = c.get(Calendar.MONTH);
//        mDay = c.get(Calendar.DAY_OF_MONTH);
//        mHour = c.get(Calendar.HOUR);
//        mMin = c.get(Calendar.MINUTE);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
//            String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
//
//            TimePickerDialog timePickerDialog = new TimePickerDialog(this, ((view1, hourOfDay, minute) -> {
//                String selectedTime = CommonUtils.getTime(hourOfDay, minute);
//
//                if (isFrom) {
//                    edFilterFrom.setText(selectedDate + " | " + selectedTime);
//                } else {
//                    edFilterTo.setText(selectedDate + " | " + selectedTime);
//                }
//            }), mHour, mMin, true);
//            timePickerDialog.show();
//
//        }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
//        datePickerDialog.show();
//    }
//
//    private void setTableLayout() {
//        try {
//            tab_lay.removeAllViews();
//            String[] text = {"Live", "Pending", "Completed", "Cancelled"};
//            for (int i = 1; i <= 2; ) {
//                TableRow a = new TableRow(this);
//                TableRow.LayoutParams param = new TableRow.LayoutParams(
//                        TableRow.LayoutParams.FILL_PARENT,
//                        TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
//                a.setLayoutParams(param);
//                a.setGravity(Gravity.CENTER_VERTICAL);
//                for (int y = 0; (y < 2) && (i <= 3); y++) {
//                    Button x = new Button(this);
//                    x.setText(text[i]);
//                    x.setGravity(Gravity.CENTER);
//                    TableRow.LayoutParams par = new TableRow.LayoutParams(y);
//                    x.setLayoutParams(par);
//                    int ids = i;
//                    x.setId(ids);
//                    x.setOnClickListener(this);
//                    a.addView(x);
//                    i++;
//                }
////                tab_lay.addView(a);
//            }
//        } catch (ArrayIndexOutOfBoundsException e) {
//            Log.e(TAG, "Error inside setTableLayout(): "
//                    + e.getLocalizedMessage());
//        }
//    }
//
//    private void setUp() {
//        View view = mActivityTaskFilterBinding.toolbar;
//        ImageView ivNavigationIcon = view.findViewById(R.id.ivNavigationIcon);
//        ivNavigationIcon.setImageResource(R.drawable.ic_cross);
//        ivNavigationIcon.setOnClickListener(this);
//        TextView toolbarTitle = view.findViewById(R.id.toolbarTitle);
//        toolbarTitle.setVisibility(View.VISIBLE);
//        toolbarTitle.setText(getString(R.string.filter));
//        TextView tvMenuText = view.findViewById(R.id.tvMenuText);
//        tvMenuText.setVisibility(View.VISIBLE);
//        tvMenuText.setText(getString(R.string.reset));
//        tvMenuText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        FrameLayout fLayoutNotification = view.findViewById(R.id.fLayoutNotification);
//        fLayoutNotification.setVisibility(View.GONE);
//        ImageView ivShare = view.findViewById(R.id.ivFilter);
//        ivShare.setVisibility(View.GONE);
//        CardView cvByBuddy = mActivityTaskFilterBinding.cvByBuddy;
//        cvByBuddy.setOnClickListener(this);
//        tvBuddyName = mActivityTaskFilterBinding.tvBuddyName;
//        tvBuddyName.setVisibility(View.GONE);
//        edFilterFrom = mActivityTaskFilterBinding.edFilterFrom;
//        edFilterFrom.setOnClickListener(this);
//        edFilterTo = mActivityTaskFilterBinding.edFilterTo;
//        edFilterTo.setOnClickListener(this);
//        Button btnApplyFilter = mActivityTaskFilterBinding.btnApplyFilter;
//        btnApplyFilter.setOnClickListener(this);
//        tab_lay = mActivityTaskFilterBinding.tabLay;
//    }
//
//    @Override
//    public void onProceedClick() {
//
//    }
//}