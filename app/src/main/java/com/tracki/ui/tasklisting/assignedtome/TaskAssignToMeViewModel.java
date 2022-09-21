package com.tracki.ui.tasklisting.assignedtome;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;

import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.AssigneeDetail;
import com.tracki.data.model.response.config.CallToActions;
import com.tracki.data.model.response.config.Executor;
import com.tracki.data.model.response.config.Task;
import com.tracki.ui.receiver.ServiceRestartReceiver;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;
import com.tracki.utils.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TaskAssignToMeViewModel {
    public final AssignedtoMeItemViewModelListener mListener;
    public final Task task;
    public ObservableField<String> taskName = new ObservableField<>("");
    public ObservableField<String> assigneeNameCode = new ObservableField<>("");
    public ObservableField<String> assigneeName = new ObservableField<>("");
    public ObservableField<Boolean> assigneeNameVisible = new ObservableField<>(false);
    public ObservableField<String> assigneeMobile = new ObservableField<>("");
    public ObservableField<Boolean> assigneeMobileVisible = new ObservableField<>(false);
    public ObservableField<String> assigneeEmail = new ObservableField<>("");
    public ObservableField<Boolean> assigneeEmailVisible = new ObservableField<>(false);
    public ObservableField<String> assigneeChatId = new ObservableField<>("");
    public ObservableField<Boolean> assigneeChatVisible = new ObservableField<>(false);
    public ObservableField<String> createdAt = new ObservableField<>("");
    public ObservableField<Boolean> createdAtVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isDateTimeVisible = new ObservableField<>(false);
    public ObservableField<String> createdTimeAt = new ObservableField<>("");
    public ObservableField<String> scheduledAt = new ObservableField<>("");
    public ObservableField<String> scheduledTimeAt = new ObservableField<>("");
    public ObservableField<Boolean> isPending = new ObservableField<>(false);
    public ObservableField<Boolean> isLive = new ObservableField<>(false);
    public ObservableField<Integer> setRadioButtonColor = new ObservableField<>(0);
    public ObservableField<String> taskStartDateTime = new ObservableField<>("");
    public ObservableField<String> taskEndDateTime = new ObservableField<>("");
    public ObservableField<Boolean> isAccepted = new ObservableField<>(false);
    public ObservableField<Boolean> isArrived = new ObservableField<>(false);
    public ObservableField<Boolean> isContactAvail = new ObservableField<>(false);
    public ObservableField<Boolean> isBuddyAvail = new ObservableField<>(false);
    public ObservableField<String> contact = new ObservableField<>("");
    public ObservableField<String> buddyName = new ObservableField<>("");
    public ObservableField<String> buddyMobile = new ObservableField<>("");
    public ObservableField<String> isAutoStart = new ObservableField<>("");
    public ObservableField<String> taskStartLoc = new ObservableField<>("");
    public ObservableField<String> taskEndLoc = new ObservableField<>("");
    public ObservableField<String> fleetDetail = new ObservableField<>("");
    public ObservableField<String> clientTaskId = new ObservableField<>("");
    public ObservableField<Boolean> isFleetDetailVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isCurrentStateVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isStartLocationVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isLocationVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isEndLocationVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isTrackingVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isCompleted = new ObservableField<>(false);
    public ObservableField<Boolean> isDetailShow = new ObservableField<>(false);
    public ObservableField<Boolean> countDownTimerVisible = new ObservableField<>(false);
    public ObservableField<String> countDownTimerText = new ObservableField<>("");
    public ObservableField<String> taskType = new ObservableField<>("");
    public ObservableField<Float> rating = new ObservableField<>(4.0f);
    //payout feature values
    public ObservableField<String> price = new ObservableField<>(AppConstants.INR + " 0");
    public ObservableField<Boolean> isPayoutEligible = new ObservableField<>(false);
    public ObservableField<Boolean> taskTypeVisible = new ObservableField<>(false);
    public ObservableField<Boolean> paymentVisible = new ObservableField<>(false);
    public ObservableField<Boolean> paymentStatusVisible = new ObservableField<>(false);
    public ObservableField<Boolean> scheduledAtVisible = new ObservableField<>(false);
    public ObservableField<Boolean> endTimeVisible = new ObservableField<>(false);
    public ObservableField<String> endTimeText = new ObservableField<>("");
    public ObservableField<String> endTimeDateText = new ObservableField<>("");
    public ObservableField<String> paymentText = new ObservableField<>("");
    public ObservableField<String> paymentStatusText = new ObservableField<>("");
    public ObservableField<Boolean> highLightExpiry = new ObservableField<>(false);
    public ObservableField<Boolean> validExpiry = new ObservableField<>(false);
    public ObservableField<String> expiryText = new ObservableField<>("");
    public ObservableField<Boolean> requestedUserVisible = new ObservableField<>(false);
    public ObservableField<Boolean> requestedUserEmailVisible = new ObservableField<>(false);
    public ObservableField<String> requestedUserEmail = new ObservableField<>("");
    public ObservableField<Boolean> requestedUserChatVisible = new ObservableField<>(false);
    public ObservableField<String> requestedUserChatId = new ObservableField<>("");
    public ObservableField<Boolean> createdByVisible = new ObservableField<>(false);
    public ObservableField<Boolean> contactPersonVisible = new ObservableField<>(false);
    public ObservableField<String> requestedUserName = new ObservableField<>("");
    public ObservableField<String> requestedUserMobile = new ObservableField<>("");
    public ObservableField<Boolean> requestedUserMobileVisible = new ObservableField<>(false);
    public ObservableField<String> referenceId = new ObservableField<>("");
    public ObservableField<Boolean> referenceIdVisible = new ObservableField<>(false);
    public ObservableField<String> pocName = new ObservableField<>("");
    public ObservableField<String> pocMobile = new ObservableField<>("");
    public ObservableField<Boolean> pocVisible = new ObservableField<>(false);
    public ObservableField<Boolean> pocMobileVisible = new ObservableField<>(false);
    public ObservableField<String> description = new ObservableField<>("");
    public ObservableField<Boolean> descriptionVisible = new ObservableField<>(false);
    public CountDownTimer countDownTimer;


    //
    public ObservableField<Boolean> assignedToDetailsVisible = new ObservableField<>(false);
    public ObservableField<Boolean> assignedToNameVisible = new ObservableField<>(false);
    public ObservableField<String> assignedToName = new ObservableField<>("");
    public ObservableField<Boolean> assignedToMobileVisible = new ObservableField<>(false);
    public ObservableField<Boolean> assignedToSelf = new ObservableField<>(false);
    public ObservableField<String> assignedToMobile = new ObservableField<>("");
    public ObservableField<Boolean> assignedToEmailVisible = new ObservableField<>(false);
    public ObservableField<String> assignedToEmail = new ObservableField<>("");
    public ObservableField<Boolean> assignedToChatVisible = new ObservableField<>(false);
    public ObservableField<String> assignedToChatId = new ObservableField<>("");

    // Map<String, Long> timerTask;


    public TaskAssignToMeViewModel(Task task, AssignedtoMeItemViewModelListener listener,
                                   Context context, PreferencesHelper preferencesHelper, String categoryId) {
        this.mListener = listener;
        this.task = task;
        try {

            if (task.getReferenceId() != null && !task.getReferenceId().isEmpty()) {
                referenceId.set(task.getReferenceId());
                referenceIdVisible.set(true);
            } else {
                referenceIdVisible.set(false);
            }
            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                description.set(task.getDescription());
                descriptionVisible.set(true);
            } else {
                descriptionVisible.set(false);
            }
            if (task.getContact() != null ) {
                if(task.getContact().getMobile()!=null&& !task.getContact().getMobile().isEmpty()){
                    pocMobile.set(task.getContact().getMobile());
                    pocMobileVisible.set(true);
                }

                if(task.getContact().getName()!=null) {
                    pocName.set(task.getContact().getName());
                    pocVisible.set(true);
                }


            } else {
                pocVisible.set(false);
            }

            if (task.getPayoutEligible() && task.getTaskStateUpdated() && task.getStatus() == TaskStatus.COMPLETED) {
                isPayoutEligible.set(task.getPayoutEligible());
                if (task.getDriverPayoutBreakUps() != null) {
                    price.set(AppConstants.INR + " " + task.getDriverPayoutBreakUps().getTotalPayout());
                }
            }
            if (task.getTaskType() != null && !task.getTaskType().isEmpty()) {
                String tt = task.getTaskType().replace("_", " ");
                taskType.set(tt);
                taskTypeVisible.set(true);
            } else {
                taskTypeVisible.set(false);
            }
            if (task.getCurrentStage() != null) {
                isCurrentStateVisible.set(true);

            }
            if (task.getClientTaskId() != null) {
                clientTaskId.set(task.getClientTaskId());

            }
//            if(task.getTrackingState()!=null ){
//                isTrackingVisible.set(task.getTrackingState());
//
//            }
            if (task.getTrackable()) {
                isTrackingVisible.set(task.getTrackable());
            }
            if (task.getAutoCreated()) {
                this.isAutoStart.set(context.getString(R.string.autostart));
            }
            if (task.getAutoCancel()) {
                this.isAutoStart.set(context.getString(R.string.autocancel));
            }
            if (task.getTcfId() != null)
                isDetailShow.set(true);

            if (task.getContact() != null) {
                if (task.getContact().getName() != null) {
                    contact.set(task.getContact().getName());
                } else if (task.getContact().getMobile() != null) {
                    contact.set(task.getContact().getMobile());
                }
                isContactAvail.set(task.getContact().getMobile() != null);
            }
            if (task.getBuddyDetail() != null) {
                if (task.getBuddyDetail().getName() != null) {
                    buddyName.set(task.getBuddyDetail().getName());
                }
                if (task.getBuddyDetail().getMobile() != null) {
                    buddyMobile.set(task.getBuddyDetail().getMobile());
                }
                isBuddyAvail.set(true);
            }
            if (task.getAssigneeDetail() != null) {
                createdByVisible.set(true);
                if (!task.getAutoCreated() && !task.getAutoCancel()) {
                    this.isAutoStart.set("Assigned By: " + task.getAssigneeDetail().getName());
                }
                this.assigneeNameCode.set(task.getAssigneeDetail().getShortCode());
                if (task.getAssigneeDetail().getName() != null && !task.getAssigneeDetail().getName().isEmpty()) {
                    this.assigneeName.set(task.getAssigneeDetail().getName());
                    assigneeNameVisible.set(true);
                }
                if (task.getAssigneeDetail().getMobile() != null && !task.getAssigneeDetail().getMobile().isEmpty()) {
                    this.assigneeMobile.set(task.getAssigneeDetail().getName());
                    assigneeMobileVisible.set(true);
                }
                if (task.getAssigneeDetail().getEmail() != null && !task.getAssigneeDetail().getEmail().isEmpty()) {
                    this.assigneeEmail.set(task.getAssigneeDetail().getEmail());
                    assigneeEmailVisible.set(true);
                }
                if (task.getAssigneeDetail().getBuddyId() != null && !task.getAssigneeDetail().getBuddyId().isEmpty()) {
                    this.assigneeChatId.set(task.getAssigneeDetail().getBuddyId());
                    assigneeChatVisible.set(true);
                }
                if (preferencesHelper.getUserDetail() != null && preferencesHelper.getUserDetail().getMobile() != null && task.getAssigneeDetail().getMobile() != null) {
                    if (preferencesHelper.getUserDetail().getMobile().equals(task.getAssigneeDetail().getMobile())) {
                        this.assigneeMobileVisible.set(false);
                    }
                }
//                this.assigneeName.set(CommonUtils.setCustomFontTypeSpan(context, task.getAssigneeDetail().getName() + " assign you a task",
//                        0, Objects.requireNonNull(task.getAssigneeDetail().getName()).length(), R.font.campton_semi_bold));
                isBuddyAvail.set(true);
            }
            if (task.getRequestedUser() != null) {
                if (task.getRequestedUser().getName() != null && !task.getRequestedUser().getName().isEmpty()) {
                    this.requestedUserVisible.set(true);
                    this.requestedUserName.set(task.getRequestedUser().getName());
                }
                if (task.getRequestedUser().getMobile() != null && !task.getRequestedUser().getMobile().isEmpty()) {
                    this.requestedUserMobileVisible.set(true);
                    this.requestedUserMobile.set(task.getRequestedUser().getMobile());
                }
                if (task.getRequestedUser().getEmail() != null && !task.getRequestedUser().getEmail().isEmpty()) {
                    this.requestedUserEmailVisible.set(true);
                    this.requestedUserEmail.set(task.getRequestedUser().getEmail());
                }
                if (task.getRequestedUser().getBuddyId() != null && !task.getRequestedUser().getBuddyId().isEmpty()) {
                    this.requestedUserChatVisible.set(true);
                    this.requestedUserChatId.set(task.getRequestedUser().getBuddyId());
                }
                if (preferencesHelper.getUserDetail() != null && preferencesHelper.getUserDetail().getMobile() != null && task.getRequestedUser().getMobile() != null) {
                    if (preferencesHelper.getUserDetail().getMobile().equals(task.getRequestedUser().getMobile())) {
                        this.requestedUserMobileVisible.set(false);
                    }
                }

            }
            if (task.getAssignedToDetails() != null && !task.getAssignedToDetails().isEmpty()) {
                if (task.getAssignmentType() != null && task.getAssignmentType().equals("GROUP")) {
                    this.assignedToDetailsVisible.set(false);
                }else{
                      AssigneeDetail assigneeDetail = task.getAssignedToDetails().get(0);
                    if (assigneeDetail != null) {


                        if (assigneeDetail.getName() != null && !assigneeDetail.getName().isEmpty()) {
                            this.assignedToDetailsVisible.set(true);
                            this.assignedToName.set(assigneeDetail.getName());
                            this.assignedToNameVisible.set(true);
                        }
                        if (assigneeDetail.getMobile() != null && !assigneeDetail.getMobile().isEmpty()) {
                            this.assignedToMobileVisible.set(true);
                            this.assignedToMobile.set(assigneeDetail.getMobile());
                        }
                        if (assigneeDetail.getEmail() != null && !assigneeDetail.getEmail().isEmpty()) {
                            this.assignedToEmailVisible.set(true);
                            this.assignedToEmail.set(assigneeDetail.getEmail());
                        }
                        if (assigneeDetail.getBuddyId() != null && !assigneeDetail.getBuddyId().isEmpty()) {
                            this.assignedToChatVisible.set(true);
                            this.assignedToChatId.set(assigneeDetail.getBuddyId());
                        }
                        if (preferencesHelper.getUserDetail() != null && preferencesHelper.getUserDetail().getMobile() != null && assigneeDetail.getMobile() != null) {
                            if (preferencesHelper.getUserDetail().getMobile().equals(assigneeDetail.getMobile())) {
                                this.assignedToSelf.set(true);
                                this.assignedToChatVisible.set(false);
                            }
                        }
                    }
                }


            }

            if (task.getContact() != null) {
                this.contactPersonVisible.set(true);
            } else {
                this.contactPersonVisible.set(false);
            }


            this.createdAt.set(DateTimeUtil.getParsedDate(task.getCreatedAt()) + " ,");
            this.createdTimeAt.set(DateTimeUtil.getParsedTime(task.getCreatedAt()));
            if (task.getCreatedAt() != 0L) {
                this.createdAtVisible.set(true);
            } else {
                this.createdAtVisible.set(false);
            }
            if (task.getStartTime() != 0L) {
                this.scheduledAt.set(DateTimeUtil.getParsedDate(task.getStartTime()) + " ,");
                this.scheduledTimeAt.set(DateTimeUtil.getParsedTime(task.getStartTime()));
                this.scheduledAtVisible.set(true);
            } else {
                this.scheduledAtVisible.set(false);
            }
            if (!createdAtVisible.get() && !scheduledAtVisible.get() && !endTimeVisible.get()) {
                this.isDateTimeVisible.set(false);
            } else {
                this.isDateTimeVisible.set(true);
            }
            if (task.getStatus() != null) {
                this.isPending.set(task.getStatus() == TaskStatus.PENDING);
                this.isLive.set(task.getStatus() == TaskStatus.LIVE);
                //if allow arrival is true then show the arrival button
                if (preferencesHelper.getAllowArrival()) {
                    this.isArrived.set(task.getStatus() == TaskStatus.ACCEPTED);
                    this.isAccepted.set(task.getStatus() == TaskStatus.ARRIVED);
                } else {
                    this.isAccepted.set(task.getStatus() == TaskStatus.ACCEPTED);
                }

                this.setRadioButtonColor.set(CommonUtils.getColor(task.getStatus()));
                isCompleted.set(task.getStatus() == TaskStatus.COMPLETED);
            }
            String s = "Task Name: " + task.getTaskName();
            this.taskName.set(CommonUtils.setCustomFontTypeSpan(context, s, 12, s.length(), R.font.campton_semi_bold));

            // if (task.getStatus() != TaskStatus.LIVE  && task.getActualStartTime() == null && task.getActualStartDate() == null)
            this.taskStartDateTime.set(DateTimeUtil.getFormattedTime(task.getStartTime(), DateTimeUtil.DATE_TIME_FORMAT_2));
            //else
            // this.taskStartDateTime.set(DateTimeUtil.getFormattedTime(task.getActualStartTime(), task.getActualStartDate()));

            //if (task.getStatus() != TaskStatus.LIVE && task.getActualEndTime() == null && task.getActualEndDate() == null)
            if (task.getEndTime() != 0L) {

                this.endTimeDateText.set(DateTimeUtil.getParsedDate(task.getEndTime()) + " ,");
                this.endTimeText.set(DateTimeUtil.getParsedTime(task.getEndTime()));

                this.endTimeVisible.set(true);
            } else {
                this.endTimeVisible.set(false);
            }
            if (task.getEndTime() > 0 && task.getCurrentStage().getTerminal() != null && !task.getCurrentStage().getTerminal()) {
                if (CommonUtils.getHighLightExpiry(categoryId, preferencesHelper)) {
                    this.highLightExpiry.set(true);
                    if (System.currentTimeMillis() < task.getEndTime()) {
                        this.validExpiry.set(true);
                        this.expiryText.set("Valid");
                        if (validExpiry.get()&&task.getTaskId() != null) {
                            // handle task's call to action for the SYSTEM executor to execute timer based tasks.
                            if (task.getCurrentStage() != null) {
                                List<CallToActions> callToActionList = task.getCurrentStage().getCallToActions();
                                if (callToActionList != null && !callToActionList.isEmpty()) {
                                    // get the actions which are performed by users only.
                                    List<CallToActions> systemCallToActions = CommonUtils.extractCallToActionsWithTypeTask(callToActionList, Executor.SYSTEM, AppConstants.TIMED);
                                    if (systemCallToActions.size() > 0 && !preferencesHelper.getTimerCallToAction().containsKey(task.getTaskId())) {

                                        if (systemCallToActions.get(0) != null &&
                                                systemCallToActions.get(0).getConditions().get(0).getProperties() != null &&
                                                systemCallToActions.get(0).getConditions().get(0).getProperties().containsKey(AppConstants.TIME)) {
                                            int timeOutInSec = Integer.parseInt(systemCallToActions.get(0).getConditions().get(0).getProperties().get(AppConstants.TIME));

                                            long counterEndTime = task.getCreatedAt() + timeOutInSec * 1000;
                                            Log.e("counterEndTime", "" + counterEndTime);
                                            if (counterEndTime > System.currentTimeMillis()) {
                                                Log.e("condition Pass", "" + System.currentTimeMillis());
                                                Random randomNumber = new Random();
                                                Map<String, Task> taskMap = new HashMap<>();
                                                task.setRequestCode(randomNumber.nextInt());
                                                taskMap.put(task.getTaskId(), task);
                                                preferencesHelper.setTimerCallToAction(taskMap);

//                                    Calendar calendar = Calendar.getInstance();
//                                    calendar.add(Calendar.SECOND, timeOutInSec);

                                                CommonUtils.registerAlarm(counterEndTime, context.getApplicationContext(),
                                                        ServiceRestartReceiver.ACTION_CALL_TO_ACTOIN_TIMER, task.getRequestCode(), task.getTaskId());
                                            }

                                        }

                                    }

                                    if (systemCallToActions.size() > 0)
                                        countDownTimerVisible.set((true));
                                    else {
                                        countDownTimerVisible.set((false));
                                    }
                                    // timerTask = preferencesHelper.getPendingTime();
//                                    int timeOutInSec = Integer.parseInt(systemCallToActions.get(0).getConditions().get(0).getProperties().get(AppConstants.TIME));
//                        long previousTime = 0;
//                        if (preferencesHelper.getPendingTime().get(task.getTaskId()) != null)
//                            previousTime = preferencesHelper.getPendingTime().get(task.getTaskId());

                                    // long fTim = timeOutInSec - previousTime;
                                    long counterEndTime = task.getEndTime() ;
                                    if (counterEndTime > System.currentTimeMillis()) {
                                        long fTim = counterEndTime - System.currentTimeMillis();
                                        countDownTimer = new CountDownTimer(fTim, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                //  timerTask.put(task.getTaskId(), (timeOutInSec - millisUntilFinished / 1000));
                                                //  preferencesHelper.setPendingTimeForTask(timerTask);
                                                // Log.e("time", "" + timerTask.get(task.getTaskId()));
                                                // long secondleft = (millisUntilFinished / 1000);
//                                if (secondleft <= 0) {
//                                    LocalBroadcastManager.getInstance(context).sendBroadcast(
//                                            new Intent(AppConstants.ACTION_REFRESH_TASK_LIST));
//                                }
                                                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
                                                countDownTimerText.set(hms + " Left");
                                                // countDownTimerText.set(secondleft + " Sec Left");
                                            }

                                            public void onFinish() {
                                                countDownTimerText.set(" Time UP");
                                                CommonUtils.showLogMessage("e", "finish 1", "1");
                                                countDownTimerVisible.set((false));
                                                if (preferencesHelper.getPendingTime().containsKey(task.getTaskId())) {
                                                    preferencesHelper.getPendingTime().remove(task.getTaskId());
                                                }


                                            }
                                        };
                                        countDownTimer.start();
                                    }

                                }
                            }
                        }

                    } else {
                        this.validExpiry.set(false);
                        this.expiryText.set("Expired");
                    }
                }

            }
            this.taskEndDateTime.set(DateTimeUtil.getFormattedTime(task.getEndTime(), DateTimeUtil.DATE_TIME_FORMAT_2));
            // else
            //   this.taskStartDateTime.set(DateTimeUtil.getFormattedTime(task.getActualStartTime(), task.getActualStartDate()));

            if (this.task.getFleetDetail() != null) {
                // fleetDetail.set("Assigned Fleet: " + task.getFleetDetail().getFleetName() + " | " + task.getFleetDetail().getRegNumber());
                fleetDetail.set(task.getFleetDetail().getFleetName() + " | " + task.getFleetDetail().getInventoryId());
                isFleetDetailVisible.set(true);
            } else {
                isFleetDetailVisible.set(false);
            }
            if (task.getSource() != null && task.getSource().getAddress()!=null&&!task.getSource().getAddress().isEmpty()) {
                this.taskStartLoc.set(task.getSource().getAddress());
                this.isStartLocationVisible.set(true);
            }
            if (task.getPaymentData() != null && task.getPaymentData().getAmountBreakup() != null && task.getPaymentData().getAmountBreakup().getTotalAmt() != null) {
                paymentVisible.set(true);
                paymentText.set(task.getPaymentData().getAmountBreakup().getTotalAmt() + " INR");
                if (task.getPaymentData().getStatus() != null && !task.getPaymentData().getStatus().isEmpty()) {
                    paymentStatusVisible.set(true);
                    paymentStatusText.set(task.getPaymentData().getStatus());
                }
            } else {
                paymentVisible.set(false);
            }
            if (task.getDestination() != null&&task.getDestination().getAddress()!=null && !task.getDestination().getAddress().isEmpty()) {
                this.taskEndLoc.set(task.getDestination().getAddress());
                this.isEndLocationVisible.set(true);
            }
            if (!isStartLocationVisible.get() && !isEndLocationVisible.get()) {
                this.isLocationVisible.set(false);
            } else {
                this.isLocationVisible.set(true);
            }


        } catch (Exception e) {
            e.printStackTrace();
            String TAG = "AssignedtoMeItemViewModel";
            Log.e(TAG, "Exception in constructor " + e.getMessage());
        }
    }

    private void blinkWork() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 600;
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (countDownTimerVisible.get())
                            countDownTimerVisible.set((false));
                        else
                            countDownTimerVisible.set((true));
                        blinkWork();
                    }
                });
            }
        }).start();
    }

