package com.tracki.data.local.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tracki.data.model.request.AcceptRejectBuddyRequest;
import com.tracki.data.model.request.CreateTaskRequest;
import com.tracki.data.model.request.EndAutoTaskRequest;
import com.tracki.data.model.request.EndTaskRequest;
import com.tracki.data.model.request.ExecuteUpdateRequest;
import com.tracki.data.model.request.RejectTaskRequest;
import com.tracki.data.model.response.config.FormData;
import com.tracki.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rahul on 1/1/19
 */
class AlarmTable extends BaseTable {
    private static final String TAG = AlarmTable.class.getSimpleName();
    private static final String TABLE_NAME = "task_action";

    private static final String _ID = "_id";
    private static final String COLUMN_TRACKING_ID = "tracking_id";
    private static final String COLUMN_TASK_ID = "task_id";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_PENDING_ACTION = "pending_action";
    private static final String COLUMN_ACTION_NAME = "action_name";
    private static final String COLUMN_IS_SYNC = "is_sync";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_LIST = "form_data";
    private static final String COLUMN_IS_AUTO_START = "is_auto_start";
    private static final String COLUMN_IS_AUTO_CANCEL = "is_auto_cancel";


    private static final String TABLE_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + " (" +
            _ID + " INTEGER PRIMARY KEY  AUTOINCREMENT, " +
            COLUMN_TIME + " LONG," +
            COLUMN_CREATED_AT + " LONG," +
            COLUMN_IS_SYNC + " INTEGER," +
            COLUMN_TRACKING_ID + " TEXT," +
            COLUMN_PENDING_ACTION + " TEXT," +
            COLUMN_DATA + " TEXT," +
            COLUMN_LIST + " TEXT," +
            COLUMN_IS_AUTO_START + " INTEGER," +
            COLUMN_ACTION_NAME + " TEXT," +
            COLUMN_TASK_ID + " TEXT," +
            COLUMN_IS_AUTO_CANCEL + " INTEGER" +
            ");";

    static void onClearTable(final SQLiteDatabase db) {
        BaseTable.onClearTable(TABLE_NAME, db);
    }

    static void onCreate(final SQLiteDatabase db) {
        BaseTable.onCreate(TABLE_NAME, db, TABLE_CREATE_QUERY);
    }

