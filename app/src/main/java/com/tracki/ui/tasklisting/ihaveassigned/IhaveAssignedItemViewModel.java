package com.tracki.ui.tasklisting.ihaveassigned;

import android.content.Context;

import androidx.databinding.ObservableField;

import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.TrackingState;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.Log;
import com.tracki.utils.TaskStatus;

import java.util.Objects;

/**
 * Created by rahul on 5/10/18
 */
public class IhaveAssignedItemViewModel {

     public final Task task;
    public IhaveAssignedItemViewModelListener mListener;
//    public ObservableField<Boolean> isEnd = new ObservableField<>(false);
//    public ObservableField<Boolean> isCancel = new ObservableField<>(false);

    public ObservableField<String> taskName = new ObservableField<>("");
    public ObservableField<String> assigneeNameCode = new ObservableField<>("");
    public ObservableField<String> assigneeName = new ObservableField<>("");
    public ObservableField<String> createdAt = new ObservableField<>("");
    public ObservableField<Boolean> isLive = new ObservableField<>(false);
    public ObservableField<Integer> setRadioButtonColor = new ObservableField<>(0);
    public ObservableField<String> taskStartDateTime = new ObservableField<>("");
    public ObservableField<String> taskEndDateTime = new ObservableField<>("");
    public ObservableField<Boolean> isArrived = new ObservableField<>(false);
    public ObservableField<Boolean> isContactAvail = new ObservableField<>(false);
    public ObservableField<String> contact = new ObservableField<>("");
    public ObservableField<String> isAutoStart = new ObservableField<>("");
    public ObservableField<String> taskStartLoc = new ObservableField<>("");
    public ObservableField<String> taskEndLoc = new ObservableField<>("");
    public ObservableField<String> fleetDetail = new ObservableField<>("");
    public ObservableField<Boolean> isFleetDetailVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isCurrentStateVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isTrackingVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isCompleted = new ObservableField<>(false);
    public ObservableField<Boolean> isPending = new ObservableField<>(false);
    public ObservableField<Boolean> isAccepted = new ObservableField<>(false);

    //payout feature values
    public ObservableField<String> price = new ObservableField<>(AppConstants.INR + " 0");
    public ObservableField<Boolean> isPayoutEligible = new ObservableField<>(false);


    IhaveAssignedItemViewModel(Task task, IhaveAssignedItemViewModelListener listener,
                               Context context, PreferencesHelper preferencesHelper) {
        this.mListener = listener;
        this.task = task;

        String TAG = "IhaveAssignedItemViewModel";
        try {
            if (task.getPayoutEligible() && task.getTaskStateUpdated() && task.getStatus() == TaskStatus.COMPLETED) {
                isPayoutEligible.set(task.getPayoutEligible());
                if (task.getDriverPayoutBreakUps() != null) {
                    price.set(AppConstants.INR + " " + task.getDriverPayoutBreakUps().getTotalPayout());
                }
            }
            if (task.getCurrentStage()!=null) {
                isCurrentStateVisible.set(true);

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

            if (task.getAssigneeDetail() != null && task.getBuddyDetail() != null) {
                if (!task.getAutoCreated() && !task.getAutoCancel()) {
                    this.isAutoStart.set("Assigned By: " + task.getAssigneeDetail().getName());
                }
                this.assigneeNameCode.set(task.getBuddyDetail().getShortCode());
                this.assigneeName.set(CommonUtils.setCustomFontTypeSpan(context, "You have assigned a task to " + task.getBuddyDetail().getName(),
                        0, Objects.requireNonNull(task.getAssigneeDetail().getName()).length(), R.font.campton_semi_bold));
            }

            this.createdAt.set(DateTimeUtil.getParsedDate(task.getCreatedAt()) + " at " + DateTimeUtil.getParsedTime(task.getCreatedAt()));
            if (task.getStatus() != null) {
//                boolean isEnd = true;
//                if (task.getStatus() == TaskStatus.LIVE ||
//                        task.getStatus() == TaskStatus.COMPLETED ||
//                        task.getStatus() == TaskStatus.CANCELLED ||
//                        task.getStatus() == TaskStatus.REJECTED) {
//
//                    isEnd = false;
//                } else if (/*If current time is greater than start time then donot make it visible*/
//                        DateTimeUtil.getCurrentDateInMillis() > task.getStartTime()) {
//                    isEnd = false;
//                }
//                this.isEnd.set(isEnd);
//                this.isCancel.set((task.getStatus() == TaskStatus.PENDING ||
//                        task.getStatus() == TaskStatus.ACCEPTED));

                this.isPending.set(task.getStatus() == TaskStatus.PENDING);
                this.isLive.set(task.getStatus() == TaskStatus.LIVE);
                //if allow arrival is true then show the arrival button
                if (preferencesHelper.getAllowArrival()) {
                    this.isArrived.set(task.getStatus() == TaskStatus.ACCEPTED);
                    this.isAccepted.set(task.getStatus() == TaskStatus.ARRIVED);
                } else {
                    this.isAccepted.set(task.getStatus() == TaskStatus.ACCEPTED);
                }
                isCompleted.set(task.getStatus() == TaskStatus.COMPLETED);

                this.setRadioButtonColor.set(CommonUtils.getColor(task.getStatus()));
            }
            String s = "Task Name: " + task.getTaskName();
            this.taskName.set(CommonUtils.setCustomFontTypeSpan(context, s, 12, s.length(), R.font.campton_semi_bold));
            this.taskStartDateTime.set(DateTimeUtil.getFormattedTime(task.getStartTime(), DateTimeUtil.DATE_TIME_FORMAT_2));
            this.taskEndDateTime.set(DateTimeUtil.getFormattedTime(task.getEndTime(), DateTimeUtil.DATE_TIME_FORMAT_2));

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
            Log.e(TAG, "Exception in constructor " + e.getMessage());
        }
    }

    public void onClickMapIcon() {
        mListener.onClickMapIcon(task);
    }

    public void onItemClick() {
        //if call to action is null or current stage is null then its in completed state
        if (task.getCurrentStage() == null || task.getCurrentStage().getCallToActions() == null) {
            mListener.onItemClick(task);
        }
    }

    public void onCallClick() {
        mListener.onCallClick(task);
    }

    public interface IhaveAssignedItemViewModelListener {

        void onItemClick(Task bean);

        void onCallClick(Task task);

        void onClickMapIcon(Task task);
    }
}