//    public void onAccept() {
//        mListener.onAcceptTask(task);
//    }
//
//    public void onReject() {
//        mListener.onRejectTask(task);
//    }
//
//    public void onArrive() {
//        mListener.onArrive(task);
//    }
//
//    public void onStart() {
//        mListener.onStartTask(task);
//    }
//
//    public void onCancel() {
//        mListener.onCancelTask(task);
//    }
//
//    public void onEnd() {
//        mListener.onEndTask(task);
//    }

    public void onClickMapIcon() {
        mListener.onClickMapIcon(task);
    }

    public void onItemClick() {
        //if call to action is null or current stage is null then its in completed state
        if (task.getCurrentStage() != null || task.getCurrentStage().getCallToActions() != null) {
            mListener.onItemClick(task);
        }
    }

    public void onCallClick(String mobile) {
        mListener.onCallClick(mobile);
    }

    public void onDetailsClick() {
        mListener.onDetailsClick(task);
    }


    public interface AssignedtoMeItemViewModelListener {

//        void onAcceptTask(Task task);
//
//        void onRejectTask(Task task);
//
//        void onArrive(Task task);
//
//        void onStartTask(Task task);
//
//        void onCancelTask(Task task);
//
//        void onEndTask(Task task);

        void onItemClick(Task bean);

        void onCallClick(String mobile);

        void onClickMapIcon(Task task);

        void onDetailsClick(Task task);

        void onChatStart(String buddyId, String buddyName);
    }


    public void onChatStart(String buddyId, String buddyName) {
        mListener.onChatStart(buddyId, buddyName);
    }


    @BindingAdapter("blink")
    public static void blink(TextView view, String time) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(400); //You can manage the blinking time with this parameter
        anim.setStartOffset(10);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }

    @BindingAdapter("colorFilter")
    public static void setColorFilter(TextView view, String status) {
        //PENDING, PAID, CANCELLED
        if (status.equals("PENDING")) {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.layout_bg_payments_status_pending));
            view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.light_blue_4));
        } else if (status.equals("PAID")) {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.layout_bg_payments_status_paid));
            view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.green));

        } else if (status.equals("CANCELLED")) {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.layout_bg_payments_status_cancel));
            view.setTextColor(ContextCompat.getColor(view.getContext(), android.R.color.holo_red_dark));
        } else {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.layout_bg_payments_status));
            view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));


        }

    }

    @BindingAdapter("highbackground")
    public static void setHighLightBackground(RelativeLayout view, Boolean status) {
        //PENDING, PAID, CANCELLED
        if (status != null && status) {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.green));

        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(), android.R.color.holo_red_dark));

        }

    }
    @BindingAdapter("highbackgroundshaped")
    public static void setHighLightBackgroundShaped(TextView view, Boolean status) {
        //PENDING, PAID, CANCELLED
        if (status != null && status) {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.valid_bg));

        } else {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.expired_bg));

        }

    }

    @BindingAdapter("paymentbg")
    public static void serPaymentBackground(TextView view, String status) {
        //PENDING, PAID, CANCELLED
        if (status != null && status.equalsIgnoreCase("Paid")) {
            view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.green));
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.payment_paid_bg));

        } else {
            view.setTextColor(ContextCompat.getColor(view.getContext(), R.color.google));
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.payment_pending_bg));

        }

    }

}
