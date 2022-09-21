package com.tracki.data.local.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.tracki.data.model.GeofenceData;
import com.trackthat.lib.internal.common.Trunk;

import java.util.Date;

public class GeofenceTable extends BaseTable {
    private static final String TAG = GeofenceTable.class.getName();
    private static final String TABLE_NAME = "trip_geofence";

    private static final String _ID = "_id";
    private static final String COLUMN_GFENCE_ID = "geofence_id";
    private static final String COLUMN_TYPE = "geofence_type";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_IS_SYNC = "is_sync";
    private static final String COLUMN_TRACKING_ID = "tracking_id";
    private static final String COLUMN_FORM_SUBMITTED = "form_submitted";
    private static final String COLUMN_FORM_FILLED = "form_filled";
    private static final String COLUMN_FORM_DATA = "form_data";
    private static final String COLUMN_TASK_ID = "task_id";


    private static final String TABLE_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + " (" +
            _ID + " INTEGER PRIMARY KEY  AUTOINCREMENT, " +
            COLUMN_TRACKING_ID + " TEXT," +
            COLUMN_GFENCE_ID + " TEXT," +
            COLUMN_TYPE + " TEXT," +
            COLUMN_CREATED_AT + " LONG," +
            COLUMN_FORM_SUBMITTED + " INTEGER," +
            COLUMN_FORM_FILLED + " INTEGER," +
            COLUMN_IS_SYNC + " INTEGER," +
            COLUMN_FORM_DATA + " TEXT," +
            COLUMN_TASK_ID + " TEXT" +
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

    long addGeoFenceToDB(SQLiteDatabase db, GeofenceData geofenceData) {
        if (db == null || geofenceData == null) {
            return -1L;
        }
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRACKING_ID, geofenceData.getTrackingId());
        values.put(COLUMN_GFENCE_ID, geofenceData.getGeofenceId());
        values.put(COLUMN_TYPE, geofenceData.getGeofenceType());
        values.put(COLUMN_FORM_SUBMITTED, geofenceData.getIsFormSubmitted());
        values.put(COLUMN_CREATED_AT, new Date().getTime());
        values.put(COLUMN_FORM_FILLED, geofenceData.getIsFormFilled());
        values.put(COLUMN_IS_SYNC, geofenceData.getIsSync());
        values.put(COLUMN_FORM_DATA, geofenceData.getFormData());
        values.put(COLUMN_TASK_ID, geofenceData.getTaskId());

        Trunk.i(TAG, values.toString());

        try {
            // Insert the new row, returning the primary key value of the new row
            return db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Trunk.e(TAG, TABLE_NAME + ".addActivity: " + e.getMessage());
        }
        return -1L;
    }

    GeofenceData checkIfPendingFormExists(SQLiteDatabase db, String taskId) {
        if (db == null) {
            return null;
        }

        String checkQueryEvent = "SELECT * FROM " + TABLE_NAME + " where "
                + "( " + COLUMN_IS_SYNC + " == 0"
                + " OR " + COLUMN_IS_SYNC + " == 1 )"
                + " AND " + COLUMN_FORM_SUBMITTED + " == 0"
                + " AND " + COLUMN_FORM_FILLED + " == 0"
                + " AND " + COLUMN_TASK_ID + " == '" + taskId
                + "' ORDER BY " + COLUMN_CREATED_AT + " ASC LIMIT 1";

        Cursor cursor = db.rawQuery(checkQueryEvent, null);

        if (cursor == null || cursor.isClosed())
            return null;

        GeofenceData geofenceData = null;

        try {
            if (cursor.moveToFirst()) {
                geofenceData = new GeofenceData.Builder()
                        .setId(cursor.getInt(0))
                        .setTrackingId(cursor.getString(1))
                        .setGeofenceId(cursor.getString(2))
                        .setGeofenceType(cursor.getString(3))
                        .setIsFormSubmitted(cursor.getInt(5))
                        .setIsFormFilled(cursor.getInt(6))
                        .setIsSync(cursor.getInt(7))
                        .setFormData(cursor.getString(8))
                        .build();
            }
        } catch (Exception e) {
            Trunk.e(TAG, TABLE_NAME + ".checkIfPendingFormExists: " + e.getMessage());
        } finally {
            cursor.close();
        }
        return geofenceData;
    }

    void updateSyncStatus(SQLiteDatabase db, int isSynced, String geofenceId, int id, String trackingId) {
        if (db == null || TextUtils.isEmpty(geofenceId) || TextUtils.isEmpty(trackingId)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_SYNC, isSynced);
        String whereClause = COLUMN_TRACKING_ID + " == '" + trackingId +
                "' AND " + _ID + " == '" + id +
                "' AND " + COLUMN_GFENCE_ID + " == '" + geofenceId + "'";
        Log.e(TAG, "===> " + whereClause);
        try {
            db.update(TABLE_NAME, values, whereClause, null);
        } catch (Exception e) {
            Log.e(TAG, TABLE_NAME + ".syncWithServer: " + e.getMessage());
        }

    }

    void removeGeofenceById(SQLiteDatabase db, String geofenceId, String trackingId) {
        if (db == null) {
            return;
        }
        String query = "DELETE FROM " + TABLE_NAME + " where "
                + COLUMN_IS_SYNC + " ==  '2' "
                + COLUMN_GFENCE_ID + " == '" + geofenceId + "' "
                + COLUMN_TRACKING_ID + " == '" + trackingId + "'";
        try (Cursor cursor = db.rawQuery(query, null)) {
        } catch (Exception e) {
            Log.e(TAG, TABLE_NAME + ".syncWithServer: " + e.getMessage());
        }
    }
}
