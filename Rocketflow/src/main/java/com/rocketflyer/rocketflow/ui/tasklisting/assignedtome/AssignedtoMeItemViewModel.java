package com.rocketflyer.rocketflow.ui.tasklisting.assignedtome;

import android.content.Context;

import androidx.databinding.ObservableField;

import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.CallToActions;
import com.tracki.data.model.response.config.Executor;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.TrackingState;
import com.tracki.ui.receiver.ServiceRestartReceiver;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;
import com.tracki.utils.TaskStatus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Created by rahul on 5/10/18
 */
public class AssignedtoMeItemViewModel {
    public final AssignedtoMeItemViewModelListener mListener;
    public final Task task;
    public ObservableField<String> taskName = new ObservableField<>("");
    public ObservableField<String> assigneeNameCode = new ObservableField<>("");
    public ObservableField<String> assigneeName = new ObservableField<>("");
    public ObservableField<String> createdAt = new ObservableField<>("");
    public ObservableField<Boolean> isPending = new ObservableField<>(false);
    public ObservableField<Boolean> isLive = new ObservableField<>(false);
    public ObservableField<Integer> setRadioButtonColor = new ObservableField<>(0);
    public ObservableField<String> taskStartDateTime = new ObservableField<>("");
    public ObservableField<String> taskEndDateTime = new ObservableField<>("");
    public ObservableField<Boolean> isAccepted = new ObservableField<>(false);
    public ObservableField<Boolean> isArrived = new ObservableField<>(false);
    public ObservableField<Boolean> isContactAvail = new ObservableField<>(false);
    public ObservableField<String> contact = new ObservableField<>("");
    public ObservableField<String> isAutoStart = new ObservableField<>("");
    public ObservableField<String> taskStartLoc = new ObservableField<>("");
    public ObservableField<String> taskEndLoc = new ObservableField<>("");
    public ObservableField<String> fleetDetail = new ObservableField<>("");
    public ObservableField<String> clientTaskId = new ObservableField<>("");
    public ObservableField<Boolean> isFleetDetailVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isCurrentStateVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isTrackingVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isCompleted = new ObservableField<>(false);
    //payout feature values
    public ObservableField<String> price = new ObservableField<>(AppConstants.INR + " 0");
    public ObservableField<Boolean> isPayoutEligible = new ObservableField<>(false);

    AssignedtoMeItemViewModel(Task task, AssignedtoMeItemViewModelListener listener,
                              Context context, PreferencesHelper preferencesHelper) {
        this.mListener = listener;
        this.task = task;
        try {

            if (task.getTaskId() != null) {
                // handle task's call to action for the SYSTEM executor to execute timer based tasks.
                if (task.getCurrentStage() != null) {
                    List<CallToActions> callToActionList = task.getCurrentStage().getCallToActions();
                    if (callToActionList != null && !callToActionList.isEmpty()) {
                        // get the actions which are performed by users only.
                        List<CallToActions> systemCallToActions = CommonUtils.extractCallToActions(callToActionList, Executor.SYSTEM);
                        if (systemCallToActions.size() > 0 &&
                                !preferencesHelper.getTimerCallToAction().containsKey(task.getTaskId())) {
                            if (systemCallToActions.get(0) != null && systemCallToActions.get(0).getProperties() != null &&
                                    systemCallToActions.get(0).getProperties().containsKey(AppConstants.TIMEOUT)) {
                                //for now there is only one item for timer so directly get at 0 position
                                int timeOutInSec = Integer.parseInt(systemCallToActions.get(0).getProperties().get(AppConstants.TIMEOUT));
                                Random randomNumber = new Random();
                                //this is used to store the request code according to task
                                Map<String, Task> taskMap = new HashMap<>();
                                //set request code into the current task and save into the preferences to access later
                                task.setRequestCode(randomNumber.nextInt());
                                taskMap.put(task.getTaskId(), task);
                                preferencesHelper.setTimerCallToAction(taskMap);

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.SECOND, timeOutInSec);
                                calendar.set(Calendar.MILLISECOND, 0);

                                Log.e("Assigned to me ", "time: " + calendar.getTimeInMillis());
                                //register alarm to get the task details.
                                CommonUtils.registerAlarm(calendar.getTimeInMillis(), context.getApplicationContext(),
                                        ServiceRestartReceiver.ACTION_CALL_TO_ACTOIN_TIMER, task.getRequestCode(), task.getTaskId());
                            }
                        }
                    }
                }
            }

            if (task.getPayoutEligible() && task.getTaskStateUpdated() && task.getStatus() == TaskStatus.COMPLETED) {
                isPayoutEligible.set(task.getPayoutEligible());
                if (task.getDriverPayoutBreakUps() != null) {
                    price.set(AppConstants.INR + " " + task.getDriverPayoutBreakUps().getTotalPayout());
                }
            }
            if (task.getCurrentStage()!=null) {
                isCurrentStateVisible.set(true);

            }
            if (task.getClientTaskId()!=null) {
                clientTaskId.set(task.getClientTaskId());

            }
            if(task.getTrackingState()!=null ){
                isTrackingVisible.set(task.getTrackingState() == TrackingState.ENABLED);

            }
            if (task.getAutoCreated()) {
                this.isAutoStart.set(context.getString(R.string.autostart));
            }
            if (task.getAutoCancel()) {
                this.isAutoStart.set(context.getString(R.string.autocancel));
            }

            if (task.getContact() != null) {
                if (task.getContact().getName() != null) {
                    contact.set(task.getContact().getName());
                } else if (task.getContact().getMobile() != null) {
                    contact.set(task.getContact().getMobile());
                }
                isContactAvail.set(task.getContact().getMobile() != null);
            }
            if (task.getAssigneeDetail() != null) {
                if (!task.getAutoCreated() && !task.getAutoCancel()) {
                    this.isAutoStart.set("Assigned By: " + task.getAssigneeDetail().getName());
                }
                this.assigneeNameCode.set(task.getAssigneeDetail().getShortCode());
                this.assigneeName.set(CommonUtils.setCustomFontTypeSpan(context, task.getAssigneeDetail().getName() + " assign you a task",
                        0, Objects.requireNonNull(task.getAssigneeDetail().getName()).length(), R.font.campton_semi_bold));
            }

            this.createdAt.set(DateTimeUtil.getParsedDate(task.getCreatedAt()) + " at " + DateTimeUtil.getParsedTime(task.getCreatedAt()));
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
            this.taskEndDateTime.set(DateTimeUtil.getFormattedTime(task.getEndTime(), DateTimeUtil.DATE_TIME_FORMAT_2));
            // else
            //   this.taskStartDateTime.set(DateTimeUtil.getFormattedTime(task.getActualStartTime(), task.getActualStartDate()));

            if (this.task.getFleetDetail() != null) {
                fleetDetail.set("Assigned Fleet: " + task.getFleetDetail().getFleetName() + " | " + task.getFleetDetail().getRegNumber());
                isFleetDetailVisible.set(true);
            } else {
                isFleetDetailVisible.set(false);
            }
            if (task.getSource() != null) {
                this.taskStartLoc.set(task.getSource().getAddress());
            }
            if (task.getDestination() != null) {
                this.taskEndLoc.set(task.getDestination().getAddress());
            }

        } catch (Exception e) {
            e.printStackTrace();
            String TAG = "AssignedtoMeItemViewModel";
            Log.e(TAG, "Exception in constructor " + e.getMessage());
        }
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

    public void onCallClick() {
        mListener.onCallClick(task);
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

        void onCallClick(Task task);

        void onClickMapIcon(Task task);
    }
}
