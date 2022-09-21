package com.tracki.ui.service.transition;

/**
 * Created by rahul on 7/3/19
 */
public enum UnknownActivityReason {
    LOCATION_DISABLED("location_disabled"),

    LOCATION_PERMISSION_DENIED("location_permission_denied"),

    ACTIVITY_PERMISSION_DENIED("activity_permission_denied"),

    DEVICE_OFF("device_off"),

    SDK_INACTIVE("sdk_inactive"),

    NO_REASON("no_reason"),

    ACTIVITY_UNKNOWN("activity_unknown");

    private String type;

    UnknownActivityReason(String unknownActivitySegment) {
        type = unknownActivitySegment;
    }

    @Override
    public String toString() {
        return "UnknownActivityReason{" +
                "type='" + type + '\'' +
                "} " + super.toString();
    }

    public String getType() {
        return type;
    }
}
