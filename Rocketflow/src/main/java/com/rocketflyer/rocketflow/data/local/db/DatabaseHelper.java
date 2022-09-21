package com.rocketflyer.rocketflow.data.local.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.tracki.data.local.db.ApiEventModel;
import com.tracki.data.local.db.GeofenceDataSource;
import com.tracki.data.local.db.ITransitionTable;
import com.tracki.data.local.db.TransitionTable;
import com.tracki.data.model.GeofenceData;
import com.tracki.ui.service.transition.UserActivity;
import com.tracki.utils.Log;

import java.util.List;

/**
 * Created by rahul on 1/1/19
 */
public class DatabaseHelper extends SQLiteOpenHelper implements IAlarmTable, ITransitionTable, GeofenceDataSource {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "ServerAPISync.db";
    private static final int DATABASE_VERSION = 10;

    private static DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private AlarmTable alarmTable;
    private TransitionTable transitionTable;
    private GeofenceTable geofenceTable;

    /**
     * Constructor of this cl ass with context of the calling class.
     *
     * @param context calling class instance.
     */
    private DatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(TAG, "initializing " + DATABASE_NAME + " database...");
        initializeDatabase();
    }

    /**
     * Method used to get the instance of this class.
     *
     * @param context Context of calling class.
     * @return this class instance.
     */
    public static DatabaseHelper getInstance(final Context context) {
        if (databaseHelper == null) {
            synchronized (DatabaseHelper.class) {
                databaseHelper = new DatabaseHelper(context.getApplicationContext());
            }
        }
        return databaseHelper;

    }

    private void initializeDatabase() {
        if (database == null) {
            database = this.getWritableDatabase();
        }

        if (alarmTable == null) {
            alarmTable = new AlarmTable();
        }

        if (transitionTable == null) {
            transitionTable = new TransitionTable();
        }

        if (geofenceTable == null) {
            geofenceTable = new GeofenceTable();
        }

    }

    void onDestroy() {
        try {
            if (database != null)
                database.close();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: " + e.getMessage());
        }
    }

    public void onClearAndRecreateDatabase() {
        try {
            // Initialize SQLiteDatabase if null
            initializeDatabase();

            // Delete existing tables
            AlarmTable.onClearTable(database);

            // Delete existing tables
            TransitionTable.onClearTable(database);

            GeofenceTable.onClearTable(database);

            Log.i(TAG, "DatabaseHelper onClearAndRecreateDatabase called.");

            // Recreate tables
            onCreate(database);

        } catch (Exception e) {
            Log.e(TAG, "onClearAndRecreateDatabase: " + e.getMessage());
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        AlarmTable.onCreate(db);
        TransitionTable.onCreate(db);
        GeofenceTable.onCreate(db);
        Log.i(TAG, "DatabaseHelper onCreate called.");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        AlarmTable.onUpgrade(db, oldVersion, newVersion);
        TransitionTable.onUpgrade(db, oldVersion, newVersion);
        GeofenceTable.onUpgrade(db, oldVersion, newVersion);
        Log.i(TAG, "DatabaseHelper onUpgrade called.");
    }

    @Override
    public void addPendingApiEvent(ApiEventModel apiEventModel) {
        initializeDatabase();
        alarmTable.addPendingApiEvent(database, apiEventModel);
    }

    @Override
    public void syncWithServer(ApiEventModel apiEventModel, int syncMode) {
        initializeDatabase();
        alarmTable.syncWithServer(database, apiEventModel, syncMode);
    }

    @Override
    public List<ApiEventModel> checkIfRecordExists() {
        initializeDatabase();
        return alarmTable.checkIfRecordExists(database);
    }

    @Override
    public int deleteData() {
        initializeDatabase();
        return alarmTable.deleteData(database);

    }

    @Override
    public boolean isTrackingIdAlreadyExist(String trackingId) {
        initializeDatabase();
        return alarmTable.isTrackingIdAlreadyExist(database, trackingId);
    }

    @Override
    public void addTransition(UserActivity userActivity) {
        initializeDatabase();
        transitionTable.addTransition(database, userActivity);
    }

    @Override
    public List<UserActivity> getAllActivities() {
        initializeDatabase();
        return transitionTable.getAllTransitions(database);
    }

    @Override
    public long addGeoFenceToDB(GeofenceData geofenceData) {
        initializeDatabase();
        return geofenceTable.addGeoFenceToDB(database, geofenceData);
    }

    @Override
    public GeofenceData checkIfPendingFormExists(String taskId) {
        initializeDatabase();
        return geofenceTable.checkIfPendingFormExists(database, taskId);
    }

    @Override
    public void updateSyncStatus(int isSynced, String geofenceId, int id, String trackingId) {
        initializeDatabase();
        geofenceTable.updateSyncStatus(database, isSynced, geofenceId, id, trackingId);
    }

    @Override
    public void deleteData(String geofenceId, @NonNull String trackingId) {
        initializeDatabase();
        geofenceTable.removeGeofenceById(database, geofenceId, trackingId);
    }

//    public long saveEvent(Events event) {
//        //Gets the data repository in write mode
//        databaseHelper = DatabaseHelper.getInstance(con);
//        SQLiteDatabase db = databaseHelper.getWritableDatabase();
//
//        // Create a new map of values, where column names are the keys
//        ContentValues values = new ContentValues();
//
//        values.put(COLUMN_EVENT_TYPE, event.getEventType().name());
//        values.put(COLUMN_START_TIME, event.getStartTime());
//        values.put(COLUMN_END_TIME, event.getEndTime());
//        values.put(COLUMN_STATE, event.getTripState().name());
//        values.put(COLUMN_CREATED_AT, new Date().getTime());
//        values.put(COLUMN_BATTERY_LEVEL, event.getBatteryLevel());
//        values.put(COLUMN_IS_SYNC, 0);
//        values.put(COLUMN_TRIP_ID, event.getTrackingId());
//        values.put(COLUMN_LAT, event.getStartCoordinates().getLatitude());
//        values.put(COLUMN_LNG, event.getStartCoordinates().getLongitude());
//        values.put(COLUMN_END_LAT, event.getEndCoordinates().getLatitude());
//        values.put(COLUMN_END_LNG, event.getEndCoordinates().getLongitude());
//        values.put(COLUMN_SPEED, event.getSpeed());
//
//        //Log.e(TAG, "save Event : " + values.toString());
//
//
//        // Insert the new row, returning the primary key value of the new row
//        /*return*/
//        return db.insert(TABLE_EVENTS, null, values);
//    }
}
