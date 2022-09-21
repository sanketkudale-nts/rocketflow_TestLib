package com.tracki.ui.service.transition;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Date;


/**
 * Created by rahul on 7/3/19
 */
public class UserActivity implements Parcelable {

    public static final Creator<UserActivity> CREATOR = new Creator<UserActivity>() {
        @Override
        public UserActivity createFromParcel(Parcel source) {
            return new UserActivity(source);
        }

        @Override
        public UserActivity[] newArray(int size) {
            return new UserActivity[size];
        }
    };
    public ActivityType type;
    public int confidence;
    public long recordedAt;
    private String transition;
    private UnknownActivityReason unknownActivityReason;

    public UserActivity(int activityType, int confidence, int transitionType) {
        this(getActivityType(activityType), confidence, UserActivity.getActivityTransition(transitionType));
    }

    public UserActivity(ActivityType type, int confidence) {
        this.type = type;
        this.confidence = confidence;
    }

    public UserActivity(int type, int confidence) {
        this(UserActivity.getActivityType(type), confidence);
    }

    public UserActivity(ActivityType type, int confidence, String transition) {
        this.type = type;
        this.confidence = confidence;
        this.recordedAt = new Date().getTime();
        this.transition = transition;
    }

    public UserActivity(ActivityType type, int confidence,Long recordedAt, String transition) {
        this.type = type;
        this.confidence = confidence;
        this.recordedAt = recordedAt;
        this.transition = transition;
    }

    protected UserActivity(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : ActivityType.values()[tmpType];
        this.confidence = in.readInt();
        this.recordedAt = in.readLong();
        this.transition = in.readString();
        int tmpUnknownActivityReason = in.readInt();
        this.unknownActivityReason = tmpUnknownActivityReason == -1 ? null : UnknownActivityReason.values()[tmpUnknownActivityReason];
    }

    public static ActivityType getActivityType(int activityType) {
        switch (activityType) {
            case DetectedActivity.STILL:
                return ActivityType.STOP;

            case DetectedActivity.ON_BICYCLE:
                return ActivityType.CYCLE;

            case DetectedActivity.ON_FOOT:
                return ActivityType.ON_FOOT;

            case DetectedActivity.WALKING:
                return ActivityType.WALK;

            case DetectedActivity.RUNNING:
                return ActivityType.RUN;

            case DetectedActivity.IN_VEHICLE:
                return ActivityType.DRIVE;

            default:
            case DetectedActivity.UNKNOWN:
                return ActivityType.UNKNOWN;
        }
    }

    public static String getActivityTransition(int transition) {
        switch (transition) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "Enter";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "Exit";
            default:
                return "Unknown";
        }
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getActivityString() {
        return type.toString();
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public long getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(long recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public UnknownActivityReason getUnknownActivityReason() {
        return unknownActivityReason;
    }

    public void setUnknownActivityReason(UnknownActivityReason unknownActivityReason) {
        this.unknownActivityReason = unknownActivityReason;
    }

    public DetectedActivity getDetectedActivity() {
        return new DetectedActivity(type.ordinal(), confidence);
    }

    public boolean isEnter() {
        return !TextUtils.isEmpty(transition) && transition.equals("Enter");
    }

    public boolean isExit() {
        return !TextUtils.isEmpty(transition) && transition.equals("Exit");
    }

    public boolean isUnknown() {
        return ActivityType.UNKNOWN.equals(getType());
    }

    @NotNull
    @Override
    public String toString() {
        return "UserActivity{" +
                "type=" + type +
                ", confidence=" + confidence +
                ", recordedAt=" + recordedAt +
                "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeInt(this.confidence);
        dest.writeLong(this.recordedAt);
        dest.writeString(this.transition);
        dest.writeInt(this.unknownActivityReason == null ? -1 : this.unknownActivityReason.ordinal());
    }

    public enum ActivityType {
        DRIVE("drive"),

        CYCLE("cycle"),

        ON_FOOT("on_foot"),

        STOP("stop"),

        UNKNOWN("unknown"),

        WALK("walk"),

        RUN("run");

        private String type;

        ActivityType(String type) {
            this.type = type;
        }

        @NotNull
        @Override
        public String toString() {
            return this.type;
        }
    }
}
