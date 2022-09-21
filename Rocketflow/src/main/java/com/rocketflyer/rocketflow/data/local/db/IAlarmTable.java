package com.rocketflyer.rocketflow.data.local.db;

import com.tracki.data.local.db.ApiEventModel;

import java.util.List;

/**
 * Created by Rahul Abrol on 4/6/18.
 */
public interface IAlarmTable {
    void addPendingApiEvent(ApiEventModel apiEventModel);

    void syncWithServer(ApiEventModel apiEventModel, int syncMode);

    List<ApiEventModel> checkIfRecordExists();

//    boolean checkIfEndSynced();

    int deleteData();

    boolean isTrackingIdAlreadyExist(String trackingId);
}
