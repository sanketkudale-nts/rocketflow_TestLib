package com.tracki.data.model;

import java.io.Serializable;

public class GeofenceData implements Serializable {

    private int id;
    private String trackingId;
    private String geofenceId;
    private String geofenceType;
    private int isFormSubmitted;
    private int isFormFilled;
    private int isSync;
    private String formData;
    private String taskId;

    private GeofenceData(Builder builder) {
        this.id = builder.id;
        this.trackingId = builder.trackingId;
        this.geofenceId = builder.geofenceId;
        this.geofenceType = builder.geofenceType;
        this.isFormSubmitted = builder.isFormSubmitted;
        this.isFormFilled = builder.isFormFilled;
        this.isSync = builder.isSync;
        this.formData = builder.formData;
        this.taskId = builder.taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getId() {
        return id;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public String getGeofenceType() {
        return geofenceType;
    }

    public int getIsFormSubmitted() {
        return isFormSubmitted;
    }

    public int getIsFormFilled() {
        return isFormFilled;
    }

    public int getIsSync() {
        return isSync;
    }

    public String getFormData() {
        return formData;
    }

    @Override
    public String toString() {
        return " { " +
                " id:" + id +
                " " +
                "trackingId: " + trackingId +
                " " +
                "geofenceId: " + geofenceId +
                " " +
                "geofenceType: " + geofenceType +
                " " +
                "isFormSubmitted: " + isFormSubmitted +
                " " +
                "isFormFilled: " + isFormFilled +
                " " +
                "isSync: " + isSync +
                " " +
                "formData: " + formData +
                " " +
                "taskId: " + taskId +
                " }";
    }


    public static class Builder {
        private int id;
        private String trackingId;
        private String geofenceId;
        private String geofenceType;
        private String formData;
        private int isFormSubmitted;
        private int isFormFilled;
        private int isSync;
        private String taskId;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTaskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder setTrackingId(String trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public Builder setGeofenceId(String geofenceId) {
            this.geofenceId = geofenceId;
            return this;
        }

        public Builder setGeofenceType(String geofenceType) {
            this.geofenceType = geofenceType;
            return this;
        }

        public Builder setIsFormSubmitted(int isFormSubmitted) {
            this.isFormSubmitted = isFormSubmitted;
            return this;
        }

        public Builder setIsFormFilled(int isFormFilled) {
            this.isFormFilled = isFormFilled;
            return this;
        }

        public Builder setIsSync(int isSync) {
            this.isSync = isSync;
            return this;
        }

        public Builder setFormData(String formData) {
            this.formData = formData;
            return this;
        }

        public GeofenceData build() {
            return new GeofenceData(this);
        }
    }

}
