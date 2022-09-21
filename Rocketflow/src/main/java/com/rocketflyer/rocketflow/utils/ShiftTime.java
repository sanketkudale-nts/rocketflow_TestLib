package com.rocketflyer.rocketflow.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Created by rahul on 28/12/18
 */
public class ShiftTime {
    private String from;
    private String to;
    private String shiftId;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @NotNull
    @Override
    public String toString() {
        return "{ " +
                "from " + from + ", " +
                "to " + to + ", " +
                "shiftId " + shiftId +
                " }";
    }
}