    static void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        BaseTable.onUpgrade(TABLE_NAME, db, TABLE_CREATE_QUERY, oldVersion, newVersion);
    }

    long addPendingApiEvent(SQLiteDatabase db, ApiEventModel apiEventModel) {
        if (db == null || apiEventModel == null) {
            return -1L;
        }
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(COLUMN_TIME, apiEventModel.getTime());
        values.put(COLUMN_CREATED_AT, new Date().getTime());
        values.put(COLUMN_IS_SYNC, 0);
        values.put(COLUMN_TRACKING_ID, apiEventModel.getTripId());
        values.put(COLUMN_PENDING_ACTION, apiEventModel.getAction().name());
        values.put(COLUMN_DATA, new Gson().toJson(apiEventModel.getData()));
        values.put(COLUMN_LIST, new Gson().toJson(apiEventModel.getFormList()));
        values.put(COLUMN_IS_AUTO_CANCEL, apiEventModel.isAutoCancel() ? 1 : 0);
        int a = 0;
        if (apiEventModel.isAutoEnd() != null && apiEventModel.isAutoEnd()) {
            a = 1;
        }
        values.put(COLUMN_IS_AUTO_START, a);//0 for false 1 for true

        if (apiEventModel.getTaskId() != null) {
            values.put(COLUMN_TASK_ID, apiEventModel.getTaskId());
        }

        if (apiEventModel.getTaskAction() != null) {
            values.put(COLUMN_ACTION_NAME, apiEventModel.getTaskAction());
        }
        Log.e(TAG, values.toString());

        try {
            // Insert the new row, returning the primary key value of the new row
            return db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e(TAG, TABLE_NAME + ".addPendingApiEvent: " + e.getMessage());
        }
        return -1L;
    }

    List<ApiEventModel> checkIfRecordExists(SQLiteDatabase db) {
        List<ApiEventModel> eventModelList = new ArrayList<>();

        if (db == null) {
            return eventModelList;
        }
        try {
            String query = "SELECT * FROM " + TABLE_NAME + " where "
                    + COLUMN_IS_SYNC + " == 0"
                    + " ORDER BY " + COLUMN_CREATED_AT + " ASC LIMIT 1";
            Log.e(TAG, "-------> " + query);

            Cursor cursorEvent = db.rawQuery(query, null);
            if (cursorEvent != null && cursorEvent.moveToFirst()) {
                try {
                    do {
                        ApiEventModel model = new ApiEventModel();
                        model.setId(cursorEvent.getInt(0));
                        model.setTime(cursorEvent.getLong(1));
                        model.setTripId(cursorEvent.getString(4));
                        String s = cursorEvent.getString(5);
                        String data = cursorEvent.getString(6);
                        int isAutoStart = cursorEvent.getInt(8);
                        String taskAc = cursorEvent.getString(9);
                        String taskId = cursorEvent.getString(10);
                        int isAutoCancel = cursorEvent.getInt(11);

                        /*TaskAction taskAction = null;
                        if (taskAc != null) {
                            if (taskAc.equalsIgnoreCase(TaskAction.START.name())) {
                                taskAction = TaskAction.START;
                            } else if (taskAc.equalsIgnoreCase(TaskAction.END.name())) {
                                taskAction = TaskAction.END;
                            } else if (taskAc.equalsIgnoreCase(TaskAction.SOURCE_GEOFENCE_IN.name())) {
                                taskAction = TaskAction.SOURCE_GEOFENCE_IN;
                            } else if (taskAc.equalsIgnoreCase(TaskAction.DESTINATION_GEOFENCE_IN.name())) {
                                taskAction = TaskAction.DESTINATION_GEOFENCE_IN;
                            }
                        }*/
                        Action action = null;
                        if (s != null) {
                            if (s.equalsIgnoreCase(Action.ARRIVE_TASK.name())) {
                                action = Action.ARRIVE_TASK;
                            } else/* if (s.equalsIgnoreCase(Action.REACH_TASK.name())) {
                                action = Action.REACH_TASK;
                            } else*/ if (s.equalsIgnoreCase(Action.START_TASK.name())) {
                        /*
                        ApiEventModel startRequest = new ApiEventModel();
                startRequest.setAction(Action.START_TASK);
                startRequest.setTripId(startedTaskId);
                startRequest.setTime(DateTimeUtil.getCurrentDateInMillis());
                         */
                                action = Action.START_TASK;
                            } else if (s.equalsIgnoreCase(Action.END_TASK.name())) {
                        /*
                        endRequest.setAction(Action.END_TASK);
                endRequest.setData(request);//EndTaskRequest
                endRequest.setTime(DateTimeUtil.getCurrentDateInMillis());
                endRequest.setAutoEnd(true);
                -------------------------------------------------------------------\
                endRequest = new ApiEventModel();
                            endRequest.setAction(Action.END_TASK);
                            endRequest.setTripId(trackingId);
                            endRequest.setData(endAutoTaskRequest);//EndAutoTaskRequest
                            endRequest.setTime(DateTimeUtil.getCurrentDateInMillis());
                            endRequest.setAutoStart(true);
                            endRequest.setAutoEnd(true);
                         */
                                Object obj;
                                boolean as;
                                if (isAutoStart == 0) {
                                    as = false;
                                    obj = new Gson().fromJson(data, EndTaskRequest.class);
                                } else {
                                    as = true;
                                    obj = new Gson().fromJson(data, EndAutoTaskRequest.class);
                                }
                                model.setData(obj);
                                model.setAutoStart(as);
                                action = Action.END_TASK;
                            } else if (s.equalsIgnoreCase(Action.ACCEPT_TASK.name())) {
                        /*
 acceptedAction.setAction(Action.ACCEPT_TASK);
            acceptedAction.setTripId(task.getTaskId());
            acceptedAction.setTime(DateTimeUtil.getCurrentDateInMillis());
                         */
                                action = Action.ACCEPT_TASK;
                            } else if (s.equalsIgnoreCase(Action.CANCEL_TASK.name())) {
                        /*
 cancelAction.setAction(Action.CANCEL_TASK);
                            cancelAction.setTripId(task.getTaskId());
                            cancelAction.setTime(DateTimeUtil.getCurrentDateInMillis());
                         */
                                model.setAutoCancel(isAutoCancel == 1);
                                action = Action.CANCEL_TASK;
                            } else if (s.equalsIgnoreCase(Action.REJECT_INVITATION.name())) {
                        /*
                            apiEventModel.action = Action.REJECT_INVITATION
            apiEventModel.data = request //AcceptRejectBuddyRequest
            apiEventModel.time = DateTimeUtil.getCurrentDateInMillis()
                         */
                                model.setData(new Gson().fromJson(data, AcceptRejectBuddyRequest.class));
                                action = Action.REJECT_INVITATION;
                            } else if (s.equalsIgnoreCase(Action.REJECT_TASK.name())) {
                        /*
rejectAction.action = Action.REJECT_TASK
                rejectAction.tripId = taskId
                rejectAction.time = DateTimeUtil.getCurrentDateInMillis()
                rejectAction.data = request(RejectTaskRequest)
                         */
                                model.setData(new Gson().fromJson(data, RejectTaskRequest.class));
                                action = Action.REJECT_TASK;
                            } else if (s.equalsIgnoreCase(Action.CREATE_TASK.name())) {
                        /*
                            createRequest.setAction(Action.CREATE_TASK);
                            createRequest.setTripId(trackingId);
                            createRequest.setData(createTaskRequest);//CreateTaskRequest
                            createRequest.setTime(DateTimeUtil.getCurrentDateInMillis());
                            createRequest.setAutoStart(true);
                         */

                                boolean as;
                                as = isAutoStart != 0;
                                model.setData(new Gson().fromJson(data, CreateTaskRequest.class));
                                model.setAutoStart(as);
                                action = Action.CREATE_TASK;
                            } else if (s.equalsIgnoreCase(Action.UPLOAD_FILE.name())) {
                        /*
            uploadFileRequest.action = Action.UPLOAD_FILE
            uploadFileRequest.data = hashMapFileRequest
            uploadFileRequest.time = DateTimeUtil.getCurrentDateInMillis()
            uploadFileRequest.formList = adapter.formDataList
            uploadFileRequest.taskId=intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                         */
                                model.setData(new Gson().fromJson(data, new TypeToken<HashMap<String, ArrayList<File>>>() {
                                }.getType()));
                                model.setFormList(new Gson().fromJson(cursorEvent.getString(7), new TypeToken<List<FormData>>() {
                                }.getType()));
                                action = Action.UPLOAD_FILE;
                                model.setTaskAction(taskAc);
                                model.setTaskId(taskId);
                            } else if (s.equalsIgnoreCase(Action.EXECUTE_UPDATE.name())) {
                                action = Action.EXECUTE_UPDATE;
                                Object obj;
                                obj = new Gson().fromJson(data, ExecuteUpdateRequest.class);
                                model.setData(obj);
                            }
                        }

                        model.setAction(action);
                        eventModelList.add(model);

                    } while (cursorEvent.moveToNext());
                } finally {
                    cursorEvent.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception inside checkIfRecordExists() " + e);
            e.printStackTrace();
        }
        return eventModelList;
    }

    /**
     * Method used to sync data with server with values 0-unsynced,
     * 1-in-progress,2-sync with server
     *
     * @param db            db obj
     * @param apiEventModel model
     * @param syncMode      mode of sync
     */
    void syncWithServer(SQLiteDatabase db, ApiEventModel apiEventModel, int syncMode) {
        if (db == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_SYNC, syncMode);

        String whereClause = COLUMN_PENDING_ACTION + "== '" + apiEventModel.getAction().name() +
                "' AND " + _ID + "==" + apiEventModel.getId();
//                " AND " + COLUMN_TRACKING_ID + " == '" + apiEventModel.getTripId() + "'";
        Log.i(TAG, "Where Clause is: " + whereClause);
        try {
            int id = db.update(TABLE_NAME, values, whereClause, null);
            Log.i(TAG, "Updated at: " + id);
        } catch (Exception e) {
            Log.e(TAG, TABLE_NAME + ".syncWithServer: " + e.getMessage());
        }
    }

    int deleteData(SQLiteDatabase db) {
        if (db == null) {
            return -1;
        }

        String whereClause = _ID + " == 1";

        return db.delete(TABLE_NAME, whereClause, null);
    }

    boolean isTrackingIdAlreadyExist(SQLiteDatabase db, String trackingId) {
        if (db == null || TextUtils.isEmpty(trackingId)) {
            return true;
        }

        String queryGeo = "SELECT * FROM " + TABLE_NAME + " where "
                + COLUMN_TRACKING_ID + " == " + "'" + trackingId + "'";

        Cursor cursorGeo = db.rawQuery(queryGeo, null);
        boolean isExist = cursorGeo.moveToFirst();
        cursorGeo.close();

        return isExist;
    }
}
